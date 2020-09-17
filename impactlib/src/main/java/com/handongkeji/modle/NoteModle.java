package com.handongkeji.modle;

/**
 * 项目回复domin
 * 
 * @ClassName:NoteModle
 * 
 * @PackageName:com.handongkeji.modle
 * 
 * @Create On 2015-12-9上午11:55:06
 * 
 * @Site:http://www.handongkeji.com
 * 
 * @author:zhouhao
 * 
 * @Copyrights 2015-12-9 handongkeji All rights reserved.
 */
public class NoteModle {

	private int commentid;
	private int projectid;
	private String commentcontent;
	private int parentid;
	private int userid;
	private long createtime;
	private int delmark;
	private String commentusernick;
	private String commentuserpic;
	private int commentuserisdel;
	private int infodelmark;
	private String infousernick;
	private int infouserisdel;
	private String infoprojectname;

	public int getCommentid() {
		return commentid;
	}

	public void setCommentid(int commentid) {
		this.commentid = commentid;
	}

	public int getProjectid() {
		return projectid;
	}

	public void setProjectid(int projectid) {
		this.projectid = projectid;
	}

	public String getCommentcontent() {
		return commentcontent;
	}

	public void setCommentcontent(String commentcontent) {
		this.commentcontent = commentcontent;
	}

	public int getParentid() {
		return parentid;
	}

	public void setParentid(int parentid) {
		this.parentid = parentid;
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

	public int getDelmark() {
		return delmark;
	}

	public void setDelmark(int delmark) {
		this.delmark = delmark;
	}

	public String getCommentusernick() {
		return commentusernick;
	}

	public void setCommentusernick(String commentusernick) {
		this.commentusernick = commentusernick;
	}

	public String getCommentuserpic() {
		return commentuserpic;
	}

	public void setCommentuserpic(String commentuserpic) {
		this.commentuserpic = commentuserpic;
	}

	public int getCommentuserisdel() {
		return commentuserisdel;
	}

	public void setCommentuserisdel(int commentuserisdel) {
		this.commentuserisdel = commentuserisdel;
	}

	public int getInfodelmark() {
		return infodelmark;
	}

	public void setInfodelmark(int infodelmark) {
		this.infodelmark = infodelmark;
	}

	public String getInfousernick() {
		return infousernick;
	}

	public void setInfousernick(String infousernick) {
		this.infousernick = infousernick;
	}

	public int getInfouserisdel() {
		return infouserisdel;
	}

	public void setInfouserisdel(int infouserisdel) {
		this.infouserisdel = infouserisdel;
	}

	public String getInfoprojectname() {
		return infoprojectname;
	}

	public void setInfoprojectname(String infoprojectname) {
		this.infoprojectname = infoprojectname;
	}

	@Override
	public String toString() {
		return "NoteModle [commentid=" + commentid + ", projectid=" + projectid + ", commentcontent=" + commentcontent + ", parentid=" + parentid + ", userid=" + userid + ", createtime=" + createtime + ", delmark=" + delmark + ", commentusernick=" + commentusernick + ", commentuserpic=" + commentuserpic + ", commentuserisdel=" + commentuserisdel + ", infodelmark=" + infodelmark + ", infousernick=" + infousernick + ", infouserisdel=" + infouserisdel + ", infoprojectname=" + infoprojectname + "]";
	}

}
