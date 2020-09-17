package com.handongkeji.modle;

public class CommentModle {

	private int commentid  ;
	private int productid  ;
	private String commentcontent  ;
	private String userid  ;
	private long createtime  ;
	private String delmark  ;
	private String username  ;
	private String userpic  ;
	public int getCommentid() {
		return commentid;
	}
	public void setCommentid(int commentid) {
		this.commentid = commentid;
	}
	public int getProductid() {
		return productid;
	}
	public void setProductid(int productid) {
		this.productid = productid;
	}
	public String getCommentcontent() {
		return commentcontent;
	}
	public void setCommentcontent(String commentcontent) {
		this.commentcontent = commentcontent;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}
	public String getDelmark() {
		return delmark;
	}
	public void setDelmark(String delmark) {
		this.delmark = delmark;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserpic() {
		return userpic;
	}
	public void setUserpic(String userpic) {
		this.userpic = userpic;
	}
	public CommentModle(int commentid, int productid, String commentcontent,
                        String userid, long createtime, String delmark, String username,
                        String userpic) {
		super();
		this.commentid = commentid;
		this.productid = productid;
		this.commentcontent = commentcontent;
		this.userid = userid;
		this.createtime = createtime;
		this.delmark = delmark;
		this.username = username;
		this.userpic = userpic;
	}
	public CommentModle() {
		super();
	}
	
	
}
