package com.mijia.app.ui.disk.fragment;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.handong.framework.account.AccountHelper;
import com.handong.framework.base.BaseFragment;
import com.handong.framework.base.BaseViewModel;
import com.hanlyjiang.library.fileviewer.FileViewer;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.lzy.imagepicker.ui.ImageBroseActivity;
import com.mijia.app.MainActivity;
import com.mijia.app.R;
import com.mijia.app.bean.DiskInfoBean;
import com.mijia.app.bean.DownLoadRequestBean;
import com.mijia.app.bean.FileBean;
import com.mijia.app.bean.HomeDataBean;
import com.mijia.app.bean.SimpleBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.constants.RxBusAction;
import com.mijia.app.constants.Sys;
import com.mijia.app.databinding.FragmentDiskManagerHomeBinding;
import com.mijia.app.presenter.OnServerTransListener;
import com.mijia.app.presenter.SeverTransOrderPresenter;
import com.mijia.app.service.ReciveFloderOrderListener;
import com.mijia.app.socket.DownTask;
import com.mijia.app.socket.InstructionOutTimeObserver;
import com.mijia.app.socket.MessageReceiveManager;
import com.mijia.app.socket.TempLoadingForMultiple;
import com.mijia.app.socket.UdpDataUtils;
import com.mijia.app.sys.FileBoxUtils;
import com.mijia.app.ui.disk.adapter.DiskFileListAdapter;
import com.mijia.app.ui.home.fragment.HomeFragment;
import com.mijia.app.ui.other.activity.PlayMusicActivity;
import com.mijia.app.ui.other.activity.PlayVideoActivity;
import com.mijia.app.utils.FileUtils;
import com.mijia.app.viewmodel.HomeDataViewModel;
import com.mijia.app.viewmodel.ServerTransOrderViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.mijia.app.constants.Constants.DISK_NOW_PAGE_PATH;
import static com.mijia.app.socket.InstructionOutTimeObserver.getInstance;

/**
 * 磁盘管理文件总览fragment
 *
 * @ClassName:DiskManagerHomeFragment
 * @PackageName:com.mijia.app.ui.disk.fragment
 * @Create On 2019/6/10   10:56
 * @Site:http://www.handongkeji.com
 * @author:闫大仙er
 * @Copyrights 2019/6/10 handongkeji All rights reserved.
 */


public class DiskManagerHomeFragment extends BaseFragment<FragmentDiskManagerHomeBinding, BaseViewModel> implements OnServerTransListener {

    private DiskFileListAdapter mFileListAdapter;
    private String TAG = DiskManagerHomeFragment.class.getSimpleName();

    private ServerTransOrderViewModel mOrderViewModel;
    private InstructionOutTimeObserver mOutTimeObserver;
    private MessageReceiveManager mMessageReceiveManager;

    private SeverTransOrderPresenter severTransOrderPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.get().register(this);
    }

    public void refresh() {
        DISK_NOW_PAGE_PATH = "";
        List<FileBean> fileBeanList = FileBoxUtils.getFileListByPath("");
        Log.i(TAG, "initView: fileListSize==" + fileBeanList.size());
        mFileListAdapter.replaceData(fileBeanList);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_disk_manager_home;
    }


    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        binding.setHandler(this);

        binding.setSys(Constants.SYS_CONTANTS_BEAN);
        binding.setIsMiWenOpen(DiskManagerFragment.isMiWenOpen);
        binding.setIsOpenSelect(DiskManagerFragment.isOpenSelect);
        binding.setSelectedNum(DiskManagerFragment.selectedNum);
        mOutTimeObserver = getInstance();
        mMessageReceiveManager = MessageReceiveManager.getInstance();
        mOrderViewModel = new ServerTransOrderViewModel();
        severTransOrderPresenter = new SeverTransOrderPresenter(this);
        initDataObserver();

        DiskManagerFragment.isOpenSelect.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (!DiskManagerFragment.isOpenSelect.get()) {
                    mFileListAdapter.clearAllSelect();
                }
            }
        });

        binding.smartview.setOnRefreshListener(refreshlayout -> {

            DISK_NOW_PAGE_PATH = "";
//            {
//                "userName": "test_user",
//                    "userId": "aaaaaaaaaa",
//                    "gsName": "test_gs",
//                    "gsId": "bbbbbbbbbbbbbb",
//                    "diskName": "test_disk",
//                    "diskId": "cccccccccccc",
//                    "path": "/"
//            }

            if (Constants.isPcOnLine) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("userName", AccountHelper.getNickname());
                    jsonObject.put("userId", AccountHelper.getUserId());
                    jsonObject.put("gsName", Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());
                    jsonObject.put("gsId", Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
                    jsonObject.put("diskName", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                    jsonObject.put("diskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
                    jsonObject.put("path", "/");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                byte[] data = UdpDataUtils.getDataByte((byte) 0X02, 0, 0, jsonObject.toString().trim());
                if (Constants.isPcConnecting) {
                    // { "userName": "test_user", "userId": "aaaaaaaaaa" }
//                    MainActivity.mConnectBinder.send(data);
//                    mOutTimeObserver.addSendInstructionAndTime("0X02", instruction -> {
//                        ToastUtils.showShort("刷新数据失败!");
//                        binding.smartview.finishRefresh();
//                    });
                    severTransOrderPresenter.orderTrans("0X02", Base64.encodeToString(data, Base64.DEFAULT));
                } else {
                    severTransOrderPresenter.orderTrans("0X02", Base64.encodeToString(data, Base64.DEFAULT));
//                    mOrderViewModel.serverTransOrder(Base64.encodeToString(data, Base64.DEFAULT));
                }
            } else {
                List<FileBean> fileBeanList = FileBoxUtils.getFileListByPath("");
                Log.i(TAG, "initView: fileListSize==" + fileBeanList.size());
                mFileListAdapter.replaceData(fileBeanList);
                binding.smartview.finishRefresh();
            }


        });

        initRecycler();

    }

    @Subscribe(tags = {@Tag(RxBusAction.DISK_HOME_REFRESH)}, thread = EventThread.MAIN_THREAD)
    public void refreshList(String str) {
        binding.smartview.autoRefresh();
    }

    private void initDataObserver() {

        mMessageReceiveManager.setmReciveFloderOrderListener(packet -> {
            byte[] dataBytes = packet.getData();
            byte[] content = new byte[dataBytes.length - 15];
            System.arraycopy(dataBytes, 15, content, 0, content.length);
            String json = new String(content).trim();

            if (!StringUtils.isEmpty(json)) {
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    mFileListAdapter.replaceData(FileBoxUtils.refreshFloderByPath("", json));
                });
            } else {
                ToastUtils.showShort("刷新列表失败！");
            }
            binding.smartview.finishRefresh();
            mOutTimeObserver.cancelInstruction("0X02");
        });


        //服务器转发指令监听 获取orderId

        mOrderViewModel.transOrderBean.observe(this, baseBean -> {
            if (baseBean != null) {
                mOutTimeObserver.addSendInstructionAndTime(baseBean.getOrderId(), instruction -> {

                    if ("0X02".equals(instruction)) {
                        dismissLoading();
                        ToastUtils.showShort("刷新数据失败!");
                        binding.smartview.finishRefresh();
                    }
                });
                mOrderViewModel.queryOrderResult(baseBean.getOrderId());

            }
        });
        //监听服务器转发指令的结果
        mOrderViewModel.queryOrderResult.observe(this, baseBean -> {
            if (baseBean != null && baseBean.getErr().getCode() == 0) {
                if (!StringUtils.isEmpty(baseBean.getJsonResult())) {
                    mOutTimeObserver.cancelInstruction(baseBean.getOrderId());
                    binding.smartview.finishRefresh();
                } else {
                    mOrderViewModel.queryOrderResult(baseBean.getOrderId());
                }
            } else {
                if (baseBean != null && !StringUtils.isEmpty(baseBean.getOrderId())) {
                    if (!mOutTimeObserver.isOutTiming(baseBean.getOrderId())) {
                        mOrderViewModel.queryOrderResult(baseBean.getOrderId());
                    }
                } else {
                    List<FileBean> fileBeanList = FileBoxUtils.getFileListByPath("");
                    Log.i(TAG, "initView: fileListSize==" + fileBeanList.size());
                    mFileListAdapter.replaceData(fileBeanList);
                    binding.smartview.finishRefresh();
                }
            }
        });
    }

    @Subscribe(tags = {@Tag(RxBusAction.FILE_DATA_LOAD_FINISH)}, thread = EventThread.MAIN_THREAD)
    public void dataLoadFinish(String str) {
//        List<FileBean> fileBeanList = FileBoxUtils.getFileListByPath("");
//        Log.i(TAG, "initView: fileListSize==" + fileBeanList.size());
//        mFileListAdapter.replaceData(fileBeanList);
        binding.smartview.autoRefresh();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_miwen://密文下载切换
                DiskManagerFragment.isMiWenOpen.set(!DiskManagerFragment.isMiWenOpen.get());
                if (!DiskManagerFragment.isOpenSelect.get()) {
                    DiskManagerFragment.isOpenSelect.set(true);
                }
                if (!DiskManagerFragment.isMiWenOpen.get()) {
                    binding.tvMiwen.setText("明文下载");
                } else {
                    binding.tvMiwen.setText("密文下载");
                }
                break;
            case R.id.tv_photo://照片
                RxBus.get().post(RxBusAction.DISK_MANAGER_FRAGMENT_CHANGE, Sys.PHOTO);
                break;
            case R.id.tv_video://视频
                RxBus.get().post(RxBusAction.DISK_MANAGER_FRAGMENT_CHANGE, Sys.VIDEO);
                break;
            case R.id.tv_file://文件
                RxBus.get().post(RxBusAction.DISK_MANAGER_FRAGMENT_CHANGE, Sys.FILE);
                break;
            case R.id.tv_voice://音频
                RxBus.get().post(RxBusAction.DISK_MANAGER_FRAGMENT_CHANGE, Sys.VOICE);
                break;
            case R.id.tv_other://其他
                RxBus.get().post(RxBusAction.DISK_MANAGER_FRAGMENT_CHANGE, Sys.OTHER);
                break;
            case R.id.tv_cancel:
                mFileListAdapter.clearAllSelect();
                break;
            default:
                break;
        }
    }


    private void initRecycler() {
        mFileListAdapter = new DiskFileListAdapter(R.layout.item_file_list);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recycler.setAdapter(mFileListAdapter);
        DiskManagerFragment.isMiWenOpen.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                mFileListAdapter.mingmichange();
            }
        });
        mFileListAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.iv_selected_status:
                    if (DiskManagerFragment.isMiWenOpen.get()) {
                        if (mFileListAdapter.getData().get(position).isSelected()) {
                            mFileListAdapter.getData().get(position).setSelected(false);
                        } else {
                            if (mFileListAdapter.getSelectedNum() == 0) {
                                mFileListAdapter.getData().get(position).setSelected(true);
                            } else {
                                ToastUtils.showShort("密文下载只支持单选！");
                            }
                        }
                    } else {
                        mFileListAdapter.getData().get(position).setSelected(!mFileListAdapter.getData().get(position).isSelected());
                    }
                    mFileListAdapter.refresh();
                    break;
                case R.id.ll_item_view:
                    if (!Constants.isPcOnLine || !Constants.isPcConnecting) {
                        ToastUtils.showShort("密夹未在线或者未成功连接通讯！");
                        return;
                    }



                    if (!StringUtils.isEmpty(mFileListAdapter.getData().get(position).getName())) {
                        if (mFileListAdapter.getData().get(position).getName().contains(".")) {

                            if (!Constants.SYS_CONTANTS_BEAN.SelectedDiskIsOnLine.get()) {
                                ToastUtils.showShort("当前磁盘未在线！");
                                return;
                            }

                            String fileName = mFileListAdapter.getData().get(position).getName();
                            String strLater = fileName.substring(fileName.lastIndexOf(".") + 1);


                            if (StringUtils.isEmpty(strLater)) {

                            } else if (equals(strLater, Arrays.asList("html", "doc", "DOCX",
                                    "DOC", "docx", "execl", "pdf", "ppt",
                                    "psd", "txt", "word", "xls", "xlsx"))) {

                                String path = getActivity().getExternalCacheDir().getPath()
                                        + "/秘夹/" + AccountHelper.getNickname()
                                        + AccountHelper.getUserId()
                                        + mFileListAdapter.getData().get(position).getName();

                                File file = new File(path);
                                if (file.exists() && file.length() == Long.parseLong(mFileListAdapter.getData().get(position).getSize())) {
//                                    FileViewer.viewFile(getActivity(), path);
                                    FileUtils.openFileReader(getActivity(),path);
                                    return;
                                }

                                DownLoadRequestBean downloadTaskBean = new DownLoadRequestBean();
                                downloadTaskBean.setFullpath(mFileListAdapter.getData().get(position).getName());
                                downloadTaskBean.setIndex("1");
                                downloadTaskBean.setDiskId(Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
                                downloadTaskBean.setDiskName(Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                                downloadTaskBean.setGsId(Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
                                downloadTaskBean.setGsName(Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());
                                downloadTaskBean.setUserId(AccountHelper.getUserId());
                                downloadTaskBean.setUserName(AccountHelper.getNickname());
                                DownTask downTask = new DownTask(downloadTaskBean, Long.parseLong(mFileListAdapter.getData().get(position).getSize()), true);
                                TempLoadingForMultiple.loadFile(downTask);
                                showLoading("");
                            } else if (equals(strLater, Arrays.asList("MP3", "WAV", "CDA", "WMA", "mp3","AAC"))) {
                                String path = getActivity().getExternalCacheDir().getPath() + "/秘夹/" + AccountHelper.getNickname() + AccountHelper.getUserId() + mFileListAdapter.getData().get(position).getName();
                                File file = new File(path);

                                if (file.exists() && file.length() == Long.parseLong(mFileListAdapter.getData().get(position).getSize())) {
                                    startActivity(new Intent(getActivity(), PlayMusicActivity.class)
                                            .putExtra("MusicPath", path));
                                    return;
                                }

                                DownLoadRequestBean downloadTaskBean = new DownLoadRequestBean();
                                downloadTaskBean.setFullpath(mFileListAdapter.getData().get(position).getName());
                                downloadTaskBean.setIndex("1");
                                downloadTaskBean.setDiskId(Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
                                downloadTaskBean.setDiskName(Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                                downloadTaskBean.setGsId(Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
                                downloadTaskBean.setGsName(Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());
                                downloadTaskBean.setUserId(AccountHelper.getUserId());
                                downloadTaskBean.setUserName(AccountHelper.getNickname());
                                DownTask downTask = new DownTask(downloadTaskBean, Long.parseLong(mFileListAdapter.getData().get(position).getSize()), true);
                                TempLoadingForMultiple.loadAudioFile(downTask);
                                showLoading("");
                            } else if (equals(strLater, Arrays.asList("bmp", "gif", "jpg", "tif", "jpeg", "pic", "png"))) {
                                String path = getActivity().getExternalCacheDir().getPath() + "/秘夹/" + AccountHelper.getNickname() + AccountHelper.getUserId() + mFileListAdapter.getData().get(position).getName();
                                File file = new File(path);

                                if (file.exists() && file.length() == Long.parseLong(mFileListAdapter.getData().get(position).getSize())) {
                                    ArrayList<String> list = new ArrayList<>();
                                    list.add(path);
                                    startActivity(new Intent(getActivity(), ImageBroseActivity.class).putExtra(ImageBroseActivity.PICS, list));
                                    return;
                                }

                                DownLoadRequestBean downloadTaskBean = new DownLoadRequestBean();
                                downloadTaskBean.setFullpath(mFileListAdapter.getData().get(position).getName());
                                downloadTaskBean.setIndex("1");
                                downloadTaskBean.setDiskId(Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
                                downloadTaskBean.setDiskName(Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                                downloadTaskBean.setGsId(Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
                                downloadTaskBean.setGsName(Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());
                                downloadTaskBean.setUserId(AccountHelper.getUserId());
                                downloadTaskBean.setUserName(AccountHelper.getNickname());
                                DownTask downTask = new DownTask(downloadTaskBean, Long.parseLong(mFileListAdapter.getData().get(position).getSize()), true);
                                TempLoadingForMultiple.loadPicFile(downTask);
                                showLoading("");

                            } else if (equals(strLater, Arrays.asList("mp4", "mpg", "mpe", "dat", "mpeg", "mov"
                                    , "asf", "avi", "rmvb"))) {
                                String path = getActivity().getExternalCacheDir().getPath() + "/秘夹/" + AccountHelper.getNickname() + AccountHelper.getUserId() + mFileListAdapter.getData().get(position).getName();
                                File file = new File(path);

                                if (file.exists() && file.length() == Long.parseLong(mFileListAdapter.getData().get(position).getSize())) {
                                    startActivity(new Intent(getContext(), PlayVideoActivity.class).putExtra(Constants.VIDEOPATH, path));
                                    return;
                                }

                                DownLoadRequestBean downloadTaskBean = new DownLoadRequestBean();
                                downloadTaskBean.setFullpath(mFileListAdapter.getData().get(position).getName());
                                downloadTaskBean.setIndex("1");
                                downloadTaskBean.setDiskId(Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
                                downloadTaskBean.setDiskName(Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                                downloadTaskBean.setGsId(Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
                                downloadTaskBean.setGsName(Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());
                                downloadTaskBean.setUserId(AccountHelper.getUserId());
                                downloadTaskBean.setUserName(AccountHelper.getNickname());
                                DownTask downTask = new DownTask(downloadTaskBean, Long.parseLong(mFileListAdapter.getData().get(position).getSize()), true);
                                TempLoadingForMultiple.loadVideoFile(downTask);
                                showLoading("");
                            }
                        } else {
                            RxBus.get().post(RxBusAction.DISK_MANAGER_INTO_FLODER, mFileListAdapter.getData().get(position).getName());
                        }
                    }
                    break;
                default:
                    break;
            }
        });

        mFileListAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            if (DiskManagerFragment.isMiWenOpen.get()) {
                if (mFileListAdapter.getData().get(position).isSelected()) {
                    mFileListAdapter.getData().get(position).setSelected(false);
                } else {
                    if (mFileListAdapter.getSelectedNum() == 0) {
                        mFileListAdapter.getData().get(position).setSelected(true);
                    } else {
                        ToastUtils.showShort("密文下载只支持单选！");
                    }
                }
            } else {
                mFileListAdapter.getData().get(position).setSelected(!mFileListAdapter.getData().get(position).isSelected());
            }
            mFileListAdapter.refresh();
            return false;
        });
    }


    @Subscribe(tags = {@Tag(RxBusAction.FILE_PREVIEW_FINISH)}, thread = EventThread.MAIN_THREAD)
    public void filePreviewFinish(String path) {
        dismissLoading();
//        FileViewer.viewFile(getActivity(), path);
        FileUtils.openFileReader(getActivity(),path);
    }

    @Subscribe(tags = {@Tag(RxBusAction.RADIO_PREVIEW_FINISH)}, thread = EventThread.MAIN_THREAD)
    public void audioPreviewFinish(String path) {
        dismissLoading();
        startActivity(new Intent(getActivity(), PlayMusicActivity.class).putExtra("MusicPath", path));
    }


    @Subscribe(tags = {@Tag(RxBusAction.VIDEO_PREVIEW_FINISH)}, thread = EventThread.MAIN_THREAD)
    public void videoPreviewFinish(String path) {
        dismissLoading();
        startActivity(new Intent(getContext(), PlayVideoActivity.class).putExtra(Constants.VIDEOPATH, path));
    }

    @Subscribe(tags = {@Tag(RxBusAction.PIC_PREVIEW_FINISH)}, thread = EventThread.MAIN_THREAD)
    public void picPreviewFinish(String path) {
        dismissLoading();
        ArrayList<String> list = new ArrayList<>();
        list.add(path);
        startActivity(new Intent(getActivity(), ImageBroseActivity.class).putExtra(ImageBroseActivity.PICS, list));
    }

    private boolean equals(String str, List<String> strings) {
        for (String string : strings) {
            if (str.equals(string)) {
                return true;
            }
        }
        return false;
    }

    @BindingAdapter(value = "isSelected", requireAll = false)
    public static void setSelected(View view, Boolean enable) {
        view.setSelected(enable);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFileListAdapter.clearAllSelect();
        binding.smartview.autoRefresh();
//        List<FileBean> fileBeanList = FileBoxUtils.getFileListByPath("");
//        Log.i(TAG, "initView: fileListSize==" + fileBeanList.size());
//        mFileListAdapter.replaceData(fileBeanList);
//        DiskManagerFragment.selectedNum.set(0);
//        DiskManagerFragment.isOpenSelect.set(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }

    @Override
    public void onTransSuccess(String order, byte[] data) {
        Objects.requireNonNull(getActivity()).runOnUiThread(this::dismissLoading);
        if (data == null) {
//            ToastUtils.showShort("");
            return;
        }
        byte[] content = new byte[data.length - 15];
        System.arraycopy(data, 15, content, 0, content.length);
        String json = new String(content).trim();
        switch (order) {
            case "0X02":
                if (!StringUtils.isEmpty(json)) {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        List<FileBean> list = FileBoxUtils.refreshFloderByPath("", json);
                        if (list.size()==0) {
                            list = FileBoxUtils.getFileListByPath("");
                        }
                        mFileListAdapter.replaceData(list);
                    });
                } else {
                    ToastUtils.showShort("刷新列表失败！");
                }
                binding.smartview.finishRefresh();
                break;
        }
    }

    @Override
    public void onFail(String order) {

    }
}
