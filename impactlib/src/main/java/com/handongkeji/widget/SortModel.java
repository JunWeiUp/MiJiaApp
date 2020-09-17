package com.handongkeji.widget;

/**
 * 报名列表的实体类
 * */
public class SortModel {

	private long registid;
	private long activityid;
	private String username;   
	private String userphone;   //手机号
	private String useraddress;  
	private String usermobile; //电话
	private String userpic;
	private String sortLetters;
	public long getRegistid() {
		return registid;
	}
	public void setRegistid(long registid) {
		this.registid = registid;
	}
	public long getActivityid() {
		return activityid;
	}
	public void setActivityid(long activityid) {
		this.activityid = activityid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserphone() {
		return userphone;
	}
	public void setUserphone(String userphone) {
		this.userphone = userphone;
	}
	public String getUseraddress() {
		return useraddress;
	}
	public void setUseraddress(String useraddress) {
		this.useraddress = useraddress;
	}
	public String getUsermobile() {
		return usermobile;
	}
	public void setUsermobile(String usermobile) {
		this.usermobile = usermobile;
	}
	public String getUserpic() {
		return userpic;
	}
	public void setUserpic(String userpic) {
		this.userpic = userpic;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	
}
