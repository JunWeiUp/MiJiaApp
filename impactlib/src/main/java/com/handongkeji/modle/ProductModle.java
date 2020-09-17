package com.handongkeji.modle;

public class ProductModle {

	private int productid;
	private String productname;
	private String producthead;
	private double productprice;
	private int productposition;
	private int producttype;
	private String productbrief;
	private String productdesc;
	private String producturl;
	private String contactname;
	private String contactmobile;
	private String contactaddress;
	private int userid;
	private long createtime;
	private int isrecommend;
	private long recommendtime;
	private int ordertemp;
	private int browsenum;
	private int clicknum;
	private int commentnum;
	private int zambianum;
	private int collectionnum;
	private int serviceid;
	private int delmark;
	private int isvalidate;
	private String username;
	public int getProductid() {
		return productid;
	}
	public void setProductid(int productid) {
		this.productid = productid;
	}
	public String getProductname() {
		return productname;
	}
	public void setProductname(String productname) {
		this.productname = productname;
	}
	public String getProducthead() {
		return producthead;
	}
	public void setProducthead(String producthead) {
		this.producthead = producthead;
	}
	public double getProductprice() {
		return productprice;
	}
	public void setProductprice(double productprice) {
		this.productprice = productprice;
	}
	public int getProductposition() {
		return productposition;
	}
	public void setProductposition(int productposition) {
		this.productposition = productposition;
	}
	public int getProducttype() {
		return producttype;
	}
	public void setProducttype(int producttype) {
		this.producttype = producttype;
	}
	public String getProductbrief() {
		return productbrief;
	}
	public void setProductbrief(String productbrief) {
		this.productbrief = productbrief;
	}
	public String getProductdesc() {
		return productdesc;
	}
	public void setProductdesc(String productdesc) {
		this.productdesc = productdesc;
	}
	public String getProducturl() {
		return producturl;
	}
	public void setProducturl(String producturl) {
		this.producturl = producturl;
	}
	public String getContactname() {
		return contactname;
	}
	public void setContactname(String contactname) {
		this.contactname = contactname;
	}
	public String getContactmobile() {
		return contactmobile;
	}
	public void setContactmobile(String contactmobile) {
		this.contactmobile = contactmobile;
	}
	public String getContactaddress() {
		return contactaddress;
	}
	public void setContactaddress(String contactaddress) {
		this.contactaddress = contactaddress;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}
	public int getIsrecommend() {
		return isrecommend;
	}
	public void setIsrecommend(int isrecommend) {
		this.isrecommend = isrecommend;
	}
	public long getRecommendtime() {
		return recommendtime;
	}
	public void setRecommendtime(long recommendtime) {
		this.recommendtime = recommendtime;
	}
	public int getOrdertemp() {
		return ordertemp;
	}
	public void setOrdertemp(int ordertemp) {
		this.ordertemp = ordertemp;
	}
	public int getBrowsenum() {
		return browsenum;
	}
	public void setBrowsenum(int browsenum) {
		this.browsenum = browsenum;
	}
	public int getClicknum() {
		return clicknum;
	}
	public void setClicknum(int clicknum) {
		this.clicknum = clicknum;
	}
	public int getCommentnum() {
		return commentnum;
	}
	public void setCommentnum(int commentnum) {
		this.commentnum = commentnum;
	}
	public int getZambianum() {
		return zambianum;
	}
	public void setZambianum(int zambianum) {
		this.zambianum = zambianum;
	}
	public int getCollectionnum() {
		return collectionnum;
	}
	public void setCollectionnum(int collectionnum) {
		this.collectionnum = collectionnum;
	}
	public int getServiceid() {
		return serviceid;
	}
	public void setServiceid(int serviceid) {
		this.serviceid = serviceid;
	}
	public int getDelmark() {
		return delmark;
	}
	public void setDelmark(int delmark) {
		this.delmark = delmark;
	}
	public int getIsvalidate() {
		return isvalidate;
	}
	public void setIsvalidate(int isvalidate) {
		this.isvalidate = isvalidate;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public ProductModle(int productid, String productname, String producthead,
                        double productprice, int productposition, int producttype,
                        String productbrief, String productdesc, String producturl,
                        String contactname, String contactmobile, String contactaddress,
                        int userid, long createtime, int isrecommend, long recommendtime,
                        int ordertemp, int browsenum, int clicknum, int commentnum,
                        int zambianum, int collectionnum, int serviceid, int delmark,
                        int isvalidate, String username) {
		super();
		this.productid = productid;
		this.productname = productname;
		this.producthead = producthead;
		this.productprice = productprice;
		this.productposition = productposition;
		this.producttype = producttype;
		this.productbrief = productbrief;
		this.productdesc = productdesc;
		this.producturl = producturl;
		this.contactname = contactname;
		this.contactmobile = contactmobile;
		this.contactaddress = contactaddress;
		this.userid = userid;
		this.createtime = createtime;
		this.isrecommend = isrecommend;
		this.recommendtime = recommendtime;
		this.ordertemp = ordertemp;
		this.browsenum = browsenum;
		this.clicknum = clicknum;
		this.commentnum = commentnum;
		this.zambianum = zambianum;
		this.collectionnum = collectionnum;
		this.serviceid = serviceid;
		this.delmark = delmark;
		this.isvalidate = isvalidate;
		this.username = username;
	}
	public ProductModle() {
		super();
	}
	@Override
	public String toString() {
		return "ProductModle [productid=" + productid + ", productname="
				+ productname + ", producthead=" + producthead
				+ ", productprice=" + productprice + ", productposition="
				+ productposition + ", producttype=" + producttype
				+ ", productbrief=" + productbrief + ", productdesc="
				+ productdesc + ", producturl=" + producturl + ", contactname="
				+ contactname + ", contactmobile=" + contactmobile
				+ ", contactaddress=" + contactaddress + ", userid=" + userid
				+ ", createtime=" + createtime + ", isrecommend=" + isrecommend
				+ ", recommendtime=" + recommendtime + ", ordertemp="
				+ ordertemp + ", browsenum=" + browsenum + ", clicknum="
				+ clicknum + ", commentnum=" + commentnum + ", zambianum="
				+ zambianum + ", collectionnum=" + collectionnum
				+ ", serviceid=" + serviceid + ", delmark=" + delmark
				+ ", isvalidate=" + isvalidate + ", username=" + username
				+ ", getProductid()=" + getProductid() + ", getProductname()="
				+ getProductname() + ", getProducthead()=" + getProducthead()
				+ ", getProductprice()=" + getProductprice()
				+ ", getProductposition()=" + getProductposition()
				+ ", getProducttype()=" + getProducttype()
				+ ", getProductbrief()=" + getProductbrief()
				+ ", getProductdesc()=" + getProductdesc()
				+ ", getProducturl()=" + getProducturl()
				+ ", getContactname()=" + getContactname()
				+ ", getContactmobile()=" + getContactmobile()
				+ ", getContactaddress()=" + getContactaddress()
				+ ", getUserid()=" + getUserid() + ", getCreatetime()="
				+ getCreatetime() + ", getIsrecommend()=" + getIsrecommend()
				+ ", getRecommendtime()=" + getRecommendtime()
				+ ", getOrdertemp()=" + getOrdertemp() + ", getBrowsenum()="
				+ getBrowsenum() + ", getClicknum()=" + getClicknum()
				+ ", getCommentnum()=" + getCommentnum() + ", getZambianum()="
				+ getZambianum() + ", getCollectionnum()=" + getCollectionnum()
				+ ", getServiceid()=" + getServiceid() + ", getDelmark()="
				+ getDelmark() + ", getIsvalidate()=" + getIsvalidate()
				+ ", getUsername()=" + getUsername() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
	
}
