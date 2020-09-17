package com.mijia.app.ui.trans.fragment;

import android.content.Intent;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.handong.framework.base.BaseFragment;
import com.hanlyjiang.library.fileviewer.FileViewer;
import com.lzy.imagepicker.ui.ImageBroseActivity;
import com.mijia.app.MyApp;
import com.mijia.app.R;
import com.mijia.app.bean.DownRowTaskBean;
import com.mijia.app.bean.DownloadListBean;
import com.mijia.app.bean.ObservableEmptyImp;
import com.mijia.app.bean.SimpleBean;
import com.mijia.app.bean.UploadRequestBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.databinding.FragmentTranferUploadBinding;
import com.mijia.app.socket.DownTask;
import com.mijia.app.socket.UpLoadManager;
import com.mijia.app.socket.UpLoadTask;
import com.mijia.app.ui.other.activity.PlayMusicActivity;
import com.mijia.app.ui.other.activity.PlayVideoActivity;
import com.mijia.app.ui.trans.TransferUploadViewModel;
import com.mijia.app.ui.trans.adapter.UploadFailAdapter;
import com.mijia.app.ui.trans.adapter.UploadFinishAdapter;
import com.mijia.app.ui.trans.adapter.UploadingAdapter;
import com.mijia.app.utils.FileUtils;
import com.mijia.app.utils.NativeShareTool;
import com.mijia.app.utils.ShareToolUtil;
import com.mijia.app.utils.file.MediaBean;
import com.mijia.app.utils.file.MediaBean_;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import io.objectbox.Box;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Administrator on 2019/6/11.
 */

public class TransferUploadFragment extends BaseFragment<FragmentTranferUploadBinding, TransferUploadViewModel> implements UpLoadManager.UpLoadScheduleListener, BaseQuickAdapter.OnItemChildClickListener {

    public ObservableBoolean isUploadingOpen = new ObservableBoolean(true);
    public ObservableBoolean isUploadFailOpen = new ObservableBoolean(true);
    public ObservableBoolean isUploadFinishOpen = new ObservableBoolean(true);


    private UploadingAdapter mUploadingAdapter;
    private UploadFailAdapter mUploadFailAdapter;
    private UploadFinishAdapter mUploadFinishAdapter;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_tranfer_upload;
    }

    private Box<MediaBean> mBox;

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mBox = MyApp.getInstance().getBoxStore().boxFor(MediaBean.class);
        binding.setHandler(this);
        binding.setIsOpenSelect(TransferListFragment.isOpenSelect);
        TransferListFragment.isOpenSelect.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (TransferListFragment.pageIndex.get() == 1) {
                    mUploadFinishAdapter.openOrCloseSelectMode(TransferListFragment.isOpenSelect.get());
                    mUploadFailAdapter.openOrCloseSelectMode(TransferListFragment.isOpenSelect.get());
                    mUploadingAdapter.openOrCloseSelectMode(TransferListFragment.isOpenSelect.get());

                    if (!TransferListFragment.isOpenSelect.get()) {
                        mUploadFinishAdapter.clearAllSelect();
                        mUploadFailAdapter.clearAllSelect();
                        mUploadingAdapter.clearAllSelect();

                        binding.ivAllSelectUploading.setSelected(false);
                        binding.ivAllSelectFail.setSelected(false);
                        binding.ivAllSelectFinish.setSelected(false);
                    }
                }
            }
        });

        TransferListFragment.pageIndex.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (TransferListFragment.pageIndex.get() != 1) {
                    mUploadFinishAdapter.clearAllSelect();
                    mUploadFailAdapter.clearAllSelect();
                    mUploadFailAdapter.openOrCloseSelectMode(false);
                    mUploadingAdapter.clearAllSelect();
                    mUploadingAdapter.openOrCloseSelectMode(false);
                }
            }
        });
        mUploadingAdapter = new UploadingAdapter();
        binding.recyclerUploading.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        binding.recyclerUploading.setAdapter(mUploadingAdapter);
        mUploadingAdapter.setOnItemChildClickListener(this);
        binding.recyclerUploading.setFocusableInTouchMode(false);
        binding.recyclerUploading.requestFocus();

        mUploadFailAdapter = new UploadFailAdapter();
        binding.recyclerUploadFail.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerUploadFail.setAdapter(mUploadFailAdapter);
        mUploadFailAdapter.setOnItemChildClickListener(this);
        binding.recyclerUploadFail.setFocusableInTouchMode(false);
        binding.recyclerUploadFail.requestFocus();

        mUploadFinishAdapter = new UploadFinishAdapter();
        binding.recyclerUploadFinish.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerUploadFinish.setAdapter(mUploadFinishAdapter);
        mUploadFinishAdapter.setOnItemChildClickListener(this);
        binding.recyclerUploadFinish.setFocusableInTouchMode(false);
        binding.recyclerUploadFinish.requestFocus();

        UpLoadManager.getInstance().setUpLoadScheduleListener(this);
        onTaskListChange();
    }

    private void refreshAllListStatus() {
        if (mUploadingAdapter.getSelectedNum() == 0 && mUploadFailAdapter.getSelectedNum() == 0 && mUploadFinishAdapter.getSelectedNum() == 0) {
            TransferListFragment.selectedNum.set(0);
            TransferListFragment.isOpenSelect.set(false);
            TransferListFragment.pageIndex.set(1);
            TransferListFragment.selectType.set(0);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_uploading:
                isUploadingOpen.set(!isUploadingOpen.get());
                break;
            case R.id.ll_upload_fail:
                isUploadFailOpen.set(!isUploadFailOpen.get());
                break;
            case R.id.ll_upload_finish:
                isUploadFinishOpen.set(!isUploadFinishOpen.get());
                break;
            case R.id.iv_all_select_uploading:
                binding.ivAllSelectUploading.setSelected(!binding.ivAllSelectUploading.isSelected());
                if (binding.ivAllSelectUploading.isSelected()) {
                    changeSelectedType(1);
                    mUploadingAdapter.selectAll();
                } else {
                    mUploadingAdapter.clearAllSelect();
                }

                break;
            case R.id.iv_all_select_fail:
                binding.ivAllSelectFail.setSelected(!binding.ivAllSelectFail.isSelected());
                if (binding.ivAllSelectFail.isSelected()) {
                    changeSelectedType(2);
                    mUploadFailAdapter.selectAll();
                } else {
                    mUploadFailAdapter.clearAllSelect();
                }

                break;
            case R.id.iv_all_select_finish:
                binding.ivAllSelectFinish.setSelected(!binding.ivAllSelectFinish.isSelected());
                if (binding.ivAllSelectFinish.isSelected()) {
                    changeSelectedType(3);
                    mUploadFinishAdapter.selectAll();
                } else {
                    mUploadFinishAdapter.clearAllSelect();
                }

                break;
            case R.id.tv_all_pause:
                // 暂停
                String text = binding.tvAllPause.getText().toString();
                if (TextUtils.equals(text, "全部暂停")) {
                    binding.tvAllPause.setText("全部开始");
                    mUploadingAdapter.setPause(true);
                } else {
                    binding.tvAllPause.setText("全部暂停");
                    mUploadingAdapter.setPause(false);
                }
                mUploadingAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_all_restart:
                List<UpLoadTask> list = mUploadFailAdapter.getData();
                if (list != null && !list.isEmpty()) {
                    for (UpLoadTask task : list) {
                        task.retryTask();
                    }
                }
                break;
        }
    }

    private void changeSelectedType(int type) {
        switch (type) {
            case 1://下载中
                mUploadFinishAdapter.clearAllSelect();
                mUploadFailAdapter.clearAllSelect();
                binding.ivAllSelectFinish.setSelected(false);
                binding.ivAllSelectFail.setSelected(false);
                if (TransferListFragment.selectType.get() != 1) {
                    TransferListFragment.selectType.set(1);
                }
                break;
            case 2://失败
                mUploadFinishAdapter.clearAllSelect();
                mUploadingAdapter.clearAllSelect();
                binding.ivAllSelectFinish.setSelected(false);
                binding.ivAllSelectUploading.setSelected(false);
                if (TransferListFragment.selectType.get() != 1) {
                    TransferListFragment.selectType.set(1);
                }
                break;
            case 3://完成
                binding.ivAllSelectFail.setSelected(false);
                binding.ivAllSelectUploading.setSelected(false);
                mUploadFailAdapter.clearAllSelect();
                mUploadingAdapter.clearAllSelect();
                if (TransferListFragment.selectType.get() != 2) {
                    TransferListFragment.selectType.set(2);
                }
                break;
        }
    }

    @Override
    public void onUpSchedule() {
        mUploadingAdapter.notifyDataSetChanged();
    }


    @Override
    public void onTaskListChange() {


        io.reactivex.Observable observable = io.reactivex.Observable.create((ObservableOnSubscribe<List<List<UpLoadTask>>>) emitter -> {
            ConcurrentMap<String, UpLoadTask> taskList = UpLoadManager.getInstance().getTaskList();

            List<List<UpLoadTask>> resultValue = new ArrayList<>();
            List<UpLoadTask> upLoadList = new ArrayList<>();
            List<UpLoadTask> upErrorList = new ArrayList<>();
            List<UpLoadTask> upFinishList = new ArrayList<>();


            for (String key : taskList.keySet()) {
                UpLoadTask task = taskList.get(key);
                int taskStatus = task.getCurrTaskStatus();
                /**
                 * 0:第一次发送了请求 ，但是没有收到回应
                 * 1：发送之后，接收到响应 （传输中）
                 * 2：传输完成
                 * 3：长时间 未接收到响应（上传失败）
                 */
                if (0 == taskStatus || 1 == taskStatus) {
                    upLoadList.add(task);
                }
                if (3 == taskStatus) {
                    upErrorList.add(task);
                }
                if (2 == taskStatus) {
                    upFinishList.add(task);
                }

//                MediaBean mediaBean = mBox.query().equal(MediaBean_.path, task.getSrcPath()).build().findFirst();
//                mediaBean.setUpLoadingStatus(taskStatus);
//                mBox.put(mediaBean);
            }
            resultValue.add(upLoadList);
            resultValue.add(upErrorList);
            resultValue.add(upFinishList);

            emitter.onNext(resultValue);

        });

        addSubscription(observable, new ObservableEmptyImp<List<List<UpLoadTask>>>() {
            @Override
            public void onNext(List<List<UpLoadTask>> value) {
                super.onNext(value);
                List<UpLoadTask> loadingList = new ArrayList<>();
                List<UpLoadTask> loaderrList = new ArrayList<>();
                List<UpLoadTask> loadedList = new ArrayList<>();

                loadingList.addAll(value.get(0));
                loaderrList.addAll(value.get(1));
                loadedList.addAll(value.get(2));

                isUploadingOpen.set(loadingList.size() > 0);
                isUploadFailOpen.set(loaderrList.size() > 0);
                isUploadFinishOpen.set(loadedList.size() > 0);

                mUploadingAdapter.setNewData(loadingList);
                mUploadFailAdapter.setNewData(loaderrList);
                mUploadFinishAdapter.setNewData(loadedList);


            }
        });


    }

    private boolean endWiths(String str, List<String> strings) {
        str = str.toLowerCase();
        for (String string : strings) {
            if (str.endsWith(string)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int id = view.getId();
        UpLoadTask task = (UpLoadTask) adapter.getItem(position);
        if (id == R.id.ll_main) {
            String strLater = task.getFileName();
            String path = task.getSrcPath();

            if (StringUtils.isEmpty(strLater)) {

            } else if (endWiths(strLater, Arrays.asList("doc", "docx", "DOC", "DOCX"))) {
//                                    Uri uri = Uri.fromFile(new File(path));
//                FileViewer.viewFile(getActivity(), path);
                FileUtils.openFileReader(getActivity(), path);
            } else if (endWiths(strLater, Arrays.asList("execl", "EXECL"))) {
//                FileViewer.viewFile(getActivity(), path);
                FileUtils.openFileReader(getActivity(), path);
            } else if (endWiths(strLater, Arrays.asList("pdf", "PDF"))) {
//                FileViewer.viewFile(getActivity(), path);
                FileUtils.openFileReader(getActivity(), path);
            } else if (endWiths(strLater, Arrays.asList("ppt", "PPT"))) {
//                FileViewer.viewFile(getActivity(), path);
                FileUtils.openFileReader(getActivity(), path);
            } else if (endWiths(strLater, Arrays.asList("psd", "PSD"))) {
//                FileViewer.viewFile(getActivity(), path);
                FileUtils.openFileReader(getActivity(), path);
            } else if (endWiths(strLater, Arrays.asList("txt", "TXT"))) {
//                FileViewer.viewFile(getActivity(), path);
                FileUtils.openFileReader(getActivity(), path);
            } else if (endWiths(strLater, Arrays.asList("word", "WORD"))) {
//                FileViewer.viewFile(getActivity(), path);
                FileUtils.openFileReader(getActivity(), path);
            } else if (endWiths(strLater, Arrays.asList("xls", "xlsx", "XLS", "XLSX"))) {
//                FileViewer.viewFile(getActivity(), path);
                FileUtils.openFileReader(getActivity(), path);
            } else if (endWiths(strLater, Arrays.asList("MP3", "WAV", "CDA", "WMA", "mp3", "AAC"))) {
                startActivity(new Intent(getActivity(), PlayMusicActivity.class).putExtra("MusicPath", path));
            } else if (endWiths(strLater, Arrays.asList("png", "jpg", "jpeg", "PNG", "JPG", "JPEG"))) {
                ArrayList<String> list = new ArrayList<>();
                list.add(path);
                startActivity(new Intent(getActivity(), ImageBroseActivity.class).putExtra(ImageBroseActivity.PICS, list));
            } else if (endWiths(strLater, Arrays.asList("mp4", "rmvb", "avi", "MP4", "RMVB", "AVI"))) {
                startActivity(new Intent(getContext(), PlayVideoActivity.class).putExtra(Constants.VIDEOPATH, path));
            }
            return;
        }

        if (id == R.id.finishSelectView) {
            task.setSelected(!task.isSelected());
            boolean allSelected = mUploadFinishAdapter.getIsAllSelected();
            if (allSelected) {
                binding.ivAllSelectFinish.setSelected(true);
            } else {
                binding.ivAllSelectFinish.setSelected(false);
            }
            changeSelectedType(3);
            adapter.notifyDataSetChanged();
            return;
        }

        if (id == R.id.ivUpLoadingSelect) {
            task.setSelected(!task.isSelected());
            boolean isAllSelected = mUploadingAdapter.getIsAllSelected();
            binding.ivAllSelectUploading.setSelected(false);
            if (isAllSelected) {
                binding.ivAllSelectUploading.setSelected(true);
            }
            changeSelectedType(1);
            adapter.notifyDataSetChanged();
            return;
        }

        if (id == R.id.upLoadingProgressBar) {
            if (task.isPause()) {
                task.setPause(false);
            } else {
                task.setPause(true);
            }
            adapter.notifyDataSetChanged();
            return;
        }

        if (id == R.id.retryBtn) {
            // 单个的失败重试
            task.retryTask();
            return;
        }

        if (id == R.id.ivFailSelectImage) {
            task.setSelected(!task.isSelected());
            boolean isAllSelected = mUploadFailAdapter.getIsAllSelected();
            if (isAllSelected) {
                binding.ivAllSelectFail.setSelected(true);
            } else {
                binding.ivAllSelectFail.setSelected(false);
            }
            changeSelectedType(2);
            adapter.notifyDataSetChanged();
            return;
        }
    }

    public void delSelectedList() {
        UpLoadManager upLoadManager = UpLoadManager.getInstance();

        List<UpLoadTask> upLoadingList = mUploadingAdapter.getData();
        List<UpLoadTask> upLoadFailList = mUploadFailAdapter.getData();
        List<UpLoadTask> upLoadFinishList = mUploadFinishAdapter.getData();

        for (UpLoadTask task : upLoadingList) {
            String key = task.getUploadRequestBean().getFullpath();
            if (task.isSelected()) {
                upLoadManager.removeTask(key);
            }
        }

        for (UpLoadTask task : upLoadFailList) {
            String key = task.getUploadRequestBean().getFullpath();
            if (task.isSelected()) {
                upLoadManager.removeTask(key);
            }
        }

        for (UpLoadTask task : upLoadFinishList) {
            String key = task.getUploadRequestBean().getFullpath();
            if (task.isSelected()) {
                upLoadManager.removeTask(key);
            }
        }
        onTaskListChange();
        if (TransferListFragment.isOpenSelect.get()) {
            TransferListFragment.isOpenSelect.set(false);
            TransferListFragment.selectType.set(0);
        }
    }

    public void shareFile() {

        List<UpLoadTask> upLoadFinishList = new ArrayList<>();
        for (UpLoadTask datum : mUploadFinishAdapter.getData()) {
            if (datum.isSelected()) {
                upLoadFinishList.add(datum);
            }
        }
        if (upLoadFinishList.size() > 1) {
            ToastUtils.showShort("不能同时分享多个文件！");
            return;
        }
        if (upLoadFinishList.size() == 0) {
            ToastUtils.showShort("请选择要分享的文件！");
            return;
        }
        String path = "";
        if (upLoadFinishList.size() == 1) {
            path = upLoadFinishList.get(0).getSrcPath();
        }

        TransferListFragment.isOpenSelect.set(false);
        TransferListFragment.selectType.set(0);


        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(getActivity(), ShareToolUtil.AUTHORITY, new File(path));
        } else {
            uri = Uri.fromFile(new File(path));
        }
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("*/*");//此处可发送多种文件
        startActivity(Intent.createChooser(shareIntent, "分享"));
//        nativeShareTool.shareWechatFriend(new File(path),false);
    }
}
