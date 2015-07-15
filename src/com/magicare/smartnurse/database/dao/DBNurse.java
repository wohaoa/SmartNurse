package com.magicare.smartnurse.database.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.magicare.smartnurse.bean.NurseBean;
import com.magicare.smartnurse.database.MySqliteHelper;

/**
 * 
 * @author scott
 * 
 *         Function:护士表
 */
public class DBNurse extends MySqliteHelper {

	public static String DATABASE_TABLE = "nurseinfo";

	public static final String CreateTableSql;

	private static SQLiteDatabase db;

	public static final String COLUMN_ID = BaseColumns._ID, COLUMN_NURSEID = "nurse_id", COLUMN_NAME = "nurse_name",
			COLUMN_MOBILE = "mobile", COLUMN_PENSIONID = "pension_id", COLUMN_AREAID = "pension_areaid",
			COlUMN_AVATARURL = "avatar_url", COLUMN_NOTE = "note", COLUMN_REMARK1 = "remark1",
			COLUMN_REMARK2 = "remark2";

	public static final String[] dispColumns = { COLUMN_ID, COLUMN_NURSEID, COLUMN_NAME, COLUMN_MOBILE,
			COLUMN_PENSIONID, COLUMN_AREAID, COlUMN_AVATARURL, COLUMN_NOTE };

	static {
		StringBuilder strSql = new StringBuilder();
		strSql.append("CREATE TABLE " + " IF NOT EXISTS " + DATABASE_TABLE + "( ");
		strSql.append(COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , ");
		strSql.append(COLUMN_NURSEID + " NVARCHAR(100) , ");
		strSql.append(COLUMN_NAME + " NVARCHAR(100) ,");
		strSql.append(COLUMN_MOBILE + " NVARCHAR(100 ,");
		strSql.append(COLUMN_PENSIONID + " INTEGER ,");
		strSql.append(COLUMN_AREAID + " INTEGER ,");
		strSql.append(COlUMN_AVATARURL + " NVARCHAR(100) ,");
		strSql.append(COLUMN_NOTE + " NVARCHAR(100) ,");
		strSql.append(COLUMN_REMARK1 + " NVARCHAR(100) ,");
		strSql.append(COLUMN_REMARK2 + " NVARCHAR(100) ");
		strSql.append(" ) ;");
		CreateTableSql = strSql.toString();
	}

	private static DBNurse dbApp = null;

	public synchronized static DBNurse getInstance(Context context) {
		if (dbApp == null) {
			dbApp = new DBNurse(context);
		}
		return dbApp;
	}

	private DBNurse(Context context) {
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
	public synchronized long insert(NurseBean bean) {
		long count = 0;
		if (bean == null) {
			ContentValues values = new ContentValues();
			values.put(COLUMN_NURSEID, bean.getNurse_id());
			values.put(COLUMN_NAME, bean.getNurse_name());
			values.put(COLUMN_MOBILE, bean.getMobile());
			values.put(COLUMN_PENSIONID, bean.getPension_id());
			values.put(COLUMN_AREAID, bean.getPension_areaid());
			values.put(COlUMN_AVATARURL, bean.getAvatar_url());
			values.put(COLUMN_NOTE, bean.getNote());
			count = db.insert(DATABASE_TABLE, null, values);
		}
		return count;
	}

	/**
	 * 
	 * @param info
	 * @return
	 */
	public long insert(List<NurseBean> list) {
		long count = 0;
		beginTransaction();
		try {
			for (int i = 0; i < list.size(); i++) {
				NurseBean bean = list.get(i);
				if (bean != null) {
					ContentValues values = new ContentValues();
					values.put(COLUMN_NURSEID, bean.getNurse_id());
					values.put(COLUMN_NAME, bean.getNurse_name());
					values.put(COLUMN_MOBILE, bean.getMobile());
					values.put(COLUMN_PENSIONID, bean.getPension_id());
					values.put(COLUMN_AREAID, bean.getPension_areaid());
					values.put(COlUMN_AVATARURL, bean.getAvatar_url());
					values.put(COLUMN_NOTE, bean.getNote());
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

	public NurseBean getNurseInfoById(String nurseId) {
		NurseBean bean = null;
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, COLUMN_NURSEID + " = ? ", new String[] { nurseId },
					null, null, null);
			if (cursor == null) {
				return null;
			}
			if (cursor.moveToPosition(0)) {
				bean = new NurseBean();
				bean.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
				bean.setNurse_id(cursor.getInt(cursor.getColumnIndex(COLUMN_NURSEID)));
				bean.setNurse_name(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
				bean.setMobile(cursor.getString(cursor.getColumnIndex(COLUMN_MOBILE)));
				bean.setPension_id(cursor.getInt(cursor.getColumnIndex(COLUMN_PENSIONID)));
				bean.setPension_areaid(cursor.getInt(cursor.getColumnIndex(COLUMN_AREAID)));
				bean.setAvatar_url(cursor.getString(cursor.getColumnIndex(COlUMN_AVATARURL)));
				bean.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
			}
			cursor.close();
		}
		return bean;
	}

	/**
	 * 通过老人id 获取老人健康信息
	 * 
	 * @param old_sn
	 * @return
	 */

	public List<NurseBean> getAllNurseInfo() {
		List<NurseBean> list = null;
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, null, null, null, null, null);
			if (cursor == null) {
				return null;
			}
			int count = cursor.getCount();
			NurseBean bean = null;
			list = new ArrayList<NurseBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {
					bean = new NurseBean();
					bean.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
					bean.setNurse_id(cursor.getInt(cursor.getColumnIndex(COLUMN_NURSEID)));
					bean.setNurse_name(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
					bean.setMobile(cursor.getString(cursor.getColumnIndex(COLUMN_MOBILE)));
					bean.setPension_id(cursor.getInt(cursor.getColumnIndex(COLUMN_PENSIONID)));
					bean.setPension_areaid(cursor.getInt(cursor.getColumnIndex(COLUMN_AREAID)));
					bean.setAvatar_url(cursor.getString(cursor.getColumnIndex(COlUMN_AVATARURL)));
					bean.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
					list.add(bean);
				}
			}
			cursor.close();
		}
		return list;
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
