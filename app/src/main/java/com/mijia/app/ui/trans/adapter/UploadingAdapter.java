package com.mijia.app.ui.trans.adapter;

import android.databinding.DataBindingUtil;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mijia.app.R;
import com.mijia.app.bean.DownloadListBean;
import com.mijia.app.bean.SimpleBean;
import com.mijia.app.databinding.ItemUpLoadingLayoutBinding;
import com.mijia.app.socket.DownTask;
import com.mijia.app.socket.UpLoadTask;
import com.mijia.app.ui.trans.fragment.TransferListFragment;
import com.mijia.app.utils.CacheManager;
import com.mijia.app.widget.ButtonProgress;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2019/6/11.
 */

public class UploadingAdapter extends BaseQuickAdapter<UpLoadTask, BaseViewHolder> {

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
        TransferListFragment.selectedNum.set(selectedNum);
    }

    public UploadingAdapter() {
        super(R.layout.item_up_loading_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, UpLoadTask item) {
        ItemUpLoadingLayoutBinding itemUpLoadingLayoutBinding = DataBindingUtil.bind(helper.getConvertView());
        String fileName = item.getFileName();
        itemUpLoadingLayoutBinding.fileNameText.setText(fileName);

        itemUpLoadingLayoutBinding.upLoadingProgressBar.setDonutProgress(item.getCurrReadEndPosition(), item.getFileSize());

        helper.addOnClickListener(R.id.iv_select);
        helper.addOnClickListener(R.id.ll_main).addOnClickListener(R.id.upLoadingProgressBar);

        boolean pause = item.isPause();
        itemUpLoadingLayoutBinding.upLoadingProgressBar.setIvStatus(pause);

        String speed = item.getUpSpeed();
        itemUpLoadingLayoutBinding.tvSpeed.setText(speed + "/S");
        if (item.isPause()) {
            itemUpLoadingLayoutBinding.tvSpeed.setText("暂停");
        }

        String pointer = CacheManager.getFormatSize(item.getCurrReadEndPosition());
        String fileSize = CacheManager.getFormatSize(item.getFileSize());

        itemUpLoadingLayoutBinding.downProcessText.setText(pointer + "/" + fileSize);

        itemUpLoadingLayoutBinding.ivUpLoadingSelect.setVisibility(View.GONE);
        if (isOpenSelected) {
            itemUpLoadingLayoutBinding.ivUpLoadingSelect.setVisibility(View.VISIBLE);
        }

        itemUpLoadingLayoutBinding.ivUpLoadingSelect.setSelected(false);
        if (item.isSelected()) {
            itemUpLoadingLayoutBinding.ivUpLoadingSelect.setSelected(true);
        }
        if (fileName.contains(".")) {
            String strLater = fileName.split("\\.").length > 1 ? fileName.split("\\.")[1] : "";
            if (StringUtils.isEmpty(strLater)) {
                itemUpLoadingLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_wenjian);
            } else if (equals1(strLater, Arrays.asList("doc", "DOCX", "DOC", "docx"))) {
                itemUpLoadingLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_doc);
            } else if (equals1(strLater, Arrays.asList("execl"))) {
                itemUpLoadingLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_excel);
            } else if (equals1(strLater, Arrays.asList("pdf"))) {
                itemUpLoadingLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_pdf);
            } else if (equals1(strLater, Arrays.asList("ppt"))) {
                itemUpLoadingLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_ppt);
            } else if (equals1(strLater, Arrays.asList("psd"))) {
                itemUpLoadingLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_ps);
            } else if (equals1(strLater, Arrays.asList("txt"))) {
                itemUpLoadingLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_txt);
            } else if (equals1(strLater, Arrays.asList("word"))) {
                itemUpLoadingLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_w);
            } else if (equals1(strLater, Arrays.asList("xls", "xlsx"))) {
                itemUpLoadingLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_xls);
            } else if (equals1(strLater, Arrays.asList("MP3", "WAV", "CDA", "WMA", "mp3","AAC"))) {
                itemUpLoadingLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_yinpin);
            } else {
                itemUpLoadingLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_qita);
            }
        } else {
            itemUpLoadingLayoutBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_wenjian);
        }
        helper.addOnClickListener(R.id.ivUpLoadingSelect);

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

    public void setPause(boolean status) {
        List<UpLoadTask> list = this.getData();
        for (UpLoadTask task : list) {
            task.setPause(status);
        }
    }
}
