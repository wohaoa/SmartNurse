package com.magicare.smartnurse.database.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.magicare.smartnurse.bean.WarningBean;
import com.magicare.smartnurse.database.MySqliteHelper;

/**
 * 未处理的报警信息
 * 
 * @author 波
 * 
 */
public class DBWarning extends MySqliteHelper {

	public static String DATABASE_TABLE = "warninginfo";

	public static final String CreateTableSql;

	private static SQLiteDatabase db;

	public static final String COLUMN_ID = BaseColumns._ID, COLUMN_WARNING_ID = "warning_id",
			COLUMN_REPORT_TIME = "warning_time", COLUMN_DETAIL = "detail", COLUMN_TYPE = "type",
			COLUMN_STATUS = "status", COLUMN_RESOLVE_TIME = "resolve_time", COLUMN_FEED_TIME = "feed_time",
			COLUMN_FEED_NURSE = "feed_nurse", COLUMN_FEED_BACK = "feed_back", COLUMN_USERID = "user_id",
			COLUMN_USERSN = "user_sn", COLUMN_USERNAME = "user_name", COLUMN_NURSEID = "nurse_id",
			COLUMN_REMARK1 = "remark1", COLUMN_REMARK2 = "remark2";

	public static final String[] dispColumns = { COLUMN_ID, COLUMN_WARNING_ID, COLUMN_REPORT_TIME, COLUMN_DETAIL,
			COLUMN_TYPE, COLUMN_STATUS, COLUMN_RESOLVE_TIME, COLUMN_FEED_TIME, COLUMN_FEED_NURSE, COLUMN_FEED_BACK,
			COLUMN_USERID, COLUMN_USERSN, COLUMN_NURSEID, COLUMN_USERNAME, COLUMN_REMARK1, COLUMN_REMARK2 };

	static {
		StringBuilder strSql = new StringBuilder();
		strSql.append("CREATE TABLE " + " IF NOT EXISTS " + DATABASE_TABLE + "(");
		strSql.append(COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,");
		strSql.append(COLUMN_WARNING_ID + " NVARCHAR(100) ,");
		strSql.append(COLUMN_REPORT_TIME + " NVARCHAR(100) ,");
		strSql.append(COLUMN_DETAIL + " NVARCHAR(100) ,");
		strSql.append(COLUMN_TYPE + " NVARCHAR(100) ,");
		strSql.append(COLUMN_STATUS + " INTEGER ,");
		strSql.append(COLUMN_RESOLVE_TIME + " NVARCHAR(100) ,");
		strSql.append(COLUMN_FEED_TIME + " NVARCHAR(100) ,");
		strSql.append(COLUMN_FEED_NURSE + " NVARCHAR(100) ,");
		strSql.append(COLUMN_FEED_BACK + " NVARCHAR(200) ,");
		strSql.append(COLUMN_USERID + " NVARCHAR(100) ,");
		strSql.append(COLUMN_USERSN + " NVARCHAR(100) ,");
		strSql.append(COLUMN_NURSEID + " NVARCHAR(100) ,");
		strSql.append(COLUMN_USERNAME + " NVARCHAR(100) ,");
		strSql.append(COLUMN_REMARK1 + " NVARCHAR(100) ,");
		strSql.append(COLUMN_REMARK2 + " NVARCHAR(100) ");
		strSql.append(")");
		CreateTableSql = strSql.toString();
	}

	private static DBWarning dbApp = null;

	public static synchronized DBWarning getInstance(Context context) {
		if (dbApp == null) {
			dbApp = new DBWarning(context);
		}
		return dbApp;
	}

	private DBWarning(Context context) {
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

	public long insert(WarningBean bean) {
		long count = 0;
		if (bean != null) {
			ContentValues values = new ContentValues();
			values.put(COLUMN_WARNING_ID, bean.getAlarm_id());
			values.put(COLUMN_REPORT_TIME, bean.getAlarm_time());
			values.put(COLUMN_DETAIL, bean.getStation_detail());
			values.put(COLUMN_TYPE, bean.getAlarm_type());
			values.put(COLUMN_STATUS, bean.getAlarm_status());
			values.put(COLUMN_RESOLVE_TIME, bean.getResolve_time());
			values.put(COLUMN_FEED_TIME, bean.getFeed_time());
			values.put(COLUMN_FEED_NURSE, bean.getFeed_nurse());
			values.put(COLUMN_FEED_BACK, bean.getFeedback());
			values.put(COLUMN_USERID, bean.getOld_id());
			values.put(COLUMN_USERSN, bean.getOld_sn());
			values.put(COLUMN_USERNAME, bean.getOld_name());
			values.put(COLUMN_NURSEID, bean.getNurse_id());
			count = db.insert(DATABASE_TABLE, null, values);
		}
		return count;
	}

	/**
	 * 
	 * @param info
	 * @return
	 */
	public long insert(List<WarningBean> list) {
		long count = 0;
		beginTransaction();
		try {
			for (int i = 0; i < list.size(); i++) {
				WarningBean bean = list.get(i);
				if (!isExistUser(bean.getAlarm_id())) {
					ContentValues values = new ContentValues();
					values.put(COLUMN_WARNING_ID, bean.getAlarm_id());
					values.put(COLUMN_REPORT_TIME, bean.getAlarm_time());
					values.put(COLUMN_DETAIL, bean.getStation_detail());
					values.put(COLUMN_TYPE, bean.getAlarm_type());
					values.put(COLUMN_STATUS, bean.getAlarm_status());
					values.put(COLUMN_RESOLVE_TIME, bean.getResolve_time());
					values.put(COLUMN_FEED_TIME, bean.getFeed_time());
					values.put(COLUMN_FEED_NURSE, bean.getFeed_nurse());
					values.put(COLUMN_FEED_BACK, bean.getFeedback());
					values.put(COLUMN_USERID, bean.getOld_id());
					values.put(COLUMN_USERSN, bean.getOld_sn());
					values.put(COLUMN_USERNAME, bean.getOld_name());
					values.put(COLUMN_NURSEID, bean.getNurse_id());
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
	 * 判断这个老人是不是已经存在了
	 * 
	 * @param old_sn
	 * @return
	 */
	public boolean isExistUser(String alarm_id) {
		if (db != null) {
			Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DATABASE_TABLE + " where " + COLUMN_WARNING_ID
					+ " = ? ", new String[] { alarm_id });
			if (cursor != null) {
				if (cursor.moveToPosition(0)) {
					return cursor.getInt(0) == 0 ? false : true;
				}
				cursor.close();
			}
		}
		return false;
	}

	/**
	 * 根据护士Id查询
	 * 
	 * @param nurse_id
	 * @return
	 */
	public List<WarningBean> getWarningInfoByNurseId(String nurse_id) {
		List<WarningBean> list = null;
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, COLUMN_NURSEID + " = ?", new String[] { nurse_id },
					null, null, null);
			if (cursor == null) {
				return null;
			}
			int count = cursor.getCount();
			WarningBean bean = null;
			list = new ArrayList<WarningBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {

					bean.setAlarm_id(cursor.getString(cursor.getColumnIndex(COLUMN_WARNING_ID)));
					bean.setAlarm_time(cursor.getString(cursor.getColumnIndex(COLUMN_REPORT_TIME)));
					bean.setStation_detail(cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL)));
					bean.setAlarm_type(cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE)));
					bean.setAlarm_status(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
					bean.setResolve_time(cursor.getString(cursor.getColumnIndex(COLUMN_RESOLVE_TIME)));
					bean.setFeed_time(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_TIME)));
					bean.setFeed_nurse(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_NURSE)));
					bean.setFeedback(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_BACK)));
					bean.setOld_id(cursor.getInt(cursor.getColumnIndex(COLUMN_USERID)));
					bean.setOld_sn(cursor.getString(cursor.getColumnIndex(COLUMN_USERSN)));
					bean.setOld_name(cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)));
					bean.setNurse_id(cursor.getString(cursor.getColumnIndex(COLUMN_NURSEID)));

					list.add(bean);
				}
			}
			cursor.close();
		}
		return list;
	}

	/**
	 * 根据护士Id查询
	 * 
	 * @param nurse_id
	 * @return
	 */
	public List<WarningBean> getWarningInfoByOldid(String oldid) {
		List<WarningBean> list = null;
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, COLUMN_USERID + " = ?", new String[] { oldid }, null,
					null, null);
			if (cursor == null) {
				return null;
			}
			int count = cursor.getCount();
			WarningBean bean = null;
			list = new ArrayList<WarningBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {
					bean = new WarningBean();
					bean.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
					bean.setAlarm_id(cursor.getString(cursor.getColumnIndex(COLUMN_WARNING_ID)));
					bean.setAlarm_time(cursor.getString(cursor.getColumnIndex(COLUMN_REPORT_TIME)));
					bean.setStation_detail(cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL)));
					bean.setAlarm_type(cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE)));
					bean.setAlarm_status(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
					bean.setResolve_time(cursor.getString(cursor.getColumnIndex(COLUMN_RESOLVE_TIME)));
					bean.setFeed_time(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_TIME)));
					bean.setFeed_nurse(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_NURSE)));
					bean.setFeedback(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_BACK)));
					bean.setOld_id(cursor.getInt(cursor.getColumnIndex(COLUMN_USERID)));
					bean.setOld_sn(cursor.getString(cursor.getColumnIndex(COLUMN_USERSN)));
					bean.setOld_name(cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)));
					bean.setNurse_id(cursor.getString(cursor.getColumnIndex(COLUMN_NURSEID)));
					list.add(bean);
				}
			}
			cursor.close();
		}
		return list;
	}

	public List<WarningBean> getWarningInfoerror() {
		List<WarningBean> list = null;
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, null, null, null, null, null);
			if (cursor == null) {
				return null;
			}
			int count = cursor.getCount();
			WarningBean bean = null;
			list = new ArrayList<WarningBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {
					bean = new WarningBean();
					bean.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
					bean.setAlarm_id(cursor.getString(cursor.getColumnIndex(COLUMN_WARNING_ID)));
					bean.setAlarm_time(cursor.getString(cursor.getColumnIndex(COLUMN_REPORT_TIME)));
					bean.setStation_detail(cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL)));
					bean.setAlarm_type(cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE)));
					bean.setAlarm_status(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
					bean.setResolve_time(cursor.getString(cursor.getColumnIndex(COLUMN_RESOLVE_TIME)));
					bean.setFeed_time(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_TIME)));
					bean.setFeed_nurse(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_NURSE)));
					bean.setFeedback(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_BACK)));
					bean.setOld_id(cursor.getInt(cursor.getColumnIndex(COLUMN_USERID)));
					bean.setOld_sn(cursor.getString(cursor.getColumnIndex(COLUMN_USERSN)));
					bean.setOld_name(cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)));
					bean.setNurse_id(cursor.getString(cursor.getColumnIndex(COLUMN_NURSEID)));
					list.add(bean);
				}
			}
			cursor.close();
		}
		return list;
	}

	public List<WarningBean> getWarningInfoByOldid(String oldid, int startIndex, int pagesize) {
		List<WarningBean> list = null;
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, COLUMN_USERID + " = ? ", new String[] { oldid },
					null, null, COLUMN_REPORT_TIME + " desc limit " + startIndex + "," + pagesize);
			if (cursor == null) {
				return null;
			}
			int count = cursor.getCount();
			WarningBean bean = null;
			list = new ArrayList<WarningBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {
					bean = new WarningBean();
					bean.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
					bean.setAlarm_id(cursor.getString(cursor.getColumnIndex(COLUMN_WARNING_ID)));
					bean.setAlarm_time(cursor.getString(cursor.getColumnIndex(COLUMN_REPORT_TIME)));
					bean.setStation_detail(cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL)));
					bean.setAlarm_type(cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE)));
					bean.setAlarm_status(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
					bean.setResolve_time(cursor.getString(cursor.getColumnIndex(COLUMN_RESOLVE_TIME)));
					bean.setFeed_time(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_TIME)));
					bean.setFeed_nurse(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_NURSE)));
					bean.setFeedback(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_BACK)));
					bean.setOld_id(cursor.getInt(cursor.getColumnIndex(COLUMN_USERID)));
					bean.setOld_sn(cursor.getString(cursor.getColumnIndex(COLUMN_USERSN)));
					bean.setOld_name(cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)));
					bean.setNurse_id(cursor.getString(cursor.getColumnIndex(COLUMN_NURSEID)));
					list.add(bean);
				}
			}
			cursor.close();
		}
		return list;
	}

	public List<WarningBean> getWarningInfoByNurseIdAndStatus(int status) {
		List<WarningBean> list = null;
		if (db != null) {

			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, COLUMN_STATUS + "=? ", new String[] { status + "" },
					null, null, COLUMN_TYPE + " asc");

			if (cursor == null) {
				return null;
			}
			int count = cursor.getCount();
			WarningBean bean = null;
			list = new ArrayList<WarningBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {
					bean = new WarningBean();
					bean.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
					bean.setAlarm_id(cursor.getString(cursor.getColumnIndex(COLUMN_WARNING_ID)));
					bean.setAlarm_time(cursor.getString(cursor.getColumnIndex(COLUMN_REPORT_TIME)));
					bean.setStation_detail(cursor.getString(cursor.getColumnIndex(COLUMN_DETAIL)));
					bean.setAlarm_type(cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE)));
					bean.setAlarm_status(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
					bean.setResolve_time(cursor.getString(cursor.getColumnIndex(COLUMN_RESOLVE_TIME)));
					bean.setFeed_time(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_TIME)));
					bean.setFeed_nurse(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_NURSE)));
					bean.setFeedback(cursor.getString(cursor.getColumnIndex(COLUMN_FEED_BACK)));
					bean.setOld_id(cursor.getInt(cursor.getColumnIndex(COLUMN_USERID)));
					bean.setOld_sn(cursor.getString(cursor.getColumnIndex(COLUMN_USERSN)));
					bean.setOld_name(cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)));
					bean.setNurse_id(cursor.getString(cursor.getColumnIndex(COLUMN_NURSEID)));
					list.add(bean);
				}
			}
			cursor.close();
		}
		return list;
	}

	public long deleteAll() {
		int count = 0;
		if (db != null)
			count = db.delete(DATABASE_TABLE, null, null);
		return count;
	}

	public long deleteByOldId(String old_id) {
		int count = 0;
		if (db != null)
			count = db.delete(DATABASE_TABLE, COLUMN_USERID + " = ?", new String[] { old_id });
		return count;
	}

	/**
	 * 删除
	 * 
	 * @param alarm
	 * @return
	 */
	public long deleteUnResolveAlarm(WarningBean bean) {
		int count = 0;
		if (db != null)
			count = db.delete(DATABASE_TABLE, COLUMN_WARNING_ID + " = ? ", new String[] { bean.getAlarm_id() });
		return count;
	}

	/**
	 * 修改状态
	 * 
	 * @param alarm
	 * @return
	 */
	public long UpdateAlarmStatus(WarningBean bean) {
		int count = 0;

		if (db != null) {
			ContentValues values = new ContentValues();
			values.put(COLUMN_STATUS, bean.getAlarm_status());
			count = db.update(DATABASE_TABLE, values, COLUMN_WARNING_ID + " = ? ", new String[] { bean.getAlarm_id() });

		}
		return count;
	}

	public boolean isDababaseIsNull() {
		int count = 0;
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, new String[] { COLUMN_NURSEID }, null, null, null, null, null);
			if (cursor == null) {
				return false;
			}
			count = cursor.getCount();
		}
		return count > 0 ? false : true;
	}

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
