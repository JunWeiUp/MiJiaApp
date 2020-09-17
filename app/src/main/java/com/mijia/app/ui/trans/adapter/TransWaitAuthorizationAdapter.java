package com.mijia.app.ui.trans.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mijia.app.R;
import com.mijia.app.bean.EmpowerListBean;
import com.mijia.app.bean.SimpleBean;
import com.mijia.app.ui.trans.fragment.TransferListFragment;

public class TransWaitAuthorizationAdapter extends BaseQuickAdapter<EmpowerListBean.WaitListBean, BaseViewHolder> {



    private boolean isOpenSelected = false;

    public void openOrCloseSelectMode(boolean isOpen) {
        isOpenSelected = isOpen;
        notifyDataSetChanged();
    }

    /**
     * 选中全部
     */
    public void selectAll(){
        for (EmpowerListBean.WaitListBean simpleBean : getData()) {
            simpleBean.setSelected(true);
        }
        refresh();
    }

    /**
     * 清除所有选择
     */
    public void clearAllSelect() {
        for (EmpowerListBean.WaitListBean simpleBean : getData()) {
            simpleBean.setSelected(false);
        }
        refresh();
    }

    /**
     * 获取选中数量
     *
     * @return
     */
    public int getSelectedNum() {
        int selectedNum = 0;
        for (EmpowerListBean.WaitListBean simpleBean : getData()) {
            if (simpleBean.isSelected()) {
                selectedNum++;
            }
        }
        return selectedNum;
    }

    /**
     * 刷新
     */
    public void refresh() {
        changeSelectedNum();
        notifyDataSetChanged();
    }

    private void changeSelectedNum() {
        int selectedNum = getSelectedNum();
        if (selectedNum>0) {
            if (!TransferListFragment.isOpenSelect.get()) {
                TransferListFragment.selectedNum.set(selectedNum);
                TransferListFragment.isOpenSelect.set(true);
                TransferListFragment.pageIndex.set(2);
                TransferListFragment.selectType.set(1);
            }else{
                TransferListFragment.selectedNum.set(selectedNum);
            }
        }else{

        }
    }

    public TransWaitAuthorizationAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, EmpowerListBean.WaitListBean item) {
        ImageView ivSelect = helper.getView(R.id.iv_select);
//        ivSelect.setVisibility(isOpenSelected ? View.VISIBLE : View.GONE);
        helper.addOnClickListener(R.id.iv_select);
        helper.addOnClickListener(R.id.ll_main);
        ivSelect.setSelected(item.isSelected());
        helper.getView(R.id.iv_select).setVisibility(View.GONE);
        TextView tvFileName = helper.getView(R.id.tv_file_name);
        ImageView ivIcon = helper.getView(R.id.iv_icon);
        TextView tvNickName = helper.getView(R.id.tv_nickname);
        TextView tvTime = helper.getView(R.id.tv_time);

        tvFileName.setText(item.getFileName());
        tvNickName.setText(item.getNickName());
        tvTime.setText(item.getCreateDate());
    }
}
