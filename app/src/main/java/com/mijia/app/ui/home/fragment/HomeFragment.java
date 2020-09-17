package com.mijia.app.ui.home.fragment;

import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Base64;
import android.util.Log;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.handong.framework.account.AccountHelper;
import com.handong.framework.base.BaseFragment;
import com.handong.framework.base.BaseViewModel;
import com.hwangjr.rxbus.RxBus;
import com.mijia.app.MainActivity;
import com.mijia.app.MyApp;
import com.mijia.app.R;
import com.mijia.app.bean.DiskInfoBean;
import com.mijia.app.bean.FileBean;
import com.mijia.app.bean.GsInfoBean;
import com.mijia.app.bean.HomeDataBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.constants.RxBusAction;
import com.mijia.app.databinding.FragmentHomePageBinding;
import com.mijia.app.dialog.MakeDialog;
import com.mijia.app.dialog.MiJiaReNameDialog;
import com.mijia.app.dialog.RenameDialog;
import com.mijia.app.dialog.TipsDialog;
import com.mijia.app.presenter.OnServerTransListener;
import com.mijia.app.presenter.SeverTransOrderPresenter;
import com.mijia.app.socket.InstructionOutTimeObserver;
import com.mijia.app.socket.MessageReceiveManager;
import com.mijia.app.socket.UdpDataUtils;
import com.mijia.app.ui.home.activity.SettingActivity;
import com.mijia.app.ui.home.adapter.HomeDiskListAdapter;
import com.mijia.app.ui.home.adapter.HomePcListAdapter;
import com.mijia.app.viewmodel.HomeDataViewModel;
import com.mijia.app.viewmodel.ServerTransOrderViewModel;
import com.nevermore.oceans.uits.ImageLoader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import io.objectbox.Box;

import static com.mijia.app.socket.InstructionOutTimeObserver.*;

/**
 * Created by Administrator on 2019/6/4.
 */

public class HomeFragment extends BaseFragment<FragmentHomePageBinding, BaseViewModel> implements OnServerTransListener {

    private HomePcListAdapter mPcListAdapter;

    private HomeDiskListAdapter mDiskListAdapter;

    private HomeDataViewModel mHomeDataViewModel;


    private Box<FileBean> mFileBeanBox;

    private Box<GsInfoBean> mGsInfoBeanBox;
    private Box<DiskInfoBean> mDiskInfoBeanBox;


    public static final ObservableBoolean isDataLoadFinish = new ObservableBoolean(false);

    private ObservableBoolean miDunIsOffLine = new ObservableBoolean(false);

    private ServerTransOrderViewModel mOrderViewModel;
    private InstructionOutTimeObserver mOutTimeObserver;
    private MessageReceiveManager mMessageReceiveManager;
    private SeverTransOrderPresenter severTransOrderPresenter;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_home_page;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        binding.setMidunisoffline(miDunIsOffLine);
        mOrderViewModel = new ServerTransOrderViewModel();
        severTransOrderPresenter = new SeverTransOrderPresenter(this);
        //初始化密夹和DISK列表
        initPcAdapter();
        initDiskAdapter();
        mFileBeanBox = MyApp.getInstance().getBoxStore().boxFor(FileBean.class);
        mGsInfoBeanBox = MyApp.getInstance().getBoxStore().boxFor(GsInfoBean.class);
        mDiskInfoBeanBox = MyApp.getInstance().getBoxStore().boxFor(DiskInfoBean.class);
        mOutTimeObserver = getInstance();
        mMessageReceiveManager = MessageReceiveManager.getInstance();

        binding.tvNickname.setText(AccountHelper.getNickname());
        ImageLoader.newLoadImage(binding.ivHeadview, AccountHelper.getAvatar());
        binding.tvSign.setText(AccountHelper.getSign());
        //数据监听
        initDataObserver();
        //定时获取首页数据
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                getData();
//            }
//        }, 0, 1800000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getData();
            }
        }, 0, 60000);
        //设置按钮点击事件
        binding.ivSetting.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SettingActivity.class));
        });
        //密夹远程退出点击事件
        binding.ivQuit.setOnClickListener(v -> {
            if (!Constants.isPcOnLine) {
                ToastUtils.showShort("密夹未在线！");
            } else {
                TipsDialog mDialog = new TipsDialog(getActivity(), "是否远程退出所有密夹？");
                mDialog.setClickListener(new TipsDialog.OnClickListener() {
                    @Override
                    public void sure() {
                        JsonObject jsonObject = new JsonObject();
                        //{
                        //	"userName": "test_user",
                        //	"userId": "aaaaaaaaaa"
                        //}
                        jsonObject.addProperty("userName", AccountHelper.getNickname());
                        jsonObject.addProperty("userId", AccountHelper.getUserId());
                        byte[] data = UdpDataUtils.getDataByte((byte) 0X08, 0, 0, jsonObject.toString());
                        if (Constants.isPcConnecting) {
                            showLoading("");
                            mOutTimeObserver.addSendInstructionAndTime("0X08", instruction -> {
                                Objects.requireNonNull(getActivity()).runOnUiThread(HomeFragment.this::dismissLoading);
                                ToastUtils.showShort("远程退出失败!");
                            });
                            MainActivity.mConnectBinder.send(data);
                        } else {
                            showLoading("");
                            severTransOrderPresenter.orderTrans("0X08", Base64.encodeToString(data, Base64.DEFAULT));
//                            mOrderViewModel.serverTransOrder(Base64.encodeToString(data, Base64.DEFAULT));
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
                mDialog.show();
            }

        });


        binding.tvNickname.setText(AccountHelper.getNickname());
        ImageLoader.newLoadImage(binding.ivHeadview, AccountHelper.getAvatar());
        binding.tvSign.setText(AccountHelper.getSign());


        initDataObserver();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getData();
            }
        }, 0, 1800000);

        mOutTimeObserver = InstructionOutTimeObserver.getInstance();


        binding.smartviewv.setOnRefreshListener(refreshlayout -> getData());

        binding.smartviewv.autoRefresh();
    }

    @Override
    public void onTransSuccess(String order, byte[] data) {
       getActivity().runOnUiThread(() -> dismissLoading());
        if (data == null) {
//            ToastUtils.showShort("");
            return;
        }
        byte[] content = new byte[data.length - 15];
        System.arraycopy(data, 15, content, 0, content.length);
        String json = new String(content).trim();
        Log.i("ServerTrans","转发指令最终结果==="+json);
        switch (order) {
            case "0X08":
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String result = jsonObject.getString("result");
                    if (!StringUtils.isEmpty(result)) {
                        if ("success".equals(result)) {
                            Constants.isPcOnLine = false;
                            Constants.isPcConnecting = false;
//                        RxBus.get().post(RxBusAction.MAIN_REFRESH,"");
                            getData();
                            ToastUtils.showShort("远程退出成功!");
                        } else {
                            ToastUtils.showShort("远程退出失败！");
                        }
                    } else {
                        ToastUtils.showShort("远程退出失败！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                binding.smartviewv.finishRefresh();
                break;
            case "0X09":
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String result = jsonObject.getString("result");
                    if (!StringUtils.isEmpty(result)) {
                        if ("success".equals(result)) {
                            getData();
                            ToastUtils.showShort("重命名成功!");
                        } else {
                            ToastUtils.showShort("重命名失败！");
                        }
                    } else {
                        ToastUtils.showShort("重命名失败！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                binding.smartviewv.finishRefresh();
                break;
            case "0X01":
                if (StringUtils.isEmpty(json)) {
//                    ToastUtils.showShort("数据获取失败！文件过多，服务器返回JSON不全导致的");
                    return;
                }
                HomeDataBean.JsonInfoBean homeDataBean = new Gson().fromJson(json, HomeDataBean.JsonInfoBean.class);
                if (homeDataBean != null) {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> setDataToView(homeDataBean));
                } else {
//                    ToastUtils.showShort("数据获取失败！文件过多，PC返回JSON不全导致的");
                    Log.e("UDP","错误:获取首页数据时，Pc端返回的json格式不正确导致的");
                }
                binding.smartviewv.finishRefresh();
                break;
            case "0X10":
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String result = jsonObject.getString("result");
                    if (!StringUtils.isEmpty(result)) {
                        if ("success".equals(result)) {
                            getData();
                            ToastUtils.showShort("更改状态成功!");
                        } else {
                            ToastUtils.showShort("更改状态失败！");
                        }
                    } else {
                        ToastUtils.showShort("更改状态失败！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                binding.smartviewv.finishRefresh();
                break;
            default:
                break;
        }

    }

    @Override
    public void onFail(String order) {
        switch (order) {
            case "0X01":
                binding.smartviewv.finishRefresh();
                break;
        }
    }

    /**
     * 数据监听
     */
    private void initDataObserver() {
        mHomeDataViewModel = new HomeDataViewModel();
        //服务器获取首页数据
        mHomeDataViewModel.mReduceLiveData.observe(this, baseBean -> {
            getActivity().runOnUiThread(() -> dismissLoading());
            if (baseBean != null && baseBean.getErr().getCode() == 0) {
                setDataToView(baseBean.getJsonInfo());
            }else{
//                ToastUtils.showLong("数据获取失败！文件过多，PC返回JSON不全导致的");
                Log.e("UDP","错误:获取首页数据时，Pc端返回的json格式不正确导致的");
            }
            binding.smartviewv.finishRefresh();
        });

        //密夹远程退出指令回复监听
        mMessageReceiveManager.setReciveExitOrderListener(packet -> {
            byte[] dataBytes = packet.getData();
            Objects.requireNonNull(getActivity()).runOnUiThread(this::dismissLoading);
            byte[] content = new byte[dataBytes.length - 15];
            System.arraycopy(dataBytes, 15, content, 0, content.length);
            String json = new String(content).trim();
            try {
                JSONObject jsonObject = new JSONObject(json);
                String result = jsonObject.getString("result");
                if (!StringUtils.isEmpty(result)) {
                    if ("success".equals(result)) {
                        Constants.isPcOnLine = false;
                        Constants.isPcConnecting = false;

//                        RxBus.get().post(RxBusAction.MAIN_REFRESH,"");
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                getData();
                            }
                        },3000);
                        ToastUtils.showShort("远程退出成功!");
                    } else {
                        ToastUtils.showShort("远程退出失败！");
                    }
                } else {
                    ToastUtils.showShort("远程退出失败！");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mOutTimeObserver.cancelInstruction("0X08");
        });

        //首页数据UDP监听

        mMessageReceiveManager.setReceiveUserInfoListener(packet -> {
            byte[] dataBytes = packet.getData();
            Objects.requireNonNull(getActivity()).runOnUiThread(this::dismissLoading);
            byte[] content = new byte[dataBytes.length - 15];
            System.arraycopy(dataBytes, 15, content, 0, content.length);
            String json = new String(content).trim();
            if (StringUtils.isEmpty(json)) {
                ToastUtils.showShort("数据获取失败！");
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
//                ToastUtils.showShort("数据获取失败！文件过多，PC返回JSON不全导致的");
                Log.e("UDP","错误:获取首页数据时，Pc端返回的json格式不正确导致的");
                mOutTimeObserver.cancelInstruction("0X01");
                dismissLoading();
                return;
            }

            HomeDataBean.JsonInfoBean homeDataBean = new Gson().fromJson(json, HomeDataBean.JsonInfoBean.class);
            if (homeDataBean != null) {
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> setDataToView(homeDataBean));
            } else {
                ToastUtils.showShort("数据获取失败！");
            }
            mOutTimeObserver.cancelInstruction("0X01");
            dismissLoading();
        });

        //重命名指令回复监听
        mMessageReceiveManager.setReciveRenameOrderListener(datagramPacket -> {
            byte[] dataBytes = datagramPacket.getData();
            Objects.requireNonNull(getActivity()).runOnUiThread(this::dismissLoading);
            byte[] content = new byte[dataBytes.length - 15];
            System.arraycopy(dataBytes, 15, content, 0, content.length);
            String json = new String(content).trim();
            try {
                JSONObject jsonObject = new JSONObject(json);
                String result = jsonObject.getString("result");
                if (!StringUtils.isEmpty(result)) {
                    if ("success".equals(result)) {
                        getData();
                        ToastUtils.showShort("重命名成功!");
                    } else {
                        ToastUtils.showShort("重命名失败！");
                    }
                } else {
                    ToastUtils.showShort("重命名失败！");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            binding.smartviewv.finishRefresh();
            mOutTimeObserver.cancelInstruction("0X09");
        });
        //改变磁盘状态指令回复监听
        mMessageReceiveManager.setChangeDiskStatusOrderListener(packet -> {
            //{
            //"userName": "test_user",
            //	"userId": "aaaaaaaaaa",
            //	"gsId": "bbbbbbbbb",
            //	"gsName": "test_gs"，
            //	"diskName":"test_disk",
            //	"diskId":"ccccccccccc",
            //"cmdType": "open",		//命令类型   "open" or "close"
            //"result":"success"		//执行结果  "success" or "failed"
            //}
            byte[] dataBytes = packet.getData();
            Objects.requireNonNull(getActivity()).runOnUiThread(this::dismissLoading);
            byte[] content = new byte[dataBytes.length - 15];
            System.arraycopy(dataBytes, 15, content, 0, content.length);
            String json = new String(content).trim();
            try {
                JSONObject jsonObject = new JSONObject(json);
                String result = jsonObject.getString("result");
                if (!StringUtils.isEmpty(result)) {
                    if ("success".equals(result)) {
                        getData();
                        ToastUtils.showShort("更改状态成功!");
                    } else {
                        ToastUtils.showShort("更改状态失败！");
                    }
                } else {
                    ToastUtils.showShort("更改状态失败！");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            binding.smartviewv.finishRefresh();
            mOutTimeObserver.cancelInstruction("0X0A");
        });
        //服务器转发指令监听 获取orderId

        mOrderViewModel.transOrderBean.observe(this, baseBean -> {
            if (baseBean != null) {
                mOutTimeObserver.addSendInstructionAndTime(baseBean.getOrderId(), new InstructOutTimeListener() {
                    @Override
                    public void onOutTime(String instruction) {
                        ToastUtils.showShort("服务器转发指令失败！");
                        HomeFragment.this.dismissLoading();
                        mOutTimeObserver.cancelInstruction(baseBean.getOrderId());
                    }
                });
                new Handler().postDelayed(() -> mOrderViewModel.queryOrderResult(baseBean.getOrderId()), 1000);

            }
        });
        //监听服务器转发指令的结果
        mOrderViewModel.queryOrderResult.observe(this, baseBean -> {

            if (baseBean != null && baseBean.getErr().getCode() == 0) {
                if (!StringUtils.isEmpty(baseBean.getJsonResult())) {
                    dismissLoading();
                    mOutTimeObserver.cancelInstruction(baseBean.getOrderId());
                } else {
                    new Handler().postDelayed(() -> mOrderViewModel.queryOrderResult(baseBean.getOrderId()), 1000);
                }
            } else {
                if (baseBean != null && !StringUtils.isEmpty(baseBean.getOrderId())) {
                    if (!mOutTimeObserver.isOutTiming(baseBean.getOrderId())) {
                        new Handler().postDelayed(() -> mOrderViewModel.queryOrderResult(baseBean.getOrderId()), 1000);
                    } else {
                        mOutTimeObserver.cancelInstruction(baseBean.getOrderId());
                        dismissLoading();
                    }
                } else {
                    dismissLoading();
                }
            }
        });
    }

    private void setDataToView(HomeDataBean.JsonInfoBean jsonInfo) {
        dismissLoading();

        if (jsonInfo == null||jsonInfo.getGsInfo() == null) {
            ToastUtils.showShort("请在PC客户端新建密夹!");
            return;
        }
        mPcListAdapter.replaceData(jsonInfo.getGsInfo());
        if (jsonInfo.getGsInfo().size() > 0) {
            mDiskListAdapter.replaceData(jsonInfo.getGsInfo().get(0).getDiskInfo());
//                    mDiskListAdapter.addData(baseBean.getJsonInfo().getGsInfo().get(0).getDiskInfo());
        }

        List<FileBean> fileBeans = new ArrayList<>();
        int onLineNum = 0;
        ArrayList<GsInfoBean> gsInfoBeans = new ArrayList<>();
        for (GsInfoBean gsInfoBean : jsonInfo.getGsInfo()) {
            if ("online".equals(gsInfoBean.getGsStatu())) {
                onLineNum++;
            }
            for (DiskInfoBean diskInfoBean : gsInfoBean.getDiskInfo()) {
                for (FileBean bean : diskInfoBean.getFileInfo()) {
                    FileBean fileBean = new FileBean();
                    fileBean.setGsId(gsInfoBean.getGsId());
                    fileBean.setDiskId(diskInfoBean.getDiskId());
                    fileBean.setName(bean.getName());
                    fileBean.setSize(bean.getSize());
                    fileBean.setType(bean.getType());
                    fileBean.setTime(bean.getTime());
                    fileBeans.add(fileBean);
                }
                gsInfoBean.diskList.add(diskInfoBean);
            }
            gsInfoBeans.add(gsInfoBean);
        }

        mGsInfoBeanBox.put(gsInfoBeans);
        if (onLineNum > 0) {
            miDunIsOffLine.set(false);
        } else {
            miDunIsOffLine.set(true);
        }

        if (fileBeans.size()==0) {
            return;
        }
        mGsInfoBeanBox.removeAll();
        mFileBeanBox.removeAll();
        Log.i("objectBox","开始存储"+System.currentTimeMillis());
        MyApp.getInstance().getBoxStore().runInTxAsync(() -> mFileBeanBox.put(fileBeans), (result, error) -> {
            RxBus.get().post(RxBusAction.FILE_DATA_LOAD_FINISH, "");
            Log.i("objectBox","存储结束"+System.currentTimeMillis());
        });
    }

    private void getData() {
//        Objects.requireNonNull(getActivity()).runOnUiThread(() -> showLoading(""));

        if (Constants.isPcOnLine) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userName", AccountHelper.getNickname());
            jsonObject.addProperty("userId", AccountHelper.getUserId());
            byte[] data = UdpDataUtils.getDataByte((byte) 0X01, 0, 0, jsonObject.toString().trim());
            if (Constants.isPcConnecting) {
                // { "userName": "test_user", "userId": "aaaaaaaaaa" }
//                MainActivity.mConnectBinder.send(data);
//                mOutTimeObserver.addSendInstructionAndTime("0X01", instruction -> {
//                    ToastUtils.showShort("数据获取失败!请求超时导致的" );
//                    dismissLoading();
//                });

                severTransOrderPresenter.orderTrans("0X01", Base64.encodeToString(data, Base64.DEFAULT));
            } else {
                severTransOrderPresenter.orderTrans("0X01", Base64.encodeToString(data, Base64.DEFAULT));
//                mOrderViewModel.serverTransOrder(Base64.encodeToString(data, Base64.DEFAULT));
            }

        } else {
            mHomeDataViewModel.getHomeData();
        }
    }

    private void initDiskAdapter() {
        mDiskListAdapter = new HomeDiskListAdapter(R.layout.item_home_disk, getActivity());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.diskRecycler.setLayoutManager(mLayoutManager);
        binding.diskRecycler.setAdapter(mDiskListAdapter);
        mDiskListAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.iv_more:
                    String status = mDiskListAdapter.getData().get(position).getDiskStatu();
                    MakeDialog makeDialog = new MakeDialog(Objects.requireNonNull(getActivity()), status.equals("online") ? "断开" : "连接", "", "");
                    makeDialog.setMakeListener(index -> {
                        if (!Constants.isPcOnLine) {
                            ToastUtils.showShort("密夹未在线！");
                            return;
                        }
                        //  { "userName": "test_user",
                        //  "userId": "aaaaaaaaaa",
                        //  "gsId": "bbbbbbbbb",
                        //  "gsName": "test_gs",
                        //  "diskName":"test_disk",
                        //  "diskId":"ccccccccccc",
                        // "cmdType": "open"		//命令类型   "open" or "close" }
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("userName", AccountHelper.getNickname());
                        jsonObject.addProperty("userId", AccountHelper.getUserId());
                        jsonObject.addProperty("gsId", Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
                        jsonObject.addProperty("gsName", Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());
                        jsonObject.addProperty("diskName", mDiskListAdapter.getData().get(position).getDiskName());
                        jsonObject.addProperty("diskId", mDiskListAdapter.getData().get(position).getDiskId());
                        if ("online".equals(status)) {
                            jsonObject.addProperty("cmdType", "offline");
                        } else {
                            jsonObject.addProperty("cmdType", "online");
                        }
                        byte[] data = UdpDataUtils.getDataByte((byte) 10, 1, 1, jsonObject.toString().trim());
                        Log.i("UDP", "udpSend===" + data);
                        if (Constants.isPcConnecting) {
                            showLoading("");
                            mOutTimeObserver.addSendInstructionAndTime("0X0A", instruction -> {
                                dismissLoading();
                                ToastUtils.showShort("更改磁盘状态失败！");
                            });
                            MainActivity.mConnectBinder.send(data);
                        } else {
                            showLoading("");
                            severTransOrderPresenter.orderTrans("0X10", Base64.encodeToString(data, Base64.DEFAULT));
//                            mOrderViewModel.serverTransOrder(Base64.encodeToString(data, Base64.DEFAULT));
                        }
                    });
                    makeDialog.show();
                    break;
                case R.id.card_main:
                    if (mDiskListAdapter.getSelectIndex() == position) {

                    } else {
                        mDiskListAdapter.setSelectIndex(position);
                        mDiskListAdapter.notifyDataSetChanged();
                        RxBus.get().post(RxBusAction.DISK_CHANGE_OR_DATA_REFRESH, "");
                    }
                    break;
                default:
                    break;
            }
        });
    }

    private void initPcAdapter() {
        mPcListAdapter = new HomePcListAdapter(R.layout.item_home_pc);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.pcRecycler.setLayoutManager(mLayoutManager);
        binding.pcRecycler.setAdapter(mPcListAdapter);
        mPcListAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.iv_more:
                    MakeDialog makeDialog = new MakeDialog(Objects.requireNonNull(getActivity()), "重命名", "", "");
                    makeDialog.setMakeListener(index -> {
                        if (index == 1) {
                            if (!Constants.isPcOnLine) {
                                ToastUtils.showShort("密夹未在线！");
                                return;
                            }
                            MiJiaReNameDialog renameDialog = new MiJiaReNameDialog(getActivity());
                            renameDialog.setOnClickSureListener(name -> {
                                // { "userName": "test_user",
                                // "userId": "aaaaaaaaaa",
                                // "gsId": "bbbbbbbbb",
                                // "gsName": "test_gs" }
                                if (Constants.isPcOnLine) {
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("userName", AccountHelper.getNickname());
                                    jsonObject.addProperty("userId", AccountHelper.getUserId());
                                    jsonObject.addProperty("gsId", mPcListAdapter.getData().get(position).getGsId());
                                    // jsonObject.addProperty("gsName",mPcListAdapter.getData().get(position).getGsName());
                                    jsonObject.addProperty("gsName", name);
                                    byte[] bytes = UdpDataUtils.getDataByte((byte) 0X09, 0, 0, jsonObject.toString());
                                    if (Constants.isPcConnecting) {
                                        showLoading("");
                                        MainActivity.mConnectBinder.send(bytes);
                                        mOutTimeObserver.addSendInstructionAndTime("0X09", instruction -> {
                                            dismissLoading();
                                            ToastUtils.showShort("重命名失败！");
                                        });
                                    } else {
                                        showLoading("");
                                        String baseString = Base64.encodeToString(bytes, Base64.DEFAULT);
                                        Log.i("UDP+SERVER", "0X09send==" + baseString.toString());
//                                        mOrderViewModel.serverTransOrder(baseString);
                                        showLoading("");
                                        severTransOrderPresenter.orderTrans("0X09", baseString);
                                    }
                                }

                            });
                            renameDialog.show();
                        }
                    });

                    makeDialog.show();
                    break;
                case R.id.rv_main:
                    mPcListAdapter.setSelectIndex(position);
                    mPcListAdapter.notifyDataSetChanged();
                    mDiskListAdapter.setSelectIndex(0);
                    mDiskListAdapter.replaceData(mPcListAdapter.getData().get(position).getDiskInfo());
                    break;
                default:
                    break;
            }
        });
    }


}
