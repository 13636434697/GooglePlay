package com.xu.googleplay.global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;


/**
 * 自定义application, 进行全局初始化
 * 一定要在清单文件里面配置一下 name属性，值就是这个类的名称
 *  这样的话，程序运行就会走这里的初始化
 * 很多全局的东西都要用，比如context，都需要传，所有在初始化的时候就准备好
 */
public class GooglePlayApplication extends Application {

    //为了方便访问都定义成全局的static
    private static Context context;
    private static Handler handler;
    private static int mainThreadId;

    @Override
    public void onCreate() {
        super.onCreate();
        //上下文
        context = getApplicationContext();
        //线程
        handler = new Handler();
        //当前主线程的ID，因为onCreate是在主线程运行的，Pid是进程ID，Tid是线程ID
        mainThreadId = android.os.Process.myTid();
    }

    //生成get方法，暴露这三个方法，让其他类能拿到初始化好的三个变量
    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }
}
