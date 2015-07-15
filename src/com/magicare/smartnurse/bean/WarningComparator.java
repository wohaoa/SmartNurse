package com.magicare.smartnurse.bean;

import java.util.Comparator;

/**
 * 
 * @author scott
 * 
 *         Function:警告排序规则： 1.未处理的警告排在已处理的前面 2.同时都是未处理的警告，则根据警告类型进行排序，
 *         主动报警>跌倒报警>离开服务区
 */
public class WarningComparator implements Comparator<WarningBean> {

	@Override
	public int compare(WarningBean one, WarningBean two) {
		// TODO Auto-generated method stub
		if (one.getAlarm_status() < two.getAlarm_status()) {
			return -1;
		} else if (one.getAlarm_status() > two.getAlarm_status()) {
			return 1;
		} else {
//			if (one.getAlarm_type() < two.getAlarm_type()) {
//				return -1;
//			} else if (one.getAlarm_type() > two.getAlarm_type()) {
//				return 1;
//			}
			return 0;
		}
	}

}
