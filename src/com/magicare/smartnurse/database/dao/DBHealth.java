package com.magicare.smartnurse.database.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.magicare.smartnurse.bean.HealthBean;
import com.magicare.smartnurse.database.MySqliteHelper;
import com.magicare.smartnurse.utils.LogUtil;

/**
 * 健康信息 db
 * 
 * @author 波
 * 
 */
public class DBHealth extends MySqliteHelper {

	public static String DATABASE_TABLE = "healthinfo";

	public static final String CreateTableSql;

	private static SQLiteDatabase db;

	public static final String COLUMN_ID = BaseColumns._ID,
			COLUMN_USERID = "user_id",// 老人真实编号
			COLUMN_USERSN = "user_sn",// 老人的编号
			COLUMN_UUID = "health_uuid",// 每条数据的唯一标识
			COLUMN_COLLECT_TIME = "collect_time",// 采集时间
			COLUMN_WEIGHT = "weight",// 体重
			COLUMN_SYSTOLIC_PRESSURE = "systolic_pressure",// 舒张压
			COLUMN_DIASTOLIC_PRESSURE = "diastolic_pressure",// 缩张压
			COlUMN_HEAR_RATE = "hear_rate", // 心率
			COLUMN_BLOOD_SUGAR = "blood_sugar",// 血糖
			COLUMN_NURSE_NAME = "nurse_name",// 采集护士姓名
			COLUMN_NURSEID = "nurse_id", // 采集护士ID
			COLUMN_ISUPDATE = "isupdate", // 是否上传
			COLUMN_REMARK1 = "remark1", COLUMN_REMARK2 = "remark2";

	public static final String[] dispColumns = { COLUMN_ID, COLUMN_USERID,
			COLUMN_USERSN, COLUMN_UUID, COLUMN_COLLECT_TIME, COLUMN_WEIGHT,
			COLUMN_SYSTOLIC_PRESSURE, COLUMN_DIASTOLIC_PRESSURE,
			COlUMN_HEAR_RATE, COLUMN_BLOOD_SUGAR, COLUMN_NURSE_NAME,
			COLUMN_NURSEID, COLUMN_ISUPDATE };
	static {
		StringBuilder strSql = new StringBuilder();
		strSql.append("CREATE TABLE " + " IF NOT EXISTS " + DATABASE_TABLE
				+ "( ");
		strSql.append(COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , ");
		strSql.append(COLUMN_USERID + " NVARCHAR(100) , ");
		strSql.append(COLUMN_USERSN + " NVARCHAR(100) , ");
		strSql.append(COLUMN_UUID + " NVARCHAR(100) , ");
		strSql.append(COLUMN_COLLECT_TIME + " NVARCHAR(100) ,");
		strSql.append(COLUMN_WEIGHT + " FLOAT ,");
		strSql.append(COLUMN_SYSTOLIC_PRESSURE + " FLOAT ,");
		strSql.append(COLUMN_DIASTOLIC_PRESSURE + " FLOAT ,");
		strSql.append(COlUMN_HEAR_RATE + " FLOAT ,");
		strSql.append(COLUMN_BLOOD_SUGAR + " FLOAT ,");
		strSql.append(COLUMN_NURSE_NAME + " NVARCHAR(100) ,");
		strSql.append(COLUMN_NURSEID + " NVARCHAR(100) , ");
		strSql.append(COLUMN_ISUPDATE + " INTEGER , ");
		strSql.append(COLUMN_REMARK1 + " NVARCHAR(100) ,");
		strSql.append(COLUMN_REMARK2 + " NVARCHAR(100) ");
		strSql.append(" ) ;");
		CreateTableSql = strSql.toString();
	}

	private static DBHealth dbApp = null;

	public synchronized static DBHealth getInstance(Context context) {
		if (dbApp == null) {
			dbApp = new DBHealth(context);
		}
		return dbApp;
	}

	private DBHealth(Context context) {
		super(context);
	}

	public void beginTransaction() {
		db.beginTransaction();
	}

	public void setTransactionSuccessful() {
		db.setTransactionSuccessful();
	}

	public void endTransaction() {
		db.endTransaction();
	}

	/**
	 * 插入
	 * 
	 * @param oldinfo
	 * @return
	 */
	public synchronized long insert(HealthBean bean) {
		long count = 0;
		if (!TextUtils.isEmpty(bean.getData_id())) {
			ContentValues values = new ContentValues();
			values.put(COLUMN_USERID, bean.getOld_id());
			values.put(COLUMN_USERSN, bean.getOld_sn());
			values.put(COLUMN_UUID, bean.getData_id());
			values.put(COLUMN_COLLECT_TIME, bean.getCollect_time());
			values.put(COLUMN_WEIGHT, bean.getWeight());
			values.put(COLUMN_SYSTOLIC_PRESSURE, bean.getSystolic_pressure());
			values.put(COLUMN_DIASTOLIC_PRESSURE, bean.getDiastolic_pressure());
			values.put(COlUMN_HEAR_RATE, bean.getHeart_rate());
			values.put(COLUMN_BLOOD_SUGAR, bean.getBlood_sugar());
			values.put(COLUMN_NURSE_NAME, bean.getNurse_name());
			values.put(COLUMN_NURSEID, bean.getNurse_id());
			values.put(COLUMN_ISUPDATE, bean.getIsUpdate());
			count = db.insert(DATABASE_TABLE, null, values);
		}
		return count;
	}

	/**
	 * 
	 * @param info
	 * @return
	 */
	public long insert(List<HealthBean> list) {
		long count = 0;
		beginTransaction();
		try {
			for (int i = 0; i < list.size(); i++) {
				HealthBean bean = list.get(i);
				if (bean != null) {
					ContentValues values = new ContentValues();
					values.put(COLUMN_USERID, bean.getOld_id());
					values.put(COLUMN_USERSN, bean.getOld_sn());
					values.put(COLUMN_UUID, bean.getData_id());
					values.put(COLUMN_COLLECT_TIME, bean.getCollect_time());
					values.put(COLUMN_WEIGHT, bean.getWeight());
					values.put(COLUMN_SYSTOLIC_PRESSURE,
							bean.getSystolic_pressure());
					values.put(COLUMN_DIASTOLIC_PRESSURE,
							bean.getDiastolic_pressure());
					values.put(COlUMN_HEAR_RATE, bean.getHeart_rate());
					values.put(COLUMN_BLOOD_SUGAR, bean.getBlood_sugar());
					values.put(COLUMN_NURSE_NAME, bean.getNurse_name());
					values.put(COLUMN_NURSEID, bean.getNurse_id());
					values.put(COLUMN_ISUPDATE, 1);
					count += db.insert(DATABASE_TABLE, null, values);
				}
			}
			setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			endTransaction();
		}
		return count;
	}

	/**
	 * 
	 * Function:修改当前采集数据的状态
	 * 
	 * @param list
	 *            采集的数据
	 * @param isUpdate
	 *            ：这个参数是为了避免修改集合中每个元素的isupdate，减少了一个循环
	 * @return
	 */
	public boolean updateHealthUpdateStatus(List<HealthBean> list, int isUpdate) {
		long count = 0;
		beginTransaction();
		try {
			for (int i = 0; i < list.size(); i++) {
				HealthBean bean = list.get(i);
				ContentValues values = new ContentValues();
				values.put(COLUMN_ISUPDATE, isUpdate);
				count += db.update(DATABASE_TABLE, values, COLUMN_UUID + "=? ",
						new String[] { bean.getData_id() });
			}
			setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			endTransaction();
		}
		return count > 0 ? true : false;
	}

	public boolean updateHealthUpdateStatus(HealthBean bean) {
		long count = 0;
		ContentValues values = new ContentValues();
		values.put(COLUMN_ISUPDATE, bean.getIsUpdate());
		count = db.update(DATABASE_TABLE, values, COLUMN_UUID + "=? ",
				new String[] { bean.getData_id() });
		return count > 0 ? true : false;
	}

	public boolean updateHealthByUuid(HealthBean bean) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_USERID, bean.getOld_id());
		values.put(COLUMN_USERSN, bean.getOld_sn());
		values.put(COLUMN_WEIGHT, bean.getWeight());
		values.put(COLUMN_SYSTOLIC_PRESSURE, bean.getSystolic_pressure());
		values.put(COLUMN_DIASTOLIC_PRESSURE, bean.getDiastolic_pressure());
		values.put(COlUMN_HEAR_RATE, bean.getHeart_rate());
		values.put(COLUMN_BLOOD_SUGAR, bean.getBlood_sugar());
		values.put(COLUMN_NURSE_NAME, bean.getNurse_name());
		values.put(COLUMN_NURSEID, bean.getNurse_id());
		values.put(COLUMN_ISUPDATE, bean.getIsUpdate());
		int count = db.update(DATABASE_TABLE, values, COLUMN_UUID + "=? ",
				new String[] { bean.getData_id() });
		return count > 0 ? true : false;
	}
	/**
	 * 根据老人编号和采集时间删除
	 */
	public long deleteHealthInfoByUserIdandDate(String oldId, String date) {
		int count = db.delete(DATABASE_TABLE, COLUMN_USERID + "=? and " + COLUMN_COLLECT_TIME + " like ? ", new String[] {
				oldId, "%" + date + "%" });
		return count;
	}

	/**
	 * 
	 * @param old_sn
	 *            老人编号
	 * @return
	 */
	public long deleteHealthInfoByUserId(String oldId) {
		int count = db.delete(DATABASE_TABLE, COLUMN_USERID + "=? ",
				new String[] { oldId });
		return count;
	}

	/**
	 * 
	 * @param old_sn
	 *            老人编号
	 * @return
	 */
	public long deleteHealthInfoByUserIdAndisUpdate(String oldId, int isUpate) {
		int count = db
				.delete(DATABASE_TABLE, COLUMN_USERID + "=? and "
						+ COLUMN_ISUPDATE + "=? ", new String[] { oldId,
						isUpate + "" });
		return count;
	}

	/**
	 * 通过老人id 获取老人健康信息
	 * 
	 * @param old_sn
	 * @return
	 */

	public List<HealthBean> getHealthInfoByUserId(String oldId) {
		DBUser dbUser = DBUser.getInstance(mContext);
		dbUser.open();
		List<HealthBean> list = null;
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, COLUMN_USERID
					+ " = ? ", new String[] { oldId }, null, null,
					COLUMN_COLLECT_TIME + "  DESC");
			if (cursor == null) {
				return null;
			}
			int count = cursor.getCount();
			HealthBean bean = null;
			list = new ArrayList<HealthBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {
					bean = new HealthBean();
					bean.setOld_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_USERID)));
					bean.setOld_sn(cursor.getString(cursor
							.getColumnIndex(COLUMN_USERSN)));
					bean.setData_id(cursor.getString(cursor
							.getColumnIndex(COLUMN_UUID)));
					bean.setCollect_time(cursor.getString(cursor
							.getColumnIndex(COLUMN_COLLECT_TIME)));
					bean.setWeight(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_WEIGHT)));
					bean.setSystolic_pressure(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_SYSTOLIC_PRESSURE)));
					bean.setDiastolic_pressure(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_DIASTOLIC_PRESSURE)));
					bean.setHeart_rate(cursor.getInt(cursor
							.getColumnIndex(COlUMN_HEAR_RATE)));
					bean.setBlood_sugar(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_BLOOD_SUGAR)));
					bean.setNurse_name(cursor.getString(cursor
							.getColumnIndex(COLUMN_NURSE_NAME)));
					bean.setNurse_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_NURSEID)));
					bean.setIsUpdate(cursor.getInt(cursor
							.getColumnIndex(COLUMN_ISUPDATE)));
					bean.setUser(dbUser.getUserInfoById(bean.getOld_id() + ""));
					list.add(bean);
				}
			}
			cursor.close();
		}
		dbUser.close();
		return list;
	}

	public List<HealthBean> getHealthInfoByUserId(String oldId, int startIndex,
			int pagesize) {
		DBUser dbUser = DBUser.getInstance(mContext);
		dbUser.open();
		List<HealthBean> list = null;
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, COLUMN_USERID
					+ " = ? ", new String[] { oldId }, null, null,
					COLUMN_COLLECT_TIME + "  DESC limit " + startIndex + ","
							+ pagesize);
			if (cursor == null) {
				return null;
			}
			int count = cursor.getCount();
			HealthBean bean = null;
			list = new ArrayList<HealthBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {
					bean = new HealthBean();
					bean.setOld_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_USERID)));
					bean.setOld_sn(cursor.getString(cursor
							.getColumnIndex(COLUMN_USERSN)));
					bean.setData_id(cursor.getString(cursor
							.getColumnIndex(COLUMN_UUID)));
					bean.setCollect_time(cursor.getString(cursor
							.getColumnIndex(COLUMN_COLLECT_TIME)));
					bean.setWeight(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_WEIGHT)));
					bean.setSystolic_pressure(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_SYSTOLIC_PRESSURE)));
					bean.setDiastolic_pressure(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_DIASTOLIC_PRESSURE)));
					bean.setHeart_rate(cursor.getInt(cursor
							.getColumnIndex(COlUMN_HEAR_RATE)));
					bean.setBlood_sugar(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_BLOOD_SUGAR)));
					bean.setNurse_name(cursor.getString(cursor
							.getColumnIndex(COLUMN_NURSE_NAME)));
					bean.setNurse_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_NURSEID)));
					bean.setIsUpdate(cursor.getInt(cursor
							.getColumnIndex(COLUMN_ISUPDATE)));
					bean.setUser(dbUser.getUserInfoById(bean.getOld_id() + ""));
					list.add(bean);
				}
			}
			cursor.close();
		}
		dbUser.close();
		return list;
	}

	public List<HealthBean> getHealthInfoByUserIdAsc(String oldId) {
		List<HealthBean> list = null;
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, COLUMN_USERID
					+ " = ? ", new String[] { oldId }, null, null,
					COLUMN_COLLECT_TIME);
			if (cursor == null) {
				return null;
			}
			int count = cursor.getCount();
			HealthBean bean = null;
			list = new ArrayList<HealthBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {
					bean = new HealthBean();
					bean.setOld_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_USERID)));
					bean.setOld_sn(cursor.getString(cursor
							.getColumnIndex(COLUMN_USERSN)));
					bean.setData_id(cursor.getString(cursor
							.getColumnIndex(COLUMN_UUID)));
					bean.setCollect_time(cursor.getString(cursor
							.getColumnIndex(COLUMN_COLLECT_TIME)));
					bean.setWeight(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_WEIGHT)));
					bean.setSystolic_pressure(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_SYSTOLIC_PRESSURE)));
					bean.setDiastolic_pressure(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_DIASTOLIC_PRESSURE)));
					bean.setHeart_rate(cursor.getInt(cursor
							.getColumnIndex(COlUMN_HEAR_RATE)));
					bean.setBlood_sugar(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_BLOOD_SUGAR)));
					bean.setNurse_name(cursor.getString(cursor
							.getColumnIndex(COLUMN_NURSE_NAME)));
					bean.setNurse_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_NURSEID)));
					bean.setIsUpdate(cursor.getInt(cursor
							.getColumnIndex(COLUMN_ISUPDATE)));
					list.add(bean);
				}
			}
			cursor.close();
		}
		return list;
	}

	public List<HealthBean> getHealthInfoByUserIdAndDate(String oldId, String date,
			int isUpate) {
		List<HealthBean> list = null;
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, COLUMN_USERID
					+ " = ? and " + COLUMN_ISUPDATE + " = ? and " + COLUMN_COLLECT_TIME + " like ? ", new String[] {
					oldId, isUpate + "", "%" + date + "%" }, null, null, COLUMN_COLLECT_TIME);
			if (cursor == null) {
				return null;
			}
			int count = cursor.getCount();
			HealthBean bean = null;
			list = new ArrayList<HealthBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {
					bean = new HealthBean();
					bean.setOld_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_USERID)));
					bean.setOld_sn(cursor.getString(cursor
							.getColumnIndex(COLUMN_USERSN)));
					bean.setData_id(cursor.getString(cursor
							.getColumnIndex(COLUMN_UUID)));
					bean.setCollect_time(cursor.getString(cursor
							.getColumnIndex(COLUMN_COLLECT_TIME)));
					bean.setWeight(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_WEIGHT)));
					bean.setSystolic_pressure(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_SYSTOLIC_PRESSURE)));
					bean.setDiastolic_pressure(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_DIASTOLIC_PRESSURE)));
					bean.setHeart_rate(cursor.getInt(cursor
							.getColumnIndex(COlUMN_HEAR_RATE)));
					bean.setBlood_sugar(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_BLOOD_SUGAR)));
					bean.setNurse_name(cursor.getString(cursor
							.getColumnIndex(COLUMN_NURSE_NAME)));
					bean.setNurse_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_NURSEID)));
					bean.setIsUpdate(cursor.getInt(cursor
							.getColumnIndex(COLUMN_ISUPDATE)));
					list.add(bean);
				}
			}
			cursor.close();
		}
		return list;
	}	
	
	public List<HealthBean> getHealthInfoByUserIdAndIsUpdateAsc(String oldId,
			int isUpate) {
		List<HealthBean> list = null;
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, COLUMN_USERID
					+ " = ? and " + COLUMN_ISUPDATE + " = ?", new String[] {
					oldId, isUpate + "" }, null, null, COLUMN_COLLECT_TIME);
			if (cursor == null) {
				return null;
			}
			int count = cursor.getCount();
			HealthBean bean = null;
			list = new ArrayList<HealthBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {
					bean = new HealthBean();
					bean.setOld_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_USERID)));
					bean.setOld_sn(cursor.getString(cursor
							.getColumnIndex(COLUMN_USERSN)));
					bean.setData_id(cursor.getString(cursor
							.getColumnIndex(COLUMN_UUID)));
					bean.setCollect_time(cursor.getString(cursor
							.getColumnIndex(COLUMN_COLLECT_TIME)));
					bean.setWeight(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_WEIGHT)));
					bean.setSystolic_pressure(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_SYSTOLIC_PRESSURE)));
					bean.setDiastolic_pressure(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_DIASTOLIC_PRESSURE)));
					bean.setHeart_rate(cursor.getInt(cursor
							.getColumnIndex(COlUMN_HEAR_RATE)));
					bean.setBlood_sugar(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_BLOOD_SUGAR)));
					bean.setNurse_name(cursor.getString(cursor
							.getColumnIndex(COLUMN_NURSE_NAME)));
					bean.setNurse_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_NURSEID)));
					bean.setIsUpdate(cursor.getInt(cursor
							.getColumnIndex(COLUMN_ISUPDATE)));
					list.add(bean);
				}
			}
			cursor.close();
		}
		return list;
	}

	public List<HealthBean> getAllHealthInfo() {
		List<HealthBean> list = null;
		DBUser dbUser = DBUser.getInstance(mContext);
		dbUser.open();
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, null, null,
					null, null, COLUMN_COLLECT_TIME + "  DESC");
			if (cursor == null) {
				return null;
			}
			int count = cursor.getCount();
			HealthBean bean = null;
			list = new ArrayList<HealthBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {
					bean = new HealthBean();
					bean.setOld_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_USERID)));
					bean.setOld_sn(cursor.getString(cursor
							.getColumnIndex(COLUMN_USERSN)));
					bean.setData_id(cursor.getString(cursor
							.getColumnIndex(COLUMN_UUID)));
					bean.setCollect_time(cursor.getString(cursor
							.getColumnIndex(COLUMN_COLLECT_TIME)));
					bean.setWeight(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_WEIGHT)));
					bean.setSystolic_pressure(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_SYSTOLIC_PRESSURE)));
					bean.setDiastolic_pressure(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_DIASTOLIC_PRESSURE)));
					bean.setHeart_rate(cursor.getInt(cursor
							.getColumnIndex(COlUMN_HEAR_RATE)));
					bean.setBlood_sugar(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_BLOOD_SUGAR)));
					bean.setNurse_name(cursor.getString(cursor
							.getColumnIndex(COLUMN_NURSE_NAME)));
					bean.setNurse_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_NURSEID)));
					bean.setIsUpdate(cursor.getInt(cursor
							.getColumnIndex(COLUMN_ISUPDATE)));
					bean.setUser(dbUser.getUserInfoById(bean.getOld_id() + ""));
					list.add(bean);
				}
			}
			cursor.close();
		}
		dbUser.close();
		return list;
	}

	/**
	 * 
	 * Function:根据是否上传的条件查询健康信息
	 * 
	 * @param update
	 *            ：是否上传的数据： 0表示没有上传，1表示已经上传
	 * @return
	 */
	public synchronized List<HealthBean> getHealthInfoByUpdate(int update) {
		List<HealthBean> list = null;
		DBUser dbUser = DBUser.getInstance(mContext);
		dbUser.open();
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns,
					COLUMN_ISUPDATE + "=? ", new String[] { update + "" },
					null, null, COLUMN_COLLECT_TIME + "  DESC");
			if (cursor == null) {
				return null;
			}
			int count = cursor.getCount();
			HealthBean bean = null;
			list = new ArrayList<HealthBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {
					bean = new HealthBean();
					bean.setOld_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_USERID)));
					bean.setOld_sn(cursor.getString(cursor
							.getColumnIndex(COLUMN_USERSN)));
					bean.setData_id(cursor.getString(cursor
							.getColumnIndex(COLUMN_UUID)));
					bean.setCollect_time(cursor.getString(cursor
							.getColumnIndex(COLUMN_COLLECT_TIME)));
					bean.setWeight(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_WEIGHT)));
					bean.setSystolic_pressure(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_SYSTOLIC_PRESSURE)));
					bean.setDiastolic_pressure(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_DIASTOLIC_PRESSURE)));
					bean.setHeart_rate(cursor.getInt(cursor
							.getColumnIndex(COlUMN_HEAR_RATE)));
					bean.setBlood_sugar(cursor.getFloat(cursor
							.getColumnIndex(COLUMN_BLOOD_SUGAR)));
					bean.setNurse_name(cursor.getString(cursor
							.getColumnIndex(COLUMN_NURSE_NAME)));
					bean.setNurse_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_NURSEID)));
					bean.setIsUpdate(cursor.getInt(cursor
							.getColumnIndex(COLUMN_ISUPDATE)));
					list.add(bean);
				}
			}
			cursor.close();
		}
		dbUser.close();
		return list;
	}

	/**
	 * 判断是否存在没有上传的数据
	 * 
	 * @param old_sn
	 * @return
	 */
	public int updateCount(int update) {
		int count = 0;
		if (db != null) {
			Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "
					+ DATABASE_TABLE + " where " + COLUMN_ISUPDATE + " = ? ",
					new String[] { update + "" });
			if (cursor != null) {
				if (cursor.moveToPosition(0)) {
					count = cursor.getInt(0);
				}
				cursor.close();
			}
		}
		return count;
	}

//	/**
//	 * 获取分组信息
//	 * 
//	 * @param old_sn
//	 * @return
//	 */
//	public List<HealthBean> groupData(String userId) {
//		List<HealthBean> list = null;
//		if (db != null) {
//			Cursor cursor = db.rawQuery("SELECT " + COLUMN_USERID + ",date("
//					+ COLUMN_COLLECT_TIME + "), avg(" + COLUMN_WEIGHT
//					+ ") FROM " + DATABASE_TABLE + " group by date("
//					+ COLUMN_COLLECT_TIME + ") having " + COLUMN_USERID
//					+ " =? and " + COLUMN_ISUPDATE + " = 1",
//					new String[] { userId + "" });
//			if (cursor == null) {
//				return null;
//			}
//			int count = cursor.getCount();
//			HealthBean bean;
//			list = new ArrayList<HealthBean>();
//			for (int i = 0; i < count; i++) {
//				if (cursor.moveToPosition(i)) {
//					System.out.println("COLUMN_USERID" + cursor.getInt(0)
//							+ "COLUMN_COLLECT_TIME" + cursor.getString(1)
//							+ "体重" + cursor.getFloat(2));
//
//					// bean = new HealthBean();
//					// bean.setOld_id(cursor.getInt(cursor
//					// .getColumnIndex(COLUMN_USERID)));
//					// bean.setOld_sn(cursor.getString(cursor
//					// .getColumnIndex(COLUMN_USERSN)));
//					// bean.setData_id(cursor.getString(cursor
//					// .getColumnIndex(COLUMN_UUID)));
//					// bean.setCollect_time(cursor.getString(cursor
//					// .getColumnIndex(COLUMN_COLLECT_TIME)));
//					// bean.setWeight(cursor.getFloat(cursor
//					// .getColumnIndex(COLUMN_WEIGHT)));
//					// bean.setSystolic_pressure(cursor.getFloat(cursor
//					// .getColumnIndex(COLUMN_SYSTOLIC_PRESSURE)));
//					// bean.setDiastolic_pressure(cursor.getFloat(cursor
//					// .getColumnIndex(COLUMN_DIASTOLIC_PRESSURE)));
//					// bean.setHeart_rate(cursor.getInt(cursor
//					// .getColumnIndex(COlUMN_HEAR_RATE)));
//					// bean.setBlood_sugar(cursor.getFloat(cursor
//					// .getColumnIndex(COLUMN_BLOOD_SUGAR)));
//					// bean.setNurse_name(cursor.getString(cursor
//					// .getColumnIndex(COLUMN_NURSE_NAME)));
//					// bean.setNurse_id(cursor.getInt(cursor
//					// .getColumnIndex(COLUMN_NURSEID)));
//					// bean.setIsUpdate(cursor.getInt(cursor
//					// .getColumnIndex(COLUMN_ISUPDATE)));
//					// list.add(bean);
//				}
//
//			}
//		}
//		return list;
//	}

	@Override
	public synchronized void close() {
		if (db != null) {
			db.close();

			db = null;
		}
	}

	@Override
	public synchronized void open() {
		if (db == null) {
			db = getWritableDatabase();
		}
	}
}