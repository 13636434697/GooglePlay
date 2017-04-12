package com.xu.googleplay.http.protocol;

import com.xu.googleplay.domain.CategoryInfo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 分类模块请求网络，解析数据
 *
 * 一堆CategoryInfo是一个集合
 */
public class CategoryProtocol extends BaseProtocol<ArrayList<CategoryInfo>> {

	@Override
	public String getKey() {
		return "category";
	}

	@Override
	public String getParams() {
		return "";
	}

	//解析数据
	@Override
	public ArrayList<CategoryInfo> parseData(String result) {
		try {
			//一开始是一个数组
			JSONArray ja = new JSONArray(result);

			//集合要放对象用的
			ArrayList<CategoryInfo> list = new ArrayList<CategoryInfo>();
			for (int i = 0; i < ja.length(); i++) {// 遍历大分类, 2次
				//拿到里面的对象
				JSONObject jo = ja.getJSONObject(i);

				//万一服务器没有title没有标题的话，在去解析title，程序会崩溃，需要判断一下，是否有title的字段
				// 初始化标题对象
				if (jo.has("title")) {// 判断是否有title这个字段
					//初始化CategoryInfo
					CategoryInfo titleInfo = new CategoryInfo();
					//可以拿到标题了
					titleInfo.title = jo.getString("title");
					//是否是标题
					titleInfo.isTitle = true;
					//标题添加到集合里面
					list.add(titleInfo);
				}

				//万一服务器没有infos没有的话，在去解析infos，程序会崩溃，需要判断一下，是否有infos的字段
				// 初始化分类对象
				if (jo.has("infos")) {
					//解析数组
					JSONArray ja1 = jo.getJSONArray("infos");

					for (int j = 0; j < ja1.length(); j++) {// 遍历小分类
						//解析对象
						JSONObject jo1 = ja1.getJSONObject(j);
						//初始化CategoryInfo
						CategoryInfo info = new CategoryInfo();
						//拿数据
						info.name1 = jo1.getString("name1");
						info.name2 = jo1.getString("name2");
						info.name3 = jo1.getString("name3");
						info.url1 = jo1.getString("url1");
						info.url2 = jo1.getString("url2");
						info.url3 = jo1.getString("url3");
						//为了数据清晰，还要赋值一下
						info.isTitle = false;
						//添加到集合
						list.add(info);
					}
				}
			}

			return list;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
