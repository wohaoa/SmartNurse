package com.magicare.smartnurse.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author scott
 * 
 *         Function:用户信息
 */
public class UserBean extends BaseBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;// 数据库表的id
	private int old_id;// 老人真实编号
	private String old_sn; // 老人编号
	private String name;// 老人名字
	private int age; // 老人年龄
	private String gender; // 老人性别
	private String mobile; // 手机号码
	private String bracelet_id; // 老人手环编号
	private double height; // 老人身高
	private String note; // 备注 例如：
	private String birthday;// 生日
	private String bed;// 床位
	private int child_id;// 子女id
	private String child_name;// 子女姓名
	private String child_mobile;// 子女电话号码
	private String child_relation;// 与老人的关系，子或女
	private int pension_id;// 养老院id(int)
	// 添加的预备字段
	private int nurseid;// 护士ID
	private int pension_areaid;// 区域ID
	private int battery;

	private String pension_areaname; // 区域名字

	private String sortLetters; // 显示数据拼音的首字母
	private String avatar_url;// 头像地址
	private String refreshTime;// 刷新时间
	private String currentStatus = "活动";// 当前状态
	private String currentLocation;// 当前地址

	private int isUpdatePhoto;// 上传头像

	// 监控界面添加的
	private List<WarningBean> list_warning = new ArrayList<WarningBean>();// 该老人未处理的警告
																			// 信息

	public int getIsUpdatePhoto() {
		return isUpdatePhoto;
	}

	public void setIsUpdatePhoto(int isUpdatePhoto) {
		this.isUpdatePhoto = isUpdatePhoto;
	}

	public int getBattery() {
		return battery;
	}

	public void setBattery(int battery) {
		this.battery = battery;
	}

	public int getOld_id() {
		return old_id;
	}

	public void setOld_id(int old_id) {
		this.old_id = old_id;
	}

	public List<WarningBean> getList_warning() {
		return list_warning;
	}

	public void setList_warning(List<WarningBean> list_warning) {
		this.list_warning = list_warning;
	}

	public String getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(String refreshTime) {
		this.refreshTime = refreshTime;
	}

	public String getPension_areaname() {
		return pension_areaname;
	}

	public void setPension_areaname(String pension_areaname) {
		this.pension_areaname = pension_areaname;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	public String getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOld_sn() {
		return old_sn;
	}

	public void setOld_sn(String old_sn) {
		this.old_sn = old_sn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getBracelet_id() {
		return bracelet_id;
	}

	public void setBracelet_id(String bracelet_id) {
		this.bracelet_id = bracelet_id;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getBed() {
		return bed;
	}

	public void setBed(String bed) {
		this.bed = bed;
	}

	public int getChild_id() {
		return child_id;
	}

	public void setChild_id(int child_id) {
		this.child_id = child_id;
	}

	public String getChild_name() {
		return child_name;
	}

	public void setChild_name(String child_name) {
		this.child_name = child_name;
	}

	public String getChild_mobile() {
		return child_mobile;
	}

	public void setChild_mobile(String child_mobile) {
		this.child_mobile = child_mobile;
	}

	public String getChild_relation() {
		return child_relation;
	}

	public void setChild_relation(String child_relation) {
		this.child_relation = child_relation;
	}

	public int getPension_id() {
		return pension_id;
	}

	public void setPension_id(int pension_id) {
		this.pension_id = pension_id;
	}

	public int getNurseid() {
		return nurseid;
	}

	public void setNurseid(int nurseid) {
		this.nurseid = nurseid;
	}

	public int getPension_areaid() {
		return pension_areaid;
	}

	public void setPension_areaid(int pension_areaid) {
		this.pension_areaid = pension_areaid;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getAvatar_url() {
		return avatar_url;
	}

	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}

	@Override
	public String toString() {
		return "UserBean [id=" + id + ", old_id=" + old_id + ", old_sn=" + old_sn + ", name=" + name + ", age=" + age
				+ ", gender=" + gender + ", mobile=" + mobile + ", bracelet_id=" + bracelet_id + ", height=" + height
				+ ", note=" + note + ", birthday=" + birthday + ", bed=" + bed + ", child_id=" + child_id
				+ ", child_name=" + child_name + ", child_mobile=" + child_mobile + ", child_relation="
				+ child_relation + ", pension_id=" + pension_id + ", nurseid=" + nurseid + ", pension_areaid="
				+ pension_areaid + ", pension_areaname=" + pension_areaname + ", sortLetters=" + sortLetters
				+ ", avatar_url=" + avatar_url + ", refreshTime=" + refreshTime + ", currentStatus=" + currentStatus
				+ ", currentLocation=" + currentLocation + ", isUpdatePhoto=" + isUpdatePhoto + ", list_warning="
				+ list_warning + ", battery= " + battery + "]";
	}

}
