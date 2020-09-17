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
import com.mijia.app.databinding.ItemDownloadFinishBinding;
import com.mijia.app.socket.DownTask;
import com.mijia.app.ui.disk.fragment.DiskManagerFragment;
import com.mijia.app.ui.trans.fragment.TransferListFragment;
import com.mijia.app.utils.CacheManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2019/6/11.
 */

public class DownloadFinishAdapter extends BaseQuickAdapter<DownTask, BaseViewHolder> {

    private ItemDownloadFinishBinding itemDownloadFinishBinding;

    public DownloadFinishAdapter() {
        super(R.layout.item_download_finish);
    }

    @Override
    protected void convert(BaseViewHolder helper, DownTask item) {
        itemDownloadFinishBinding = DataBindingUtil.bind(helper.getConvertView());

        helper.addOnClickListener(R.id.finishSelectView);


        String fileName = item.getFileName();
        itemDownloadFinishBinding.fileNameText.setText(fileName);

        long endTime = item.getEndTime();
        String endText = TimeUtils.millis2String(endTime);

        itemDownloadFinishBinding.endTimeYYMMDDText.setText(endText);
        String fileSize = CacheManager.getFormatSize(item.getFileSize());
        itemDownloadFinishBinding.fileSizeText.setText(fileSize);

        itemDownloadFinishBinding.finishSelectView.setVisibility(View.GONE);
        if (isOpenSelected) {
            itemDownloadFinishBinding.finishSelectView.setVisibility(View.VISIBLE);
        }
//        itemDownloadFinishBinding.tvMiwen.setVisibility(item.getDownLoadResponseBean().getType() == 1 ? View.VISIBLE : View.GONE);
        itemDownloadFinishBinding.tvMiwen.setText(item.getDownLoadResponseBean().getType() == 1 ? "密文" : "明文");

        if (fileName.contains(".")) {
            String strLater = fileName.split("\\.").length > 1 ? fileName.split("\\.")[1] : "";
            if (StringUtils.isEmpty(strLater)) {
                itemDownloadFinishBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_wenjian);
            } else if (equals1(strLater, Arrays.asList("doc", "DOCX", "DOC", "docx"))) {
                itemDownloadFinishBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_doc);
            } else if (equals1(strLater, Arrays.asList("execl"))) {
                itemDownloadFinishBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_excel);
            } else if (equals1(strLater, Arrays.asList("pdf"))) {
                itemDownloadFinishBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_pdf);
            } else if (equals1(strLater, Arrays.asList("ppt"))) {
                itemDownloadFinishBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_ppt);
            } else if (equals1(strLater, Arrays.asList("psd"))) {
                itemDownloadFinishBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_ps);
            } else if (equals1(strLater, Arrays.asList("txt"))) {
                itemDownloadFinishBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_txt);
            } else if (equals1(strLater, Arrays.asList("word"))) {
                itemDownloadFinishBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_w);
            } else if (equals1(strLater, Arrays.asList("xls", "xlsx"))) {
                itemDownloadFinishBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_xls);
            } else if (equals1(strLater, Arrays.asList("MP3", "WAV", "CDA", "WMA", "mp3", "AAC"))) {
                itemDownloadFinishBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_yinpin);
            } else {
                itemDownloadFinishBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_qita);
            }
        } else {
            itemDownloadFinishBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_wenjian);
        }

        helper.addOnClickListener(R.id.finishiRootViewLayout);

        itemDownloadFinishBinding.finishSelectView.setSelected(item.isSelected());

    }

    private boolean equals1(String str, List<String> strings) {
        return strings.contains(str);
    }

    private boolean isOpenSelected = false;

    public void openOrCloseSelectMode(boolean isOpen) {
        isOpenSelected = isOpen;
        notifyDataSetChanged();
    }

    /**
     * 选中全部
     */
    public void selectAll() {
        for (DownTask task : getData()) {
            task.setSelected(true);
        }
        refresh();
    }

    /**
     * 清除所有选择
     */
    public void clearAllSelect() {
        for (DownTask task : getData()) {
            task.setSelected(false);
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
        for (DownTask task : getData()) {
            if (task.isSelected()) {
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
                TransferListFragment.pageIndex.set(0);
                TransferListFragment.selectType.set(2);
            } else {
                TransferListFragment.selectedNum.set(selectedNum);
            }
        }
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

    public boolean getIsAllSelected() {
        boolean b = true;
        List<DownTask> list = this.getData();
        for (DownTask task : list) {
            boolean status = task.isSelected();
            b = b && status;
        }
        return b;
    }

}
