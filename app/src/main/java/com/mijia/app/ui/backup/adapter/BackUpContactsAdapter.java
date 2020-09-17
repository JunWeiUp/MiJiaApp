package com.mijia.app.ui.backup.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.handongkeji.utils.DateUtil;
import com.mijia.app.R;
import com.mijia.app.bean.DiskBackUpContactBean;
import com.mijia.app.databinding.ItemBackupContactRecordBinding;
import com.mijia.app.utils.StringCheckUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Administrator on 2019/6/11.
 */

public class BackUpContactsAdapter extends BaseQuickAdapter<DiskBackUpContactBean.AddBookBean, BaseViewHolder> {

    public BackUpContactsAdapter() {
        super(R.layout.item_backup_contact_record);
    }

    @Override
    protected void convert(BaseViewHolder helper, DiskBackUpContactBean.AddBookBean item) {
        ItemBackupContactRecordBinding binding = DataBindingUtil.bind(helper.getConvertView());

        binding.timeText.setText(DateUtil.getTimeStr(Long.parseLong(item.getBookTime()) * 1000));

        binding.tvBackNum.setText(item.getBookCount()+"条");

        TextView tvSize = helper.getView(R.id.tv_back_size);
        String size;
        if (StringUtils.isEmpty(item.getBookSize())) {
            size = "";
        } else {
            if (StringCheckUtils.isNumeric(item.getBookSize())) {
                size = com.mijia.app.utils.file.FileUtils.FormetFileSize(Long.parseLong(item.getBookSize()));
            } else {
                size = item.getBookSize();
            }
        }

        tvSize.setText(size);

        try {
            StringBuffer stringBuffer = new StringBuffer();
            InputStreamReader inputreader = null;
            inputreader = new InputStreamReader(new FileInputStream(item.getBookPath()), "UTF-8");
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line = "";
            //分行读取
            while ((line = buffreader.readLine()) != null) {
                stringBuffer.append(line);
            }
            List<LinkedTreeMap<String, String>> list = new Gson().fromJson(stringBuffer.toString(), List.class);
            binding.tvBackNum.setText(list.size()+"条");
            binding.tvBackSize.setText(FileUtils.getFileSize(new File(item.getBookPath())));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }




        helper.addOnClickListener(R.id.ll_item_view);
        helper.addOnClickListener(R.id.restoreText);
        helper.addOnClickListener(R.id.delText);
    }
}
