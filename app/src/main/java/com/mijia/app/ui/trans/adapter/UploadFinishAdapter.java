package com.mijia.app.ui.trans.adapter;

import android.databinding.DataBindingUtil;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mijia.app.R;
import com.mijia.app.bean.SimpleBean;
import com.mijia.app.databinding.ItemUpLoadFinishLayoutBinding;
import com.mijia.app.socket.UpLoadTask;
import com.mijia.app.ui.trans.fragment.TransferListFragment;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2019/6/11.
 */

public class UploadFinishAdapter extends BaseQuickAdapter<UpLoadTask, BaseViewHolder> {

    private boolean isOpenSelected = false;

    public void openOrCloseSelectMode(boolean isOpen) {
        isOpenSelected = isOpen;
        notifyDataSetChanged();
    }

    /**
     * 选中全部
     */
    public void selectAll() {
        for (UpLoadTask simpleBean : getData()) {
            simpleBean.setSelected(true);
        }
        refresh();
    }

    /**
     * 清除所有选择
     */
    public void clearAllSelect() {
        for (UpLoadTask simpleBean : getData()) {
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
        for (UpLoadTask simpleBean : getData()) {
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

        if (selectedNum > 0) {
            if (!TransferListFragment.isOpenSelect.get()) {
                TransferListFragment.selectedNum.set(selectedNum);
                TransferListFragment.isOpenSelect.set(true);
                TransferListFragment.pageIndex.set(1);
                TransferListFragment.selectType.set(2);
            } else {
                TransferListFragment.selectedNum.set(selectedNum);
            }
        }
    }

    public UploadFinishAdapter() {
        super(R.layout.item_up_load_finish_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, UpLoadTask item) {
        ItemUpLoadFinishLayoutBinding itemUpLoadFinishLayoutBinding = DataBindingUtil.bind(helper.getConvertView());
        itemUpLoadFinishLayoutBinding.fileNameText.setText(item.getFileName());

        String fileName = item.getFileName();

        long endTime = item.getFinishTime();

        itemUpLoadFinishLayoutBinding.endTimeYYMMDDText.setText(TimeUtils.millis2String(endTime));

        itemUpLoadFinishLayoutBinding.finishSelectView.setSelected(item.isSelected());

        itemUpLoadFinishLayoutBinding.finishSelectView.setVisibility(View.GONE);
        if (isOpenSelected) {
            itemUpLoadFinishLayoutBinding.finishSelectView.setVisibility(View.VISIBLE);
        }

        if (fileName.contains(".")) {
            String strLater = fileName.split("\\.").length > 1 ? fileName.split("\\.")[1] : "";
            if (StringUtils.isEmpty(strLater)) {
                itemUpLoadFinishLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_wenjian);
            } else if (equals1(strLater, Arrays.asList("doc", "DOCX", "DOC", "docx"))) {
                itemUpLoadFinishLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_doc);
            } else if (equals1(strLater, Arrays.asList("execl","EXECL"))) {
                itemUpLoadFinishLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_excel);
            } else if (equals1(strLater, Arrays.asList("pdf","PDF"))) {
                itemUpLoadFinishLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_pdf);
            } else if (equals1(strLater, Arrays.asList("ppt","PPT"))) {
                itemUpLoadFinishLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_ppt);
            } else if (equals1(strLater, Arrays.asList("psd","PSD"))) {
                itemUpLoadFinishLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_ps);
            } else if (equals1(strLater, Arrays.asList("txt","TXT"))) {
                itemUpLoadFinishLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_txt);
            } else if (equals1(strLater, Arrays.asList("word","WORD"))) {
                itemUpLoadFinishLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_w);
            } else if (equals1(strLater, Arrays.asList("xls", "xlsx","XLS", "XLSX"))) {
                itemUpLoadFinishLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_xls);
            } else if (equals1(strLater, Arrays.asList("MP3", "WAV", "CDA", "WMA", "mp3"))) {
                itemUpLoadFinishLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_yinpin);
            } else {
                itemUpLoadFinishLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_qita);
            }
        } else {
            itemUpLoadFinishLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_wenjian);
        }

        helper.addOnClickListener(R.id.ll_main);
        helper.addOnClickListener(R.id.finishSelectView);
    }

    private boolean equals1(String str, List<String> strings) {
        return strings.contains(str);
    }
    public boolean getIsAllSelected() {
        boolean b = true;
        List<UpLoadTask> list = this.getData();
        for (UpLoadTask task : list) {
            boolean status = task.isSelected();
            b = b && status;
        }
        return b;
    }

}
