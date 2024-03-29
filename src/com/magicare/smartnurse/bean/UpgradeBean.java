package com.magicare.smartnurse.bean;


public class UpgradeBean extends BaseBean {

	private String appName;
	private String packName;
	private int versionCode;
	private String versionName;
	private String upgradeUrl;
	
	

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackName() {
		return packName;
	}

	public UpgradeBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UpgradeBean(String appName, String packName, int versionCode,
			String versionName, String upgradeUrl) {
		super();
		this.appName = appName;
		this.packName = packName;
		this.versionCode = versionCode;
		this.versionName = versionName;
		this.upgradeUrl = upgradeUrl;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getUpgradeUrl() {
		return upgradeUrl;
	}

	public void setUpgradeUrl(String upgradeUrl) {
		this.upgradeUrl = upgradeUrl;
	}

	@Override
	public String toString() {
		return "UpgradeBean [appName=" + appName + ", packName=" + packName + ", versionCode=" + versionCode
				+ ", versionName=" + versionName + ", upgradeUrl=" + upgradeUrl + "]";
	}

}
