package com.mijia.app.ui.trans.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.blankj.utilcode.util.ZipUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.handong.framework.account.AccountHelper;
import com.handong.framework.base.BaseFragment;
import com.hanlyjiang.library.fileviewer.FileViewer;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.lzy.imagepicker.ui.ImageBroseActivity;
import com.mijia.app.R;
import com.mijia.app.bean.ObservableEmptyImp;
import com.mijia.app.constants.Constants;
import com.mijia.app.constants.RxBusAction;
import com.mijia.app.databinding.FragmentTransferDownloadingBinding;
import com.mijia.app.socket.DownLoadManager;
import com.mijia.app.socket.DownTask;
import com.mijia.app.ui.other.activity.PlayMusicActivity;
import com.mijia.app.ui.other.activity.PlayVideoActivity;
import com.mijia.app.ui.trans.TransferDownloadViewModel;
import com.mijia.app.ui.trans.adapter.DownLoadFailAdapter;
import com.mijia.app.ui.trans.adapter.DownloadFinishAdapter;
import com.mijia.app.ui.trans.adapter.DownloadingAdapter;
import com.mijia.app.utils.FileUtils;
import com.mijia.app.utils.NativeShareTool;
import com.mijia.app.utils.ShareToolUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Administrator on 2019/6/11.
 */

public class TransferDownloadFragment extends BaseFragment<FragmentTransferDownloadingBinding, TransferDownloadViewModel> implements View.OnClickListener, DownLoadManager.DownLoadScheduleListener, BaseQuickAdapter.OnItemChildClickListener {


    private DownloadingAdapter mDownloadingAdapter;
    private DownLoadFailAdapter mDownLoadFailAdapter;
    private DownloadFinishAdapter mDownloadFinishAdapter;
    private DownLoadManager mDownLoadManager = DownLoadManager.getInstance();

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_transfer_downloading;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        binding.setListener(this);
        binding.setIsOpenSelect(TransferListFragment.isOpenSelect);
        RxBus.get().register(this);
        TransferListFragment.isOpenSelect.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                if (0 == TransferListFragment.pageIndex.get()) {
                    mDownloadFinishAdapter.openOrCloseSelectMode(TransferListFragment.isOpenSelect.get());
                    mDownLoadFailAdapter.openOrCloseSelectMode(TransferListFragment.isOpenSelect.get());
                    mDownloadingAdapter.openOrCloseSelectMode(TransferListFragment.isOpenSelect.get());

                    if (!TransferListFragment.isOpenSelect.get()) {
                        binding.ivAllSelectDownloading.setSelected(false);
                        binding.ivAllSelectFail.setSelected(false);
                        binding.ivAllSelectFinish.setSelected(false);

                        mDownloadingAdapter.clearAllSelect();
                        mDownLoadFailAdapter.clearAllSelect();
                        mDownloadFinishAdapter.clearAllSelect();
                    }
                }
            }
        });

        mDownLoadManager.setDownLoadScheduleListener(this);

        mDownloadingAdapter = new DownloadingAdapter();
        binding.downLoadingList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.downLoadingList.setAdapter(mDownloadingAdapter);
        binding.downLoadingList.setFocusableInTouchMode(false);
        binding.downLoadingList.requestFocus();


        mDownLoadFailAdapter = new DownLoadFailAdapter();
        binding.recyclerDownloadFail.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerDownloadFail.setAdapter(mDownLoadFailAdapter);
        binding.recyclerDownloadFail.setFocusableInTouchMode(false);
        binding.recyclerDownloadFail.requestFocus();


        mDownloadFinishAdapter = new DownloadFinishAdapter();
        binding.recyclerDownloadFinish.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerDownloadFinish.setAdapter(mDownloadFinishAdapter);
        binding.recyclerDownloadFinish.setFocusableInTouchMode(false);
        binding.recyclerDownloadFinish.requestFocus();

        mDownloadingAdapter.setOnItemChildClickListener(this);
        mDownLoadFailAdapter.setOnItemChildClickListener(this);
        mDownloadFinishAdapter.setOnItemChildClickListener(this);

        mDownloadFinishAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                TransferListFragment.isOpenSelect.set(true);
                DownTask downTask = (DownTask) adapter.getItem(position);
                // 完成的多选
                downTask.setSelected(!downTask.isSelected());
                binding.ivAllSelectFinish.setSelected(false);
                if (mDownloadFinishAdapter.getIsAllSelected()) {
                    binding.ivAllSelectFinish.setSelected(true);
                }
                adapter.notifyDataSetChanged();
                changeSelectedType(3);
                return false;
            }
        });


        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();


        String userInfo = AccountHelper.getNickname() + AccountHelper.getUserId();
        ///sdcard/95xiu

        File absolutePathFile = new File(absolutePath, "秘夹/" + userInfo);

        binding.tvDownloadPath.setText(absolutePathFile.getAbsolutePath());
//        tv_download_path
    }



    @Subscribe(tags = {@Tag(RxBusAction.MAIN_PAGE_IN_TRAN)},thread = EventThread.MAIN_THREAD)
    public void refreshData(String str){
        onTaskListChange();
    }



    @Override
    public void onResume() {
        super.onResume();
        onTaskListChange();
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.downAddressLayout) {
//            viewModel.getOneFileInfo();
            return;
        }
        if (id == R.id.downLoadingAllPauseText) {
            String text = binding.downLoadingAllPauseText.getText().toString();
            if (TextUtils.equals(text, "全部暂停")) {
                mDownloadingAdapter.setAllPause();
                binding.downLoadingAllPauseText.setText("全部开始");
            } else {
                mDownloadingAdapter.setAllStart();
                binding.downLoadingAllPauseText.setText("全部暂停");
            }
            return;
        }

        if (id == R.id.downLoadingExpandLayout) {
            if (binding.downLoadingList.getVisibility() == View.VISIBLE) {
                binding.downLoadingList.setVisibility(View.GONE);
                binding.loadingNumberText.setSelected(false);
            } else {
                binding.downLoadingList.setVisibility(View.VISIBLE);
                binding.loadingNumberText.setSelected(true);
            }
            return;
        }

        if (id == R.id.retryAllText) {
            List<DownTask> list = mDownLoadFailAdapter.getData();
            if (list != null && !list.isEmpty()) {
                for (DownTask task : list) {
                    task.retry();
                }
            }
            return;
        }

        if (id == R.id.ll_download_fail) {
            if (binding.recyclerDownloadFail.getVisibility() == View.VISIBLE) {
                binding.recyclerDownloadFail.setVisibility(View.GONE);
                binding.errorText.setSelected(false);
            } else {
                binding.recyclerDownloadFail.setVisibility(View.VISIBLE);
                binding.errorText.setSelected(true);
            }
            return;
        }

        if (id == R.id.ll_download_finish) {
            if (binding.recyclerDownloadFinish.getVisibility() == View.VISIBLE) {
                binding.recyclerDownloadFinish.setVisibility(View.GONE);
                binding.downFinishNumberText.setSelected(false);
                binding.llDownloadFinish.setPadding(0, 0, 0, SizeUtils.dp2px(50));
            } else {
                binding.llDownloadFinish.setPadding(0, 0, 0, 0);
                binding.recyclerDownloadFinish.setVisibility(View.VISIBLE);
                binding.downFinishNumberText.setSelected(true);
            }
            return;
        }

        if (id == R.id.iv_all_select_downloading) {
            binding.ivAllSelectDownloading.setSelected(!binding.ivAllSelectDownloading.isSelected());
            if (binding.ivAllSelectDownloading.isSelected()) {
                mDownloadingAdapter.setAllSelectStatus(true);
            } else {
                mDownloadingAdapter.setAllSelectStatus(false);
            }
            changeSelectedType(1);
            return;
        }
        if (id == R.id.iv_all_select_fail) {
            binding.ivAllSelectFail.setSelected(!binding.ivAllSelectFail.isSelected());
            if (binding.ivAllSelectFail.isSelected()) {
                mDownLoadFailAdapter.selectAll();
            } else {
                mDownLoadFailAdapter.clearAllSelect();
            }
            changeSelectedType(2);
            return;
        }
        if (id == R.id.iv_all_select_finish) {
            binding.ivAllSelectFinish.setSelected(!binding.ivAllSelectFinish.isSelected());
            if (binding.ivAllSelectFinish.isSelected()) {
                mDownloadFinishAdapter.selectAll();
            } else {
                mDownloadFinishAdapter.clearAllSelect();
            }
            changeSelectedType(3);
        }
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
        DownTask downTask = (DownTask) adapter.getItem(position);
        int id = view.getId();
        if (id == R.id.finishiRootViewLayout) {
            String strLater = downTask.getFileName();
            String path = downTask.getLocationFilePath();

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
            // 完成的多选
            downTask.setSelected(!downTask.isSelected());
            binding.ivAllSelectFinish.setSelected(false);
            if (mDownloadFinishAdapter.getIsAllSelected()) {
                binding.ivAllSelectFinish.setSelected(true);
            }
            adapter.notifyDataSetChanged();
            changeSelectedType(3);
            return;
        }

        if (id == R.id.downLoadingProgressBar) {
            if (downTask.isPause()) {
                downTask.setPause(false);
            } else {
                downTask.setPause(true);
            }
            adapter.notifyDataSetChanged();
            boolean isAllPause = mDownloadingAdapter.getIsAllPause();
            if (isAllPause) {
                binding.downLoadingAllPauseText.setText("全部开始");
            } else {
                binding.downLoadingAllPauseText.setText("全部暂停");
            }
            return;
        }

        if (id == R.id.downLoadingSelectImage) {
            downTask.setSelected(!downTask.isSelected());
            adapter.notifyDataSetChanged();
            if (mDownloadingAdapter.getIsAllSelected()) {
                binding.ivAllSelectDownloading.setSelected(true);
            } else {
                binding.ivAllSelectDownloading.setSelected(false);
            }
            changeSelectedType(1);
            return;
        }

        if (id == R.id.retryBtn) {
            downTask.retry();
            return;
        }
        if (id == R.id.iv_fail_select) {

            downTask.setSelected(!downTask.isSelected());
            adapter.notifyDataSetChanged();
            if (mDownLoadFailAdapter.getIsAllSelected()) {
                binding.ivAllSelectFail.setSelected(true);
            } else {
                binding.ivAllSelectFail.setSelected(false);
            }
            changeSelectedType(2);
            return;
        }
    }

    private void changeSelectedType(int type) {
        switch (type) {
            case 1://下载中
                mDownloadFinishAdapter.clearAllSelect();
//                mDownLoadFailAdapter.clearAllSelect();
                binding.ivAllSelectFinish.setSelected(false);
//                binding.ivAllSelectFail.setSelected(false);
                if (TransferListFragment.selectType.get() != 1) {
                    TransferListFragment.selectType.set(1);
                }
                break;
            case 2://失败
                mDownloadFinishAdapter.clearAllSelect();
                binding.ivAllSelectFinish.setSelected(false);
//                binding.ivAllSelectDownloading.setSelected(false);
                if (TransferListFragment.selectType.get() != 1) {
                    TransferListFragment.selectType.set(1);
                }
                break;
            case 3://完成
                binding.ivAllSelectFail.setSelected(false);
                binding.ivAllSelectDownloading.setSelected(false);
                mDownLoadFailAdapter.clearAllSelect();
                mDownloadingAdapter.clearAllSelect();

                if (TransferListFragment.selectType.get() != 2) {
                    TransferListFragment.selectType.set(2);
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onLoadSchedule() {
        mDownloadingAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTaskListChange() {
        Observable observable = Observable.create((ObservableOnSubscribe<List<List<DownTask>>>) emitter -> {
            Map<String, DownTask> value = DownLoadManager.getInstance().getTaskList();
            if (value == null || value.isEmpty()) {
                emitter.onNext(new ArrayList<>());
                return;
            }
            List<DownTask> loadingList = new ArrayList<>();
            List<DownTask> loadedList = new ArrayList<>();
            List<DownTask> loaderrList = new ArrayList<>();
            for (String key : value.keySet()) {
                DownTask downTask = value.get(key);
                int currTaskStatus = downTask.getCurrTaskStatus();
                if (0 == currTaskStatus || 1 == currTaskStatus || 4 == currTaskStatus) {
                    loadingList.add(downTask);
                }
                if (2 == currTaskStatus) {
                    loadedList.add(downTask);
                }
                if (3 == currTaskStatus) {
                    loaderrList.add(downTask);
                }
            }
            Collections.sort(loadingList, (downTask, t1) -> downTask.getTaskId() > t1.getTaskId() ? 1 : 0);
            Collections.sort(loadedList, (downTask, t1) -> downTask.getTaskId() > t1.getTaskId() ? 0 : 1);
            Collections.sort(loaderrList, (downTask, t1) -> downTask.getTaskId() > t1.getTaskId() ? 1 : 0);
            List<List<DownTask>> data = new ArrayList<>();
            data.add(loadingList);
            data.add(loadedList);
            data.add(loaderrList);
            emitter.onNext(data);
        });


        addSubscription(observable, new ObservableEmptyImp<List<List<DownTask>>>() {
            @Override
            public void onNext(List<List<DownTask>> list) {
                super.onNext(list);
                List<DownTask> loadingList = new ArrayList<>();
                List<DownTask> loadedList = new ArrayList<>();
                List<DownTask> loaderrList = new ArrayList<>();

                if (!list.isEmpty()) {
                    loadingList.addAll(list.get(0));
                    loadedList.addAll(list.get(1));
                    loaderrList.addAll(list.get(2));
                }

                binding.loadingNumberText.setSelected(false);
                binding.errorText.setSelected(false);
                binding.downFinishNumberText.setSelected(false);

                if (!loadingList.isEmpty() && mDownloadingAdapter.getData().isEmpty()) {
                    binding.loadingNumberText.setSelected(true);
                }

                if (!loaderrList.isEmpty() && mDownLoadFailAdapter.getData().isEmpty()) {
                    binding.errorText.setSelected(true);
                }

                if (!loadedList.isEmpty() && mDownloadFinishAdapter.getData().isEmpty()) {
                    binding.downFinishNumberText.setSelected(true);
                }

                mDownloadingAdapter.setNewData(loadingList);
                mDownLoadFailAdapter.setNewData(loaderrList);
                mDownloadFinishAdapter.setNewData(loadedList);

                binding.loadingNumberText.setSelected(loadingList.size()>0);
                binding.errorText.setSelected(loaderrList.size()>0);
                binding.downFinishNumberText.setSelected(loadedList.size()>0);

                binding.loadingNumberText.setText("正在下载(" + loadingList.size() + ")");
                binding.errorText.setText("下载失败(" + loaderrList.size() + ")");
                binding.downFinishNumberText.setText("下载完成(" + loadedList.size() + ")");

                boolean isAllPause = mDownloadingAdapter.getIsAllPause();
                if (isAllPause) {
                    binding.downLoadingAllPauseText.setText("全部开始");
                } else {
                    binding.downLoadingAllPauseText.setText("全部暂停");
                }
            }
        });
    }

    public void delSelectedList() {
        if (mDownloadingAdapter == null) {
            return;
        }
        List<DownTask> downLoadingList = mDownloadingAdapter.getSelectedTaskList();
        for (DownTask task : downLoadingList) {
            if (task.getCurrTaskStatus() == 1 || task.getCurrTaskStatus() == 0) {
                task.setPause(true);
            }
            String key = task.getDownLoadResponseBean().getFullpath();
            key = key + task.getDownLoadResponseBean().getType();
            mDownLoadManager.removeTask(key);
        }

        List<DownTask> downFailList = mDownLoadFailAdapter.getSelectedTaskList();
        for (DownTask task : downFailList) {
            String key = task.getDownLoadResponseBean().getFullpath();
            key = key + task.getDownLoadResponseBean().getType();
            mDownLoadManager.removeTask(key);
        }
        List<DownTask> downFinishList = mDownloadFinishAdapter.getSelectedTaskList();
        for (DownTask task : downFinishList) {
            String key = task.getDownLoadResponseBean().getFullpath();
            key = key + task.getDownLoadResponseBean().getType();
            mDownLoadManager.removeTask(key);
        }

        TransferListFragment.isOpenSelect.set(false);
        TransferListFragment.selectType.set(0);
    }

    public void shareFile() {

        List<DownTask> downFinishList = mDownloadFinishAdapter.getSelectedTaskList();
        if (downFinishList.size() > 1) {
            ToastUtils.showShort("不能同时分享多个文件！");
            return;
        }
        if (downFinishList.size() == 0) {
            ToastUtils.showShort("请选择要分享的文件！");
            return;
        }
        String path = "";
        if (downFinishList.size() == 1) {
            path = downFinishList.get(0).getLocationFilePath();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onUnsubscribe();
        RxBus.get().unregister(this);
    }
}
