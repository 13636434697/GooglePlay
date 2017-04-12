package com.xu.googleplay.ui.holder;

import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.xu.googleplay.R;
import com.xu.googleplay.domain.AppInfo;
import com.xu.googleplay.http.HttpHelper;
import com.xu.googleplay.utils.BitmapHelper;
import com.xu.googleplay.utils.UIUtils;

/**
 * 详情页-应用信息
 */
public class DetailAppInfoHolder extends BaseHolder<AppInfo> {

	private ImageView ivIcon;
	private TextView tvName;
	private TextView tvDownloadNum;
	private TextView tvVersion;
	private TextView tvDate;
	private TextView tvSize;
	private RatingBar rbStar;
	private BitmapUtils mBitmapUtils;

	//初始化布局
	@Override
	public View initView() {
		//加载xml布局
		View view = UIUtils.inflate(R.layout.layout_detail_appinfo);

		ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
		tvName = (TextView) view.findViewById(R.id.tv_name);
		tvDownloadNum = (TextView) view.findViewById(R.id.tv_download_num);
		tvVersion = (TextView) view.findViewById(R.id.tv_version);
		tvDate = (TextView) view.findViewById(R.id.tv_date);
		tvSize = (TextView) view.findViewById(R.id.tv_size);
		rbStar = (RatingBar) view.findViewById(R.id.rb_star);

		mBitmapUtils = BitmapHelper.getBitmapUtils();

		return view;
	}

	//刷新界面
	@Override
	public void refreshView(AppInfo data) {
		mBitmapUtils.display(ivIcon, HttpHelper.URL + "image?name=" + data.iconUrl);
		tvName.setText(data.name);
		tvDownloadNum.setText("下载量:" + data.downloadNum);
		tvVersion.setText("版本号:" + data.version);
		tvDate.setText(data.date);
		//大小需要格式化
		tvSize.setText(Formatter.formatFileSize(UIUtils.getContext(), data.size));
		rbStar.setRating(data.stars);
	}

}
