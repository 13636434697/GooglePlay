package com.xu.googleplay.http.protocol;

import com.xu.googleplay.domain.AppInfo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 应用网络请求
 *加载网络的数据
 *
 * 因为是一堆应用，是一个集合，集合里面是一堆的appinfo
 * 重写了三个方法
 */
public class AppProtocol extends BaseProtocol<ArrayList<AppInfo>> {

	//服务器里面的后缀是/app，key就是app
	@Override
	public String getKey() {
		return "app";
	}

	//没有参数就返回空字符串
	@Override
	public String getParams() {
		return "";
	}

	//解析数据，上来就[]就是一个数组，
	@Override
	public ArrayList<AppInfo> parseData(String result) {
		try {
			//上来就[]就是一个数组，就把结果传过来
			JSONArray ja = new JSONArray(result);

			//通过jo解析字段，这里new出来要搞到集合里面，上面new一个集合
			ArrayList<AppInfo> list = new ArrayList<AppInfo>();
			//遍历上面的数组
			for (int i = 0; i < ja.length(); i++) {
				//里面能拿到object
				JSONObject jo = ja.getJSONObject(i);

				//通过jo解析字段，这里new出来要搞到集合里面，上面new一个集合
				AppInfo info = new AppInfo();
				info.des = jo.getString("des");
				info.downloadUrl = jo.getString("downloadUrl");
				info.iconUrl = jo.getString("iconUrl");
				info.id = jo.getString("id");
				info.name = jo.getString("name");
				info.packageName = jo.getString("packageName");
				info.size = jo.getLong("size");
				info.stars = (float) jo.getDouble("stars");
				//通过jo解析字段，这里new出来要搞到集合里面，上面new一个集合，这里添加进去
				list.add(info);
			}

			//返回集合
			return list;

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

}
