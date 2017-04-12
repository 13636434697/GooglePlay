package com.xu.googleplay.ui.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xu.googleplay.R;
import com.xu.googleplay.utils.UIUtils;
/*
* 加载更多的类型，在写一个holder，加载跟多的holder对象来初始化布局，凡是一个布局都要写一个holder
*
* 需要传一个泛型
* */

public class MoreHolder extends BaseHolder<Integer> {

	// 加载更多的几种状态
	// 1. 可以加载更多
	// 2. 加载更多失败
	// 3. 没有更多数据
	public static final int STATE_MORE_MORE = 1;
	public static final int STATE_MORE_ERROR = 2;
	public static final int STATE_MORE_NONE = 3;

	private LinearLayout llLoadMore;
	private TextView tvLoadError;

	//是否有更多数据通过构造方法传过来
	public MoreHolder(boolean hasMore) {
		// 如果有更多数据,状态为STATE_MORE_MORE,否则为STATE_MORE_NONE,将此状态传递给父类的data,
		// 父类会同时刷新界面
		//三元表达式，如果是hasmore（更多数据）就是跟多数据，否者就是没有数据
		setData(hasMore ? STATE_MORE_MORE : STATE_MORE_NONE);//setData结束后一定会调refreshView
	}

	@Override
	public View initView() {
		View view = UIUtils.inflate(R.layout.list_item_more);

		llLoadMore = (LinearLayout) view.findViewById(R.id.ll_load_more);
		tvLoadError = (TextView) view.findViewById(R.id.tv_load_error);

		return view;
	}

	//在什么情况下没有更多数据，有些页面就是没有更多数据，一初始化这个类的时候，就要传给我，有更多数据还是没有更多数据
	//
	@Override
	public void refreshView(Integer data) {
		switch (data) {
		case STATE_MORE_MORE:
			// 显示加载更多
			llLoadMore.setVisibility(View.VISIBLE);
			tvLoadError.setVisibility(View.GONE);
			break;
		case STATE_MORE_NONE:
			// 隐藏加载更多
			llLoadMore.setVisibility(View.GONE);
			tvLoadError.setVisibility(View.GONE);
			break;
		case STATE_MORE_ERROR:
			// 显示加载失败的布局
			llLoadMore.setVisibility(View.GONE);
			tvLoadError.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}

}
