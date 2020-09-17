package com.handongkeji.modle;

import java.util.ArrayList;

/**
 * 
 * @author Apathy����
 * 
 *         ��������ʵ��
 * 
 * */

public class ChildEntity {

	private int groupColor;

	private String groupName;
	
	private int groupId;

	private ArrayList<String> childNames;
	private ArrayList<Integer> childIds;


	/* ==========================================================
	 * ======================= get method =======================
	 * ========================================================== */
	
	public int getGroupColor() {
		return groupColor;
	}

	public String getGroupName() {
		return groupName;
	}

	public ArrayList<String> getChildNames() {
		return childNames;
	}
	public ArrayList<Integer> getChildIds() {
		return childIds;
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

	public void setChildNames(ArrayList<String> childNames) {
		this.childNames = childNames;
	}
	public void setChildIds(ArrayList<Integer> ids) {
		this.childIds = ids;
	}
	public void setGroupId(int gid) {
		groupId = gid;
	}
	
	public int getGroupId() {
		return groupId;
	}

}
