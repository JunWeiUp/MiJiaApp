package com.handongkeji.utils;

import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表 选择管理
 *
 * @ClassName:SelectItemManger
 * @PackageName:com.handongkeji.utils
 * @Create On 2017/3/27 0027   09:19
 * @Site:http://www.handongkeji.com
 * @author:xuchuanting
 * @Copyrights 2017/3/27 0027 handongkeji All rights reserved.
 */

public class SelectItemManger {

    public enum Mode {
        MODE_SINGLE_SELECTION, MODE_MULTI_SELECTION;
    }

    //用于监听多选模式达到上限
    public interface OnReachToMaxListener {
        void reachToMax();
    }

    //单选模式 item 变化
    public interface OnItemChangeListener {
        void itemChange(int position, Object item);
    }

    public interface OnGetItem {
        Object getItem(int positon);
    }


    //最近一次点击的条目位置
    private int lastposition = -1;

    private OnGetItem getItem;

    private OnItemChangeListener itemChangeListener;
    private Mode mode;

    private int mMax = -1;//多选模式，上限，-1无上限

    private int selectedItemCount = 0;//已经选择的条目数量

    private OnReachToMaxListener maxListener;

    /**
     * 多选模式
     */

    public static int MODE_MULTI_SELECTION = 1;

    private int size;
    private final SparseBooleanArray booleanArray;


    public SelectItemManger(int size, Mode mode) {

        this.size = size;
        this.mode = mode;
        booleanArray = new SparseBooleanArray();
    }

    public int getItemCount() {
        return size;
    }


    public void setReachToMaxListener(OnReachToMaxListener maxListener) {
        this.maxListener = maxListener;
    }

    /**
     * 切换模式
     *
     * @param mode
     */
    public void changeMode(Mode mode) {
        this.mode = mode;
    }


    public OnItemChangeListener getItemChangeListener() {
        return itemChangeListener;
    }

    public void setItemChangeListener(OnItemChangeListener itemChangeListener, OnGetItem getItem) {
        this.itemChangeListener = itemChangeListener;
        this.getItem = getItem;
    }

    /**
     * 设置多选上限
     *
     * @param max
     */
    public void setMaxItem(int max) {
        mMax = max;
    }


    /**
     * 选中条目
     *
     * @param position
     */
    public void selectItem(int position) {
        if (this.mode.equals(Mode.MODE_SINGLE_SELECTION)) {
            if (lastposition != position) {
                booleanArray.put(lastposition, false);
                if (itemChangeListener != null && getItem != null) {
                    itemChangeListener.itemChange(position, getItem.getItem(position));
                }
            }

            booleanArray.put(position, true);

        } else {//多选模式，第一次选种，第二次取消
            boolean b = booleanArray.get(position);
            if (b) {
                booleanArray.put(position, false);
                selectedItemCount--;
            } else {


                if (maxListener != null && mMax != -1 && selectedItemCount == mMax) {//达到上限
                    maxListener.reachToMax();//上限到达后，再次选择调用
                } else {
                    booleanArray.put(position, true);
                    selectedItemCount++;
                }

            }
        }

        lastposition = position;
    }


    /**
     * 适用于多选模式，选择多个条目(一般用于初始化操作，很少有一次性点击选中多个条目的需求吧)
     * 一个一个选就用{@link #selectItem}好了
     *
     * @param positions
     */
    public void selectItems(List<Integer> positions) {
        if (this.mode.equals(Mode.MODE_MULTI_SELECTION)) {
            selectedItemCount += positions.size();
            for (int i = 0; i < positions.size(); i++) {
                Integer integer = positions.get(i);
                booleanArray.put(integer, true);
            }


        } else {
            throw new IllegalStateException("单选模式不能选多个条目！");
        }

    }


    /**
     * 获取被选中条目位置position
     *
     * @return
     */
    public List<Integer> getSelectedItem() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < booleanArray.size(); i++) {
            if (booleanArray.get(booleanArray.keyAt(i))) {
                list.add(booleanArray.keyAt(i));
            }
        }
        return list;
    }

    /**
     * 判断条目是否被选中
     *
     * @param position
     * @return
     */
    public boolean isItemSelected(int position) {

        return booleanArray.get(position);
    }

    /**
     * 添加新条目，如上拉加载场景
     *
     * @param size
     */
    public void addNewItems(int size) {

        this.size += size;
    }

    /**
     * 删除已选中条目
     */
    public void delectSelectedItem() {
        List<Integer> selectItem = getSelectedItem();
        for (int i = 0; i < selectItem.size(); i++) {
            Integer key = selectItem.get(i);
            booleanArray.delete(key);
        }
        size -= selectItem.size();
    }


}
