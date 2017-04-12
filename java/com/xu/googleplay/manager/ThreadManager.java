package com.xu.googleplay.manager;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

/**
 * 线程管理器
 *
 * 用户下载的话，可能几十个一起下载，这样的话，就十几个线程一起运行就会卡
 *
 * 需要线程池来维护这些线程，就是这类
 *
 * 线程池的优化好，如果程序线程多的话，就用线程池
 */
public class ThreadManager {

	//声明类
	private static ThreadPool mThreadPool;

	//线程池，只能有一个，所以是单例模式，要暴漏一个获取单例模式的方法
	public static ThreadPool getThreadPool() {
		if (mThreadPool == null) {
			//要加个关键字，线程安全问题，加了一把锁
			synchronized (ThreadManager.class) {
				//还要判断一下
				if (mThreadPool == null) {
					int cpuCount = Runtime.getRuntime().availableProcessors();// 获取cpu数量
					System.out.println("cup个数:" + cpuCount);

					// int threadCount = cpuCount * 2 + 1;//线程个数
					int threadCount = 10;
					//new一个类
					mThreadPool = new ThreadPool(threadCount, threadCount, 1L);
				}
			}
		}
		//然后返回出去
		return mThreadPool;
	}

	// 创建的线程池
	public static class ThreadPool {

		private int corePoolSize;// 核心线程数
		private int maximumPoolSize;// 最大线程数
		private long keepAliveTime;// 休息时间

		private ThreadPoolExecutor executor;
		//线程池的构造方法
		//构造方法私有的之后里面才能new，上面只能new一次
		private ThreadPool(int corePoolSize, int maximumPoolSize,long keepAliveTime) {
			//参数传进来，可以在外面指定
			this.corePoolSize = corePoolSize;
			this.maximumPoolSize = maximumPoolSize;
			this.keepAliveTime = keepAliveTime;
		}

		//执行某个线程的方法
		// 线程池几个参数的理解:
		// 比如去火车站买票, 有10个售票窗口, 但只有5个窗口对外开放. 那么对外开放的5个窗口称为核心线程数,
		// 而最大线程数是10个窗口.
		// 如果5个窗口都被占用, 那么后来的人就必须在后面排队, 但后来售票厅人越来越多, 已经人满为患, 就类似于线程队列已满.
		// 这时候火车站站长下令, 把剩下的5个窗口也打开, 也就是目前已经有10个窗口同时运行. 后来又来了一批人,
		// 10个窗口也处理不过来了, 而且售票厅人已经满了, 这时候站长就下令封锁入口,不允许其他人再进来, 这就是线程异常处理策略.
		// 而线程存活时间指的是, 允许售票员休息的最长时间, 以此限制售票员偷懒的行为.
		public void execute(Runnable r) {
			//这个线程池只能初始化一次，所有判断一下
			if (executor == null) {
				//java自带的线程池的执行者
				executor = new ThreadPoolExecutor(corePoolSize,maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
						new LinkedBlockingQueue<Runnable>(),Executors.defaultThreadFactory(), new AbortPolicy());
				// 参1:核心线程数;参2:最大线程数;参3:线程休眠时间;参4:时间单位;参5:线程队列;参6:生产线程的工厂（默认的工厂）;参7:线程异常处理策略
			}

			// 线程池执行一个Runnable对象, 具体运行时机线程池说了算，runnable传进来
			executor.execute(r);
		}


		// 取消任务
		public void cancel(Runnable r) {
			if (executor != null) {
				// 拿到线程队列中移除对象
				executor.getQueue().remove(r);
			}
		}

	}
}
