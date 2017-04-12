package com.xu.googleplay.ui.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.xu.googleplay.R;
import com.xu.googleplay.domain.SubjectInfo;
import com.xu.googleplay.http.HttpHelper;
import com.xu.googleplay.utils.BitmapHelper;
import com.xu.googleplay.utils.UIUtils;

/**
 * 专题holder
 *
 */
public class SubjectHolder extends BaseHolder<SubjectInfo> {

	private ImageView ivPic;
	private TextView tvTitle;

	private BitmapUtils mBitmapUtils;


	//需要新建一个布局文件
	@Override
	public View initView() {
		//加载布局
		View view = UIUtils.inflate(R.layout.list_item_subject);
		ivPic = (ImageView) view.findViewById(R.id.iv_pic);
		tvTitle = (TextView) view.findViewById(R.id.tv_title);

		mBitmapUtils = BitmapHelper.getBitmapUtils();

		return view;
	}

	@Override
	public void refreshView(SubjectInfo data) {
		//设置文字
		tvTitle.setText(data.des);
		//设置图片，BitmapUtils
		mBitmapUtils.display(ivPic, HttpHelper.URL + "image?name=" + data.url);
	}

}
