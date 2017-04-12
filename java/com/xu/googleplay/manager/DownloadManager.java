package com.xu.googleplay.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Intent;
import android.net.Uri;

import com.xu.googleplay.domain.AppInfo;
import com.xu.googleplay.domain.DownloadInfo;
import com.xu.googleplay.http.HttpHelper;
import com.xu.googleplay.utils.IOUtils;
import com.xu.googleplay.utils.UIUtils;

/**
 * 下载管理器，下载的单例类（一般全局的管理器都是单例模式）注意：synchronized，要加锁，涉及多线程，这样保险些
 * 
 * - 未下载 - 等待下载 - 正在下载 - 暂停下载 - 下载失败 - 下载成功
 * 
 * DownloadManager: 被观察者, 有责任通知所有观察者状态和进度发生变化
 *
 *
 * 使用了观察者设计模式，一处在做，但是好几个界面都在同步，被观察者只有一个，
 *
 * 注册观察者和注销观察者
 */
public class DownloadManager {

	//声明一下几种状态
	public static final int STATE_UNDO = 1;//未下载
	public static final int STATE_WAITING = 2;// 等待下载
	public static final int STATE_DOWNLOADING = 3;//正在下载
	public static final int STATE_PAUSE = 4;//暂停下载
	public static final int STATE_ERROR = 5;//下载失败
	public static final int STATE_SUCCESS = 6;//下载成功

	//偶尔可以用下饿汉模式，最简单，而且还没有线程安全问题，一上来就new一个
	private static DownloadManager mDM = new DownloadManager();

	// 4. 观察者集合，观察者进来和出去，就是集合添加和删除
	private ArrayList<DownloadObserver> mObservers = new ArrayList<DownloadObserver>();

	// 下载对象的集合, 使用线程安全的HashMap
	// 下载的对象，保存在集合里面，因为可能会下载很多个
	// private HashMap<String, DownloadInfo> mDownloadInfoMap = new HashMap<String, DownloadInfo>();
	//线程安全的Concurrent并发的意思
	private ConcurrentHashMap<String, DownloadInfo> mDownloadInfoMap = new ConcurrentHashMap<String, DownloadInfo>();

	// 下载任务的集合
	// 任务可能有很多个，需要一个集合，将下载任务放入集合中
	private ConcurrentHashMap<String, DownloadTask> mDownloadTaskMap = new ConcurrentHashMap<String, DownloadTask>();

	//首先是构造方法
	private DownloadManager() {
	};

	//然后是公开的方法
	public static DownloadManager getInstance() {
		return mDM;
	}

	// 2. 注册观察者，就是新的观察者对象进来
	public synchronized void registerObserver(DownloadObserver observer) {
		//注册就是给集合添加观察者，对象传进来，做了一个安全的判断，集合里面没有observer的话
		if (observer != null && !mObservers.contains(observer)) {
			mObservers.add(observer);
		}
	}

	// 3. 注销观察者，就是观察者对象离去
	public synchronized void unregisterObserver(DownloadObserver observer) {
		//需要注销的对象，传进来，做了一个判断，observer确实包含，才删除
		if (observer != null && mObservers.contains(observer)) {
			mObservers.remove(observer);
		}
	}

	// 5.通知下载状态发生变化
	// 下载进度发生变化，变化的详情对象，需要给观察者传过去
	public synchronized void notifyDownloadStateChanged(DownloadInfo info) {
		//通知所有的观察者自己的变化
		//通知所有的Observers，所以要遍历Observers
		for (DownloadObserver observer : mObservers) {
			 //就是调用每个人的方法
			observer.onDownloadStateChanged(info);
		}
	}

	// 6.通知下载进度发生变化
	public synchronized void notifyDownloadProgressChanged(DownloadInfo info) {
		//通知所有的观察者自己的变化
		//通知所有的Observers，所以要遍历Observers
		for (DownloadObserver observer : mObservers) {
			//就是调用每个人的方法
			observer.onDownloadProgressChanged(info);
		}
	}

	// 开始下载
	public synchronized void download(AppInfo info) {
		// 如果对象是第一次下载, 需要创建一个新的DownloadInfo对象,从头下载
		// 如果之前下载过, 要接着下载,实现断点续传
		//拿到id
		DownloadInfo downloadInfo = mDownloadInfoMap.get(info.id);

		// 需要判断一下，下载的对象，保存在集合里面，因为可能会下载很多个
		if (downloadInfo == null) {
			// 生成一个下载的对象，没有的话，在生成
			downloadInfo = DownloadInfo.copy(info);
		}

		downloadInfo.currentState = STATE_WAITING;// 状态切换为等待下载

		// 下载进度发生变化，变化的详情对象，需要给观察者传过去
		notifyDownloadStateChanged(downloadInfo);// 通知所有的观察者, 状态发生变化了

		System.out.println(downloadInfo.name + "等待下载啦");

		// 将下载对象放入集合中，key用应用的id，value是下载的详情
		mDownloadInfoMap.put(downloadInfo.id, downloadInfo);


		// 开始下载，初始化下载任务
		DownloadTask task = new DownloadTask(downloadInfo);
		//并放入线程池中运行
		ThreadManager.getThreadPool().execute(task);

		// 任务可能有很多个，需要一个集合，将下载任务放入集合中
		mDownloadTaskMap.put(downloadInfo.id, task);
	}

	// 下载任务对象
	class DownloadTask implements Runnable {
		//数据用构造方法传进来
		private DownloadInfo downloadInfo;
		//数据用构造方法传进来
		public DownloadTask(DownloadInfo downloadInfo) {
			this.downloadInfo = downloadInfo;
		}

		//在这里开始下载文件
		@Override
		public void run() {
			System.out.println(downloadInfo.name + "开始下载啦");

			// 状态切换为正在下载
			downloadInfo.currentState = STATE_DOWNLOADING;
			notifyDownloadStateChanged(downloadInfo);

			//downloadInfo.path已经知道有没有文件了，new一个文件
			//服务器要下载本地要newfile
			File file = new File(downloadInfo.path);

			HttpHelper.HttpResult httpResult;

			//判断文件有没有，如果有就接着下，如果没有文件就从头开始下载，
			// 如果文件长度不完整的话，重写下载，如果当前位置是0的话，也要重写下载
			if (!file.exists() || file.length() != downloadInfo.currentPos || downloadInfo.currentPos == 0) {
				// 从头开始下载
				// 删除无效文件
				file.delete();// 文件如果不存在也是可以删除的, 只不过没有效果而已
				downloadInfo.currentPos = 0;// 当前下载位置置为0

				// 从头开始下载
				httpResult = HttpHelper.download(HttpHelper.URL + "download?name=" + downloadInfo.downloadUrl);
			} else {
				// 断点续传
				// range 表示请求服务器从文件的哪个位置开始返回数据，就是文件的当前长度
				httpResult = HttpHelper.download(HttpHelper.URL + "download?name=" + downloadInfo.downloadUrl + "&range=" + file.length());
			}

			//这里开始读httpResult文件了，判断是否为空并且流是否为空
			if (httpResult != null && httpResult.getInputStream() != null) {
				//先拿到输入流
				InputStream in = httpResult.getInputStream();
				//还要输出流，因为流读出来后，要给文件去读
				FileOutputStream out = null;
				try {
					//本地已经有文件，还读的话，就覆盖掉了，断电续传就没有意义了，要追加文件继续读
					out = new FileOutputStream(file, true);// 要在原有文件基础上追加数据

					//然后开始读
					int len = 0;
					byte[] buffer = new byte[1024];

					// 只有状态是正在下载, 才继续轮询. 解决下载过程中中途暂停的问题
					//最后一个判断是否暂停状态
					while ((len = in.read(buffer)) != -1 && downloadInfo.currentState == STATE_DOWNLOADING) {
						//写进来
						out.write(buffer, 0, len);
						//因为读的时候，缓冲期还没有刷新，所有需要把剩余数据刷入本地
						out.flush();

						// 更新下载进度
						downloadInfo.currentPos += len;
						//通知观察者
						notifyDownloadProgressChanged(downloadInfo);
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					//关掉流
					IOUtils.close(in);
					IOUtils.close(out);
				}

				// 文件下载结束，怎么判断到底有没有下载成功，看文件的完整性
				if (file.length() == downloadInfo.size) {
					// 文件完整, 表示下载成功
					downloadInfo.currentState = STATE_SUCCESS;
					notifyDownloadStateChanged(downloadInfo);
				} else if (downloadInfo.currentState == STATE_PAUSE) {
					// 中途暂停
					notifyDownloadStateChanged(downloadInfo);
				} else {
					// 下载失败
					file.delete();// 删除无效文件
					downloadInfo.currentState = STATE_ERROR;
					downloadInfo.currentPos = 0;
					notifyDownloadStateChanged(downloadInfo);
				}
			} else {
				// 网络异常
				file.delete();// 删除无效文件
				downloadInfo.currentState = STATE_ERROR;
				downloadInfo.currentPos = 0;
				//通知观察者
				notifyDownloadStateChanged(downloadInfo);
			}

			// 下载完成之后，从集合中移除下载任务
			mDownloadTaskMap.remove(downloadInfo.id);
		}

	}

	// 下载暂停，暂停谁要知道，所以传进来集合
	public synchronized void pause(AppInfo info) {
		// 取出下载对象，看看有没有
		DownloadInfo downloadInfo = mDownloadInfoMap.get(info.id);

		if (downloadInfo != null) {
			//有正在下载的对象，才有必要暂停
			// 只有在正在下载和等待下载时才需要暂停
			if (downloadInfo.currentState == STATE_DOWNLOADING || downloadInfo.currentState == STATE_WAITING) {

				//看看有没有当前的任务
				DownloadTask task = mDownloadTaskMap.get(downloadInfo.id);

				if (task != null) {
					// 移除下载任务, 如果任务还没开始,正在等待, 可以通过此方法移除
					// 如果任务已经开始运行, 需要在run方法里面进行中断
					ThreadManager.getThreadPool().cancel(task);
				}
				
				// 将下载状态切换为暂停
				downloadInfo.currentState = STATE_PAUSE;
				//通知观察者，状态发生变化了
				notifyDownloadStateChanged(downloadInfo);
			}
		}
	}

	// 开始安装，要安装的集合
	public synchronized void install(AppInfo info) {
		DownloadInfo downloadInfo = mDownloadInfoMap.get(info.id);
		if (downloadInfo != null) {
			// 跳到系统的安装页面进行安装
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.parse("file://" + downloadInfo.path),"application/vnd.android.package-archive");
			UIUtils.getContext().startActivity(intent);
		}
	}

	/**
	 * 1. 声明观察者的接口，观察下载的各种情况
	 */
	public interface DownloadObserver {

		//状态的监听
		// 下载状态发生变化，变化的详情对象，需要给观察者传过去
		public void onDownloadStateChanged(DownloadInfo info);
		//状态的监听
		// 下载进度发生变化，变化的详情对象，需要给观察者传过去
		public void onDownloadProgressChanged(DownloadInfo info);
	}

	//暴漏了一个方法，下载过的集合
	// 根据应用信息返回下载对象
	public DownloadInfo getDownloadInfo(AppInfo info) {
		return mDownloadInfoMap.get(info.id);
	}

}
