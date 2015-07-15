package com.magicare.smartnurse.utils;

import java.text.DecimalFormat;

public class DataFormatUtil {

	public static String floatFormat(float data) {
		DecimalFormat decimalFormat = new DecimalFormat(".0");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
		return decimalFormat.format(data);// format 返回的是字符串
	}

}