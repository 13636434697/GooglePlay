package com.xu.googleplay.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import android.util.Log;

import com.xu.googleplay.utils.IOUtils;
import com.xu.googleplay.utils.LogUtils;
import com.xu.googleplay.utils.StringUtils;


/*
* 核心类
* */
public class HttpHelper {
	//声明了一个主链接，是服务器声明的，8090端口
	public static final String URL = "http://127.0.0.1:8090/";

	/** get请求，获取返回字符串内容 */
	public static HttpResult get(String url) {
		//底层用httpClient
		HttpGet httpGet = new HttpGet(url);
		return execute(url, httpGet);
	}

	/** post请求用来提交数据，获取返回字符串内容 */
	public static HttpResult post(String url, byte[] bytes) {
		HttpPost httpPost = new HttpPost(url);
		ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytes);
		httpPost.setEntity(byteArrayEntity);
		return execute(url, httpPost);
	}

	/** 下载 */
	public static HttpResult download(String url) {
		HttpGet httpGet = new HttpGet(url);
		return execute(url, httpGet);
	}

	/** 执行网络访问 */
	private static HttpResult execute(String url, HttpRequestBase requestBase) {
		boolean isHttps = url.startsWith("https://");//判断是否需要采用https
		//生产
		AbstractHttpClient httpClient = HttpClientFactory.create(isHttps);
		HttpContext httpContext = new SyncBasicHttpContext(new BasicHttpContext());
		//不会死循环。因为有循环策略的处理器，这个处理器是httpClient底层的。
		HttpRequestRetryHandler retryHandler = httpClient.getHttpRequestRetryHandler();//获取重试机制
		int retryCount = 0;
		boolean retry = true;
		//有时候请求，可能会请求失败，会重试，不会死循环。因为有循环策略的处理器
		while (retry) {
			try {
				HttpResponse response = httpClient.execute(requestBase, httpContext);//访问网络,并且拿到结果
				if (response != null) {
					//把这个结果封装到HttpResult返回出去，HttpResult是自己定义的内部类
					return new HttpResult(response, httpClient, requestBase);
				}
			} catch (Exception e) {
				IOException ioException = new IOException(e.getMessage());
				retry = retryHandler.retryRequest(ioException, ++retryCount, httpContext);//把错误异常交给重试机制，以判断是否需要采取从事
				LogUtils.e(e);
			}
		}
		return null;
	}

	/** http的返回结果的封装，可以直接从中获取返回的字符串或者流 */
	//把这个结果封装到HttpResult返回出去，HttpResult是自己定义的内部类
	public static class HttpResult {
		//定义了一些数据
		private HttpResponse mResponse;//响应
		private InputStream mIn;//输入流
		private String mStr;//字符串
		private HttpClient mHttpClient;
		private HttpRequestBase mRequestBase;


		public HttpResult(HttpResponse response, HttpClient httpClient, HttpRequestBase requestBase) {
			mResponse = response;
			mHttpClient = httpClient;
			mRequestBase = requestBase;
		}

		public int getCode() {
			StatusLine status = mResponse.getStatusLine();
			return status.getStatusCode();
		}

		/** 从结果中获取字符串，一旦获取，会自动关流，并且把字符串保存，方便下次获取 */
		public String getString() {
			if (!StringUtils.isEmpty(mStr)) {
				return mStr;
			}
			InputStream inputStream = getInputStream();
			ByteArrayOutputStream out = null;
			if (inputStream != null) {
				try {
					out = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024 * 4];
					int len = -1;
					while ((len = inputStream.read(buffer)) != -1) {
						out.write(buffer, 0, len);
					}
					byte[] data = out.toByteArray();
					mStr = new String(data, "utf-8");
				} catch (Exception e) {
					LogUtils.e(e);
				} finally {
					IOUtils.close(out);
					close();
				}
			}
			return mStr;
		}

		/** 获取流，需要使用完毕后调用close方法关闭网络连接 */
		public InputStream getInputStream() {
			if (mIn == null && getCode() < 300) {
				HttpEntity entity = mResponse.getEntity();
				try {
					mIn = entity.getContent();
				} catch (Exception e) {
					LogUtils.e(e);
				}
			}
			return mIn;
		}

		/** 关闭网络连接 */
		public void close() {
			if (mRequestBase != null) {
				mRequestBase.abort();
			}
			IOUtils.close(mIn);
			if (mHttpClient != null) {
				mHttpClient.getConnectionManager().closeExpiredConnections();
			}
		}
	}
}
