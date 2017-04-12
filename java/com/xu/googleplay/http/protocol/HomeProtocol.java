package com.xu.googleplay.http.protocol;

import com.xu.googleplay.domain.AppInfo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 请求数据
 * 首页网络数据解析
 *	T是泛型，是一个对象，一个类型，这个T是将来解析json之后的T（对象）
 * 需要创建对象，需要对照json的内容来创造
 *
 * 这里的泛型不一定就是AppInfo，是一个appinfo的集合里面才是appinfo
 */
public class HomeProtocol extends BaseProtocol<ArrayList<AppInfo>> {


	private ArrayList<String> pictures;

	//重写方法，获取链接的关键词
	@Override
	public String getKey() {
		return "home";
	}

	//没有参数，但是不能写null，因为不能把null穿过去，没有参数就传空字符串
	@Override
	public String getParams() {
		return "";// 如果没有参数,就传空串,不要传null
	}

	//这里的泛型不一定就是AppInfo，是一个appinfo的集合里面才是appinfo
	@Override
	public ArrayList<AppInfo> parseData(String result) {
		//解析json用Gson，还有一种是JsonObject，也可以解析，创建的对象，把字段抄过来AppInfo
		//那个对象在用一个arrayList包起来，那边没有直接包起来

		// 使用JsonObject解析方式: 如果遇到{},就是JsonObject;如果遇到[], 就是JsonArray
		try {
			//这里需要把json传过来
			JSONObject jo = new JSONObject(result);

			// 解析应用列表数据
			//JsonArray是JSONObject里面的JsonArray，所以jo.getJSONArray，返回的就是数组
			JSONArray ja = jo.getJSONArray("list");
			//jo1就可以拿到字段了，拿到字段后，就复制给上面的AppInfo，之后在放到一个集合里面
			ArrayList<AppInfo> appInfoList = new ArrayList<AppInfo>();
			//遍历上面返回的数组
			for (int i = 0; i < ja.length(); i++) {
				//ja就能拿到JSONObject，这样就拿到其中的JSONObject
				JSONObject jo1 = ja.getJSONObject(i);

				AppInfo info = new AppInfo();
				//jo1就可以拿到字段了，拿到字段后，就复制给上面的AppInfo
				info.des = jo1.getString("des");
				info.downloadUrl = jo1.getString("downloadUrl");
				info.iconUrl = jo1.getString("iconUrl");
				info.id = jo1.getString("id");
				info.name = jo1.getString("name");
				info.packageName = jo1.getString("packageName");
				info.size = jo1.getLong("size");
				info.stars = (float) jo1.getDouble("stars");

				//jo1就可以拿到字段了，拿到字段后，就复制给上面的AppInfo，之后在放到一个集合里面
				appInfoList.add(info);
			}

			// 初始化轮播条的数据
			//如果遇到[], 是一个数组，就是JsonArray，还是通过最外层的jo.getJSONArray
			JSONArray ja1 = jo.getJSONArray("picture");
			//直接获取字符串，放到集合里面
			//一定要刷新view，所有要调用这个方法，底层把数据塞进去
			// 设置轮播条数据
			//这个data不对，是appinfo，这里要的是一堆字符串集合，在HomeProtocol里面解析
			//这里暴漏一个方法，让其他方法能够获取到这个数据
			//把这个搞成全局的了
			pictures = new ArrayList<String>();
			//遍历上面的数组
			for (int i = 0; i < ja1.length(); i++) {
				//直接获取字符串，放到集合里面
				String pic = ja1.getString(i);
				//添加到集合里面
				pictures.add(pic);
			}

			//返回集合（应用列表）
			return appInfoList;

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}


	//一定要刷新view，所有要调用这个方法，底层把数据塞进去
	// 设置轮播条数据
	//这个data不对，是appinfo，这里要的是一堆字符串集合，在HomeProtocol里面解析
	//这里暴漏一个方法，让其他方法能够获取到这个数据
	public ArrayList<String> getPictureList() {
		return pictures;
	}


}
