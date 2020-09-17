package com.mijia.app.ui.trans.adapter;

import android.databinding.DataBindingUtil;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mijia.app.R;
import com.mijia.app.databinding.ItemDownloadingBinding;
import com.mijia.app.socket.DownTask;
import com.mijia.app.ui.trans.fragment.TransferListFragment;
import com.mijia.app.utils.CacheManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2019/6/11.
 */

public class DownloadingAdapter extends BaseQuickAdapter<DownTask, BaseViewHolder> {

    private boolean isOpenSelected = false;
    private ItemDownloadingBinding mItemBinding;

    public DownloadingAdapter() {
        super(R.layout.item_downloading);
    }

    @Override
    protected void convert(BaseViewHolder helper, DownTask item) {

        mItemBinding = DataBindingUtil.bind(helper.getConvertView());

        ImageView ivSelect = helper.getView(R.id.downLoadingSelectImage);
        ivSelect.setVisibility(isOpenSelected ? View.VISIBLE : View.GONE);
        ivSelect.setSelected(item.isSelected());

        mItemBinding.downLoadingProgressBar.setDonutProgress(item.getFilePointer(), item.getFileSize());
        String fileName = item.getFileName();
        mItemBinding.fileNameText.setText(fileName);

        String pointer = CacheManager.getFormatSize(item.getFilePointer());
        String fileSize = CacheManager.getFormatSize(item.getFileSize());
        mItemBinding.downProcessText.setText(pointer + "/" + fileSize);

//        mItemBinding.tvMiwen.setVisibility(item.getDownLoadResponseBean().getType()==1?View.VISIBLE:View.GONE);
        mItemBinding.tvMiwen.setText(item.getDownLoadResponseBean().getType() == 1 ? "密文" : "明文");


        /**
         * 0:未发送请求
         * 1：接收到响应 （传输中）
         * 2：传输完成
         * 3：长时间（5 分钟）未接收到响应（下载失败）
         * 4: 等待下载
         */
        int currStatus = item.getCurrTaskStatus();
        if (0 == currStatus || 1 == currStatus) {
            String speed = item.getDownSpeed();
            mItemBinding.tvSpeed.setText(speed + "/s");
            boolean pause = item.isPause();
            mItemBinding.downLoadingProgressBar.setIvStatus(pause);
            if (pause) {
                mItemBinding.tvSpeed.setText("暂停");
            }
        } else if (4 == currStatus) {
            mItemBinding.tvSpeed.setText("等待");
        }


        if (fileName.contains(".")) {
            String strLater = fileName.split("\\.").length > 1 ? fileName.split("\\.")[1] : "";
            if (StringUtils.isEmpty(strLater)) {
                mItemBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_wenjian);
            } else if (equals1(strLater, Arrays.asList("doc", "DOCX", "DOC", "docx"))) {
                mItemBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_doc);
            } else if (equals1(strLater, Arrays.asList("execl"))) {
                mItemBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_excel);
            } else if (equals1(strLater, Arrays.asList("pdf"))) {
                mItemBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_pdf);
            } else if (equals1(strLater, Arrays.asList("ppt"))) {
                mItemBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_ppt);
            } else if (equals1(strLater, Arrays.asList("psd"))) {
                mItemBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_ps);
            } else if (equals1(strLater, Arrays.asList("txt"))) {
                mItemBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_txt);
            } else if (equals1(strLater, Arrays.asList("word"))) {
                mItemBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_w);
            } else if (equals1(strLater, Arrays.asList("xls", "xlsx"))) {
                mItemBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_xls);
            } else if (equals1(strLater, Arrays.asList("MP3", "WAV", "CDA", "WMA", "mp3", "AAC"))) {
                mItemBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_yinpin);
            } else {
                mItemBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_qita);
            }
        } else {
            mItemBinding.iconImage.setImageResource(R.drawable.cipan_fenlei_wenjian);
        }


        helper.addOnClickListener(R.id.downLoadingSelectImage);
        helper.addOnClickListener(R.id.ll_main);
        helper.addOnClickListener(R.id.downLoadingProgressBar);
    }

    public void openOrCloseSelectMode(boolean isOpen) {
        isOpenSelected = isOpen;
        if (!isOpen) {
            setAllSelectStatus(false);
        }
        notifyDataSetChanged();
    }

    // 设置全部暂停
    public void setAllPause() {
        List<DownTask> list = this.getData();
        for (DownTask task : list) {
            if (!task.isPause()) {
                task.setPause(true);
            }
        }
        notifyDataSetChanged();
    }

    public void setAllStart() {
        List<DownTask> list = this.getData();
        for (int i = 0; i < list.size(); i++) {
            DownTask task = list.get(i);
            if (task.isPause()) {
                task.setPause(false);
            }
        }
        notifyDataSetChanged();
    }

    public boolean getIsAllPause() {
        boolean b = true;
        List<DownTask> list = this.getData();
        for (DownTask task : list) {
            boolean status = task.isPause();
            b = b && status;
        }
        return b;
    }

    public void setAllSelectStatus(boolean status) {
        List<DownTask> list = this.getData();
        for (DownTask task : list) {
            task.setSelected(status);
        }
        notifyDataSetChanged();
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

    public List<DownTask> getSelectedTaskList() {
        List<DownTask> list = new ArrayList<>();
        for (DownTask simpleBean : getData()) {
            if (simpleBean.isSelected()) {
                list.add(simpleBean);
            }
        }
        return list;
    }

    private boolean equals1(String str, List<String> strings) {
        return strings.contains(str);
    }
}
