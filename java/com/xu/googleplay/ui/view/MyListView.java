package com.xu.googleplay.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.ListView;

/*
* listView控件封装，因为其他页面也要用
* 不管代码，布局还是设置样式，都会强制设置，因为构造方法里面就重写了这三个方法
* */
public class MyListView extends ListView {

	public MyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//构造方法里初始化listView
		initView();
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//构造方法里初始化listView
		initView();
	}

	public MyListView(Context context) {
		super(context);
		//构造方法里初始化listView
		initView();
	}

	private void initView() {
		//listView点击的时候默认的背景颜色会出来，这里不传颜色（全透明），
		// 不能直接传null，必须有个对象
		this.setSelector(new ColorDrawable());
		//listView默认会有分割线，在这里传null去掉
		this.setDivider(null);
		//有时候滑动listView背景会变成黑色，此方法将背景变成为全透明
		this.setCacheColorHint(Color.TRANSPARENT);

	}

}
