package com.magicare.smartnurse.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.magicare.smartnurse.bean.RegionBean;
import com.magicare.smartnurse.database.MySqliteHelper;

/**
 * 区域信息
 * 
 * @author 波
 * 
 */
public class DBRegion extends MySqliteHelper {

	public static String DATABASE_TABLE = "regioninfo";

	public static final String CreateTableSql;

	private static SQLiteDatabase db;

	public static final String COLUMN_ID = BaseColumns._ID, COLUMN_REGIONID = "regionid",
			COLUMN_PERSION_AREASN = "pension_areasn", COLUMN_NAME = "name", COLUMN_CONTACT = "contact",
			COLUMN_TELEPHONE = "telephone", COLUMN_AREA_NUM = "area_oldnum", COLUMN_RANGE = "range",
			COlUMN_NOTE = "note", COlUMN_NURSE_ID = "nurse_id", COLUMN_REMARK1 = "remark1", COLUMN_REMARK2 = "remark2";

	public static final String[] dispColumns = { COLUMN_ID, COLUMN_REGIONID, COLUMN_PERSION_AREASN, COLUMN_AREA_NUM,
			COLUMN_RANGE, COlUMN_NOTE, COLUMN_CONTACT, COLUMN_TELEPHONE, COLUMN_NAME, COlUMN_NURSE_ID, COLUMN_REMARK1,
			COLUMN_REMARK2 };

	static {
		StringBuilder strSql = new StringBuilder();
		strSql.append("CREATE TABLE " + " IF NOT EXISTS " + DATABASE_TABLE + "(");
		strSql.append(COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,");
		strSql.append(COLUMN_REGIONID + " NVARCHAR(50) , ");
		strSql.append(COLUMN_PERSION_AREASN + " NVARCHAR(50) , ");
		strSql.append(COLUMN_NAME + " NVARCHAR(100) , ");
		strSql.append(COLUMN_CONTACT + " NVARCHAR(100) ,");
		strSql.append(COLUMN_TELEPHONE + " NVARCHAR(100) ,");
		strSql.append(COLUMN_AREA_NUM + " INTEGER ,");
		strSql.append(COLUMN_RANGE + " NVARCHAR(100) ,");
		strSql.append(COlUMN_NOTE + " NVARCHAR(100) ,");
		strSql.append(COlUMN_NURSE_ID + " NVARCHAR(50) , ");
		strSql.append(COLUMN_REMARK1 + " NVARCHAR(50) , ");
		strSql.append(COLUMN_REMARK2 + " NVARCHAR(50) ");
		strSql.append(" ) ;");
		CreateTableSql = strSql.toString();
	}

	private static DBRegion dbApp = null;

	public static synchronized DBRegion getInstance(Context context) {
		if (dbApp == null) {
			dbApp = new DBRegion(context);
		}
		return dbApp;
	}

	private DBRegion(Context context) {
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
	public long insert(RegionBean bean) {
		long count = 0;

		if (bean != null) {
			ContentValues values = new ContentValues();
			values.put(COLUMN_REGIONID, bean.getPension_areaid());
			values.put(COLUMN_PERSION_AREASN, bean.getPension_areasn());
			values.put(COLUMN_NAME, bean.getName());
			values.put(COLUMN_CONTACT, bean.getContact());
			values.put(COLUMN_TELEPHONE, bean.getTelephone());
			values.put(COLUMN_AREA_NUM, bean.getArea_oldnum());
			values.put(COLUMN_RANGE, bean.getRange());
			values.put(COlUMN_NOTE, bean.getNote());
			values.put(COlUMN_NURSE_ID, bean.getNurse_id());
			count = db.insert(DATABASE_TABLE, null, values);
		}
		return count;
	}

	public long delete(String nurse_id) {
		int count = db.delete(DATABASE_TABLE, COlUMN_NURSE_ID + "=? ", new String[] { nurse_id });
		return count;
	}

	public RegionBean queryRegionInfoByAreaID(int areaId) {

		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, COLUMN_REGIONID + " = ?",
					new String[] { areaId + "" }, null, null, null);
			if (cursor == null) {
				return null;
			}
			RegionBean bean;
			if (cursor.moveToFirst()) {
				bean = new RegionBean();
				bean.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
				bean.setPension_areaid(cursor.getInt(cursor.getColumnIndex(COLUMN_REGIONID)));
				bean.setPension_areasn(cursor.getString(cursor.getColumnIndex(COLUMN_PERSION_AREASN)));
				bean.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
				bean.setContact(cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT)));
				bean.setTelephone(cursor.getString(cursor.getColumnIndex(COLUMN_TELEPHONE)));
				bean.setArea_oldnum(cursor.getInt(cursor.getColumnIndex(COLUMN_AREA_NUM)));
				bean.setRange(cursor.getString(cursor.getColumnIndex(COLUMN_RANGE)));
				bean.setNote(cursor.getString(cursor.getColumnIndex(COlUMN_NOTE)));
				bean.setNurse_id(cursor.getString(cursor.getColumnIndex(COlUMN_NURSE_ID)));
				return bean;
			}

		}
		return null;

	}

	/**
	 * 查询区域信息
	 */
	public RegionBean queryRegionInfoByNurseID(String nurse_id) {

		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, COlUMN_NURSE_ID + " = ?", new String[] { nurse_id },
					null, null, null);
			if (cursor == null) {
				return null;
			}
			RegionBean bean;
			if (cursor.moveToFirst()) {
				bean = new RegionBean();
				bean.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
				bean.setPension_areaid(cursor.getInt(cursor.getColumnIndex(COLUMN_REGIONID)));
				bean.setPension_areasn(cursor.getString(cursor.getColumnIndex(COLUMN_PERSION_AREASN)));
				bean.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
				bean.setContact(cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT)));
				bean.setTelephone(cursor.getString(cursor.getColumnIndex(COLUMN_TELEPHONE)));
				bean.setArea_oldnum(cursor.getInt(cursor.getColumnIndex(COLUMN_AREA_NUM)));
				bean.setRange(cursor.getString(cursor.getColumnIndex(COLUMN_RANGE)));
				bean.setNote(cursor.getString(cursor.getColumnIndex(COlUMN_NOTE)));
				bean.setNurse_id(cursor.getString(cursor.getColumnIndex(COlUMN_NURSE_ID)));
				return bean;
			}

		}
		return null;

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
