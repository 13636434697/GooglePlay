package com.xu.googleplay.ui.holder;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.xu.googleplay.domain.DownloadInfo;
import com.xu.googleplay.manager.DownloadManager;
import com.xu.googleplay.manager.DownloadManager.DownloadObserver;
import com.xu.googleplay.R;
import com.xu.googleplay.domain.AppInfo;
import com.xu.googleplay.ui.view.ProgressHorizontal;
import com.xu.googleplay.utils.UIUtils;

/**
 * 详情页-下载模块
 *
 * 下载有几种状态，未下载，等待下载，正在下载，暂停下载，下载失败，下载成功
 * 不同的状态，界面有几种展现等等，需要封装
 */
public class DetailDownloadHolder extends BaseHolder<AppInfo> implements DownloadObserver, OnClickListener {

	private DownloadManager mDM;

	private int mCurrentState;
	private float mProgress;

	private FrameLayout flProgress;
	private Button btnDownload;
	private ProgressHorizontal pbProgress;

	//初始化布局文件
	@Override
	public View initView() {
		View view = UIUtils.inflate(R.layout.layout_detail_download);
		btnDownload = (Button) view.findViewById(R.id.btn_download);
		//给button的下载设置点击事件
		btnDownload.setOnClickListener(this);

		// 初始化自定义
		flProgress = (FrameLayout) view.findViewById(R.id.fl_progress);
		//给自定义进度条设置点击事件
		flProgress.setOnClickListener(this);

		//水平方向的进度条
		pbProgress = new ProgressHorizontal(UIUtils.getContext());
		pbProgress.setProgressBackgroundResource(R.drawable.progress_bg);// 进度条背景图片
		pbProgress.setProgressResource(R.drawable.progress_normal);// 进度条图片
		pbProgress.setProgressTextColor(Color.WHITE);// 进度文字颜色
		//sp实在没有办法传进去
		pbProgress.setProgressTextSize(UIUtils.dip2px(18));// 进度文字大小

		// 宽高填充父窗体，布局参数
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);

		// 给帧布局添加自定义进度条
		flProgress.addView(pbProgress, params);

		//监听下载进度和状态的变化，已经写了观察者模式，这个类注册观察者的行列来
		mDM = DownloadManager.getInstance();
		//在上面实现接口，实现2个方法，只要有状态和进度，收到回调，
		mDM.registerObserver(this);// 注册观察者, 监听状态和进度变化

		return view;
	}

	//刷新界面数据，拿到打他对象
	@Override
	public void refreshView(AppInfo data) {
		//拿到下载过的集合
		DownloadInfo downloadInfo = mDM.getDownloadInfo(data);
		// 判断当前应用是否下载过
		if (downloadInfo != null) {
			// 之前下载过
			//展示界面的时候，根据下载对象的状态和进度来更新界面
			//拿到当时的下载状态
			mCurrentState = downloadInfo.currentState;
			//拿到当时下载进度
			mProgress = downloadInfo.getProgress();
		} else {
			// 没有下载过
			//状态就是默认状态，就是没有下载
			mCurrentState = DownloadManager.STATE_UNDO;
			//进度就是0，没有下载过
			mProgress = 0;
		}
		//根据当时的状态和进度来更新界面，把状态和进度传进来
		refreshUI(mCurrentState, mProgress);
	}


	//这是一个公开的方法，
	// 根据当前的下载进度和状态来更新界面
	private void refreshUI(int currentState, float progress) {

		//System.out.println("刷新ui了:" + currentState);

		mCurrentState = currentState;
		mProgress = progress;

		//看看当时是什么状态
		switch (currentState) {
			//只是一个下载的按钮
		case DownloadManager.STATE_UNDO:// 未下载
			flProgress.setVisibility(View.GONE);
			btnDownload.setVisibility(View.VISIBLE);
			btnDownload.setText("下载");
			break;

		case DownloadManager.STATE_WAITING:// 等待下载
			flProgress.setVisibility(View.GONE);
			btnDownload.setVisibility(View.VISIBLE);
			btnDownload.setText("等待中..");
			break;

		case DownloadManager.STATE_DOWNLOADING:// 正在下载
			flProgress.setVisibility(View.VISIBLE);
			btnDownload.setVisibility(View.GONE);
			pbProgress.setCenterText("");
			pbProgress.setProgress(mProgress);// 设置下载进度
			break;

		case DownloadManager.STATE_PAUSE:// 下载暂停
			flProgress.setVisibility(View.VISIBLE);
			btnDownload.setVisibility(View.GONE);
			pbProgress.setCenterText("暂停");
			pbProgress.setProgress(mProgress);// 设置下载进度

			System.out.println("暂停界面更新:" + mCurrentState);
			break;

		case DownloadManager.STATE_ERROR:// 下载失败
			flProgress.setVisibility(View.GONE);
			btnDownload.setVisibility(View.VISIBLE);
			btnDownload.setText("下载失败");
			break;

		case DownloadManager.STATE_SUCCESS:// 下载成功
			flProgress.setVisibility(View.GONE);
			btnDownload.setVisibility(View.VISIBLE);
			btnDownload.setText("安装");
			break;

		default:
			break;
		}

	}

	// 状态更新，这个有时候在主线程，有时候在子线程。如果在子线程更新UI的话，就会崩溃，所以写这个方法
	// 主线程更新ui 3-4
	private void refreshUIOnMainThread(final DownloadInfo info) {
		//每个应用只关心自己的信息，所有要过滤信息
		// 判断下载对象是否是当前应用
		//拿到当前的应用
		AppInfo appInfo = getData();
		//根据id判断，是否是一个应用
		if (appInfo.id.equals(info.id)) {
			//主线程更新ui，这个方法在主线程的
			UIUtils.runOnUIThread(new Runnable() {

				@Override
				public void run() {
					//是自己应用的话就刷新
					//就传详情对象，在里面取出状态和进度
					//对象里面的字段是可以改的，不能把infonew成另外对象，在这里取的就是最新的
					refreshUI(info.currentState, info.getProgress());
				}
			});
		}
	}

	// 状态更新，这个有时候在主线程，有时候在子线程。如果在子线程更新UI的话，就会崩溃
	//在主线程刷新ui的参数是传进来的，是用handler的runnable，是异步执行的，不是马上执行的
	@Override
	public void onDownloadStateChanged(DownloadInfo info) {
		// 判断下载对象是否是当前应用
		// AppInfo appInfo = getData();
		// if (appInfo.id.equals(info.id)) {
		// System.out.println("当前状态:" + info.currentState);
		// refreshUIOnMainThread(info.currentState, info.getProgress());
		//状态和进度发生变化的时候，都要调用这个方法
		refreshUIOnMainThread(info);
		// }
	}

	// 进度更新, 子线程,在run方法里面，所有是子线程
	@Override
	public void onDownloadProgressChanged(DownloadInfo info) {
		// 判断下载对象是否是当前应用
		// AppInfo appInfo = getData();
		// if (appInfo.id.equals(info.id)) {
		// System.out.println("当前状态:" + info.currentState + ";"
		// + info.getProgress());
		// refreshUIOnMainThread(info.currentState, info.getProgress());
		//状态和进度发生变化的时候，都要调用这个方法
		refreshUIOnMainThread(info);
		// }
	}

	@Override
	public void onClick(View v) {
		//System.out.println("点击事件响应了:" + mCurrentState);

		switch (v.getId()) {
		case R.id.btn_download:
		case R.id.fl_progress:
			// 根据当前状态来决定下一步操作
			//未下载状态，或者下载失败了，或者是暂停下载，这些情况都需要下载
			if (mCurrentState == DownloadManager.STATE_UNDO || mCurrentState == DownloadManager.STATE_ERROR || mCurrentState == DownloadManager.STATE_PAUSE) {
				mDM.download(getData());// 开始下载
				//如果是这个在下载，或者等待下载
			} else if (mCurrentState == DownloadManager.STATE_DOWNLOADING || mCurrentState == DownloadManager.STATE_WAITING) {
				mDM.pause(getData());// 暂停下载
				//状态是成功的状态
			} else if (mCurrentState == DownloadManager.STATE_SUCCESS) {
				mDM.install(getData());// 开始安装
			}

			break;

		default:
			break;
		}
	}

}
