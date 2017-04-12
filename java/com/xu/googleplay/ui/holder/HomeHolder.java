package com.xu.googleplay.ui.holder;

import android.text.format.Formatter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.xu.googleplay.R;
import com.xu.googleplay.domain.AppInfo;
import com.xu.googleplay.domain.DownloadInfo;
import com.xu.googleplay.http.HttpHelper;
import com.xu.googleplay.manager.DownloadManager;
import com.xu.googleplay.ui.view.ProgressArc;
import com.xu.googleplay.utils.BitmapHelper;
import com.xu.googleplay.utils.UIUtils;

/**
 * 首页holder
 * Baseholder的一个实现，但是必须要一个泛型，就是item的类型
 */
public class HomeHolder extends BaseHolder<AppInfo>  implements DownloadManager.DownloadObserver, View.OnClickListener {

	private TextView tvName, tvSize, tvDes;
	private ImageView ivIcon;
	private RatingBar rbStar;

	private BitmapUtils mBitmapUtils;

	private ProgressArc pbProgress;

	private int mCurrentState;
	private float mProgress;
	private DownloadManager mDM;
	private TextView tvDownload;
	//这个是初始化布局
	@Override
	public View initView() {
		// 1. 加载布局
		View view = UIUtils.inflate(R.layout.list_item_home);
		// 2. 初始化控件
		tvName = (TextView) view.findViewById(R.id.tv_name);
		tvSize = (TextView) view.findViewById(R.id.tv_size);
		tvDes = (TextView) view.findViewById(R.id.tv_des);
		ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
		rbStar = (RatingBar) view.findViewById(R.id.rb_star);

		//mBitmapUtils = new BitmapUtils(UIUtils.getContext());
		mBitmapUtils = BitmapHelper.getBitmapUtils();



		// 初始化进度条
		FrameLayout flProgress = (FrameLayout) view.findViewById(R.id.fl_progress);
		//父控件相应事件也可以
		flProgress.setOnClickListener(this);

		//另外一个进度条
		pbProgress = new ProgressArc(UIUtils.getContext());
		// 设置圆形进度条直径
		pbProgress.setArcDiameter(UIUtils.dip2px(26));
		// 设置进度条颜色
		pbProgress.setProgressColor(UIUtils.getColor(R.color.progress));
		// 设置进度条宽高布局参数
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(UIUtils.dip2px(27), UIUtils.dip2px(27));
		//最后添加到进度条
		flProgress.addView(pbProgress, params);

		// pbProgress.setOnClickListener(this);

		//监听进度
		mDM = DownloadManager.getInstance();
		mDM.registerObserver(this);// 注册观察者, 监听状态和进度变化
		return view;
	}

	//这个是刷新界面
	@Override
	public void refreshView(AppInfo data) {
		//设置文字的时候是对象的应用名称的name
		tvName.setText(data.name);
		//接收的是long类型的数据，需要格式化成单位
		tvSize.setText(Formatter.formatFileSize(UIUtils.getContext(), data.size));
		//设置文字的时候是对象的应用描述的des
		tvDes.setText(data.des);
		//设置文字的时候是对象的应用星星数量
		rbStar.setRating(data.stars);

		//xUtils的图片加载方法（图片，图片的下载链接，地址需要加前缀）是一个名字的方法返回的
		mBitmapUtils.display(ivIcon, HttpHelper.URL + "image?name=" + data.iconUrl);



		// 判断当前应用是否下载过
		DownloadInfo downloadInfo = mDM.getDownloadInfo(data);
		if (downloadInfo != null) {
			// 之前下载过
			mCurrentState = downloadInfo.currentState;
			mProgress = downloadInfo.getProgress();
		} else {
			// 没有下载过
			mCurrentState = DownloadManager.STATE_UNDO;
			mProgress = 0;
		}

		refreshUI(mCurrentState, mProgress, data.id);
	}

	/**
	 * 刷新界面
	 *
	 * @param progress
	 * @param state
	 */
	private void refreshUI(int state, float progress, String id) {
		// 由于listview重用机制, 会让其他item也会跟着刷新，
		// 要确保刷新之前, 确实是同一个应用
		if (!getData().id.equals(id)) {
			return;
		}

		mCurrentState = state;
		mProgress = progress;
		switch (state) {
			case DownloadManager.STATE_UNDO:
				// 自定义进度条背景
				pbProgress.setBackgroundResource(R.drawable.ic_download);
				// 没有进度
				pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
				tvDownload.setText("下载");
				break;
			case DownloadManager.STATE_WAITING:
				pbProgress.setBackgroundResource(R.drawable.ic_download);
				// 等待模式
				pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_WAITING);
				tvDownload.setText("等待");
				break;
			case DownloadManager.STATE_DOWNLOADING:
				pbProgress.setBackgroundResource(R.drawable.ic_pause);
				// 下载中模式
				pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_DOWNLOADING);
				pbProgress.setProgress(progress, true);
				tvDownload.setText((int) (progress * 100) + "%");
				break;
			case DownloadManager.STATE_PAUSE:
				pbProgress.setBackgroundResource(R.drawable.ic_resume);
				pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
				break;
			case DownloadManager.STATE_ERROR:
				pbProgress.setBackgroundResource(R.drawable.ic_redownload);
				pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
				tvDownload.setText("下载失败");
				break;
			case DownloadManager.STATE_SUCCESS:
				pbProgress.setBackgroundResource(R.drawable.ic_install);
				pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
				tvDownload.setText("安装");
				break;

			default:
				break;
		}
	}

	// 主线程更新ui 3-4
	private void refreshUIOnMainThread(final DownloadInfo info) {
		// 判断下载对象是否是当前应用
		AppInfo appInfo = getData();
		if (appInfo.id.equals(info.id)) {
			UIUtils.runOnUIThread(new Runnable() {

				@Override
				public void run() {
					refreshUI(info.currentState, info.getProgress(), info.id);
				}
			});
		}
	}

	@Override
	public void onDownloadStateChanged(DownloadInfo info) {
		refreshUIOnMainThread(info);
	}

	@Override
	public void onDownloadProgressChanged(DownloadInfo info) {
		refreshUIOnMainThread(info);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fl_progress:
				// 根据当前状态来决定下一步操作
				if (mCurrentState == DownloadManager.STATE_UNDO || mCurrentState == DownloadManager.STATE_ERROR || mCurrentState == DownloadManager.STATE_PAUSE) {
					mDM.download(getData());// 开始下载
				} else if (mCurrentState == DownloadManager.STATE_DOWNLOADING || mCurrentState == DownloadManager.STATE_WAITING) {
					mDM.pause(getData());// 暂停下载
				} else if (mCurrentState == DownloadManager.STATE_SUCCESS) {
					mDM.install(getData());// 开始安装
				}

				break;

			default:
				break;
		}
	}

}
