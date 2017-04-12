package com.xu.googleplay.http.protocol;

import com.xu.googleplay.domain.AppInfo.SafeInfo;
import com.xu.googleplay.domain.AppInfo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * 首页详情页网络访问
 *
 * 字段都有，就是缺少，还是用AppInfo，在补充一些字段，
 * AppInfo是一个大的对象
 */
public class HomeDetailProtocol extends BaseProtocol<AppInfo> {

	public String packageName;

	public HomeDetailProtocol(String packageName) {
		this.packageName = packageName;
	}

	//请求的detail字段
	@Override
	public String getKey() {
		return "detail";
	}

	//这里要通过传参的方式来拼接地址，根据方面来找到地址，
	// 包名需要在构造方法里面传过来
	@Override
	public String getParams() {
		return "&packageName=" + packageName;
	}

	// 补充字段, 供应用详情页使用
	public String author;
	public String date;
	public String downloadNum;
	public String version;
	public ArrayList<SafeInfo> safe;
	public ArrayList<String> screen;

	@Override
	public AppInfo parseData(String result) {
		try {
			//解析对象
			JSONObject jo = new JSONObject(result);

			AppInfo info = new AppInfo();
			info.des = jo.getString("des");
			info.downloadUrl = jo.getString("downloadUrl");
			info.iconUrl = jo.getString("iconUrl");
			info.id = jo.getString("id");
			info.name = jo.getString("name");
			info.packageName = jo.getString("packageName");
			info.size = jo.getLong("size");
			info.stars = (float) jo.getDouble("stars");

			info.author = jo.getString("author");
			info.date = jo.getString("date");
			info.downloadNum = jo.getString("downloadNum");
			info.version = jo.getString("version");

			//在解析一个数组
			JSONArray ja = jo.getJSONArray("safe");

			// 解析安全信息，放到集合里面
			ArrayList<SafeInfo> safe = new ArrayList<AppInfo.SafeInfo>();
			//在解析一个数组
			for (int i = 0; i < ja.length(); i++) {
				//在解析一个对象
				JSONObject jo1 = ja.getJSONObject(i);
				//对象是SafeInfo
				SafeInfo safeInfo = new SafeInfo();
				safeInfo.safeDes = jo1.getString("safeDes");
				safeInfo.safeDesUrl = jo1.getString("safeDesUrl");
				safeInfo.safeUrl = jo1.getString("safeUrl");
				// 解析安全信息，放到集合里面
				safe.add(safeInfo);
			}

			//安全信息和截图信息要放在整个大对象里面
			info.safe = safe;

			// 解析截图信息
			JSONArray ja1 = jo.getJSONArray("screen");
			//图片添加到集合
			ArrayList<String> screen = new ArrayList<String>();
			for (int i = 0; i < ja1.length(); i++) {
				String pic = ja1.getString(i);
				//图片添加到集合
				screen.add(pic);
			}
			//安全信息和截图信息要放在整个大对象里面
			info.screen = screen;

			return info;

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

}
