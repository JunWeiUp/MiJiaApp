package com.mijia.app.ui.disk.fragment;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.handong.framework.account.AccountHelper;
import com.handong.framework.base.BaseFragment;
import com.handong.framework.base.BaseViewModel;
import com.handongkeji.utils.FileUtil;
import com.hanlyjiang.library.fileviewer.FileViewer;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.mijia.app.MainActivity;
import com.mijia.app.R;
import com.mijia.app.bean.DownLoadRequestBean;
import com.mijia.app.bean.FileBean;
import com.mijia.app.bean.TransOrderBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.constants.RxBusAction;
import com.mijia.app.constants.Sys;
import com.mijia.app.databinding.FragmentDiskManagerPageLayoutBinding;
import com.mijia.app.dialog.DeleteTipsDialog;
import com.mijia.app.dialog.RenameDialog;
import com.mijia.app.dialog.TipReplaceDialog;
import com.mijia.app.presenter.OnServerTransListener;
import com.mijia.app.presenter.SeverTransOrderPresenter;
import com.mijia.app.socket.DownLoadManager;
import com.mijia.app.socket.DownTask;
import com.mijia.app.socket.InstructionOutTimeObserver;
import com.mijia.app.socket.MessageReceiveManager;
import com.mijia.app.socket.UdpDataUtils;
import com.mijia.app.sys.FileBoxUtils;
import com.mijia.app.ui.disk.dialog.CreateTimePingDialog;
import com.mijia.app.ui.other.activity.SelectUploadLocationActivity;
import com.mijia.app.viewmodel.ServerTransOrderViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.mijia.app.constants.Sys.DISKHOME;
import static com.mijia.app.constants.Sys.FILE;
import static com.mijia.app.constants.Sys.OTHER;
import static com.mijia.app.constants.Sys.PHOTO;
import static com.mijia.app.constants.Sys.TRANS;
import static com.mijia.app.constants.Sys.VIDEO;
import static com.mijia.app.constants.Sys.VOICE;

/**
 * 磁盘管理最外层fragment
 *
 * @ClassName:DiskManagerFragment
 * @PackageName:com.mijia.app.ui.disk.fragment
 * @Create On 2019/6/10   10:54
 * @Site:http://www.handongkeji.com
 * @author:闫大仙er
 * @Copyrights 2019/6/10 handongkeji All rights reserved.
 */

public class DiskManagerFragment extends BaseFragment<FragmentDiskManagerPageLayoutBinding, BaseViewModel> implements OnServerTransListener {

    private FragmentManager mFragmentManager;

    public final static ObservableBoolean isMiWenOpen = new ObservableBoolean(false);
    public final static ObservableInt selectedNum = new ObservableInt(0);
    public final static ObservableBoolean isOpenSelect = new ObservableBoolean(false);

    public static boolean isOutPage = true;

    public final static ObservableArrayList<FileBean> selectedFileList = new ObservableArrayList<>();

    private ServerTransOrderViewModel mOrderViewModel;
    private InstructionOutTimeObserver mOutTimeObserver;
    private DiskManagerHomeFragment mManagerHomeFragment;
    private DiskManagerCurrentFragment mManagerCurrentFragment;

    private MessageReceiveManager mMessageReceiveManager;

    private SeverTransOrderPresenter severTransOrderPresenter;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_disk_manager_page_layout;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        RxBus.get().register(this);
        binding.setHandler(this);
        binding.setIsMiWenOpen(isMiWenOpen);
        binding.setSelectedFileNum(selectedNum);
        binding.setIsOpenSelect(isOpenSelect);

        DiskManagerFragment.isMiWenOpen.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (isMiWenOpen.get() && isOpenSelect.get()) {
                    binding.tvMakeTimePing.setText("制作时间瓶");
                }
            }
        });


        severTransOrderPresenter = new SeverTransOrderPresenter(this);
        mFragmentManager = getActivity().getSupportFragmentManager();
        mManagerHomeFragment = new DiskManagerHomeFragment();
        mManagerCurrentFragment = new DiskManagerCurrentFragment();


        mFragmentManager.beginTransaction().add(R.id.fragment, mManagerHomeFragment).commit();
        mMessageReceiveManager = MessageReceiveManager.getInstance();
        mOrderViewModel = new ServerTransOrderViewModel();
        isOpenSelect.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                MainActivity.isBottomGone.set(isOpenSelect.get());
                if (isMiWenOpen.get() && isOpenSelect.get()) {
                    binding.tvMakeTimePing.setText("制作时间瓶");
                }
            }
        });

        initTransObserver();


//        selectedNum.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
//            @Override
//            public void onPropertyChanged(Observable observable, int i) {
//                if (selectedNum.get() > 0) {
//                    isOpenSelect.set(true);
////                    MainActivity.isBottomGone.set(true);
//                } else {
////                    MainActivity.isBottomGone.set(false);
//                    isOpenSelect.set(false);
//                }
//            }
//        });
    }

    private void setReciveListener() {

        mMessageReceiveManager.setmReciveNewFloderOrderListener(packet -> {
            byte[] dataBytes = packet.getData();
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> dismissLoading());
            byte[] content = new byte[dataBytes.length - 15];
            System.arraycopy(dataBytes, 15, content, 0, content.length);
            String json = new String(content).trim();
            try {
                JSONObject jsonObject = new JSONObject(json);
                String result = jsonObject.getString("result");
                if (!StringUtils.isEmpty(result)) {
                    if ("success".equals(result)) {
                        if (isOutPage) {
                            RxBus.get().post(RxBusAction.DISK_HOME_REFRESH, "");
                        } else {
                            RxBus.get().post(RxBusAction.DISK_CURRENT_REFRESH, "");
                        }
                        ToastUtils.showShort("文件操作成功!");
                    } else {
                        ToastUtils.showShort("文件操作失败！");
                    }
                } else {
                    ToastUtils.showShort("文件操作失败！");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mOutTimeObserver.cancelInstruction("0X0B");
        });

    }

    private void initTransObserver() {


        mOutTimeObserver = InstructionOutTimeObserver.getInstance();


        mOrderViewModel.transOrderBean.observe(this, new Observer<TransOrderBean>() {
            @Override
            public void onChanged(@Nullable TransOrderBean transOrderBean) {
                if (transOrderBean != null) {
                    mOutTimeObserver.addSendInstructionAndTime(transOrderBean.getOrderId(), new InstructionOutTimeObserver.InstructOutTimeListener() {
                        @Override
                        public void onOutTime(String instruction) {
                            if ("0X0B".equals(instruction)) {
                                dismissLoading();
                                ToastUtils.showShort("文件操作失败!");
                            }
                        }
                    });
                    mOrderViewModel.queryOrderResult(transOrderBean.getOrderId());
                }
            }
        });
        mOrderViewModel.queryOrderResult.observe(this, transOrderResultBean -> {
            if (transOrderResultBean != null) {
                dismissLoading();
                if (!StringUtils.isEmpty(transOrderResultBean.getJsonResult())) {
                    mOutTimeObserver.cancelInstruction(transOrderResultBean.getOrderId());
                } else {
                    mOrderViewModel.queryOrderResult(transOrderResultBean.getOrderId());
                }
            } else {
                if (transOrderResultBean != null && !StringUtils.isEmpty(transOrderResultBean.getOrderId())) {
                    if (!mOutTimeObserver.isOutTiming(transOrderResultBean.getOrderId())) {
                        mOrderViewModel.queryOrderResult(transOrderResultBean.getOrderId());
                    }
                }
            }
        });
    }

    /**
     * 数据刷新 页面返回首页
     */
    @Subscribe(tags = {@Tag(RxBusAction.DISK_CHANGE_OR_DATA_REFRESH)}, thread = EventThread.MAIN_THREAD)
    public void refreshDataList(String action) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
                R.anim.slide_left_out,
                R.anim.slide_left_in,
                R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.fragment, mManagerHomeFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        mManagerHomeFragment.refresh();
        isOutPage = true;
    }

    /**
     * 进入文件夹内部
     *
     * @param title
     */
    @Subscribe(tags = {@Tag(RxBusAction.DISK_MANAGER_INTO_FLODER)}, thread = EventThread.MAIN_THREAD)
    public void intoFloderFragment(String title) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
                R.anim.slide_left_out,
                R.anim.slide_left_in,
                R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.fragment, mManagerCurrentFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        mManagerCurrentFragment.setPathAndRefresh(title);
        isOutPage = false;
    }


    @Subscribe(tags = {@Tag(RxBusAction.DISK_MANAGER_FRAGMENT_CHANGE)}, thread = EventThread.MAIN_THREAD)
    public void changeFragment(Integer type) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
                R.anim.slide_left_out,
                R.anim.slide_left_in,
                R.anim.slide_out_right);
        switch (type) {
            case PHOTO:
                fragmentTransaction.replace(R.id.fragment, new DiskManagerPhotoFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                isOutPage = false;
//                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment, new DiskManagerPhotoFragment()).commit();
                break;
            case VIDEO:
                fragmentTransaction.replace(R.id.fragment, mManagerCurrentFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                mManagerCurrentFragment.setTypeAndRefresh(VIDEO);
                isOutPage = false;
                break;
            case FILE:
                fragmentTransaction.replace(R.id.fragment, mManagerCurrentFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                mManagerCurrentFragment.setTypeAndRefresh(FILE);
                isOutPage = false;
                break;
            case VOICE:
                fragmentTransaction.replace(R.id.fragment, mManagerCurrentFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                mManagerCurrentFragment.setTypeAndRefresh(VOICE);
                isOutPage = false;
                break;
            case OTHER:
                fragmentTransaction.replace(R.id.fragment, mManagerCurrentFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                mManagerCurrentFragment.setTypeAndRefresh(OTHER);
                isOutPage = false;
                break;
            case DISKHOME:

                fragmentTransaction.replace(R.id.fragment, mManagerHomeFragment);
                fragmentTransaction.commit();
                isOutPage = true;
                break;
            default:
                break;
        }
    }

    public void onClick(View view) {
        if (!Constants.SYS_CONTANTS_BEAN.SelectedDiskIsOnLine.get()) {
            ToastUtils.showShort("当前磁盘未在线！");
            return;
        }
        switch (view.getId()) {
            case R.id.tv_make_time_ping://制作时间瓶
                CreateTimePingDialog dialog = new CreateTimePingDialog(Objects.requireNonNull(getActivity()));
                dialog.setOnSureListener((day, hour) -> {
                    if (day.equals("0") && hour.equals("0")) {
                        binding.tvMakeTimePing.setText("制作时间瓶");
                    } else {
                        binding.tvMakeTimePing.setText(day + "天" + hour + "小时");
                    }
                });
                dialog.show();
                break;
            case R.id.tv_mingwen_download:
                if (!Constants.isPcOnLine) {
                    ToastUtils.showShort("密夹未在线！");
                    return;
                }
                if (selectedFileList.size() == 0) {
                    ToastUtils.showShort("请选择文件！");
                    return;
                }
                boolean isAgreeBackupContacts = getActivity().getSharedPreferences(Sys.FLOW_SHAREPREFERENCES, MODE_PRIVATE).getBoolean(Sys.IS_AGREE_DOWN_FILE, false);

                if (Constants.Net_Status == 0 && !isAgreeBackupContacts) {
                    ToastUtils.showShort("请在设置中开启流量下载文件！");
                    return;
                }

                for (FileBean fileBean : selectedFileList) {
                    if ("folder".equals(fileBean.getType())) {
                        ToastUtils.showShort("不能下载文件夹！");
                        return;
                    }

                    boolean isAgreeFlowDownloadFile = getActivity().getSharedPreferences(Sys.FLOW_SHAREPREFERENCES, MODE_PRIVATE).getBoolean(Sys.IS_AGREE_DOWN_FILE, false);
                    if (!Constants.isPcOnLine || !Constants.isPcConnecting) {
                        ToastUtils.showShort("密夹未在线或者未成功连接通讯！");
                        return;
                    }
                    if (Constants.Net_Status == 0 && !isAgreeFlowDownloadFile) {
                        ToastUtils.showShort("请在设置中开启流量下载文件！");
                        return;
                    }


                    DownLoadRequestBean downloadTaskBean = new DownLoadRequestBean();
                    downloadTaskBean.setFullpath(fileBean.getName());
                    downloadTaskBean.setIndex("1");
                    downloadTaskBean.setDiskId(Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
                    downloadTaskBean.setDiskName(Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                    downloadTaskBean.setGsId(Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
                    downloadTaskBean.setGsName(Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());
                    downloadTaskBean.setUserId(AccountHelper.getUserId());
                    downloadTaskBean.setUserName(AccountHelper.getNickname());


                    String key = downloadTaskBean.getFullpath();
                    key = key + downloadTaskBean.getType();

                    Map<String, DownTask> mapList = DownLoadManager.getInstance().getTaskList();

                    if (!mapList.containsKey(key)) {
                        DownTask downTask = new DownTask(downloadTaskBean, Long.parseLong(fileBean.getSize()), false);
                        DownLoadManager.getInstance().addTask(downTask);
                    } else {
                        // 存在
                        DownTask downTask = mapList.get(key);
                        if (0 == downTask.getCurrTaskStatus() || 1 == downTask.getCurrTaskStatus()) {
                            ToastUtils.showShort("该文件正在下载");
                            return;
                        }

                        String finalKey = key;
                        new AlertDialog.Builder(this.getActivity())
                                .setTitle("提示")
                                .setMessage(fileBean.getName()+"已经下载过，是否覆盖？")
                                .setPositiveButton("是", (dialogInterface, i) -> {
                                    DownTask downTask12 = mapList.remove(finalKey);
                                    downTask12.delFile();

                                    DownTask downTask1 = new DownTask(downloadTaskBean, Long.parseLong(fileBean.getSize()), false);
                                    DownLoadManager.getInstance().addTask(downTask1);
                                })
                                .setNegativeButton("否", null)
                                .show();
                    }
                }

                RxBus.get().post(RxBusAction.MAIN_PAGE_CURRENT, TRANS + "");
                selectedNum.set(0);
                isOpenSelect.set(false);
                selectedFileList.clear();


//                startActivity(new Intent(getActivity(), SelectUploadLocationActivity.class));
                break;
            case R.id.tv_move:
                if (!Constants.isPcOnLine) {
                    ToastUtils.showShort("密夹未在线！");
                    return;
                }
                if (selectedFileList.size() == 0) {
                    ToastUtils.showShort("请选择文件！");
                    return;
                }
                startActivityForResult(new Intent(getActivity(), SelectUploadLocationActivity.class)
                        .putExtra(SelectUploadLocationActivity.SelectLocationType, SelectUploadLocationActivity.Move), Sys.SELECT_PATH_TO_MOVE);
                break;
            case R.id.tv_copy:
                if (!Constants.isPcOnLine) {
                    ToastUtils.showShort("密夹未在线！");
                    return;
                }
                if (selectedFileList.size() == 0) {
                    ToastUtils.showShort("请选择文件！");
                    return;
                }
                startActivityForResult(new Intent(getActivity(), SelectUploadLocationActivity.class)
                        .putExtra(SelectUploadLocationActivity.SelectLocationType, SelectUploadLocationActivity.Copy), Sys.SELECT_PATH_TO_COPY);
                break;
            case R.id.tv_reset_name:
                if (!Constants.isPcOnLine) {
                    ToastUtils.showShort("密夹未在线！");
                    return;
                }
                if (selectedNum.get() > 1) {
                    ToastUtils.showShort("不能同时重命名多个文件!");
                    return;
                }
                RenameDialog renameDialog = new RenameDialog(getActivity());
                renameDialog.setOnClickSureListener(name -> {
//                    {   0X0B
//                        "userName":"test_user",
//                            "userId":"aaaaaaaaaa",
//                            "gsId":"bbbbbbbbb",
//                            "gsName":"test_gs",
//                            "fileinfo":{
//                                "fileCmd":"move",    //copy、rename、delete、new
//                                "objType":"file",    //folder
//                                "srcDisk":"disk1",
//                                "srcDiskId":"ccccccccc",
//                                "srcObj":"/local/test.txt",
//                                "dstDisk":"disk2",    //delete、new无dst
//                                "dstDiskId":"dddddddd",
//                                "dstObj":"test.txt"
//                    }
//                    }
                    if (selectedFileList.size() == 0) {
                        return;
                    }
                    JsonObject mainObject = new JsonObject();
                    mainObject.addProperty("userName", AccountHelper.getNickname());
                    mainObject.addProperty("userId", AccountHelper.getUserId());
                    mainObject.addProperty("gsId", Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
                    mainObject.addProperty("gsName", Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());
                    JsonArray jsonArray = new JsonArray();
                    JsonObject fileinfoObject = new JsonObject();
                    fileinfoObject.addProperty("fileCmd", "rename");
                    fileinfoObject.addProperty("objType", selectedFileList.get(0).getType()); // file or floder
                    fileinfoObject.addProperty("srcDisk", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                    fileinfoObject.addProperty("srcDiskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
                    fileinfoObject.addProperty("srcObj", selectedFileList.get(0).getName());//path
                    fileinfoObject.addProperty("dstDisk", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                    fileinfoObject.addProperty("dstDiskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
                    String aboveName = selectedFileList.get(0).getName();
                    String path = aboveName.substring(0, aboveName.lastIndexOf("/"));
                    String nowName = path + "/" + name;

                    if (selectedFileList.get(0).getType().equals("file")) {
                        if (aboveName.contains(".")) {
                            int lenght = aboveName.split("\\.").length;
                            String suffix = lenght > 1 ? aboveName.split("\\.")[lenght - 1] : "";
                            nowName = nowName + "." + suffix;
                        }
                    }


                    fileinfoObject.addProperty("dstObj", nowName);
                    jsonArray.add(fileinfoObject);
                    mainObject.add("fileInfo", jsonArray);
                    sendUdpMessage(mainObject);
//                    System.out.println("UDPsend" + data);
                });
                renameDialog.show();
                break;
            case R.id.tv_delete:
                if (!Constants.isPcOnLine) {
                    ToastUtils.showShort("密夹未在线！");
                    return;
                }
                if (selectedFileList.size() == 0) {
                    ToastUtils.showShort("请选择文件！");
                    return;
                }
                DeleteTipsDialog deleteTipsDialog = new DeleteTipsDialog(getActivity());
                deleteTipsDialog.setOnClickDelListener(() -> {
                    if (selectedFileList.size() == 0) {
                        return;
                    }
                    JsonObject mainObject = new JsonObject();
                    mainObject.addProperty("userName", AccountHelper.getNickname());
                    mainObject.addProperty("userId", AccountHelper.getUserId());
                    mainObject.addProperty("gsId", Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
                    mainObject.addProperty("gsName", Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());
                    JsonArray jsonArray = new JsonArray();
                    JsonObject fileinfoObject = new JsonObject();
                    fileinfoObject.addProperty("fileCmd", "delete");
                    fileinfoObject.addProperty("objType", selectedFileList.get(0).getType()); // file or floder
                    fileinfoObject.addProperty("srcDisk", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                    fileinfoObject.addProperty("srcDiskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
                    fileinfoObject.addProperty("srcObj", selectedFileList.get(0).getName());//path
//                    fileinfoObject.addProperty("dstDisk", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
//                    fileinfoObject.addProperty("dstDiskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
//                    String aboveName = selectedFileList.get(0).getName();
//                    String path = aboveName.substring(0, aboveName.lastIndexOf("/"));
//                    String nowName = path + name;
//                    fileinfoObject.addProperty("dstObj", nowName);
                    jsonArray.add(fileinfoObject);
                    mainObject.add("fileInfo", jsonArray);
                    sendUdpMessage(mainObject);
                });
                deleteTipsDialog.show();
                break;
            case R.id.tv_miwen_sure:


                if (!Constants.isPcOnLine) {
                    ToastUtils.showShort("密夹未在线！");
                    return;
                }
                if (selectedFileList.size() == 0) {
                    ToastUtils.showShort("请选择文件！");
                    return;
                }

                boolean isAgreeBackupContactss = getActivity().getSharedPreferences(Sys.FLOW_SHAREPREFERENCES, MODE_PRIVATE).getBoolean(Sys.IS_AGREE_DOWN_FILE, false);

                if (Constants.Net_Status == 0 && !isAgreeBackupContactss) {
                    ToastUtils.showShort("请在设置中开启流量下载文件！");
                    return;
                }

                for (FileBean fileBean : selectedFileList) {
                    if ("folder".equals(fileBean.getType())) {
                        ToastUtils.showShort("不能下载文件夹！");
                        return;
                    }

                    boolean isAgreeFlowDownloadFile = Objects.requireNonNull(getActivity()).getSharedPreferences(Sys.FLOW_SHAREPREFERENCES, MODE_PRIVATE).getBoolean(Sys.IS_AGREE_DOWN_FILE, false);
                    if (!Constants.isPcOnLine || !Constants.isPcConnecting) {
                        ToastUtils.showShort("密夹未在线或者未成功连接通讯！");
                        return;
                    }
                    if (Constants.Net_Status == 0 && !isAgreeFlowDownloadFile) {
                        ToastUtils.showShort("请在设置中开启流量下载文件！");
                        return;
                    }

                    String selectTime = binding.tvMakeTimePing.getText().toString();
//

                    int second = 0;
                    if (!selectTime.equals("制作时间瓶")) {
//                        ToastUtils.showShort("请制作时间瓶!");
                        int day = Integer.parseInt(selectTime.substring(0, selectTime.lastIndexOf("天")));
                        int hour = Integer.parseInt(selectTime.substring(selectTime.lastIndexOf("天") + 1, selectTime.lastIndexOf("小时")));
                        Log.i("Time", "日" + day + "小时" + hour);
                        second = day * 24 * 60 * 60 + hour * 60 * 60;
//                        return;
                    }


                    DownLoadRequestBean downloadTaskBean = new DownLoadRequestBean();
                    downloadTaskBean.setFullpath(fileBean.getName());
                    downloadTaskBean.setIndex("1");
                    downloadTaskBean.setDiskId(Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
                    downloadTaskBean.setDiskName(Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                    downloadTaskBean.setGsId(Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
                    downloadTaskBean.setGsName(Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());
                    downloadTaskBean.setUserId(AccountHelper.getUserId());
                    downloadTaskBean.setType(1);
                    downloadTaskBean.setTime(second);
                    downloadTaskBean.setUserName(AccountHelper.getNickname());


                    String key = downloadTaskBean.getFullpath();
                    key = key + downloadTaskBean.getType();

                    Map<String, DownTask> mapList = DownLoadManager.getInstance().getTaskList();

                    if (!mapList.containsKey(key)) {
                        DownTask downTask = new DownTask(downloadTaskBean, Long.parseLong(fileBean.getSize()), false);
                        DownLoadManager.getInstance().addTask(downTask);
                    } else {
                        // 存在
                        DownTask downTask = mapList.get(key);
                        if (0 == downTask.getCurrTaskStatus() || 1 == downTask.getCurrTaskStatus()) {
                            ToastUtils.showShort("该文件正在下载");
                            return;
                        }

                        String finalKey = key;
                        new AlertDialog.Builder(this.getActivity())
                                .setTitle("提示")
                                .setMessage(fileBean.getName() + "已经下载过，是否覆盖？")
                                .setPositiveButton("是", (dialogInterface, i) -> {
                                    DownTask downTask12 = mapList.remove(finalKey);
                                    downTask12.delFile();

                                    DownTask downTask1 = new DownTask(downloadTaskBean, Long.parseLong(fileBean.getSize()), false);
                                    DownLoadManager.getInstance().addTask(downTask1);
                                })
                                .setNegativeButton("否", null)
                                .show();
                    }


                }


                RxBus.get().post(RxBusAction.MAIN_PAGE_CURRENT, TRANS + "");
                selectedNum.set(0);
                isOpenSelect.set(false);
                selectedFileList.clear();
                break;
            default:
                break;
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Sys.SELECT_PATH_TO_COPY) {
                if (data != null) {
                    String selectedPath = data.getStringExtra(Sys.SELECT_PATH);
                    JsonObject mainObject = new JsonObject();
                    mainObject.addProperty("userName", AccountHelper.getNickname());
                    mainObject.addProperty("userId", AccountHelper.getUserId());
                    mainObject.addProperty("gsId", Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
                    mainObject.addProperty("gsName", Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());
                    JsonArray jsonArray = new JsonArray();

                    List<FileBean> haveListBean = new ArrayList<>();
                    for (FileBean fileBean : selectedFileList) {
                        String nowName = fileBean.getName().substring(fileBean.getName().lastIndexOf("/") + 1);
                        if (FileBoxUtils.isHaveFileByFloder(selectedPath, nowName)) {
                            haveListBean.add(fileBean);
                        }
                    }

                    if (haveListBean.size() != 0) {
                        TipReplaceDialog tipReplaceDialog = new TipReplaceDialog(getActivity(), haveListBean);

                        tipReplaceDialog.setClickListener(new TipReplaceDialog.OnClickListener() {
                            @Override
                            public void sure(List<FileBean> list) {
                                for (FileBean fileBean : list) {
                                    JsonObject fileinfoObject = new JsonObject();
                                    fileinfoObject.addProperty("fileCmd", "copy");
                                    fileinfoObject.addProperty("objType", fileBean.getType()); // file or floder
                                    fileinfoObject.addProperty("srcDisk", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                                    fileinfoObject.addProperty("srcDiskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
                                    fileinfoObject.addProperty("srcObj", fileBean.getName());//path
                                    fileinfoObject.addProperty("dstDisk", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                                    fileinfoObject.addProperty("dstDiskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());

                                    String nowName = fileBean.getName().substring(fileBean.getName().lastIndexOf("/") + 1);
                                    String finalFilePath = selectedPath + nowName;
                                    fileinfoObject.addProperty("dstObj", finalFilePath);
                                    jsonArray.add(fileinfoObject);
                                }
                                if (jsonArray.size() == 0) {
                                    return;
                                }
                                mainObject.add("fileInfo", jsonArray);
                                sendUdpMessage(mainObject);
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                        tipReplaceDialog.show();

                    } else {
                        for (FileBean fileBean : selectedFileList) {
                            JsonObject fileinfoObject = new JsonObject();
                            fileinfoObject.addProperty("fileCmd", "copy");
                            fileinfoObject.addProperty("objType", fileBean.getType()); // file or floder
                            fileinfoObject.addProperty("srcDisk", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                            fileinfoObject.addProperty("srcDiskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
                            fileinfoObject.addProperty("srcObj", fileBean.getName());//path
                            fileinfoObject.addProperty("dstDisk", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                            fileinfoObject.addProperty("dstDiskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());

                            String nowName = fileBean.getName().substring(fileBean.getName().lastIndexOf("/") + 1);
                            String finalFilePath = selectedPath + nowName;
                            fileinfoObject.addProperty("dstObj", finalFilePath);
                            jsonArray.add(fileinfoObject);
//                        jsonArray.add(fileinfoObject);
                        }

                        mainObject.add("fileInfo", jsonArray);
                        sendUdpMessage(mainObject);
                    }


                }
            }

            if (requestCode == Sys.SELECT_PATH_TO_MOVE) {
                if (data != null) {
                    String selectedPath = data.getStringExtra(Sys.SELECT_PATH);
                    JsonObject mainObject = new JsonObject();
                    mainObject.addProperty("userName", AccountHelper.getNickname());
                    mainObject.addProperty("userId", AccountHelper.getUserId());
                    mainObject.addProperty("gsId", Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
                    mainObject.addProperty("gsName", Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());
                    JsonArray jsonArray = new JsonArray();


                    List<FileBean> haveListBean = new ArrayList<>();
                    for (FileBean fileBean : selectedFileList) {
                        String nowName = fileBean.getName().substring(fileBean.getName().lastIndexOf("/") + 1);
                        if (FileBoxUtils.isHaveFileByFloder(selectedPath, nowName)) {
                            haveListBean.add(fileBean);
                        }
                    }

                    if (haveListBean.size() != 0) {
                        TipReplaceDialog tipReplaceDialog = new TipReplaceDialog(getActivity(), haveListBean);

                        tipReplaceDialog.setClickListener(new TipReplaceDialog.OnClickListener() {
                            @Override
                            public void sure(List<FileBean> list) {
                                for (FileBean fileBean : list) {
                                    JsonObject fileinfoObject = new JsonObject();
                                    fileinfoObject.addProperty("fileCmd", "move");
                                    fileinfoObject.addProperty("objType", fileBean.getType()); // file or floder
                                    fileinfoObject.addProperty("srcDisk", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                                    fileinfoObject.addProperty("srcDiskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
                                    fileinfoObject.addProperty("srcObj", fileBean.getName());//path
                                    fileinfoObject.addProperty("dstDisk", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                                    fileinfoObject.addProperty("dstDiskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());


                                    String nowName = fileBean.getName().substring(fileBean.getName().lastIndexOf("/") + 1);
                                    String finalFilePath = selectedPath + nowName;
                                    fileinfoObject.addProperty("dstObj", finalFilePath);
                                    jsonArray.add(fileinfoObject);
                                }
                                if (jsonArray.size() == 0) {
                                    return;
                                }
                                mainObject.add("fileInfo", jsonArray);
                                sendUdpMessage(mainObject);
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                        tipReplaceDialog.show();

                    } else {
                        for (FileBean fileBean : selectedFileList) {
                            JsonObject fileinfoObject = new JsonObject();
                            fileinfoObject.addProperty("fileCmd", "move");
                            fileinfoObject.addProperty("objType", fileBean.getType()); // file or floder
                            fileinfoObject.addProperty("srcDisk", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                            fileinfoObject.addProperty("srcDiskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
                            fileinfoObject.addProperty("srcObj", fileBean.getName());//path
                            fileinfoObject.addProperty("dstDisk", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                            fileinfoObject.addProperty("dstDiskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());

                            String nowName = fileBean.getName().substring(fileBean.getName().lastIndexOf("/") + 1);
                            String finalFilePath = selectedPath + nowName;
                            fileinfoObject.addProperty("dstObj", finalFilePath);
                            jsonArray.add(fileinfoObject);
//                        jsonArray.add(fileinfoObject);
                        }

                        mainObject.add("fileInfo", jsonArray);
                        sendUdpMessage(mainObject);
                    }


//
//                    for (FileBean fileBean : selectedFileList) {
//
//
//                        String nowName = fileBean.getName().substring(fileBean.getName().lastIndexOf("/") + 1);
//                        String finalFilePath = selectedPath + nowName;
//                        fileinfoObject.addProperty("dstObj", finalFilePath);
//                        if (FileBoxUtils.isHaveFileByFloder(selectedPath, nowName)) {
//                            new AlertDialog.Builder(getActivity())
//                                    .setTitle("提示")
//                                    .setMessage("目标文件夹中已存在名为" + nowName + "的文件，是否替换？")
//                                    .setNegativeButton("否", (dialogInterface, i) -> {
//
//                                    })
//                                    .setPositiveButton("是", (dialogInterface, i) -> jsonArray.add(fileinfoObject))
//                                    .show();
//                        } else {
//                            jsonArray.add(fileinfoObject);
//                        }
//                    }
//                    if (jsonArray.size() == 0) {
//                        return;
//                    }
//                    mainObject.add("fileInfo", jsonArray);
//
//                    sendUdpMessage(mainObject);


                }
            }
        }
    }

    private void sendUdpMessage(JsonObject mainObject) {
        byte[] bytes = UdpDataUtils.getDataByte((byte) 0X0B, 0, 0, mainObject.toString());
        if (Constants.isPcConnecting) {
            MainActivity.mConnectBinder.send(bytes);
            setReciveListener();
            mOutTimeObserver.addSendInstructionAndTime("0X0B", instruction -> {
                ToastUtils.showShort("文件操作失败!");
            });
        } else {
            showLoading("");
            severTransOrderPresenter.orderTrans("0X0B", Base64.encodeToString(bytes, Base64.DEFAULT));
//                        mOrderViewModel.serverTransOrder(Base64.encodeToString(bytes, Base64.DEFAULT));
        }
        System.out.println("UDPsend" + bytes);
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
            case "0X0B":
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String result = jsonObject.getString("result");
                    if (!StringUtils.isEmpty(result)) {
                        if ("success".equals(result)) {
                            if (isOutPage) {
                                RxBus.get().post(RxBusAction.DISK_HOME_REFRESH, "");
                            } else {
                                RxBus.get().post(RxBusAction.DISK_CURRENT_REFRESH, "");
                            }
                            ToastUtils.showShort("文件操作成功!");
                        } else {
                            ToastUtils.showShort("文件操作失败！");
                        }
                    } else {
                        ToastUtils.showShort("文件操作失败！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onFail(String order) {

    }
}
