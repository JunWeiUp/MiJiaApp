package com.handongkeji.modle;

public class AdModle {
	private int adid ;
	private int areaid ;
	private String adtitle ;
	private int adpostion ;
	private int adtype ;
	private String adcontents ;
	private String adpicture ;
	private String accountid ;
	public int getAdid() {
		return adid;
	}
	public void setAdid(int adid) {
		this.adid = adid;
	}
	public int getAreaid() {
		return areaid;
	}
	public void setAreaid(int areaid) {
		this.areaid = areaid;
	}
	public String getAdtitle() {
		return adtitle;
	}
	public void setAdtitle(String adtitle) {
		this.adtitle = adtitle;
	}
	public int getAdpostion() {
		return adpostion;
	}
	public void setAdpostion(int adpostion) {
		this.adpostion = adpostion;
	}
	public int getAdtype() {
		return adtype;
	}
	public void setAdtype(int adtype) {
		this.adtype = adtype;
	}
	public String getAdcontents() {
		return adcontents;
	}
	public void setAdcontents(String adcontents) {
		this.adcontents = adcontents;
	}
	public String getAdpicture() {
		return adpicture;
	}
	public void setAdpicture(String adpicture) {
		this.adpicture = adpicture;
	}
	public String getAccountid() {
		return accountid;
	}
	public void setAccountid(String accountid) {
		this.accountid = accountid;
	}
	public AdModle(int adid, int areaid, String adtitle, int adpostion,
                   int adtype, String adcontents, String adpicture, String accountid) {
		super();
		this.adid = adid;
		this.areaid = areaid;
		this.adtitle = adtitle;
		this.adpostion = adpostion;
		this.adtype = adtype;
		this.adcontents = adcontents;
		this.adpicture = adpicture;
		this.accountid = accountid;
	}
	public AdModle() {
		super();
	}
	
}
