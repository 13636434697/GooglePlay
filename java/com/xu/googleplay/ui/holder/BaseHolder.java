package com.xu.googleplay.ui.holder;

import android.view.View;


/*
*	为了给ListView的Adapter进行封装，所以写这个类
*
* 	还有其他作用，不一定用listView的adapter，所有的布局都要，加载布局，刷新数据。这里就封装了2个方法，
* 	所以写一个头条新闻holder对象，在里面处理布局和数据，从而实现了头条新闻的细节模块和HomeFragment实现了解耦
*
* 因为Baseadapter里面的getView里面都穿插了viewholder，而且每个viewHolder都不一样，
* 所有就抽出来，在做要做的事情，要抽共性，不局限与抽取变量，要做几个步骤
*
* */
public abstract class BaseHolder<T> {
	// 把数据设置进来，需要暴漏一个方法来接收数据，把T传过来
	private View mRootView;// 一个item的根布局
	// 把数据设置进来，需要暴漏一个方法来接收数据，把T传过来
	private T data;

	//当new这个对象时, 就会加载布局, 初始化控件,设置tag
	public BaseHolder() {
		// 把数据设置进来，需要暴漏一个方法来接收数据，把T传过来
		mRootView = initView();
		//convertView就是item的布局对象，这里的mRootView就是布局对象，当前对象就是this
		// 3. 打一个标记tag
		mRootView.setTag(this);
	}

	// 1. 加载布局文件
	// 2. 初始化布局控件 有了布局文件之后就可以直接findViewById（初始化控件）
	//一般初始化布局的时候，要知道布局文件，但是不知道，所以抽象方法，让子类去实现
	public abstract View initView();

	// 返回item的布局对象
	//在暴漏一个方法，让外面能拿到这个对象，
	public View getRootView() {
		return mRootView;
	}

	//刷新界面必须要有数据。所以要设置数据
	// 把数据设置进来，把T传过来
	// 设置当前item的数据
	public void setData(T data) {
		this.data = data;
		//有数据之后就刷新界面
		refreshView(data);
	}

	//将来可能要用到数据，所有暴露一个方法，把这个data返回出去
	// 获取当前item的数据
	public T getData() {
		return data;
	}

	// 4. 根据数据来刷新界面，需要有数据，数据在对象集合里面拿泛型。这里写T上面就要也写个T，
	public abstract void refreshView(T data);

}
