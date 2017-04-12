package com.xu.googleplay.utils;


public class StringUtils {
	/** 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false */
	public static boolean isEmpty(String value) {
		//因为TextUtils.isEmpty太简单了
		//这里判断首先不等于空，还有不等于空字符串（equalsIgnoreCase）还忽略了大小写（还做了trim裁剪），还判断了也不能等于null
		if (value != null && !"".equalsIgnoreCase(value.trim())
				&& !"null".equalsIgnoreCase(value.trim())) {
			return false;
		} else {
			return true;
		}
	}
}
