package com.mijia.app.ui.disk.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.handongkeji.utils.DateUtil;
import com.mijia.app.R;
import com.mijia.app.bean.FileBean;
import com.mijia.app.bean.SimpleBean;
import com.mijia.app.ui.disk.fragment.DiskManagerFragment;
import com.mijia.app.utils.StringCheckUtils;
import com.mijia.app.utils.file.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2019/6/6.
 */

public class DiskFileListAdapter extends BaseQuickAdapter<FileBean, BaseViewHolder> {


    private boolean isHideSelectIcon = false;

    public void setHideSelectIcon(boolean hideSelectIcon) {
        isHideSelectIcon = hideSelectIcon;
    }

    /**
     * 获取已选中的文件
     *
     * @return
     */
    public List<FileBean> getSelectedFileBean() {
        List<FileBean> list = new ArrayList<>();
        for (FileBean datum : getData()) {
            if (datum.isSelected()) {
                list.add(datum);
            }
        }
        return list;
    }

    /**
     * 清除所有选择
     */
    public void clearAllSelect() {
        for (FileBean simpleBean : getData()) {
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
        for (FileBean simpleBean : getData()) {
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
        if (selectedNum == 0) {
            DiskManagerFragment.isOpenSelect.set(false);
        } else {
            if (!DiskManagerFragment.isOpenSelect.get()) {
                DiskManagerFragment.isOpenSelect.set(true);
            }
        }
        DiskManagerFragment.selectedNum.set(selectedNum);
        DiskManagerFragment.selectedFileList.clear();
        DiskManagerFragment.selectedFileList.addAll(getSelectedFileBean());
    }


    public void mingmichange() {
        if (DiskManagerFragment.isMiWenOpen.get()) {
            if (getSelectedNum() > 0) {
                int index = 0;
                for (FileBean simpleBean : getData()) {
                    if (simpleBean.isSelected() && index == 0) {
                        index = 1;
                    } else {
                        simpleBean.setSelected(false);
                    }
                }
            }
        }
        refresh();
    }


    public DiskFileListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, FileBean item) {
        ImageView ivSelected = helper.getView(R.id.iv_selected_status);
        if (isHideSelectIcon) {
            ivSelected.setVisibility(View.GONE);
        } else {
            ivSelected.setVisibility(View.VISIBLE);
        }
        ivSelected.setSelected(item.isSelected());
        helper.addOnClickListener(R.id.ll_item_view);
        helper.addOnClickListener(R.id.iv_selected_status);

        TextView tvTime = helper.getView(R.id.tv_file_time);
        tvTime.setText(DateUtil.getTimeStr(item.getTime() * 1000));

        TextView tvSize = helper.getView(R.id.tv_file_size);
        String size;
        if (StringUtils.isEmpty(item.getSize())) {
            size = "";
        } else {
            if (StringCheckUtils.isNumeric(item.getSize())) {
                size = FileUtils.FormetFileSize(Long.parseLong(item.getSize()));
            } else {
                size = item.getSize();
            }
        }

        tvSize.setText(size);

        TextView tvFileName = helper.getView(R.id.tv_file_name);
        String fileName = "";
//        if (item.getType().equals("file")) {
        fileName = item.getName().substring(item.getName().lastIndexOf("/") + 1);
//        }else{
//            fileName = item.getName().substring(0, item.getName().lastIndexOf("/"));
//            fileName= fileName.substring(fileName.lastIndexOf("/")+ 1);
//        }
        tvFileName.setText(fileName);
        ImageView ivFilePic = helper.getView(R.id.iv_file_pic);
        if (StringUtils.isEmpty(item.getName())) {
            return;
        }
//        if (item.getName().contains(".") && item.getName().split("\\.").length > 0) {
//            ivSelected.setVisibility(View.VISIBLE);
//        }else{
//            ivSelected.setVisibility(View.GONE);
//        }

        if (item.getName().contains(".")) {
            String strLater = item.getName().split("\\.").length > 1 ? item.getName().split("\\.")[1] : "";
            if (StringUtils.isEmpty(strLater)) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_wenjian);
            } else if (equals(strLater, Arrays.asList("doc", "DOCX", "DOC", "docx"))) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_doc);
            } else if (equals(strLater, Arrays.asList("execl","EXECL"))) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_excel);
            } else if (equals(strLater, Arrays.asList("pdf","PDF"))) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_pdf);
            } else if (equals(strLater, Arrays.asList("ppt","PPT"))) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_ppt);
            } else if (equals(strLater, Arrays.asList("psd","PSD"))) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_ps);
            } else if (equals(strLater, Arrays.asList("txt","TXT"))) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_txt);
            } else if (equals(strLater, Arrays.asList("word","WORD"))) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_w);
            } else if (equals(strLater, Arrays.asList("xls", "xlsx","XLS", "XLSX"))) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_xls);
            } else if (equals(strLater, Arrays.asList("MP3", "WAV", "CDA", "WMA", "mp3","AAC"))) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_yinpin);
            } else {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_qita);
            }
        } else {
            ivFilePic.setImageResource(R.drawable.cipan_fenlei_wenjian);
        }

    }

    private boolean equals(String str, List<String> strings) {
        for (String string : strings) {
            if (str.equals(string)) {
                return true;
            }
        }
        return false;
    }
}
