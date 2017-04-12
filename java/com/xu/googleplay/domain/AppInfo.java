package com.xu.googleplay.domain;

import java.util.ArrayList;

/**
 * 首页应用信息封装
 * 还有一种是JsonObject，也可以解析，创建的对象，把字段抄过来
 * 用一个arrayList包起来，不再在这里写，这里最简单应用的信息
 */
public class AppInfo {

	public String des;
	public String downloadUrl;
	public String iconUrl;
	public String id;
	public String name;
	public String packageName;
	public long size;
	public float stars;

	//补充字段, 供应用详情页使用
	public String author;
	public String date;
	public String downloadNum;
	public String version;
	//一些安全信息的集合
	public ArrayList<SafeInfo> safe;
	//图片的地址
	public ArrayList<String> screen;

	//safe是一个arrayList一个集合，集合里面又是一个个对象
	//当一个内部类是public static的时候, 和外部类没有区别（因为是子对象，所以写了内部类）
	public static class SafeInfo {
		public String safeDes;
		public String safeDesUrl;
		public String safeUrl;
	}

}
