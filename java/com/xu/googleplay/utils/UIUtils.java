package com.xu.googleplay.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;

import com.xu.googleplay.global.GooglePlayApplication;

/**
*  这个工具类来获取全局初始化，全局的工具类
* */

public class UIUtils {

    //获取上下文
    public static Context getContext() {
        //直接返回该类的方法，因为已经初始化并暴露出方法
        return GooglePlayApplication.getContext();
    }
    //获取线程
    public static Handler getHandler() {
        //直接返回该类的方法，因为已经初始化并暴露出方法
        return GooglePlayApplication.getHandler();
    }
    //获取主线程的ID
    public static int getMainThreadId() {
        //直接返回该类的方法，因为已经初始化并暴露出方法
        return GooglePlayApplication.getMainThreadId();
    }

    // /////////////////加载资源文件 ///////////////////////////

    // 获取字符串
    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    // 获取字符串数组
    public static String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    // 获取图片
    public static Drawable getDrawable(int id) {
        return getContext().getResources().getDrawable(id);
    }

    // 获取颜色
    public static int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    //根据id获取颜色的状态选择器
    public static ColorStateList getColorStateList(int id) {
        return getContext().getResources().getColorStateList(id);
    }

    // 获取尺寸
    public static int getDimen(int id) {
        return getContext().getResources().getDimensionPixelSize(id);// 返回具体像素值
    }

    // /////////////////dip和px转换//////////////////////////

    public static int dip2px(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        //因为整型的精度关系会舍去小数点后面的，所以加0.5，能准确些
        return (int) (dip * density + 0.5f);
    }

    public static float px2dip(int px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return px / density;
    }

    // /////////////////加载布局文件//////////////////////////
    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }

    // /////////////////判断是否运行在主线程//////////////////////////
    public static boolean isRunOnUIThread() {
        // 获取当前线程id, 如果当前线程id和主线程id相同, 那么当前就是主线程
        int myTid = android.os.Process.myTid();
        if (myTid == getMainThreadId()) {
            return true;
        }

        return false;
    }

    // 运行在主线程
    public static void runOnUIThread(Runnable r) {
        if (isRunOnUIThread()) {
            // 已经是主线程, 直接运行
            r.run();
        } else {
            // 如果是子线程, 借助handler让其运行在主线程，
            // （队列和循环，post到队列之后就等待循环，因为队列是主线程，所有post就是主线程了）
            getHandler().post(r);
        }
    }

}
