package com.magicare.smartnurse.database.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.magicare.smartnurse.bean.SportsBean;
import com.magicare.smartnurse.database.MySqliteHelper;
import com.magicare.smartnurse.utils.LogUtil;

/**
 * 老人运动记录
 * 
 * @author 波
 * 
 */
public class DBSports extends MySqliteHelper {

	public static String DATABASE_TABLE = "sportsinfo";

	public static final String CreateTableSql;

	private static SQLiteDatabase db;

	public static final String COLUMN_ID = BaseColumns._ID, COLUMN_DATAID = "data_id", COLUMN_USERID = "user_id",
			COLUMN_START_TIME = "start_time", COLUMN_END_TIME = "end_time", COLUMN_TIME_VALUE = "time_value",
			COLUMN_MODE = "mode", COLUMN_STATE = "state", COLUMN_STEP = "step", COLUMN_CALORIE = "calorie",
			COLUMN_METER = "meter", COLUMN_SLEEP_0 = "sleep_0", COLUMN_SLEEP_1 = "sleep_1", COLUMN_SLEEP_2 = "sleep_2",
			COLUMN_SlEEP_QUANTITY = "sleep_quantity", COLUMN_REMARK1 = "remark1", COLUMN_REMARK2 = "remark2";

	public static final String[] dispColumns = { COLUMN_ID, COLUMN_USERID, COLUMN_DATAID, COLUMN_START_TIME,
			COLUMN_END_TIME, COLUMN_TIME_VALUE, COLUMN_MODE, COLUMN_STATE, COLUMN_STEP, COLUMN_CALORIE, COLUMN_METER,
			COLUMN_SLEEP_0, COLUMN_SLEEP_1, COLUMN_SLEEP_2, COLUMN_SlEEP_QUANTITY };

	static {
		StringBuilder strSql = new StringBuilder();
		strSql.append("CREATE TABLE " + " IF NOT EXISTS " + DATABASE_TABLE + "(");
		strSql.append(COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , ");
		strSql.append(COLUMN_USERID + " NVARCHAR(100) , ");
		strSql.append(COLUMN_DATAID + " NVARCHAR(100) ,");
		strSql.append(COLUMN_START_TIME + " NVARCHAR(100) ,");
		strSql.append(COLUMN_END_TIME + " NVARCHAR(100) ,");
		strSql.append(COLUMN_TIME_VALUE + " INTEGER ,");
		strSql.append(COLUMN_MODE + " INTEGER ,");
		strSql.append(COLUMN_STATE + " INTEGER ,");
		strSql.append(COLUMN_STEP + " INTEGER ,");
		strSql.append(COLUMN_CALORIE + " FLOAT ,");
		strSql.append(COLUMN_METER + " FLOAT , ");
		strSql.append(COLUMN_SLEEP_0 + " FLOAT , ");
		strSql.append(COLUMN_SLEEP_1 + " FLOAT , ");
		strSql.append(COLUMN_SLEEP_2 + " FLOAT , ");
		strSql.append(COLUMN_SlEEP_QUANTITY + " FLOAT , ");
		strSql.append(COLUMN_REMARK1 + " NVARCHAR(100) , ");
		strSql.append(COLUMN_REMARK2 + " NVARCHAR(100)  ");
		strSql.append(") ;");
		CreateTableSql = strSql.toString();
	}

	private static DBSports dbApp = null;

	public static synchronized DBSports getInstance(Context context) {
		if (dbApp == null) {
			dbApp = new DBSports(context);
		}
		return dbApp;
	}

	private DBSports(Context context) {
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
	public long insert(SportsBean bean) {
		long count = 0;
		if (bean != null) {
			ContentValues values = new ContentValues();
			values.put(COLUMN_USERID, bean.getOld_id());
			values.put(COLUMN_DATAID, bean.getData_id());
			values.put(COLUMN_START_TIME, bean.getStart_time());
			values.put(COLUMN_END_TIME, bean.getEnd_time());
			values.put(COLUMN_TIME_VALUE, bean.getTime_value());
			values.put(COLUMN_MODE, bean.getMode());
			values.put(COLUMN_STATE, bean.getState());
			values.put(COLUMN_STEP, bean.getStep());
			values.put(COLUMN_CALORIE, bean.getCalorie());
			values.put(COLUMN_METER, bean.getMeter());
			values.put(COLUMN_SLEEP_0, bean.getSleep_0());
			values.put(COLUMN_SLEEP_1, bean.getSleep_1());
			values.put(COLUMN_SLEEP_2, bean.getSleep_2());
			values.put(COLUMN_SlEEP_QUANTITY, bean.getSleep_quantity());
			count = db.insert(DATABASE_TABLE, null, values);
		}
		return count;
	}

	/**
	 * 
	 * @param info
	 * @return
	 */
	public long insert(List<SportsBean> list ,String date) {
		long count = 0;
		beginTransaction();
		try {
			for (int i = 0; i < list.size(); i++) {
				SportsBean bean = list.get(i);
				if(bean.getStart_time().split(" ")[0].equals(date.split(" ")[0])){
					ContentValues values = new ContentValues();
					values.put(COLUMN_USERID, bean.getOld_id());
					values.put(COLUMN_DATAID, bean.getData_id());
					values.put(COLUMN_START_TIME, bean.getStart_time());
					values.put(COLUMN_END_TIME, bean.getEnd_time());
					values.put(COLUMN_TIME_VALUE, bean.getTime_value());
					values.put(COLUMN_MODE, bean.getMode());
					values.put(COLUMN_STATE, bean.getState());
					values.put(COLUMN_STEP, bean.getStep());
					values.put(COLUMN_CALORIE, bean.getCalorie());
					values.put(COLUMN_METER, bean.getMeter());
					values.put(COLUMN_SLEEP_0, bean.getSleep_0());
					values.put(COLUMN_SLEEP_1, bean.getSleep_1());
					values.put(COLUMN_SLEEP_2, bean.getSleep_2());
					values.put(COLUMN_SlEEP_QUANTITY, bean.getSleep_quantity());
					count += db.insert(DATABASE_TABLE, null, values);
					LogUtil.info(DBSports.class, "insert i="+i);
					LogUtil.info(DBSports.class, "insert count="+count);
				}
			}

			setTransactionSuccessful();
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.info(DBSports.class, "exception"+e.getMessage());
			e.printStackTrace();

		} finally {
			endTransaction();
		}
		return count;
	}

	public int getSportsCount() {
		if (db != null) {
			Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DATABASE_TABLE, null);
			if (cursor != null) {
				if (cursor.moveToPosition(0)) {
					return cursor.getInt(0);
				}
				cursor.close();
			}
		}
		return 0;
	}

	public int update(SportsBean bean) {
		int count = 0;
		ContentValues values = new ContentValues();
		values.put(COLUMN_USERID, bean.getOld_id());
		values.put(COLUMN_DATAID, bean.getData_id());
		values.put(COLUMN_START_TIME, bean.getStart_time());
		values.put(COLUMN_END_TIME, bean.getEnd_time());
		values.put(COLUMN_TIME_VALUE, bean.getTime_value());
		values.put(COLUMN_MODE, bean.getMode());
		values.put(COLUMN_STATE, bean.getState());
		values.put(COLUMN_STEP, bean.getStep());
		values.put(COLUMN_CALORIE, bean.getCalorie());
		values.put(COLUMN_METER, bean.getMeter());
		values.put(COLUMN_SLEEP_0, bean.getSleep_0());
		values.put(COLUMN_SLEEP_1, bean.getSleep_1());
		values.put(COLUMN_SLEEP_2, bean.getSleep_2());
		values.put(COLUMN_SlEEP_QUANTITY, bean.getSleep_quantity());
		count = db.update(DATABASE_TABLE, values, COLUMN_START_TIME + "=? and " + COLUMN_END_TIME + "=?", new String[] {
				bean.getStart_time(), bean.getEnd_time() });
		return count;

	}

	public List<SportsBean> getSportsByUserId(String oldId) {
		System.out.println("getSportsByUserId");
		List<SportsBean> list = null;
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, COLUMN_USERID + " = ? ", new String[] { oldId },
					null, null, null);
			
			if (cursor == null) {
				System.out.println(cursor.toString());
				return null;
			}
			int count = cursor.getCount();
			SportsBean bean = null;
			list = new ArrayList<SportsBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {
					bean = new SportsBean();
					bean.setOld_id(cursor.getInt(cursor.getColumnIndex(COLUMN_USERID)));
					bean.setData_id(cursor.getString(cursor.getColumnIndex(COLUMN_DATAID)));
					bean.setStart_time(cursor.getString(cursor.getColumnIndex(COLUMN_START_TIME)));
					bean.setEnd_time(cursor.getString(cursor.getColumnIndex(COLUMN_END_TIME)));
					bean.setTime_value(cursor.getInt(cursor.getColumnIndex(COLUMN_TIME_VALUE)));
					bean.setMode(cursor.getInt(cursor.getColumnIndex(COLUMN_MODE)));
					bean.setState(cursor.getInt(cursor.getColumnIndex(COLUMN_STATE)));
					bean.setStep(cursor.getInt(cursor.getColumnIndex(COLUMN_STEP)));
					bean.setCalorie(cursor.getFloat(cursor.getColumnIndex(COLUMN_CALORIE)));
					bean.setMeter(cursor.getFloat(cursor.getColumnIndex(COLUMN_METER)));
					bean.setSleep_0(cursor.getFloat(cursor.getColumnIndex(COLUMN_SLEEP_0)));
					bean.setSleep_1(cursor.getFloat(cursor.getColumnIndex(COLUMN_SLEEP_1)));
					bean.setSleep_2(cursor.getFloat(cursor.getColumnIndex(COLUMN_SLEEP_2)));
					bean.setSleep_quantity(cursor.getFloat(cursor.getColumnIndex(COLUMN_SlEEP_QUANTITY)));
					list.add(bean);
				}
			}
			cursor.close();
		}
		return list;
	}

	public List<SportsBean> getSportsByUserIdandDate(String oldId, String date) {
		List<SportsBean> list = null;
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, COLUMN_USERID + " = ? and " + COLUMN_START_TIME
					+ " like ?", new String[] { oldId, "%" + date + "%" }, null, null, COLUMN_START_TIME + " asc ");
			if (cursor == null) {
				return null;
			}
			int count = cursor.getCount();
			LogUtil.info(DBSports.class, "getSportsByUserIdandDate get"+ "count= " + count);
			SportsBean bean = null;
			list = new ArrayList<SportsBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {
					bean = new SportsBean();
					bean.setOld_id(cursor.getInt(cursor.getColumnIndex(COLUMN_USERID)));
					bean.setData_id(cursor.getString(cursor.getColumnIndex(COLUMN_DATAID)));
					bean.setStart_time(cursor.getString(cursor.getColumnIndex(COLUMN_START_TIME)));
					bean.setEnd_time(cursor.getString(cursor.getColumnIndex(COLUMN_END_TIME)));
					bean.setTime_value(cursor.getInt(cursor.getColumnIndex(COLUMN_TIME_VALUE)));
					bean.setMode(cursor.getInt(cursor.getColumnIndex(COLUMN_MODE)));
					bean.setState(cursor.getInt(cursor.getColumnIndex(COLUMN_STATE)));
					bean.setStep(cursor.getInt(cursor.getColumnIndex(COLUMN_STEP)));
					bean.setCalorie(cursor.getFloat(cursor.getColumnIndex(COLUMN_CALORIE)));
					bean.setMeter(cursor.getFloat(cursor.getColumnIndex(COLUMN_METER)));
					bean.setSleep_0(cursor.getFloat(cursor.getColumnIndex(COLUMN_SLEEP_0)));
					bean.setSleep_1(cursor.getFloat(cursor.getColumnIndex(COLUMN_SLEEP_1)));
					bean.setSleep_2(cursor.getFloat(cursor.getColumnIndex(COLUMN_SLEEP_2)));
					bean.setSleep_quantity(cursor.getFloat(cursor.getColumnIndex(COLUMN_SlEEP_QUANTITY)));
					list.add(bean);
					LogUtil.info(DBSports.class, "得到的sportsBean数据= " + bean.toString());
				}
			}
			cursor.close();
		}
		return list;
	}

	public long deleteSportsInfoByUserId(String oldId) {
		int count = db.delete(DATABASE_TABLE, COLUMN_USERID + "=? ", new String[] { oldId });
		return count;
	}

	public long deleteSportsInfoByUserIdandDate(String oldId, String date) {
		int count = db.delete(DATABASE_TABLE, COLUMN_USERID + "=? and " + COLUMN_START_TIME + " like ? ", new String[] {
				oldId, "%" + date + "%" });
		LogUtil.info(DBSports.class, "从sports数据库删除记录条数="+count);
		return count;
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
