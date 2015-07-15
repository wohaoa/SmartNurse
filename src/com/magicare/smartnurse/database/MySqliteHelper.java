package com.magicare.smartnurse.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.magicare.smartnurse.database.dao.DBFeed;
import com.magicare.smartnurse.database.dao.DBHealth;
import com.magicare.smartnurse.database.dao.DBUser;
import com.magicare.smartnurse.database.dao.DBSports;
import com.magicare.smartnurse.database.dao.DBRegion;
import com.magicare.smartnurse.database.dao.DBWarning;

public abstract class MySqliteHelper extends SQLiteOpenHelper {

	private static final String DATABASENAME = "smartnurse.db";
	private static final int DATABASE_VERSION = 1;
	public Context mContext;

	public MySqliteHelper(Context context) {
		super(context, DATABASENAME, null, DATABASE_VERSION);
		this.mContext=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// 创建用户表
		db.execSQL("DROP TABLE IF EXISTS " + DBUser.DATABASE_TABLE);
		db.execSQL(DBUser.CreateTableSql);

		db.execSQL("DROP TABLE IF EXISTS " + DBHealth.DATABASE_TABLE);
		db.execSQL(DBHealth.CreateTableSql);

		db.execSQL("DROP TABLE IF EXISTS " + DBSports.DATABASE_TABLE);
		db.execSQL(DBSports.CreateTableSql);

		db.execSQL("DROP TABLE IF EXISTS " + DBRegion.DATABASE_TABLE);
		db.execSQL(DBRegion.CreateTableSql);

		db.execSQL("DROP TABLE IF EXISTS " + DBWarning.DATABASE_TABLE);
		db.execSQL(DBWarning.CreateTableSql);
		
		db.execSQL("DROP TABLE IF EXISTS " + DBFeed.DATABASE_TABLE);
		db.execSQL(DBFeed.CreateTableSql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public abstract void open();

	public abstract void close();

}
