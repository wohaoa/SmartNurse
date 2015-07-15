package com.magicare.smartnurse.database.dao;

import java.util.ArrayList;
import java.util.List;

import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.magicare.smartnurse.bean.UserBean;
import com.magicare.smartnurse.bean.WarningBean;
import com.magicare.smartnurse.database.MySqliteHelper;
import com.magicare.smartnurse.utils.LogUtil;
import com.magicare.smartnurse.utils.PingYinUtil;

/**
 * 老人用户表
 * 
 * @author 波
 * 
 *         scott: 添加了用户姓名的拼音字段
 * 
 */
public class DBUser extends MySqliteHelper {

	public static String DATABASE_TABLE = "userInfo";

	public static final String CreateTableSql;

	private static SQLiteDatabase db;

	public static final String COLUMN_ID = BaseColumns._ID,

			COlUMN_USERID = "user_id", // 这是备用ID ， 和服务器的ID相同， 一般情况下，
										// oldid和_id是相同的,这里只是避免后期逻辑更改保证数据统一的字段
			COLUMN_USERSN = "user_sn",// 老人的编号
			COLUMN_NAME = "name",
			COLUMN_AGE = "age",
			COLUMN_GENDER = "gender",
			COLUMN_MOBILE = "mobile",
			COLUMN_BRACELETID = "bracelet_id",// 手环ID
			COLUMN_HEIGHT = "height",// 额外字段
			COLUMN_NOTE = "note",
			COLUMN_NURSEID = "nurse_id",// 护士ID
			COLUMN_SORT_LETTER = "sort_letter",// 字母排序字段，用户名的拼音
			COLUMN_AVATAR_URL = "avatar_url",// 字母排序字段，用户名的拼音
			COLUMN_REMARK1 = "remark1",// 备用字段，避免后期数据库改动用的，默认值为0
			COLUMN_REMARK2 = "remark2",

			COLUMN_BIRTHDAY = "birthday",
			COLUMN_BED = "bed",
			COLUMN_CHILDID = "child_id",
			COLUMN_CHILD_NAME = "child_name",
			COLUMN_CHILD_MOBILE = "child_mobile",// 子女电话
			COLUMN_CHILD_RELATION = "child_relation",// 与老人的关系，子或女
			COLUMN_PENSION_ID = "pension_id",
			COLUMN_REGIONID = "pension_areaid",// 区域ID
			COLUMN_REGIONNAME = "pension_areaname",
			COLUMN_REFRESHTIME = "refresh_time",// 刷新时间
			COLUMN_CURRENT_STATUS = "current_status",// 当前状态
			COLUMN_CURRENT_LOCATION = "current_location",// 当前位置
			COlUMN_ISUPDATE_PHOTO = "isupdate_photo";// 上传头像

	public static final String[] dispColumns = { COLUMN_ID, COlUMN_USERID,
			COLUMN_USERSN, COLUMN_NAME, COLUMN_AGE, COLUMN_GENDER,
			COLUMN_MOBILE, COLUMN_BIRTHDAY, COLUMN_BED, COLUMN_CHILDID,
			COLUMN_CHILD_NAME, COLUMN_CHILD_MOBILE, COLUMN_CHILD_RELATION,
			COLUMN_PENSION_ID, COLUMN_BRACELETID, COLUMN_HEIGHT, COLUMN_NOTE,
			COLUMN_NURSEID, COLUMN_REGIONID, COLUMN_REGIONNAME,
			COLUMN_CHILD_RELATION, COLUMN_SORT_LETTER, COLUMN_AVATAR_URL,
			COLUMN_REFRESHTIME, COLUMN_CURRENT_STATUS, COLUMN_CURRENT_LOCATION,
			COlUMN_ISUPDATE_PHOTO };

	static {
		StringBuilder strSql = new StringBuilder();
		strSql.append("CREATE TABLE " + " IF NOT EXISTS " + DATABASE_TABLE
				+ " (");
		strSql.append(COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,");
		strSql.append(COlUMN_USERID + " NVARCHAR(100) , ");
		strSql.append(COLUMN_USERSN + " NVARCHAR(100) , ");
		strSql.append(COLUMN_NAME + " NVARCHAR(100) , ");
		strSql.append(COLUMN_AGE + " INTEGER ,");
		strSql.append(COLUMN_GENDER + " NVARCHAR(10) ,");
		strSql.append(COLUMN_MOBILE + " NVARCHAR(100) ,");
		strSql.append(COLUMN_BIRTHDAY + " NVARCHAR(100) ,");
		strSql.append(COLUMN_BED + " NVARCHAR(100) ,");
		strSql.append(COLUMN_BRACELETID + " NVARCHAR(100) ,");
		strSql.append(COLUMN_HEIGHT + " FLOAT , ");
		strSql.append(COLUMN_NOTE + " NVARCHAR(100) ,");
		strSql.append(COLUMN_SORT_LETTER + " NVARCHAR(30) ,");
		strSql.append(COLUMN_AVATAR_URL + " NVARCHAR(100) , ");
		strSql.append(COLUMN_CHILDID + " INTEGER ,");
		strSql.append(COLUMN_CHILD_NAME + " NVARCHAR(100) ,");
		strSql.append(COLUMN_CHILD_MOBILE + " NVARCHAR(100) ,");
		strSql.append(COLUMN_CHILD_RELATION + " NVARCHAR(100) ,");
		strSql.append(COLUMN_PENSION_ID + " INTEGER ,");
		strSql.append(COLUMN_REGIONID + " INTEGER ,");
		strSql.append(COLUMN_REGIONNAME + " NVARCHAR(100) ,");
		strSql.append(COLUMN_NURSEID + " NVARCHAR(100) ,");
		strSql.append(COLUMN_REFRESHTIME + " NVARCHAR(100) , ");
		strSql.append(COLUMN_CURRENT_STATUS + " NVARCHAR(100) , ");
		strSql.append(COLUMN_CURRENT_LOCATION + " NVARCHAR(100) , ");
		strSql.append(COlUMN_ISUPDATE_PHOTO + " INTEGER , ");
		strSql.append(COLUMN_REMARK1 + " NVARCHAR(100) ,");
		strSql.append(COLUMN_REMARK2 + " NVARCHAR(100) ");

		strSql.append(" ) ;");
		CreateTableSql = strSql.toString();
	}

	private static DBUser dbApp = null;

	public static synchronized DBUser getInstance(Context context) {
		if (dbApp == null) {
			dbApp = new DBUser(context);
		}
		return dbApp;
	}

	private DBUser(Context context) {
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
	public long insert(UserBean bean) {
		long count = 0;
		if (!isExistUser(bean.getOld_id())) {
			ContentValues values = new ContentValues();
			values.put(COlUMN_USERID, bean.getOld_id());
			values.put(COLUMN_USERSN, bean.getOld_sn());
			values.put(COLUMN_NAME, bean.getName());
			values.put(COLUMN_AGE, bean.getAge());
			values.put(COLUMN_GENDER, bean.getGender());
			values.put(COLUMN_MOBILE, bean.getMobile());
			values.put(COLUMN_BIRTHDAY, bean.getBirthday());
			values.put(COLUMN_BED, bean.getBed());
			values.put(COLUMN_BRACELETID, bean.getBracelet_id());
			values.put(COLUMN_HEIGHT, bean.getHeight());
			values.put(COLUMN_NOTE, bean.getNote());
			values.put(COLUMN_SORT_LETTER, PingYinUtil.cn2py(bean.getName()));
			values.put(COLUMN_AVATAR_URL, bean.getAvatar_url());
			values.put(COLUMN_CHILDID, bean.getChild_id());
			values.put(COLUMN_CHILD_NAME, bean.getChild_name());
			values.put(COLUMN_CHILD_MOBILE, bean.getChild_mobile());
			values.put(COLUMN_CHILD_RELATION, bean.getChild_relation());
			values.put(COLUMN_PENSION_ID, bean.getPension_id());
			values.put(COLUMN_REGIONID, bean.getPension_areaid());
			values.put(COLUMN_REGIONNAME, bean.getPension_areaname());
			values.put(COLUMN_NURSEID, bean.getNurseid());
			values.put(COLUMN_REFRESHTIME, bean.getRefreshTime());
			values.put(COLUMN_CURRENT_STATUS, bean.getCurrentStatus());
			values.put(COLUMN_CURRENT_LOCATION, bean.getCurrentLocation());
			values.put(COlUMN_ISUPDATE_PHOTO, bean.getIsUpdatePhoto());
			count = db.insert(DATABASE_TABLE, null, values);
		} else {
			updateOldInfo(bean);
		}
		return count;
	}

	public long insert(List<UserBean> list) {
		long count = 0;
		beginTransaction();
		try {
			for (int i = 0; i < list.size(); i++) {
				UserBean bean = list.get(i);
				if (!isExistUser(bean.getOld_id())) {
					ContentValues values = new ContentValues();
					values.put(COlUMN_USERID, bean.getOld_id());
					values.put(COLUMN_USERSN, bean.getOld_sn());
					values.put(COLUMN_NAME, bean.getName());
					values.put(COLUMN_AGE, bean.getAge());
					values.put(COLUMN_GENDER, bean.getGender());
					values.put(COLUMN_MOBILE, bean.getMobile());
					values.put(COLUMN_BIRTHDAY, bean.getBirthday());
					values.put(COLUMN_BED, bean.getBed());
					values.put(COLUMN_BRACELETID, bean.getBracelet_id());
					values.put(COLUMN_HEIGHT, bean.getHeight());
					values.put(COLUMN_NOTE, bean.getNote());
					values.put(COLUMN_SORT_LETTER,
							PingYinUtil.cn2py(bean.getName()));
					values.put(COLUMN_AVATAR_URL, bean.getAvatar_url());
					values.put(COLUMN_CHILDID, bean.getChild_id());
					values.put(COLUMN_CHILD_NAME, bean.getChild_name());
					values.put(COLUMN_CHILD_MOBILE, bean.getChild_mobile());
					values.put(COLUMN_CHILD_RELATION, bean.getChild_relation());
					values.put(COLUMN_PENSION_ID, bean.getPension_id());
					values.put(COLUMN_REGIONID, bean.getPension_areaid());
					values.put(COLUMN_REGIONNAME, bean.getPension_areaname());
					values.put(COLUMN_NURSEID, bean.getNurseid());
					values.put(COLUMN_REFRESHTIME, bean.getRefreshTime());
					values.put(COLUMN_CURRENT_STATUS, bean.getCurrentStatus());
					values.put(COLUMN_CURRENT_LOCATION,
							bean.getCurrentLocation());
					values.put(COlUMN_ISUPDATE_PHOTO, bean.getIsUpdatePhoto());
					count += db.insert(DATABASE_TABLE, null, values);
				} else {
					updateOldInfo(bean);
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
	public boolean isExistUser(int old_id) {
		if (db != null) {
			Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "
					+ DATABASE_TABLE + " where " + COlUMN_USERID + " = ? ",
					new String[] { old_id + "" });
			if (cursor != null) {
				if (cursor.moveToPosition(0)) {
					return cursor.getInt(0) == 0 ? false : true;
				}
				cursor.close();
			}
		}
		return false;
	}

	public long updateOldInfo(UserBean bean) {
		long count = 0;
		ContentValues values = new ContentValues();
		values.put(COlUMN_USERID, bean.getOld_id());
		values.put(COLUMN_USERSN, bean.getOld_sn());
		values.put(COLUMN_NAME, bean.getName());
		values.put(COLUMN_AGE, bean.getAge());
		values.put(COLUMN_GENDER, bean.getGender());
		values.put(COLUMN_MOBILE, bean.getMobile());
		values.put(COLUMN_BIRTHDAY, bean.getBirthday());
		values.put(COLUMN_BED, bean.getBed());
		values.put(COLUMN_BRACELETID, bean.getBracelet_id());
		values.put(COLUMN_HEIGHT, bean.getHeight());
		values.put(COLUMN_NOTE, bean.getNote());
		values.put(COLUMN_SORT_LETTER, PingYinUtil.cn2py(bean.getName()));
		values.put(COLUMN_AVATAR_URL, bean.getAvatar_url());
		values.put(COLUMN_CHILDID, bean.getChild_id());
		values.put(COLUMN_CHILD_NAME, bean.getChild_name());
		values.put(COLUMN_CHILD_MOBILE, bean.getChild_mobile());
		values.put(COLUMN_CHILD_RELATION, bean.getChild_relation());
		values.put(COLUMN_PENSION_ID, bean.getPension_id());
		values.put(COLUMN_REGIONID, bean.getPension_areaid());
		values.put(COLUMN_REGIONNAME, bean.getPension_areaname());
		values.put(COLUMN_NURSEID, bean.getNurseid());
		values.put(COLUMN_REFRESHTIME, bean.getRefreshTime());
		values.put(COLUMN_CURRENT_STATUS, bean.getCurrentStatus());
		values.put(COLUMN_CURRENT_LOCATION, bean.getCurrentLocation());
		values.put(COlUMN_ISUPDATE_PHOTO, bean.getIsUpdatePhoto());
		count = db.update(DATABASE_TABLE, values, COlUMN_USERID + "=? ",
				new String[] { bean.getOld_id() + "" });
		return count;
	}

	public long delete(UserBean bean) {
		int count = db.delete(DATABASE_TABLE, COlUMN_USERID + "=? ",
				new String[] { bean.getOld_id() + "" });
		return count;
	}

	public long deleteAll() {
		int count = db.delete(DATABASE_TABLE, null, null);
		return count;
	}

	public long updateUpdatePhotoStatus(UserBean bean) {
		long count = 0;
		LogUtil.info("smarhit", "IsUpdatePhoto=" + bean.getIsUpdatePhoto());
		ContentValues values = new ContentValues();
		values.put(COLUMN_AVATAR_URL, bean.getAvatar_url());
		values.put(COlUMN_ISUPDATE_PHOTO, bean.getIsUpdatePhoto());
		count = db.update(DATABASE_TABLE, values, COlUMN_USERID + "=? ",
				new String[] { bean.getOld_id() + "" });
		return count;
	}

	/**
	 * 
	 * Function:获取未上传头像的用户信息
	 * 
	 * @param updateStatus
	 *            ：上传状态：0表示未上传，1表示已经上传
	 * @return
	 */
	public List<UserBean> getUpdatePhotoInfo(int updateStatus) {
		List<UserBean> list = null;
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, new String[] { COLUMN_ID,
					COlUMN_USERID, COLUMN_USERSN, COLUMN_AVATAR_URL,
					COlUMN_ISUPDATE_PHOTO }, COlUMN_ISUPDATE_PHOTO + "=? ",
					new String[] { updateStatus + "" }, null, null, null);
			if (cursor == null) {
				return null;
			}
			int count = cursor.getCount();
			UserBean bean = null;
			list = new ArrayList<UserBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {
					bean = new UserBean();
					bean.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
					bean.setOld_id(cursor.getInt(cursor
							.getColumnIndex(COlUMN_USERID)));
					bean.setOld_sn(cursor.getString(cursor
							.getColumnIndex(COLUMN_USERSN)));
					bean.setAvatar_url(cursor.getString(cursor
							.getColumnIndex(COLUMN_AVATAR_URL)));
					bean.setIsUpdatePhoto(cursor.getInt(cursor
							.getColumnIndex(COlUMN_ISUPDATE_PHOTO)));
					list.add(bean);
				}
			}
			cursor.close();
		}
		return list;
	}

	/**
	 * 通过老人 编号获取老人信息
	 * 
	 * @param oldsn
	 *            老人编号
	 * @return
	 */
	public UserBean getUserInfoById(String oldId) {
		Cursor cursor = db
				.query(DATABASE_TABLE, dispColumns, COlUMN_USERID + "=? ",
						new String[] { oldId }, null, null, COLUMN_SORT_LETTER);
		if (cursor == null) {
			return null;
		}
		UserBean bean = null;
		if (cursor.moveToPosition(0)) {
			bean = new UserBean();
			bean.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
			bean.setOld_id(cursor.getInt(cursor.getColumnIndex(COlUMN_USERID)));
			bean.setOld_sn(cursor.getString(cursor
					.getColumnIndex(COLUMN_USERSN)));
			bean.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
			bean.setAge(cursor.getInt(cursor.getColumnIndex(COLUMN_AGE)));
			bean.setGender(cursor.getString(cursor
					.getColumnIndex(COLUMN_GENDER)));
			bean.setMobile(cursor.getString(cursor
					.getColumnIndex(COLUMN_MOBILE)));

			bean.setBirthday(cursor.getString(cursor
					.getColumnIndex(COLUMN_BIRTHDAY)));
			bean.setBed(cursor.getString(cursor.getColumnIndex(COLUMN_BED)));
			bean.setBracelet_id(cursor.getString(cursor
					.getColumnIndex(COLUMN_BRACELETID)));
			bean.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
			bean.setHeight(cursor.getDouble(cursor
					.getColumnIndex(COLUMN_HEIGHT)));
			bean.setSortLetters(cursor.getString(cursor
					.getColumnIndex(COLUMN_SORT_LETTER)));
			bean.setAvatar_url(cursor.getString(cursor
					.getColumnIndex(COLUMN_AVATAR_URL)));

			bean.setChild_id(cursor.getInt(cursor
					.getColumnIndex(COLUMN_CHILDID)));
			bean.setChild_name(cursor.getString(cursor
					.getColumnIndex(COLUMN_CHILD_NAME)));
			bean.setChild_mobile(cursor.getString(cursor
					.getColumnIndex(COLUMN_CHILD_MOBILE)));
			bean.setChild_relation(cursor.getString(cursor
					.getColumnIndex(COLUMN_CHILD_RELATION)));
			bean.setPension_id(cursor.getInt(cursor
					.getColumnIndex(COLUMN_PENSION_ID)));
			bean.setPension_areaname(cursor.getString(cursor
					.getColumnIndex(COLUMN_REGIONNAME)));
			bean.setPension_areaid(cursor.getInt(cursor
					.getColumnIndex(COLUMN_REGIONID)));
			bean.setNurseid(cursor.getInt(cursor.getColumnIndex(COLUMN_NURSEID)));
			bean.setRefreshTime(cursor.getString(cursor
					.getColumnIndex(COLUMN_REFRESHTIME)));
			bean.setCurrentStatus(cursor.getString(cursor
					.getColumnIndex(COLUMN_CURRENT_STATUS)));
			bean.setCurrentLocation(cursor.getString(cursor
					.getColumnIndex(COLUMN_CURRENT_LOCATION)));
			bean.setIsUpdatePhoto(cursor.getInt(cursor
					.getColumnIndex(COlUMN_ISUPDATE_PHOTO)));
		}
		cursor.close();
		return bean;
	}

	/**
	 * 通过老人手环id
	 * 
	 * @param Braceletid
	 * @return
	 */
	public UserBean getUserInfoByBraceleId(String Braceletid) {
		Cursor cursor = db.query(DATABASE_TABLE, dispColumns, COLUMN_BRACELETID
				+ "=? ", new String[] { Braceletid }, null, null,
				COLUMN_SORT_LETTER);
		if (cursor == null) {
			return null;
		}
		UserBean bean = null;
		if (cursor.moveToPosition(0)) {
			bean = new UserBean();
			bean.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
			bean.setOld_id(cursor.getInt(cursor.getColumnIndex(COlUMN_USERID)));
			bean.setOld_sn(cursor.getString(cursor
					.getColumnIndex(COLUMN_USERSN)));
			bean.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
			bean.setAge(cursor.getInt(cursor.getColumnIndex(COLUMN_AGE)));
			bean.setGender(cursor.getString(cursor
					.getColumnIndex(COLUMN_GENDER)));
			bean.setMobile(cursor.getString(cursor
					.getColumnIndex(COLUMN_MOBILE)));

			bean.setBirthday(cursor.getString(cursor
					.getColumnIndex(COLUMN_BIRTHDAY)));
			bean.setBed(cursor.getString(cursor.getColumnIndex(COLUMN_BED)));
			bean.setBracelet_id(cursor.getString(cursor
					.getColumnIndex(COLUMN_BRACELETID)));
			bean.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
			bean.setHeight(cursor.getDouble(cursor
					.getColumnIndex(COLUMN_HEIGHT)));
			bean.setSortLetters(cursor.getString(cursor
					.getColumnIndex(COLUMN_SORT_LETTER)));
			bean.setAvatar_url(cursor.getString(cursor
					.getColumnIndex(COLUMN_AVATAR_URL)));

			bean.setChild_id(cursor.getInt(cursor
					.getColumnIndex(COLUMN_CHILDID)));
			bean.setChild_name(cursor.getString(cursor
					.getColumnIndex(COLUMN_CHILD_NAME)));
			bean.setChild_mobile(cursor.getString(cursor
					.getColumnIndex(COLUMN_CHILD_MOBILE)));
			bean.setChild_relation(cursor.getString(cursor
					.getColumnIndex(COLUMN_CHILD_RELATION)));
			bean.setPension_id(cursor.getInt(cursor
					.getColumnIndex(COLUMN_PENSION_ID)));
			bean.setPension_areaid(cursor.getInt(cursor
					.getColumnIndex(COLUMN_REGIONID)));
			bean.setPension_areaname(cursor.getString(cursor
					.getColumnIndex(COLUMN_REGIONNAME)));
			bean.setNurseid(cursor.getInt(cursor.getColumnIndex(COLUMN_NURSEID)));
			bean.setRefreshTime(cursor.getString(cursor
					.getColumnIndex(COLUMN_REFRESHTIME)));
			bean.setCurrentStatus(cursor.getString(cursor
					.getColumnIndex(COLUMN_CURRENT_STATUS)));
			bean.setCurrentLocation(cursor.getString(cursor
					.getColumnIndex(COLUMN_CURRENT_LOCATION)));
			bean.setIsUpdatePhoto(cursor.getInt(cursor
					.getColumnIndex(COlUMN_ISUPDATE_PHOTO)));
		}
		cursor.close();
		return bean;
	}

	public UserBean getUserInfoByIdOrBraceleId(String id) {
		Cursor cursor = db.query(DATABASE_TABLE, dispColumns, COLUMN_USERSN
				+ "=? or " + COLUMN_BRACELETID + "=? ",
				new String[] { id, id }, null, null, COLUMN_SORT_LETTER);
		if (cursor == null) {
			return null;
		}
		UserBean bean = null;
		if (cursor.moveToPosition(0)) {
			bean = new UserBean();
			bean.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
			bean.setOld_id(cursor.getInt(cursor.getColumnIndex(COlUMN_USERID)));
			bean.setOld_sn(cursor.getString(cursor
					.getColumnIndex(COLUMN_USERSN)));
			bean.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
			bean.setAge(cursor.getInt(cursor.getColumnIndex(COLUMN_AGE)));
			bean.setGender(cursor.getString(cursor
					.getColumnIndex(COLUMN_GENDER)));
			bean.setMobile(cursor.getString(cursor
					.getColumnIndex(COLUMN_MOBILE)));

			bean.setBirthday(cursor.getString(cursor
					.getColumnIndex(COLUMN_BIRTHDAY)));
			bean.setBed(cursor.getString(cursor.getColumnIndex(COLUMN_BED)));
			bean.setBracelet_id(cursor.getString(cursor
					.getColumnIndex(COLUMN_BRACELETID)));
			bean.setNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
			bean.setHeight(cursor.getDouble(cursor
					.getColumnIndex(COLUMN_HEIGHT)));
			bean.setSortLetters(cursor.getString(cursor
					.getColumnIndex(COLUMN_SORT_LETTER)));
			bean.setAvatar_url(cursor.getString(cursor
					.getColumnIndex(COLUMN_AVATAR_URL)));

			bean.setChild_id(cursor.getInt(cursor
					.getColumnIndex(COLUMN_CHILDID)));
			bean.setChild_name(cursor.getString(cursor
					.getColumnIndex(COLUMN_CHILD_NAME)));
			bean.setChild_mobile(cursor.getString(cursor
					.getColumnIndex(COLUMN_CHILD_MOBILE)));
			bean.setChild_relation(cursor.getString(cursor
					.getColumnIndex(COLUMN_CHILD_RELATION)));
			bean.setPension_id(cursor.getInt(cursor
					.getColumnIndex(COLUMN_PENSION_ID)));
			bean.setPension_areaid(cursor.getInt(cursor
					.getColumnIndex(COLUMN_REGIONID)));
			bean.setPension_areaname(cursor.getString(cursor
					.getColumnIndex(COLUMN_REGIONNAME)));
			bean.setNurseid(cursor.getInt(cursor.getColumnIndex(COLUMN_NURSEID)));
			bean.setRefreshTime(cursor.getString(cursor
					.getColumnIndex(COLUMN_REFRESHTIME)));
			bean.setCurrentStatus(cursor.getString(cursor
					.getColumnIndex(COLUMN_CURRENT_STATUS)));
			bean.setCurrentLocation(cursor.getString(cursor
					.getColumnIndex(COLUMN_CURRENT_LOCATION)));
			bean.setIsUpdatePhoto(cursor.getInt(cursor
					.getColumnIndex(COlUMN_ISUPDATE_PHOTO)));
		}
		cursor.close();
		return bean;
	}

	/**
	 * 
	 * Function:获取所有的老人信息
	 * 
	 * @return
	 */
	public List<UserBean> getAllUserInfo() {
		List<UserBean> list = null;
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, null, null,
					null, null, COLUMN_SORT_LETTER);
			if (cursor == null) {
				return null;
			}
			int count = cursor.getCount();
			UserBean bean = null;
			list = new ArrayList<UserBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {
					bean = new UserBean();
					bean.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
					bean.setOld_id(cursor.getInt(cursor
							.getColumnIndex(COlUMN_USERID)));
					bean.setOld_sn(cursor.getString(cursor
							.getColumnIndex(COLUMN_USERSN)));
					bean.setName(cursor.getString(cursor
							.getColumnIndex(COLUMN_NAME)));
					bean.setAge(cursor.getInt(cursor.getColumnIndex(COLUMN_AGE)));
					bean.setGender(cursor.getString(cursor
							.getColumnIndex(COLUMN_GENDER)));
					bean.setMobile(cursor.getString(cursor
							.getColumnIndex(COLUMN_MOBILE)));

					bean.setBirthday(cursor.getString(cursor
							.getColumnIndex(COLUMN_BIRTHDAY)));
					bean.setBed(cursor.getString(cursor
							.getColumnIndex(COLUMN_BED)));
					bean.setBracelet_id(cursor.getString(cursor
							.getColumnIndex(COLUMN_BRACELETID)));
					bean.setNote(cursor.getString(cursor
							.getColumnIndex(COLUMN_NOTE)));
					bean.setHeight(cursor.getDouble(cursor
							.getColumnIndex(COLUMN_HEIGHT)));
					bean.setSortLetters(cursor.getString(cursor
							.getColumnIndex(COLUMN_SORT_LETTER)));
					bean.setAvatar_url(cursor.getString(cursor
							.getColumnIndex(COLUMN_AVATAR_URL)));

					bean.setChild_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_CHILDID)));
					bean.setChild_name(cursor.getString(cursor
							.getColumnIndex(COLUMN_CHILD_NAME)));
					bean.setChild_mobile(cursor.getString(cursor
							.getColumnIndex(COLUMN_CHILD_MOBILE)));
					bean.setChild_relation(cursor.getString(cursor
							.getColumnIndex(COLUMN_CHILD_RELATION)));
					bean.setPension_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_PENSION_ID)));
					bean.setPension_areaname(cursor.getString(cursor
							.getColumnIndex(COLUMN_REGIONNAME)));
					bean.setPension_areaid(cursor.getInt(cursor
							.getColumnIndex(COLUMN_REGIONID)));
					bean.setNurseid(cursor.getInt(cursor
							.getColumnIndex(COLUMN_NURSEID)));
					bean.setRefreshTime(cursor.getString(cursor
							.getColumnIndex(COLUMN_REFRESHTIME)));
					bean.setCurrentStatus(cursor.getString(cursor
							.getColumnIndex(COLUMN_CURRENT_STATUS)));
					bean.setCurrentLocation(cursor.getString(cursor
							.getColumnIndex(COLUMN_CURRENT_LOCATION)));
					bean.setIsUpdatePhoto(cursor.getInt(cursor
							.getColumnIndex(COlUMN_ISUPDATE_PHOTO)));
					list.add(bean);
				}
			}
			cursor.close();
		}
		return list;
	}

	/**
	 * 
	 * Function:获取所有的老人信息和警告信息
	 * 
	 * @return
	 */
	public List<UserBean> getAllUserAndWarningInfo() {
		List<UserBean> list = null;
		if (db != null) {
			Cursor cursor = db.query(DATABASE_TABLE, dispColumns, null, null,
					null, null, COLUMN_SORT_LETTER);
			if (cursor == null) {
				return null;
			}
			int count = cursor.getCount();
			UserBean bean = null;
			list = new ArrayList<UserBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {
					bean = new UserBean();
					bean.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
					bean.setOld_id(cursor.getInt(cursor
							.getColumnIndex(COlUMN_USERID)));
					bean.setOld_sn(cursor.getString(cursor
							.getColumnIndex(COLUMN_USERSN)));
					bean.setName(cursor.getString(cursor
							.getColumnIndex(COLUMN_NAME)));
					bean.setAge(cursor.getInt(cursor.getColumnIndex(COLUMN_AGE)));
					bean.setGender(cursor.getString(cursor
							.getColumnIndex(COLUMN_GENDER)));
					bean.setMobile(cursor.getString(cursor
							.getColumnIndex(COLUMN_MOBILE)));

					bean.setBirthday(cursor.getString(cursor
							.getColumnIndex(COLUMN_BIRTHDAY)));
					bean.setBed(cursor.getString(cursor
							.getColumnIndex(COLUMN_BED)));
					bean.setBracelet_id(cursor.getString(cursor
							.getColumnIndex(COLUMN_BRACELETID)));
					bean.setNote(cursor.getString(cursor
							.getColumnIndex(COLUMN_NOTE)));
					bean.setHeight(cursor.getDouble(cursor
							.getColumnIndex(COLUMN_HEIGHT)));
					bean.setSortLetters(cursor.getString(cursor
							.getColumnIndex(COLUMN_SORT_LETTER)));
					bean.setAvatar_url(cursor.getString(cursor
							.getColumnIndex(COLUMN_AVATAR_URL)));

					bean.setChild_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_CHILDID)));
					bean.setChild_name(cursor.getString(cursor
							.getColumnIndex(COLUMN_CHILD_NAME)));
					bean.setChild_mobile(cursor.getString(cursor
							.getColumnIndex(COLUMN_CHILD_MOBILE)));
					bean.setChild_relation(cursor.getString(cursor
							.getColumnIndex(COLUMN_CHILD_RELATION)));
					bean.setPension_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_PENSION_ID)));
					bean.setPension_areaid(cursor.getInt(cursor
							.getColumnIndex(COLUMN_REGIONID)));
					bean.setPension_areaname(cursor.getString(cursor
							.getColumnIndex(COLUMN_REGIONNAME)));
					bean.setNurseid(cursor.getInt(cursor
							.getColumnIndex(COLUMN_NURSEID)));
					bean.setRefreshTime(cursor.getString(cursor
							.getColumnIndex(COLUMN_REFRESHTIME)));
					bean.setCurrentStatus(cursor.getString(cursor
							.getColumnIndex(COLUMN_CURRENT_STATUS)));
					bean.setCurrentLocation(cursor.getString(cursor
							.getColumnIndex(COLUMN_CURRENT_LOCATION)));
					bean.setIsUpdatePhoto(cursor.getInt(cursor
							.getColumnIndex(COlUMN_ISUPDATE_PHOTO)));
					bean.setList_warning(getWarningInfoByOldid(bean.getOld_id()
							+ ""));
					list.add(bean);
				}
			}
			cursor.close();
		}
		return list;
	}

	private List<WarningBean> getWarningInfoByOldid(String oldId) {
		List<WarningBean> list = null;
		if (db != null) {
			Cursor dbcCursor = db.query(DBWarning.DATABASE_TABLE,
					DBWarning.dispColumns, DBWarning.COLUMN_USERID
							+ " = ? and " + DBWarning.COLUMN_STATUS + "!=2",
					new String[] { oldId }, null, null, DBWarning.COLUMN_TYPE
							+ " asc");
			if (dbcCursor == null) {
				return null;
			}
			int count = dbcCursor.getCount();
			WarningBean bean = null;
			list = new ArrayList<WarningBean>();
			for (int i = 0; i < count; i++) {
				if (dbcCursor.moveToPosition(i)) {
					bean = new WarningBean();
					bean.setId(dbcCursor.getInt(dbcCursor
							.getColumnIndex(COLUMN_ID)));
					bean.setAlarm_id(dbcCursor.getString(dbcCursor
							.getColumnIndex(DBWarning.COLUMN_WARNING_ID)));
					bean.setAlarm_time(dbcCursor.getString(dbcCursor
							.getColumnIndex(DBWarning.COLUMN_REPORT_TIME)));
					bean.setStation_detail(dbcCursor.getString(dbcCursor
							.getColumnIndex(DBWarning.COLUMN_DETAIL)));
					bean.setAlarm_type(dbcCursor.getInt(dbcCursor
							.getColumnIndex(DBWarning.COLUMN_TYPE)));
					bean.setAlarm_status(dbcCursor.getInt(dbcCursor
							.getColumnIndex(DBWarning.COLUMN_STATUS)));
					bean.setResolve_time(dbcCursor.getString(dbcCursor
							.getColumnIndex(DBWarning.COLUMN_RESOLVE_TIME)));
					bean.setFeed_time(dbcCursor.getString(dbcCursor
							.getColumnIndex(DBWarning.COLUMN_FEED_TIME)));
					bean.setFeed_nurse(dbcCursor.getString(dbcCursor
							.getColumnIndex(DBWarning.COLUMN_FEED_NURSE)));
					bean.setFeedback(dbcCursor.getString(dbcCursor
							.getColumnIndex(DBWarning.COLUMN_FEED_BACK)));
					bean.setOld_id(dbcCursor.getInt(dbcCursor
							.getColumnIndex(DBWarning.COLUMN_USERID)));
					bean.setOld_sn(dbcCursor.getString(dbcCursor
							.getColumnIndex(DBWarning.COLUMN_USERSN)));
					bean.setOld_name(dbcCursor.getString(dbcCursor
							.getColumnIndex(DBWarning.COLUMN_USERNAME)));
					bean.setNurse_id(dbcCursor.getString(dbcCursor
							.getColumnIndex(COLUMN_NURSEID)));
					list.add(bean);
				}
			}
			dbcCursor.close();
		}
		return list;
	}

	public boolean updateUserCurrentStatus(UserBean bean) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_REFRESHTIME, bean.getRefreshTime());
		values.put(COLUMN_CURRENT_STATUS, bean.getCurrentStatus());
		values.put(COLUMN_CURRENT_LOCATION, bean.getCurrentLocation());
		int count = db.update(DATABASE_TABLE, values, COlUMN_USERID + "=? ",
				new String[] { bean.getOld_id() + "" });
		return count > 0 ? true : false;
	}

	public List<UserBean> getUserInfoByNameOrId(String idOrName) {
		List<UserBean> list = null;
		if (db != null) {
			Cursor cursor = null;
			if (!TextUtils.isEmpty(idOrName)) {
				cursor = db.query(DATABASE_TABLE, dispColumns, COLUMN_USERSN
						+ "= ? or " + COLUMN_NAME + " like ? ", new String[] {
						idOrName, "%" + idOrName + "%" }, null, null,
						COLUMN_SORT_LETTER);
			} else {
				cursor = db.query(DATABASE_TABLE, dispColumns, null, null,
						null, null, COLUMN_SORT_LETTER);
			}
			if (cursor == null) {
				return null;
			}
			int count = cursor.getCount();
			UserBean bean = null;
			list = new ArrayList<UserBean>();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToPosition(i)) {
					bean = new UserBean();
					bean.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
					bean.setOld_id(cursor.getInt(cursor
							.getColumnIndex(COlUMN_USERID)));
					bean.setOld_sn(cursor.getString(cursor
							.getColumnIndex(COLUMN_USERSN)));
					bean.setName(cursor.getString(cursor
							.getColumnIndex(COLUMN_NAME)));
					bean.setAge(cursor.getInt(cursor.getColumnIndex(COLUMN_AGE)));
					bean.setGender(cursor.getString(cursor
							.getColumnIndex(COLUMN_GENDER)));
					bean.setMobile(cursor.getString(cursor
							.getColumnIndex(COLUMN_MOBILE)));

					bean.setBirthday(cursor.getString(cursor
							.getColumnIndex(COLUMN_BIRTHDAY)));
					bean.setBed(cursor.getString(cursor
							.getColumnIndex(COLUMN_BED)));
					bean.setBracelet_id(cursor.getString(cursor
							.getColumnIndex(COLUMN_BRACELETID)));
					bean.setNote(cursor.getString(cursor
							.getColumnIndex(COLUMN_NOTE)));
					bean.setHeight(cursor.getDouble(cursor
							.getColumnIndex(COLUMN_HEIGHT)));
					bean.setSortLetters(cursor.getString(cursor
							.getColumnIndex(COLUMN_SORT_LETTER)));
					bean.setAvatar_url(cursor.getString(cursor
							.getColumnIndex(COLUMN_AVATAR_URL)));

					bean.setChild_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_CHILDID)));
					bean.setChild_name(cursor.getString(cursor
							.getColumnIndex(COLUMN_CHILD_NAME)));
					bean.setChild_mobile(cursor.getString(cursor
							.getColumnIndex(COLUMN_CHILD_MOBILE)));
					bean.setChild_relation(cursor.getString(cursor
							.getColumnIndex(COLUMN_CHILD_RELATION)));
					bean.setPension_id(cursor.getInt(cursor
							.getColumnIndex(COLUMN_PENSION_ID)));
					bean.setPension_areaid(cursor.getInt(cursor
							.getColumnIndex(COLUMN_REGIONID)));
					bean.setPension_areaname(cursor.getString(cursor
							.getColumnIndex(COLUMN_REGIONNAME)));
					bean.setNurseid(cursor.getInt(cursor
							.getColumnIndex(COLUMN_NURSEID)));
					bean.setRefreshTime(cursor.getString(cursor
							.getColumnIndex(COLUMN_REFRESHTIME)));
					bean.setCurrentStatus(cursor.getString(cursor
							.getColumnIndex(COLUMN_CURRENT_STATUS)));
					bean.setCurrentLocation(cursor.getString(cursor
							.getColumnIndex(COLUMN_CURRENT_LOCATION)));
					bean.setIsUpdatePhoto(cursor.getInt(cursor
							.getColumnIndex(COlUMN_ISUPDATE_PHOTO)));
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