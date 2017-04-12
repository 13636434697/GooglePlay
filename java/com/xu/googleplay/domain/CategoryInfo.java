package com.xu.googleplay.domain;

/**
 * 分类信息
 */
public class CategoryInfo {

	public String name1;
	public String name2;
	public String name3;
	public String url1;
	public String url2;
	public String url3;

	//arrayList的集合，里面要塞对象的，标题和信息是一个对象
	public String title;// 分类标题

	//标题和信息需要区分开来
	public boolean isTitle;// 标记是否是标题
}
