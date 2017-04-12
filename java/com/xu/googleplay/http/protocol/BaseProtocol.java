package com.xu.googleplay.http.protocol;

import com.xu.googleplay.http.HttpHelper;
import com.xu.googleplay.utils.IOUtils;
import com.xu.googleplay.utils.StringUtils;
import com.xu.googleplay.utils.UIUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 请求网络获取数据
 * 缓存机制（写缓存和读缓存）
 * 解析数据
 * 请求网络前，先判断是否有缓存，话的话就加载缓存
 * 访问网络的基类
 */
public abstract class BaseProtocol<T> {

	// index表示的是从哪个位置开始返回20条数据, 用于分页
	//获取数据
	public T getData(int index) {
		// 先判断是否有缓存, 有的话就加载缓存
		String result = getCache(index);

		if (StringUtils.isEmpty(result)) {// 如果没有缓存,或者缓存失效
			// 请求服务器
			result = getDataFromServer(index);
		}

		// 拿到json之后开始解析数据
		if (result != null) {
			T data = parseData(result);
			return data;
		}

		return null;
	}

	// 从网络获取数据，
	// index表示的是从哪个位置开始返回20条数据, 用于分页
	private String getDataFromServer(int index) {
		//获取网络并返回结果
		// http://www.itheima.com/home?index=0&name=zhangsan&age=18
		//网址+参数（但是不能写死）需要写二个抽象方法
		HttpHelper.HttpResult httpResult = HttpHelper.get(HttpHelper.URL + getKey()
				+ "?index=" + index + getParams());

		//如果返回的结果不等于空
		if (httpResult != null) {
			//怎么拿数据，底层已经封装好了，直接获取就可以了
			String result = httpResult.getString();
			System.out.println("访问结果:" + result);
			// 判断缓存是否为空
			if (!StringUtils.isEmpty(result)) {
				// 写缓存
				setCache(index, result);
			}

			return result;
		}

		return null;
	}

	//网址+参数（但是不能写死）需要写二个抽象方法
	// 获取网络链接关键词, 子类必须实现
	public abstract String getKey();
	//网址+参数（但是不能写死）需要写二个抽象方法
	// 获取网络链接参数, 子类必须实现
	public abstract String getParams();

	// 写缓存
	// 以url为key, 以json为value，在智慧北京用sharedpreference不好，应该写在文件里面
	public void setCache(int index, String json) {
		// 以url为文件名, 以json为文件内容,保存在本地
		File cacheDir = UIUtils.getContext().getCacheDir();// 保存在本应用的缓存文件夹
		// 生成缓存文件（参数1：文件夹，文件名就是url）这里特殊符号的话可以用md5，但是这里不用前面网址就不用了
		File cacheFile = new File(cacheDir, getKey() + "?index=" + index + getParams());

		//开始向缓存文件写东西了
		FileWriter writer = null;
		try {
			writer = new FileWriter(cacheFile);
			// 缓存要加有效期，缓存失效的截止时间
			long deadline = System.currentTimeMillis() + 30 * 60 * 1000;// 半个小时有效期
			// 给本地文件，第一行把缓存的有效期写进去, 换行
			writer.write(deadline + "\n");
			// 写入json
			writer.write(json);
			//刷新下
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//关掉流
			IOUtils.close(writer);
		}
	}

	// 读缓存
	public String getCache(int index) {
		// 以url为文件名, 以json为文件内容,保存在本地
		File cacheDir = UIUtils.getContext().getCacheDir();// 本应用的缓存文件夹
		// 生成缓存文件
		File cacheFile = new File(cacheDir, getKey() + "?index=" + index + getParams());

		// 判断缓存是否存在
		if (cacheFile.exists()) {
			// 判断缓存是否有效
			BufferedReader reader = null;
			try {
				//BufferedReader有一个方法readLine，就用BufferedReader
				reader = new BufferedReader(new FileReader(cacheFile));
				// 读取第一行的有效期
				String deadline = reader.readLine();
				//转换成时间
				long deadtime = Long.parseLong(deadline);

				//判断缓存是否在有效期
				if (System.currentTimeMillis() < deadtime) {// 判断当前时间小于截止时间,
															// 说明缓存有效
					// 缓存有效，才开始往下读，并且要保存起来。
					StringBuffer sb = new StringBuffer();
					String line;
					//一行一行往下读，判断一行是否为空
					while ((line = reader.readLine()) != null) {
						//读的时候要添加进来
						sb.append(line);
					}
					//读的时候不会有第一行的有效期，因为第二行才开始用StringBuffer
					//读的内容返回出去
					return sb.toString();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				//关闭流
				IOUtils.close(reader);
			}

		}
		//没有缓存
		return null;
	}

	// 解析json数据, 子类必须实现，返回值对象是泛型
	//T是泛型，是一个对象，一个类型，这个T是将来解析json之后的T（对象）
	// 需要创建对象，需要对照json的内容来创造
	public abstract T parseData(String result);
}
