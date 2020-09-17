package com.mijia.app.ui.backup.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.handong.framework.account.AccountHelper;
import com.handong.framework.base.BaseFragment;
import com.handong.framework.base.BaseViewModel;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.mijia.app.MainActivity;
import com.mijia.app.R;
import com.mijia.app.bean.DiskBackUpContactBean;
import com.mijia.app.bean.DownLoadRequestBean;
import com.mijia.app.bean.HomeDataBean;
import com.mijia.app.bean.ObservableEmptyImp;
import com.mijia.app.bean.PhoneBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.constants.RxBusAction;
import com.mijia.app.constants.Sys;
import com.mijia.app.databinding.FragmentBackupContactFragmentBinding;
import com.mijia.app.presenter.OnServerTransListener;
import com.mijia.app.presenter.SeverTransOrderPresenter;
import com.mijia.app.service.AddressBookWrite;
import com.mijia.app.socket.BackupDownLoad;
import com.mijia.app.socket.BackupUpload;
import com.mijia.app.socket.InstructionOutTimeObserver;
import com.mijia.app.socket.MessageReceiveManager;
import com.mijia.app.socket.UdpDataUtils;
import com.mijia.app.ui.backup.adapter.BackUpContactsAdapter;
import com.mijia.app.utils.PhoneUtil;
import com.mijia.app.utils.WriteTxtUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.tbruyelle.rxpermissions2.Permission;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import static android.content.Context.MODE_PRIVATE;

/**
 * 通讯录备份fragment
 *
 * @ClassName:BackUpContactsFragment
 * @PackageName:com.mijia.app.ui.backup.fragment
 * @Create On 2019/6/11   14:48
 * @Site:http://www.handongkeji.com
 * @author:闫大仙er
 * @Copyrights 2019/6/11 handongkeji All rights reserved.
 */


public class BackUpContactsFragment extends BaseFragment<FragmentBackupContactFragmentBinding, BaseViewModel> implements View.OnClickListener, BackupUpload.BackUpListener, OnServerTransListener {

    private BackUpContactsAdapter mBackUpContactsAdapter;
    private Gson mGson = new Gson();
    private SeverTransOrderPresenter severTransOrderPresenter;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_backup_contact_fragment;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        binding.setClickListener(this);
        RxBus.get().register(this);
        binding.setSys(Constants.SYS_CONTANTS_BEAN);
        mOutTimeObserver = InstructionOutTimeObserver.getInstance();
        mMessageReceiveManager = MessageReceiveManager.getInstance();
        severTransOrderPresenter = new SeverTransOrderPresenter(this);
        initRecycler();
        MessageReceiveManager.getInstance().setBackUpListListener(value -> {
            if (value != null && value.getAddBook() != null) {
                mBackUpContactsAdapter.replaceData(value.getAddBook());
            }else{
                mBackUpContactsAdapter.replaceData(new ArrayList<>());
            }
        });

        binding.smartview.setOnRefreshListener(refreshlayout -> {
            getContactListData();
            getContactList();
            binding.smartview.finishRefresh();
        });
    }

    @Subscribe(tags = {@Tag(RxBusAction.FILE_DATA_LOAD_FINISH)}, thread = EventThread.MAIN_THREAD)
    public void dataLoadFinish(String str) {
        binding.smartview.autoRefresh();
    }

    private void getContactListData() {
        if (Constants.isPcOnLine) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userName", AccountHelper.getNickname());
            jsonObject.addProperty("userId", AccountHelper.getUserId());
            jsonObject.addProperty("gsId", Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
            jsonObject.addProperty("gsName", Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());
            jsonObject.addProperty("diskName", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
            jsonObject.addProperty("diskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());

            byte[] bytes = UdpDataUtils.getDataByte((byte) 0X05, 1, 1, jsonObject.toString());
            if (Constants.isPcConnecting) {
                MainActivity.mConnectBinder.send(bytes);
            } else {
                severTransOrderPresenter.orderTrans("0X05", Base64.encodeToString(bytes, Base64.DEFAULT));
            }
        } else {
//            ToastUtils.showShort("密夹不在线！");
        }
    }

    private void initRecycler() {
        mBackUpContactsAdapter = new BackUpContactsAdapter();
        binding.recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recycler.setAdapter(mBackUpContactsAdapter);
//        mBackUpContactsAdapter.replaceData(Arrays.asList("", "", ""));
        mBackUpContactsAdapter.setOnItemChildClickListener((adapter, view, position) -> {


            int id = view.getId();
            DiskBackUpContactBean.AddBookBean bookBean = (DiskBackUpContactBean.AddBookBean) adapter.getItem(position);
            if (id == R.id.restoreText) {
                if (!Constants.SYS_CONTANTS_BEAN.SelectedDiskIsOnLine.get()) {
                    ToastUtils.showShort("当前磁盘未在线！");
                    return;
                }

                if (!Constants.isPcOnLine || !Constants.isPcConnecting) {
                    ToastUtils.showShort("密夹未在线或者未成功连接通讯！");
                    return;
                }



                boolean isAgreeBackupContacts = getActivity().getSharedPreferences(Sys.FLOW_SHAREPREFERENCES, MODE_PRIVATE).getBoolean(Sys.IS_AGREE_DOWN_CONTACTS, false);

                if (Constants.Net_Status == 0 && !isAgreeBackupContacts) {
                    ToastUtils.showShort("请在设置中开启流量恢复通讯录！");
                    return;
                }

                getActivity().runOnUiThread(() -> showLoading(""));
                String fullPath = bookBean.getBookName();
                DownLoadRequestBean downLoadRequestBean = new DownLoadRequestBean();
                downLoadRequestBean.setIndex(1 + "");
                downLoadRequestBean.setFullpath(fullPath);
                BackupDownLoad.getInstance().startDownLoading(downLoadRequestBean);
                BackupDownLoad.getInstance().setBackUpListener(new BackupDownLoad.BackDownListener() {
                    @Override
                    public void onBackUpDownFinish(String absolutePath) {
                        addSubscription(Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
                            AddressBookWrite bookWrite = new AddressBookWrite();
                            emitter.onNext(bookWrite.write(absolutePath));
                        }), new ObservableEmptyImp<Boolean>() {
                            @Override
                            public void onNext(Boolean o) {
                                super.onNext(o);
                                if (o) {
                                    ToastUtils.showShort("恢复成功");
                                    getActivity().runOnUiThread(() -> dismissLoading());
                                    return;
                                }
                                getActivity().runOnUiThread(() -> dismissLoading());
                                ToastUtils.showShort("恢复异常");
                            }
                        });
                    }

                    @Override
                    public void onBackUpDownError() {
                        getActivity().runOnUiThread(() -> dismissLoading());
                        ToastUtils.showShort("同步异常");
                    }
                });

                return;
            }

            if (id == R.id.delText) {
//                System.out.println("---- del");
                JsonObject mainObject = new JsonObject();
                mainObject.addProperty("userName", AccountHelper.getNickname());
                mainObject.addProperty("userId", AccountHelper.getUserId());
                mainObject.addProperty("gsId", Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
                mainObject.addProperty("gsName", Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());
                JsonArray jsonArray = new JsonArray();
                JsonObject fileinfoObject = new JsonObject();
                fileinfoObject.addProperty("fileCmd", "delete");
                fileinfoObject.addProperty("objType", "file"); // file or floder
                fileinfoObject.addProperty("srcDisk", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                fileinfoObject.addProperty("srcDiskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
                fileinfoObject.addProperty("srcObj", bookBean.getBookName());//path
//                    fileinfoObject.addProperty("dstDisk", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
//                    fileinfoObject.addProperty("dstDiskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
//                    String aboveName = selectedFileList.get(0).getName();
//                    String path = aboveName.substring(0, aboveName.lastIndexOf("/"));
//                    String nowName = path + name;
//                    fileinfoObject.addProperty("dstObj", nowName);
                jsonArray.add(fileinfoObject);
                mainObject.add("fileInfo", jsonArray);
                byte[] data = UdpDataUtils.getDataByte((byte) 0X0B, 0, 0, mainObject.toString());
                if (Constants.isPcConnecting) {
                    MainActivity.mConnectBinder.send(data);
                    setReciveListener();
                    mOutTimeObserver.addSendInstructionAndTime("0X0B", instruction -> {
                        ToastUtils.showShort("文件操作失败!");
                    });
                } else {
                    showLoading("");
                    severTransOrderPresenter.orderTrans("0X0B", Base64.encodeToString(data, Base64.DEFAULT));
//                        mOrderViewModel.serverTransOrder(Base64.encodeToString(data, Base64.DEFAULT));
                }
            }


        });
    }

    private InstructionOutTimeObserver mOutTimeObserver;
    private MessageReceiveManager mMessageReceiveManager;

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
                        ToastUtils.showShort("删除成功!");
                        getActivity().runOnUiThread(() -> binding.smartview.autoRefresh());
                    } else {
                        ToastUtils.showShort("删除失败！");
                    }
                } else {
                    ToastUtils.showShort("删除失败！");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mOutTimeObserver.cancelInstruction("0X0B");
        });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_backup) {

            if (!Constants.SYS_CONTANTS_BEAN.SelectedDiskIsOnLine.get()) {
                ToastUtils.showShort("当前磁盘未在线！");
                return;
            }

            if (!Constants.isPcOnLine || !Constants.isPcConnecting) {
                ToastUtils.showShort("密夹未在线或者未成功连接通讯！");
                return;
            }

            boolean isAgreeBackupContacts = getActivity().getSharedPreferences(Sys.FLOW_SHAREPREFERENCES, MODE_PRIVATE).getBoolean(Sys.IS_AGREE_BACKUP_CONTACTS, false);

            if (Constants.Net_Status == 0 && !isAgreeBackupContacts) {
                ToastUtils.showShort("请在设置中开启流量备份通讯录！");
                return;
            }

            if (!TextUtils.equals(binding.tvBackup.getText().toString(), "开始备份")) {
                return;
            }
            RxPermissions rxPermissions = new RxPermissions(getActivity());
            addSubscription(rxPermissions.requestEachCombined(Manifest.permission.READ_CONTACTS),
                    new ObservableEmptyImp<Permission>() {
                        @Override
                        public void onNext(Permission permission) {
                            super.onNext(permission);
                            if (!permission.granted) {
                                return;
                            }

                            PhoneUtil phoneUtil = new PhoneUtil(getActivity());
                            List<PhoneBean> mPhone = phoneUtil.getPhone();
                            if (mPhone == null || mPhone.isEmpty()) {
                                ToastUtils.showShort("无备份数据");
                                return;
                            }
                            binding.tvBackup.setText("正在备份");
                            String j = mGson.toJson(mPhone);
                            String absolutePath = Utils.getApp().getApplicationContext().getExternalCacheDir().getAbsolutePath();
                            File file = new File(absolutePath, "phoneNum");
                            if (!file.exists()) {
                                file.mkdirs();
                            }

                            try {
                                File phoneNumFile = new File(file, "phoneNumUp.txt");
                                if (phoneNumFile.exists()) {
                                    phoneNumFile.delete();
                                }
                                phoneNumFile.createNewFile();
                                FileOutputStream fileOutputStream = new FileOutputStream(phoneNumFile);
                                fileOutputStream.write(j.getBytes());

                                BackupUpload backupUpload = BackupUpload.getInstance().upLoadBackUpPath(phoneNumFile.getAbsolutePath());
                                backupUpload.setBackUpListener(BackUpContactsFragment.this);
                                backupUpload.startBackUp(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            return;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
           getContactList();
        }
    }

    private void getContactList() {
        RxPermissions rxPermissions = new RxPermissions(getActivity());
        addSubscription(rxPermissions.requestEachCombined(Manifest.permission.READ_CONTACTS),
                new ObservableEmptyImp<Permission>() {
                    @Override
                    public void onNext(Permission permission) {
                        super.onNext(permission);
                        if (permission.granted) {
                            PhoneUtil phoneUtil = new PhoneUtil(getActivity());
                            List<PhoneBean> mPhone = phoneUtil.getPhone();
                            binding.tvContactNum.setText("本机：" + mPhone.size() + "条");
                        }
                    }
                }
        );
    }

    @Override
    public void onUpLoadingFinish() {
        binding.tvBackup.setText("开始备份");
        ToastUtils.showShort("备份完成");
        binding.smartview.autoRefresh();
    }

    @Override
    public void onUpLoadingError() {
        ToastUtils.showShort("备份发生异常,请稍后重试");
        binding.tvBackup.setText("开始备份");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }

    @Override
    public void onTransSuccess(String order, byte[] data) {
        if (data == null) {
//            ToastUtils.showShort("");
            return;
        }
        byte[] content = new byte[data.length - 15];
        System.arraycopy(data, 15, content, 0, content.length);
        String json = new String(content).trim();
        Log.i("ServerTrans", "转发指令最终结果===" + json);
        switch (order) {
            case "0X05":
                DiskBackUpContactBean backUpHistoryListBean = mGson.fromJson(json, DiskBackUpContactBean.class);
                if (backUpHistoryListBean != null && backUpHistoryListBean.getAddBook() != null) {
                    getActivity().runOnUiThread(() -> mBackUpContactsAdapter.replaceData(backUpHistoryListBean.getAddBook()));
                } else {
                    getActivity().runOnUiThread(() -> mBackUpContactsAdapter.replaceData(new ArrayList<>()));
                }
                break;
            case "0X0B":
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String result = jsonObject.getString("result");
                    if (!StringUtils.isEmpty(result)) {
                        if ("success".equals(result)) {
                            getActivity().runOnUiThread(() -> binding.smartview.autoRefresh());
                            ToastUtils.showShort("删除成功!");
                        } else {
                            ToastUtils.showShort("删除失败！");
                        }
                    } else {
                        ToastUtils.showShort("删除失败！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onFail(String order) {

    }
}
