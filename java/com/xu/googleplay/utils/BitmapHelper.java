package com.xu.googleplay.utils;


import com.lidroid.xutils.BitmapUtils;

/*
* 因为如果不用这个类的话，可能每个holder都要new BitmapUtils，
* 底层用的是lrucache，用来控制缓存的，一个只能加载2M，其他都会回收。
*
* 应该要用一个的，要用单例模式
*
* */
public class BitmapHelper {

	private static BitmapUtils mBitmapUtils = null;

	// 单例, 懒汉模式
	public static BitmapUtils getBitmapUtils() {
		//线程有安全问题，
		if (mBitmapUtils == null) {
			//解决线程安全问题,加锁.，创建的时候才加锁，以这个类为锁
			// 写数据和修改数据可能线程安全，读数据不用考虑所以上面不用加
			synchronized (BitmapHelper.class) {
				//还得在判断
				if (mBitmapUtils == null) {
					mBitmapUtils = new BitmapUtils(UIUtils.getContext());
				}
			}
		}

		return mBitmapUtils;
	}
}
