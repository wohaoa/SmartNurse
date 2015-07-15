package com.magicare.smartnurse.utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;



import com.magicare.smartnurse.bean.SportsBean;

public class TestUtil {

	public static void test1() {
		List<SportsBean> sportsBeans = new ArrayList<SportsBean>();
		
		SportsBean bean0 = new SportsBean();
		bean0.setStart_time("2015-04-16 00:00:00");
		bean0.setEnd_time("2015-04-16 00:10:00");
		bean0.setMode(255);
		bean0.setState(0);
		bean0.setCalorie(1);
//		bean0.setSleep_quality(0);
		bean0.setStep(1);
		bean0.setMeter(1);

		SportsBean bean1 = new SportsBean();
		bean1.setStart_time("2015-04-16 00:11:00");
		bean1.setEnd_time("2015-04-16 00:21:00");
		bean1.setMode(255);
		bean1.setState(0);
		bean1.setCalorie(4);
//		bean1.setSleep_quality(0);
		bean1.setStep(1);
		bean1.setMeter(0);

		SportsBean bean2 = new SportsBean();
		bean2.setStart_time("2015-04-16 00:22:00");
		bean2.setEnd_time("2015-04-16 00:32:00");
		bean2.setMode(254);
		bean2.setState(0);
		bean2.setCalorie(0);
//		bean2.setSleep_quality(26);
		bean2.setStep(0);
		bean2.setMeter(0);

		SportsBean bean3 = new SportsBean();
		bean3.setStart_time("2015-04-16 00:32:00");
		bean3.setEnd_time("2015-04-16 00:42:00");
		bean3.setMode(254);
		bean3.setState(0);
		bean3.setCalorie(4);
//		bean3.setSleep_quality(21);
		bean3.setStep(0);
		bean3.setMeter(0);

		SportsBean bean4 = new SportsBean();
		bean4.setStart_time("2015-04-16 00:43:00");
		bean4.setEnd_time("2015-04-16 03:09:00");
		bean4.setMode(255);
		bean4.setState(1);
		bean4.setCalorie(32);
//		bean4.setSleep_quality(190);
		bean4.setStep(0);
		bean4.setMeter(0);
		
		sportsBeans.add(bean0);
		sportsBeans.add(bean1);
		sportsBeans.add(bean2);
		sportsBeans.add(bean3);
		sportsBeans.add(bean4);
		
		String temp_time;
		StringBuffer time;
		char x;
		
		
		for(int j = 0;j < sportsBeans.size();j++){	//四舍五入每段起始点时间为整十数
			time = new StringBuffer (sportsBeans.get(j).getStart_time());	//获得每个开始点的时间
			x = time.charAt(15);
			time.deleteCharAt(15);
			time.insert(15,"0");
			if(Character.getNumericValue(x) < 5){
				temp_time = time.toString();
				sportsBeans.get(j).setStart_time(time.toString());
			}else{
				temp_time = DateUtil.addMinute(time.toString(), 10);
				sportsBeans.get(j).setStart_time(temp_time);
			}
			
			if(sportsBeans.get(j).getState()==0){	//在线时间段
				
				sportsBeans.get(j).setEnd_time(DateUtil.addMinute(temp_time, 10));
				
			}else{//离线时间段
				time = new StringBuffer (sportsBeans.get(j).getEnd_time());	//获得每个结束点的时间
				x = time.charAt(15);
				time.deleteCharAt(15);
				time.insert(15,"0");
				if(Character.getNumericValue(x) < 5){
					temp_time = time.toString();
					sportsBeans.get(j).setEnd_time(time.toString());
				}else{
					temp_time = DateUtil.addMinute(time.toString(), 10);
					sportsBeans.get(j).setEnd_time(temp_time);
				}
			}

		}
//		
		for(int k=0;k<sportsBeans.size();k++){
				System.out.println("打印sportsBeans："+sportsBeans.get(k).toString());
		}
		
		if (sportsBeans != null && sportsBeans.size() > 0) {
			// 格式化横坐标值
//			axisValues = new ArrayList<AxisValue>();
//			List<Line> lines = new ArrayList<Line>(); // 所有曲线的集合(按条)

			String first = sportsBeans.get(0).getEnd_time();
			System.out.println("开始点的时间:" + first);

			String last = sportsBeans.get(sportsBeans.size() - 1).getEnd_time();
			System.out.println("结束点的时间:" + last);
			
			int beanSize = sportsBeans.size();

			long diff = 0;
				diff = DateUtil.getMinuteDiff(first, last);
				System.out.println("总共经过分钟数:" + diff);
				diff = 0;

			int size = (int) (diff / 10 + 1);
			System.out.println("点的个数:" + size);

			String everypoint = first;
			for (int i = 0; i < size; i++) {

				if (i == 0) {
					String point = DateUtil.formathhmm(first, true);
					System.out.println("第0个横坐标："+point);
					// axisValues.add(new AxisValue(i, point.toCharArray()));
				} else {
					everypoint = DateUtil.addMinute(everypoint, 10);
					System.out.println("第"+i+"个横坐标："+everypoint);
					// axisValues.add(new AxisValue(i,everypoint.toCharArray()));
				}
				for(SportsBean sportsBean : sportsBeans){
					if(sportsBean.getEnd_time().equals(everypoint)){
						System.out.println("第"+i+"个点的值为sportsBean的某个值");
						sportsBeans.remove(sportsBean);
						break;
					}else{
						System.out.println("第"+i+"个点的值为0");
					}
				}
				
			}
		}
	}
}