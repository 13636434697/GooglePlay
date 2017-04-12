package com.xu.googleplay.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

public class DrawableUtils {

	//获取一个shape对象
	public static GradientDrawable getGradientDrawable(int color, int radius) {
		// xml中定义的shape标签 对应此类，就是一个形状
		GradientDrawable shape = new GradientDrawable();
		//可以设置形状
		shape.setShape(GradientDrawable.RECTANGLE);// 矩形
		//要设置圆角的矩形
		shape.setCornerRadius(radius);// 圆角半径
		shape.setColor(color);// 颜色

		return shape;
	}
	//还有变色，是一个状态选择器
	//获取状态选择器，要传2个图片，一个是默认的图片，一个是按下的图片
	public static StateListDrawable getSelector(Drawable normal, Drawable press) {
		//要拿一个图片
		StateListDrawable selector = new StateListDrawable();
		//加一个状态，new一个int数组
		selector.addState(new int[] { android.R.attr.state_pressed }, press);// 按下图片
		selector.addState(new int[] {}, normal);// 默认图片

		return selector;
	}

	//有三部太烦了，需要重载封装下
	//获取状态选择器
	public static StateListDrawable getSelector(int normal, int press, int radius) {
		//默认图片
		GradientDrawable bgNormal = getGradientDrawable(normal, radius);
		//按下图片
		GradientDrawable bgPress = getGradientDrawable(press, radius);
		//有了2张图片后，可以调用上面的方法，重载
		StateListDrawable selector = getSelector(bgNormal, bgPress);
		return selector;
	}


}
