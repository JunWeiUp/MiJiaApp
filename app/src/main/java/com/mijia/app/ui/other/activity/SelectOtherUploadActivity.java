package com.mijia.app.ui.other.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.handong.framework.account.AccountHelper;
import com.handong.framework.base.BaseActivity;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.R;
import com.mijia.app.bean.UploadRequestBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.constants.Sys;
import com.mijia.app.databinding.ActivitySelectOtherUploadBinding;
import com.mijia.app.socket.UpLoadManager;
import com.mijia.app.socket.UpLoadTask;
import com.mijia.app.utils.file.GetFilesUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mijia.app.constants.Constants.DISK_NOW_PAGE_PATH;

public class SelectOtherUploadActivity extends BaseActivity<ActivitySelectOtherUploadBinding, BaseViewModel> {

    private ListAdapter sAdapter;
    private List<Map<String, Object>> aList;
    private String baseFile;

    private boolean isSelectedAll = false;

    private PathTitleAdapter mPathTitleAdapter;

    private List<String> pathList = new ArrayList<>();


    @Override
    public int getLayoutRes() {
        return R.layout.activity_select_other_upload;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

        mBinding.setHandler(this);

//        mBinding.ivReturn.setOnClickListener(v -> {
//            finish();
//        });

        mBinding.tvChoiceFloder.setOnClickListener(v -> {
            startActivityForResult(new Intent(SelectOtherUploadActivity.this, SelectUploadLocationActivity.class)
                    .putExtra(SelectUploadLocationActivity.SelectLocationType, SelectUploadLocationActivity.Upload), 0x11);
        });

        baseFile = GetFilesUtils.getInstance().getBasePath();
        for (String s : baseFile.split("/")) {
            if (!StringUtils.isEmpty(s)) {
                pathList.add(s);
            }
        }

        mBinding.tvFloderLocation.setText(baseFile);
        aList = new ArrayList<>();
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        sAdapter = new ListAdapter(R.layout.item_file_list);
        mBinding.recycler.setAdapter(sAdapter);


        initPathRecycler();
        mPathTitleAdapter.replaceData(pathList);

        sAdapter.setOnItemClickListener((adapter, view, position) -> {
            // TODO Auto-generated method stub
            try {
                if (sAdapter.getData().get(position).get("fIsDir").equals(true)) {
                    String path = aList.get(position).get("fPath").toString();
                    pathList.add(path.substring(path.lastIndexOf("/") + 1));
                    mPathTitleAdapter.replaceData(pathList);
                    loadFolderList(path);
                } else {
                    Toast.makeText(SelectOtherUploadActivity.this, "这是文件，处理程序待添加", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

        try {
            loadFolderList(baseFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mDestPath = DISK_NOW_PAGE_PATH+"/";
        mBinding.tvChoiceFloderName.setText(DISK_NOW_PAGE_PATH+"/");


        UpLoadManager manager = UpLoadManager.getInstance();
        mBinding.rvSure.setOnClickListener(view -> {
            if (!sAdapter.selectedList.isEmpty()) {
                if (TextUtils.isEmpty(mDestPath)) {
                    ToastUtils.showShort("未选择目的地址");
                    return;
                }

                boolean isAgreeBackupContacts = getSharedPreferences(Sys.FLOW_SHAREPREFERENCES, MODE_PRIVATE).getBoolean(Sys.IS_AGREE_UPLOAD_FILE, false);

                if (Constants.Net_Status == 0 && !isAgreeBackupContacts) {
                    ToastUtils.showShort("请在设置中开启流量上传文件！");
                    return;
                }

                String fileNames = "";
                for (Integer itemIndex : sAdapter.selectedList) {
                    Map<String, Object> map = aList.get(itemIndex);
                    File file = (File) map.get("fPath");
                    UploadRequestBean uploadRequestBean = new UploadRequestBean();
                    uploadRequestBean.setUserName(AccountHelper.getNickname());
                    uploadRequestBean.setUserId(AccountHelper.getUserId());
                    uploadRequestBean.setGsId(Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
                    uploadRequestBean.setGsName(Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());
                    uploadRequestBean.setDiskName(Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                    uploadRequestBean.setDiskId(Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
                    uploadRequestBean.setFullpath(mDestPath  + file.getName());
                    UpLoadTask task = new UpLoadTask(uploadRequestBean, file.getAbsolutePath());
                    boolean b = manager.addTask(task);
                    if (!b) {
                        fileNames += file.getName() + " ";
                    }
                }

                if (!TextUtils.isEmpty(fileNames.trim())) {
                    ToastUtils.showShort(fileNames + " 该文件(s)已存在于下载列表");
                    return;
                } else {
                    ToastUtils.showShort("添加任务成功");
                }

                finish();
            }
        });
    }

    private class PathTitleAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public PathTitleAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            TextView tvFloderName = helper.getView(R.id.tv_floder_name);
            TextView tvRightJiantou = helper.getView(R.id.tv_right_jiantou);
            tvFloderName.setText(item);
            if (helper.getAdapterPosition() == getData().size() - 1) {
                tvRightJiantou.setVisibility(View.GONE);
            } else {
                tvRightJiantou.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initPathRecycler() {
        mPathTitleAdapter = new PathTitleAdapter(R.layout.item_path_title);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(SelectOtherUploadActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBinding.recyclerPath.setLayoutManager(mLayoutManager);
        mBinding.recyclerPath.setAdapter(mPathTitleAdapter);

        mPathTitleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (position <= 1) {
                    return;
                }
                String path = "";
                for (int i = 0; i <= position; i++) {
                    path += "/" + mPathTitleAdapter.getItem(i);
                }
                baseFile = path;
                int index = pathList.indexOf(mPathTitleAdapter.getItem(position)) + 1;
                pathList = pathList.subList(0, index);

                mPathTitleAdapter.replaceData(pathList);
                try {
                    loadFolderList(baseFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String mDestPath = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x11 && resultCode == Activity.RESULT_OK) {
            String path = data.getStringExtra(Sys.SELECT_PATH);
            mDestPath = path;
//            tv_choice_floder_name
            mBinding.tvChoiceFloderName.setText(mDestPath);
        }
    }


    private class ListAdapter extends BaseQuickAdapter<Map<String, Object>, BaseViewHolder> {

        private List<Integer> selectedList = new ArrayList<>();

        private boolean isSelected(int index) {
            for (Integer integer : selectedList) {
                if (index == integer) {
                    return true;
                }
            }
            return false;
        }

        private void setPoiSelected(int index, boolean isSelected) {
            if (isSelected) {
                if (!isSelected(index)) {
                    selectedList.add(index);
                }
            } else {
                if (isSelected(index)) {
                    for (int i = 0; i < selectedList.size(); i++) {
                        if (selectedList.get(i) == index) {
                            selectedList.remove(i);
                            return;
                        }
                    }
                }
            }
        }

        private void allSelected(boolean isAllSelected) {
            if (!isAllSelected) {
                selectedList.clear();
            } else {
                selectedList.clear();
                for (int i = 0; i < getData().size(); i++) {
                    selectedList.add(i);
                }
            }
        }

        public void refresh() {
            if (selectedList.size() == getData().size()) {
                isSelectedAll = true;
                mBinding.tvSelectAll.setText("取消全选");
            } else {
                isSelectedAll = false;
                mBinding.tvSelectAll.setText("全选");
            }
            notifyDataSetChanged();
        }

        public ListAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(BaseViewHolder helper, Map<String, Object> item) {
            ImageView ivFilePic = helper.getView(R.id.iv_file_pic);
            TextView tvFileName = helper.getView(R.id.tv_file_name);
            TextView tvSize = helper.getView(R.id.tv_file_size);
            String fileName = item.get("fName") != null ? item.get("fName").toString() : "";
            tvFileName.setText(fileName);
            tvSize.setText(item.get("fInfo") != null ? item.get("fInfo").toString() : "");
            TextView tvTime = helper.getView(R.id.tv_file_time);

            boolean isDisk = item.get("fIsDir") != null && (boolean) item.get("fIsDir");
            if (isDisk) {
                tvTime.setVisibility(View.GONE);
            } else {
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(item.get("fDate") != null ? item.get("fDate").toString() : "");
            }

            int fImgId = item.get("fImg") != null ? (int) item.get("fImg") : 0;
            ivFilePic.setImageResource(fImgId);

            if (StringUtils.isEmpty(fileName)) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_wenjian);
            } else if (endWiths(fileName, Arrays.asList("doc", "DOCX", "DOC", "docx"))) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_doc);
            } else if (endWiths(fileName, Arrays.asList("execl"))) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_excel);
            } else if (endWiths(fileName, Arrays.asList("pdf"))) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_pdf);
            } else if (endWiths(fileName, Arrays.asList("ppt"))) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_ppt);
            } else if (endWiths(fileName, Arrays.asList("psd"))) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_ps);
            } else if (endWiths(fileName, Arrays.asList("txt"))) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_txt);
            } else if (endWiths(fileName, Arrays.asList("word"))) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_w);
            } else if (endWiths(fileName, Arrays.asList("xls", "xlsx"))) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_xls);
            } else if (endWiths(fileName, Arrays.asList("MP3", "WAV", "CDA", "WMA", "mp3","AAC"))) {
                ivFilePic.setImageResource(R.drawable.cipan_fenlei_yinpin);
            }
            ImageView ivSelect = helper.getView(R.id.iv_selected_status);

            File fPathFile = (File) item.get("fPath");
            ivSelect.setVisibility(View.VISIBLE);
            if (fPathFile.isDirectory()) {
                ivSelect.setVisibility(View.GONE);
            }
            ivSelect.setOnClickListener(v -> {
                if (isSelected(helper.getAdapterPosition())) {
                    setPoiSelected(helper.getAdapterPosition(), false);
                } else {
                    setPoiSelected(helper.getAdapterPosition(), true);
                }
                refresh();
            });

            ivSelect.setSelected(isSelected(helper.getAdapterPosition()));

        }

        private boolean endWiths(String str, List<String> strings) {
            for (String string : strings) {
                if (str.endsWith(string)) {
                    return true;
                }
            }
            return false;
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

    private void loadFolderList(String file) throws IOException {
        showLoading("");
        isSelectedAll = false;
        mBinding.tvSelectAll.setText("全选");
        sAdapter.allSelected(false);
        if (file.equals("/storage/emulated")) {
            file = "/storage/emulated/0";
        }
        List<Map<String, Object>> list = GetFilesUtils.getInstance().getSonNode(file);
        if (list != null) {
            Collections.sort(list, GetFilesUtils.getInstance().defaultOrder());
            aList.clear();
            for (Map<String, Object> map : list) {
                String fileType = (String) map.get(GetFilesUtils.FILE_INFO_TYPE);
                Map<String, Object> gMap = new HashMap<String, Object>();
                if (map.get(GetFilesUtils.FILE_INFO_ISFOLDER).equals(true)) {
                    gMap.put("fIsDir", true);
                    gMap.put("fImg", R.drawable.cipan_fenlei_wenjian);
                    gMap.put("fInfo", map.get(GetFilesUtils.FILE_INFO_NUM_SONDIRS) + "个文件夹和" +
                            map.get(GetFilesUtils.FILE_INFO_NUM_SONFILES) + "个文件");
                } else {
                    gMap.put("fIsDir", false);
                    if (fileType.equals("txt") || fileType.equals("text")) {
                        gMap.put("fImg", R.drawable.zhanweitu);
                    } else {
                        gMap.put("fImg", R.drawable.zhanweitu);
                    }
                    gMap.put("fInfo", "文件大小:" + GetFilesUtils.getInstance().getFileSize(map.get(GetFilesUtils.FILE_INFO_PATH).toString()));
                }
                gMap.put("fDate", map.get(GetFilesUtils.FILE_INFO_DATE));
                gMap.put("fName", map.get(GetFilesUtils.FILE_INFO_NAME));
                gMap.put("fPath", map.get(GetFilesUtils.FILE_INFO_PATH));
                aList.add(gMap);
            }
        } else {
            aList.clear();
        }

        sAdapter.replaceData(aList);
        mBinding.tvFloderLocation.setText(file);
        dismissLoading();
    }


    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.tv_floder_location || v.getId() == R.id.iv_return) {
//            try {
//                if (mBinding.tvFloderLocation.getText().toString().equals("/storage/emulated/0")) {
//                    finish();
//                }
//                String folder = GetFilesUtils.getInstance().getParentPath(mBinding.tvFloderLocation.getText().toString());
//                if (folder == null) {
//                    Toast.makeText(this, "无父目录，待处理", Toast.LENGTH_SHORT).show();
//                } else {
//
//                    loadFolderList(folder);
//                }
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
            finish();
        }
        if (v.getId() == R.id.tv_select_all) {
            if (isSelectedAll) {
                isSelectedAll = false;
                mBinding.tvSelectAll.setText("全选");
                sAdapter.allSelected(false);
            } else {
                isSelectedAll = true;
                mBinding.tvSelectAll.setText("取消全选");
                sAdapter.allSelected(true);
            }
            sAdapter.refresh();
        }
    }


    @Override
    public void onBackPressed() {
//        try {
//            if (mBinding.tvFloderLocation.getText().toString().equals("/storage/emulated/0")) {
//                super.onBackPressed();
//                return;
//            }
//            String folder = GetFilesUtils.getInstance().getParentPath(mBinding.tvFloderLocation.getText().toString());
//            if (folder == null) {
//                super.onBackPressed();
//            } else {
//                loadFolderList(folder);
//            }
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            super.onBackPressed();
//        }
        finish();
    }
}
