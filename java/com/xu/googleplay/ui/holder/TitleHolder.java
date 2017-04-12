package com.xu.googleplay.ui.holder;

import android.view.View;
import android.widget.TextView;

import com.xu.googleplay.R;
import com.xu.googleplay.domain.CategoryInfo;
import com.xu.googleplay.utils.UIUtils;

/**
 * 分类模块标题holder
 * 要求的泛型CategoryInfo
 */
public class TitleHolder extends BaseHolder<CategoryInfo> {

	public TextView tvTitle;

	//初始化布局
	@Override
	public View initView() {
		View view = UIUtils.inflate(R.layout.list_item_title);
		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		return view;
	}

	//刷新界面的时候
	@Override
	public void refreshView(CategoryInfo data) {
		//设置标题
		tvTitle.setText(data.title);
	}

}
