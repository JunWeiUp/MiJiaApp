package com.mijia.app.ui.trans.adapter;

import android.databinding.DataBindingUtil;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mijia.app.R;
import com.mijia.app.bean.SimpleBean;
import com.mijia.app.databinding.ItemDownloadFailBinding;
import com.mijia.app.socket.DownTask;
import com.mijia.app.ui.trans.fragment.TransferListFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2019/6/11.
 */

public class DownLoadFailAdapter extends BaseQuickAdapter<DownTask, BaseViewHolder> {

    private boolean isOpenSelected = false;

    public void openOrCloseSelectMode(boolean isOpen) {
        isOpenSelected = isOpen;
        notifyDataSetChanged();
    }


    /**
     * 清除所有选择
     */
    public void clearAllSelect() {
        for (DownTask simpleBean : getData()) {
            simpleBean.setSelected(false);
        }
        refresh();
    }

    /**
     * 选中全部
     */
    public void selectAll() {
        for (DownTask simpleBean : getData()) {
            simpleBean.setSelected(true);
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
        for (DownTask simpleBean : getData()) {
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
        TransferListFragment.selectedNum.set(selectedNum);
    }


    public DownLoadFailAdapter() {
        super(R.layout.item_download_fail);
    }

    @Override
    protected void convert(BaseViewHolder helper, DownTask item) {
        ItemDownloadFailBinding failBinding = DataBindingUtil.bind(helper.getConvertView());
        String fileName = item.getFileName();
        failBinding.titleText.setText(fileName);

        ImageView retryBtn = helper.getView(R.id.retryBtn);
        ImageView ivSelect = helper.getView(R.id.iv_fail_select);
        ivSelect.setVisibility(isOpenSelected ? View.VISIBLE : View.GONE);
        retryBtn.setVisibility(!isOpenSelected ? View.VISIBLE : View.GONE);
        ivSelect.setSelected(item.isSelected());
        failBinding.tvMiwen.setVisibility(item.getDownLoadResponseBean().getType()==1?View.VISIBLE:View.GONE);


        if (fileName.contains(".")) {
            String strLater = fileName.split("\\.").length > 1 ? fileName.split("\\.")[1] : "";
            if (StringUtils.isEmpty(strLater)) {
                failBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_wenjian);
            } else if (equals1(strLater, Arrays.asList("doc", "DOCX", "DOC", "docx"))) {
                failBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_doc);
            } else if (equals1(strLater, Arrays.asList("execl"))) {
                failBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_excel);
            } else if (equals1(strLater, Arrays.asList("pdf"))) {
                failBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_pdf);
            } else if (equals1(strLater, Arrays.asList("ppt"))) {
                failBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_ppt);
            } else if (equals1(strLater, Arrays.asList("psd"))) {
                failBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_ps);
            } else if (equals1(strLater, Arrays.asList("txt"))) {
                failBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_txt);
            } else if (equals1(strLater, Arrays.asList("word"))) {
                failBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_w);
            } else if (equals1(strLater, Arrays.asList("xls", "xlsx"))) {
                failBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_xls);
            } else if (equals1(strLater, Arrays.asList("MP3", "WAV", "CDA", "WMA", "mp3","AAC"))) {
                failBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_yinpin);
            } else {
                failBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_qita);
            }
        } else {
            failBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_wenjian);
        }



        helper.addOnClickListener(R.id.iv_fail_select);
        helper.addOnClickListener(R.id.ll_main);
        helper.addOnClickListener(R.id.retryBtn);

    }
    private boolean equals1(String str, List<String> strings) {
        return strings.contains(str);
    }

    public boolean getIsAllSelected() {
        boolean b = true;
        List<DownTask> list = this.getData();
        for (DownTask task : list) {
            boolean status = task.isSelected();
            b = b && status;
        }
        return b;
    }

    public List<DownTask> getSelectedTaskList() {
        List<DownTask> list = new ArrayList<>();
        for (DownTask simpleBean : getData()) {
            if (simpleBean.isSelected()) {
                list.add(simpleBean);
            }
        }
        return list;
    }
}
