package com.xu.googleplay.utils;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {
	/** 关闭流，这样写为了少写几行代码，因为还要try，什么流都可以关的 ，Closeable这个是祖宗流*/
	public static boolean close(Closeable io) {
		if (io != null) {
			try {
				io.close();
			} catch (IOException e) {
				LogUtils.e(e);
			}
		}
		return true;
	}
}
