package com.handongkeji.modle;

import java.util.ArrayList;

/**
 * 
 * @author Apathy����
 * 
 *         ��������ʵ��
 * 
 * */

public class ParentEntity {

	private int groupColor;

	private String groupName;
	private int groupId;

	private ArrayList<ChildEntity> childs;

	
	/* ==========================================================
	 * ======================= get method =======================
	 * ========================================================== */
	
	public int getGroupColor() {
		return groupColor;
	}

	public String getGroupName() {
		return groupName;
	}

	public ArrayList<ChildEntity> getChilds() {
		return childs;
	}
	
	/* ==========================================================
	 * ======================= set method =======================
	 * ========================================================== */

	public void setGroupColor(int groupColor) {
		this.groupColor = groupColor;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setChilds(ArrayList<ChildEntity> childs) {
		this.childs = childs;
	}
	public void setGroupId(int gid) {
		groupId = gid;
	}
	
	public int getGroupId() {
		return groupId;
	}

}
