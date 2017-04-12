package com.xu.googleplay.domain;

import java.io.File;

import android.os.Environment;

import com.xu.googleplay.manager.DownloadManager;

/**
 * 下载对象，封装成对象，下载的话就操作这个对象就可以了
 * 
 * 注意: 一定要有读写sdcard的权限!!!!
 * 
 *  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 *
 */
public class DownloadInfo {

	public String id;//应用的id
	public String name;//应用的名称
	public String downloadUrl;//应用的下载地址
	public long size;//应用的大小
	public String packageName;//应用的包名

	public long currentPos;// 当前下载位置
	public int currentState;// 当前下载状态
	public String path;// 下载到本地文件的路径

	public static final String GOOGLE_MARKET = "GOOGLE_MARKET";// sdcard根目录文件夹名称
	public static final String DONWLOAD = "download";// 子文件夹名称, 存放下载的文件

	// 获取下载进度(0-1)
	public float getProgress() {
		//这里size不能为0，不然会挂掉
		if (size == 0) {
			return 0;
		}

		float progress = currentPos / (float) size;
		return progress;
	}

	// 拷贝对象, 从AppInfo中拷贝出一个DownloadInfo
	public static DownloadInfo copy(AppInfo info) {
		DownloadInfo downloadInfo = new DownloadInfo();

		downloadInfo.id = info.id;
		downloadInfo.name = info.name;
		downloadInfo.downloadUrl = info.downloadUrl;
		downloadInfo.packageName = info.packageName;
		downloadInfo.size = info.size;

		//下载的位置默认是0，
		downloadInfo.currentPos = 0;
		//下载的状态就是默认未下载
		downloadInfo.currentState = DownloadManager.STATE_UNDO;
		//下载地址
		downloadInfo.path = downloadInfo.getFilePath();

		return downloadInfo;
	}

	// 获取文件下载路径
	public String getFilePath() {
		//用这个StringBuffer来拼接字符串的操作
		StringBuffer sb = new StringBuffer();
		//先拿到路径
		String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
		//先是根目录
		sb.append(sdcard);
		// sb.append("/");
		//这里用兼容的斜杠
		sb.append(File.separator);
		//文件夹名
		sb.append(GOOGLE_MARKET);
		//这里用兼容的斜杠
		sb.append(File.separator);
		//文件夹名
		sb.append(DONWLOAD);

		if (createDir(sb.toString())) {
			// 文件夹存在或者已经创建完成
			return sb.toString() + File.separator + name + ".apk";// 返回文件路径
		}

		return null;
	}

	//创建文件夹
	private boolean createDir(String dir) {
		//路径传进来
		File dirFile = new File(dir);

		// 文件夹不存在或者不是一个文件夹
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			//创建文件夹，是否创建成功返回出去
			return dirFile.mkdirs();
		}

		return true;// 文件夹存在
	}

}
