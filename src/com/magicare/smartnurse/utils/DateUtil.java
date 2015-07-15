package com.magicare.smartnurse.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.magicare.smartnurse.bean.HealthBean;

public class DateUtil {
	
	public static String getMonPreviousOrNext(int position){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, position);
		Date now = c.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String str = dateFormat.format(now);
		return str;
	}

	/**
	 * 获取以当天开�?前一天或者后�?��
	 * 
	 * @param postion
	 * @return
	 */
	public static String getDatePerviousAndNext(int postion) {

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, postion);
		Date now = c.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String str = dateFormat.format(now);
		String temp = str + " 00:00:00";
		return temp;
	}

	public static String DateToString(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static String DateToString(long date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static String formatDate(String date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String str = "";
		try {
			Date date2 = sdf.parse(date);
			str = sdf.format(date2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}

	public static String formatDate(String dateStr, boolean isFlag) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM.dd");
		String str = null;
		try {
			Date date2 = dateFormat.parse(dateStr);
			if (!isFlag) {
				str = dateFormat1.format(date2);
			} else {
				str = dateFormat.format(date2);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}

	public static String formathhmm(String dateStr, boolean isFlag) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm");
//		SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM-dd HH:mm");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM-dd");
		String str = null;
		try {
			Date date2 = dateFormat.parse(dateStr);
			if (!isFlag) {
				str = dateFormat1.format(date2);
			} else {
				str = dateFormat2.format(date2);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	
	public static String addMinute(String dateStr, int minute) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date begin = null;
		try {
			begin = formatter.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		Calendar c = Calendar.getInstance();
		c.setTime(begin);
		c.add(Calendar.MINUTE, minute);
		
		String next = formatter.format(c.getTime());
		return next;
	}
	
	/**
	 * 返回x的长度
	 * 
	 * @param date
	 * @param date1
	 * @return
	 * @throws ParseException
	 */
	public static long getMinuteDiff(String date, String date1) {

		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date begin = null;
		java.util.Date end = null;
		try {
			begin = dfs.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			end = dfs.parse(date1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long between = (end.getTime() - begin.getTime()) / 1000;// 
		long minute1 = between / 60;
		return minute1;
	}

	public static String getTime(String number) {
		int num = (int) Float.parseFloat(number);
		int hh = num / 60;
		int mm = num - hh * 60;
		if (hh == 0) {
			return mm + "'";
		}
		return hh + "h" + mm + "'";
	}

	/**
	 * 补全数据
	 * 
	 * @param beans
	 * @throws ParseException
	 */

	public static List<HealthBean> getListHealth(List<HealthBean> beans)
			throws ParseException {

		if (beans.size() == 0 || beans.size() == 1) {
			return beans;
		}

		// for (HealthBean healthBean : beans) {
		// System.out.println("原始数据" + healthBean);
		// }

		HealthBean startbean = beans.get(0);
		HealthBean endbean = beans.get(beans.size() - 1);
		// 计算数据x的长�?
		long xnum = getTimeDiff(startbean.getCollect_time(),
				endbean.getCollect_time());
		int k = 0;

		int position = 0;

		map.clear();
		// 分类
		getMap(position, k, beans);

		// System.out.println("getmap" + map.size());

		// 分类过后的�?
		List<HealthBean> beans2 = new ArrayList<HealthBean>();
		HealthBean bean2 = null;
		for (int i = 0; i < map.size(); i++) {
			List<HealthBean> beans3 = map.get(i);
			int[] counts = new int[4];
			// for (HealthBean healthBean : beans3) {
			// System.out
			// .println("healthBean" + "i" + i + "    " + healthBean);
			// }
			float weight = 0.0f;
			float systolic_pressure = 0.0f;
			float heart_rate = 0.0f;
			float blood_sugar = 0.0f;
			for (int j = 0; j < beans3.size(); j++) {
				HealthBean bean = beans3.get(j);
				if (j == 0) {
					bean2 = new HealthBean();
					bean2.setCollect_time(formathhmm(bean.getCollect_time()));
					if (bean.getWeight() > 0) {
						counts[0] = counts[0] + 1;
						weight += bean.getWeight();
					}
					if (bean.getSystolic_pressure() > 0) {
						counts[1] = counts[1] + 1;
						systolic_pressure += bean.getSystolic_pressure();
					}
					if (bean.getHeart_rate() > 0) {
						counts[2] = counts[2] + 1;
						heart_rate += bean.getHeart_rate();
					}
					if (bean.getBlood_sugar() > 0) {
						counts[3] = counts[3] + 1;
						blood_sugar += bean.getBlood_sugar();
					}
				} else {

					if (bean.getWeight() > 0) {
						counts[0] = counts[0] + 1;
						weight += bean.getWeight();
					}
					if (bean.getSystolic_pressure() > 0) {
						counts[1] = counts[1] + 1;
						systolic_pressure += bean.getSystolic_pressure();
					}
					if (bean.getHeart_rate() > 0) {
						counts[2] = counts[2] + 1;
						heart_rate += bean.getHeart_rate();
					}
					if (bean.getBlood_sugar() > 0) {
						counts[3] = counts[3] + 1;
						blood_sugar += bean.getBlood_sugar();
					}

				}
			}
			bean2.setWeight(((float) weight) / (counts[0] == 0 ? 1 : counts[0]));
			bean2.setSystolic_pressure(((float) systolic_pressure)
					/ (counts[1] == 0 ? 1 : counts[1]));
			bean2.setHeart_rate(((int) ((float) heart_rate) / (counts[2] == 0 ? 1
					: counts[2])));
			bean2.setBlood_sugar(((float) blood_sugar)
					/ (counts[3] == 0 ? 1 : counts[3]));
			beans2.add(bean2);
		}
		// for (HealthBean healthBean : beans2) {
		// System.out.println("分组过后" + healthBean);
		// }

		List<HealthBean> beans3 = new ArrayList<HealthBean>();

		System.out.println(getDatePerviousAndNextHours(
				startbean.getCollect_time(), 1));

		for (int i = 0; i < (xnum + 1); i++) {
			HealthBean bean = new HealthBean();
			bean.setCollect_time(getDatePerviousAndNextHours(
					startbean.getCollect_time(), i));
			beans3.add(bean);
		}

		List<HealthBean> beans4 = new ArrayList<HealthBean>();

		for (int i = 0; i < beans3.size(); i++) {
			int k1 = 0;
			HealthBean bean = beans3.get(i);
			for (int j = k1; j < beans2.size(); j++) {
				if (k1 < beans.size()) {
					HealthBean beantemp = beans2.get(j);
					if (bean.getCollect_time().equals(
							beantemp.getCollect_time())) {
						bean = beantemp;
						k1 = j + 1;
						break;
					}
				}
			}
			if (k1 == beans2.size()) {
				beans4.add(beans2.get(beans2.size() - 1));
			} else {
				beans4.add(bean);
			}
		}
		return beans4;
	}

	public static Map<Integer, List<HealthBean>> map = new HashMap<Integer, List<HealthBean>>();

	public static void getMap(int postion, int k, List<HealthBean> beans)
			throws ParseException {
		int p = postion;// p表示在map中的位置
		if (k == (beans.size() - 1)) {
			List<HealthBean> beans3 = new ArrayList<HealthBean>();
			beans3.add(beans.get(beans.size() - 1));
			map.put(p, beans3);
			return;
		}
		List<HealthBean> beans2 = new ArrayList<HealthBean>();

		HealthBean beanpervious = null;
		if (k > (beans.size() - 1)) {
			return;
		} else {
			beanpervious = beans.get(k);
		}
		if (!(beans2.contains(beanpervious))) {
			beans2.add(beanpervious);
		}
		if (beanpervious != null) {
			for (int i = (k + 1); i < beans.size(); i++) {
				if (i < beans.size()) {
					HealthBean beannext = beans.get(i);
					if (panduan(beanpervious.getCollect_time(),
							beannext.getCollect_time())) {
						if (!(beans2.contains(beannext))) {
							beans2.add(beannext);
						}
						k = i + 1;
					} else {
						k = i;
						break;
					}
				}
			}
			map.put(p, beans2);
		}
		p++;
		getMap(p, k, beans);

	}

	/**
	 * 返回x的长度
	 * 
	 * @param date
	 * @param date1
	 * @return
	 * @throws ParseException
	 */
	public static long getTimeDiff(String date, String date1)
			throws ParseException {

		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		java.util.Date begin = dfs.parse(date);
		java.util.Date end = dfs.parse(date1);
		long between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成�?

		long day1 = between / (24 * 3600);
		long hour1 = between % (24 * 3600) / 3600;

		// long minute1 = between % 3600 / 60;
		// long second1 = between % 60 / 60;
		return day1 * 24 + hour1;
	}

	public static boolean panduan(String date, String date1)
			throws ParseException {

		boolean isflag = false;
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH");
		java.util.Date begin = dfs.parse(date);
		java.util.Date end = dfs.parse(date1);

		String str = dfs.format(begin);
		String str1 = dfs.format(end);

		if (str.equals(str1)) {
			isflag = true;
		}

		return isflag;
	}

	public static boolean isEqual(String date, String date1)
			throws ParseException {
		boolean isflag = false;
		if (TextUtils.isEmpty(date) || TextUtils.isEmpty(date1)) {
			return isflag;
		}
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date begin = dfs.parse(date);
		java.util.Date end = dfs.parse(date1);
		String str = dfs.format(begin);
		String str1 = dfs.format(end);
		if (str.equals(str1)) {
			isflag = true;
		}

		return isflag;
	}

	/**
	 * 
	 * 补齐
	 * 
	 * @param postion
	 * @return
	 * @throws ParseException
	 */
	public static String getDatePerviousAndNext(String date, int posion)
			throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date begin = dateFormat.parse(date);
		Calendar c = Calendar.getInstance();
		c.set(begin.getYear() + 1900, begin.getMonth(), begin.getDate());
		c.add(Calendar.DATE, posion);
		Date now = c.getTime();
		String str = dateFormat.format(now);
		String temp = str + " 00:00:00";
		return temp;
	}

	public static String getDatePerviousAndNextHours(String date, int posion)
			throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
		java.util.Date begin = dateFormat.parse(date);
		Calendar c = Calendar.getInstance();
		c.set(begin.getYear() + 1900, begin.getMonth(), begin.getDate(),
				begin.getHours(), 0, 0);
		c.add(Calendar.HOUR, posion);
		Date now = c.getTime();
		String str = dateFormat.format(now);
		String temp = str + ":00";
		return temp;
	}

	public static String formathhmm(String dateStr) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
		String str = null;
		try {
			Date date = dateFormat.parse(dateStr);

			str = dateFormat.format(date);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str + ":00";
	}

	public static String formathh(String dateStr) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String str = null;
		try {
			Date date = dateFormat.parse(dateStr);

			str = dateFormat.format(date);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}

	public static long StringTolong(String str_date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long time = 0;
		try {
			Date date = sdf.parse(str_date);
			time = date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return time;

	}

	public static long getMinutes(String srcDate, String desDate) {

		return (StringTolong(desDate) - StringTolong(srcDate)) / (60 * 1000);
	}
}
