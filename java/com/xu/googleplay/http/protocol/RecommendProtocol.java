package com.xu.googleplay.http.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
/**
 * 推荐网络访问
 * 服务器里面就是字符组数组
 */
public class RecommendProtocol extends BaseProtocol<ArrayList<String>> {

	//页面的key，服务器传过来的
	@Override
	public String getKey() {
		return "recommend";
	}
	//无参数传空字符串
	@Override
	public String getParams() {
		return "";
	}

	@Override
	public ArrayList<String> parseData(String result) {
		//解析数组
		try {
			JSONArray ja = new JSONArray(result);

			//声明一个集合要放下面遍历的数据
			ArrayList<String> list = new ArrayList<String>();
			//遍历上面的数组
			for (int i = 0; i < ja.length(); i++) {
				//获取字符串
				String keyword = ja.getString(i);
				//添加到集合
				list.add(keyword);
			}
			//返回集合
			return list;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
