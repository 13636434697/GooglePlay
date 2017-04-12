package com.xu.googleplay.http.protocol;

import com.xu.googleplay.domain.SubjectInfo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 专题网络请求
 * 来解析subjectinfo，这里的泛型是一群对象，不是一个对象
 */
public class SubjectProtocol extends BaseProtocol<ArrayList<SubjectInfo>> {

	//key就是subject，因为服务器数据链接就是/subject
	@Override
	public String getKey() {
		return "subject";
	}

	//没有参数，传了一个空字符串
	@Override
	public String getParams() {
		return "";
	}

	@Override
	public ArrayList<SubjectInfo> parseData(String result) {
		try {
			//解析的是一个数组
			JSONArray ja = new JSONArray(result);
			//需要把解析的数据添加到集合里面new了一个集合
			ArrayList<SubjectInfo> list = new ArrayList<SubjectInfo>();
			//遍历出上面的数组
			for (int i = 0; i < ja.length(); i++) {
				//在获取里面的对象
				JSONObject jo = ja.getJSONObject(i);
				//解析获取的对象的信息
				SubjectInfo info = new SubjectInfo();
				info.des = jo.getString("des");
				info.url = jo.getString("url");
				//添加到集合里面
				list.add(info);
			}
			//集合返回出去
			return list;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

}
