package com.mijia.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.databinding.ObservableBoolean;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.handong.framework.account.AccountHelper;
import com.handong.framework.base.BaseActivity;
import com.handong.framework.base.BaseViewModel;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.mijia.app.bean.DiskInfoBean;
import com.mijia.app.bean.FileBean;
import com.mijia.app.bean.HomeDataBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.constants.RxBusAction;
import com.mijia.app.databinding.ActivityMainBinding;
import com.mijia.app.dialog.MessageDialog;
import com.mijia.app.dialog.UploadFileDialog;
import com.mijia.app.presenter.OnServerTransListener;
import com.mijia.app.presenter.SeverTransOrderPresenter;
import com.mijia.app.service.MiJiaService;
import com.mijia.app.service.ReciveNewFloderOrderListener;
import com.mijia.app.service.TransferListener;
import com.mijia.app.socket.Communication;
import com.mijia.app.socket.DownLoadManager;
import com.mijia.app.socket.MessageReceiveManager;
import com.mijia.app.socket.NetWorkStateReceiver;
import com.mijia.app.socket.UdpDataUtils;
import com.mijia.app.socket.UpLoadManager;
import com.mijia.app.ui.backup.fragment.BackUpContactsFragment;
import com.mijia.app.ui.disk.fragment.DiskManagerFragment;
import com.mijia.app.ui.home.fragment.HomeFragment;
import com.mijia.app.ui.other.activity.SelectOtherUploadActivity;
import com.mijia.app.ui.other.activity.SelectPhotoArrayActivity;
import com.mijia.app.ui.other.activity.SelectVoiceVideoFileActivity;
import com.mijia.app.ui.other.dialog.CreateNewFolderDialog;
import com.mijia.app.ui.trans.fragment.TransferListFragment;
import com.mijia.app.socket.InstructionOutTimeObserver;
import com.mijia.app.utils.NetState;
import com.mijia.app.viewmodel.HeartBeatViewModel;
import com.mijia.app.viewmodel.HomeDataViewModel;
import com.mijia.app.viewmodel.ServerTransOrderViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.mijia.app.constants.Constants.DISK_NOW_PAGE_PATH;
import static com.mijia.app.constants.Constants.NEICONNECTTIME;
import static com.mijia.app.constants.Sys.BACK_UP;
import static com.mijia.app.constants.Sys.DISK;
import static com.mijia.app.constants.Sys.HOME_PAGE;
import static com.mijia.app.constants.Sys.TRANS;
import static com.mijia.app.dialog.UploadFileDialog.CREATE_FOLDER;
import static com.mijia.app.dialog.UploadFileDialog.UPLOAD_FILE;
import static com.mijia.app.dialog.UploadFileDialog.UPLOAD_PHOTO;
import static com.mijia.app.dialog.UploadFileDialog.UPLOAD_TOTHER;
import static com.mijia.app.dialog.UploadFileDialog.UPLOAD_VIDEO;
import static com.mijia.app.dialog.UploadFileDialog.UPLOAD_VOICE;
import static com.mijia.app.socket.InstructionOutTimeObserver.getInstance;

/**
 * 
 * 
 * author yandon'tknow
 * email yancygzzy@gmail.com
 * create  
 */
public class MainActivity extends BaseActivity<ActivityMainBinding, BaseViewModel> implements OnServerTransListener {

    public static MiJiaService.ConnectBinder mConnectBinder = null;

    private int mSelectorIndex = HOME_PAGE;

    public static ObservableBoolean isBottomGone = new ObservableBoolean(false);

    private final int CODE_FOR_WRITE_PERMISSION = 123;

    private HeartBeatViewModel mHeartBeatViewModel;


    private SeverTransOrderPresenter severTransOrderPresenter;

    NetWorkStateReceiver netWorkStateReceiver;


    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        RxBus.get().register(this);
        severTransOrderPresenter = new SeverTransOrderPresenter(this);
        ServiceUtils.bindService(MiJiaService.class, mServiceConnection, BIND_AUTO_CREATE);
        initViewPager();
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
        mHeartBeatViewModel = new HeartBeatViewModel();
        mHeartBeatViewModel.heartBeatBean.observe(this, heartBeatBean -> {
            if (heartBeatBean != null && heartBeatBean.getErr().getCode() == 0) {
                if (mConnectBinder == null) {
                    return;
                }
                // 是否和pc端通訊着
                Constants.isPcOnLine = true;
                if (!Constants.isPcConnecting) {
                    mConnectBinder.setAddress(heartBeatBean.getSip(), heartBeatBean.getSport());
                    mConnectBinder.hole();
                }
            } else {
                if (heartBeatBean != null && heartBeatBean.getErr() != null) {
                    ToastUtils.showShort(heartBeatBean.getErr().getMsg());
                }
                Constants.isPcOnLine = false;
            }
        });


//        String json = "{\"userName\":\"test_user\",\"userId\":\"aaaaaaaaaa\",\"ip\":\"192.168.1.100\",\"port\":\"8888\"}";
//
//        byte[] testByte = UdpDataUtils.getDataByte((byte) 0X0D, 0, 0, json);
//        String testStr = UdpDataUtils.getData((byte) 0X0D, 0, 0, json);
        //     System.out.println("测试工具类Byte=="+ new String(testByte));
        //   System.out.println("测试工具类Str=="+ testStr);


//        Observable.interval(3, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
//            @Override
//            public void accept(Long aLong) throws Exception {
//                InstructionOutTimeObserver instructionOutTimeObservable = InstructionOutTimeObserver.getInstance();
//                instructionOutTimeObservable.addSendInstructionAndTime("指令 " + aLong, new InstructionOutTimeObserver.InstructOutTimeListener() {
//                    @Override
//                    public void onOutTime(String instruction) {
//                        ToastUtils.showShort("超时1=="+instruction);
//                    }
//                });
//            }
//        });

        mOrderViewModel = new ServerTransOrderViewModel();
        mOutTimeObserver = getInstance();
        mMessageReceiveManager = MessageReceiveManager.getInstance();
        //数据监听
        initDataObserver();

        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);

        if (NetState.getNetWorkConnectionType(this) == ConnectivityManager.TYPE_WIFI) {
            Constants.Net_Status = 1;
        }
        if (NetState.getNetWorkConnectionType(this) == ConnectivityManager.TYPE_MOBILE) {
            Constants.Net_Status = 0;
        }



//        String json = "{\"userName\":\"在路上\",\"userId\":\"18dd6ab35ed740c17a50d66250bbc089\",\"gsId\":\"48919fb4e340430b47b1a94aeeb467a9\",\"gsName\":\"太累了\"}";
//        byte[] bytes = UdpDataUtils.getDataByte((byte) 0X01, 0, 0, json.toString());
//        String baseString = Base64.encodeToString(bytes, Base64.DEFAULT);
//        Log.i("TEST",baseString.toString());

    }

    private void setReviceListener() {
        mMessageReceiveManager.setmReciveNewFloderOrderListener(new ReciveNewFloderOrderListener() {
            @Override
            public void onRecive(DatagramPacket packet) {
                byte[] dataBytes = packet.getData();
                runOnUiThread(() -> dismissLoading());
                byte[] content = new byte[dataBytes.length - 15];
                System.arraycopy(dataBytes, 15, content, 0, content.length);
                String json = new String(content).trim();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String result = jsonObject.getString("result");
                    if (!StringUtils.isEmpty(result)) {
                        if ("success".equals(result)) {
                            if (StringUtils.isEmpty(DISK_NOW_PAGE_PATH)) {
                                RxBus.get().post(RxBusAction.DISK_HOME_REFRESH, "");
                            } else {
                                RxBus.get().post(RxBusAction.DISK_CURRENT_REFRESH, "");
                            }
                            ToastUtils.showShort("新建文件夹成功!");
                        } else {
                            ToastUtils.showShort("新建文件夹失败！");
                        }
                    } else {
                        ToastUtils.showShort("新建文件夹失败！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mOutTimeObserver.cancelInstruction("0X0B");
            }
        });

    }

    /**
     * 数据监听
     */
    private void initDataObserver() {


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

        });
        //服务器转发指令监听 获取orderId

        mOrderViewModel.transOrderBean.observe(this, baseBean -> {
            if (baseBean != null) {
                mOutTimeObserver.addSendInstructionAndTime(baseBean.getOrderId(), instruction -> {
                    if ("0X0B".equals(instruction)) {
                        dismissLoading();
                        ToastUtils.showShort("新建文件夹失败!");
                    }
                });
                mOrderViewModel.queryOrderResult(baseBean.getOrderId());

            }
        });
        //监听服务器转发指令的结果
        mOrderViewModel.queryOrderResult.observe(this, baseBean -> {
            if (baseBean != null && baseBean.getErr().getCode() == 0) {
                dismissLoading();
                if (!StringUtils.isEmpty(baseBean.getJsonResult())) {
                    mOutTimeObserver.cancelInstruction(baseBean.getOrderId());
                } else {
                    mOrderViewModel.queryOrderResult(baseBean.getOrderId());
                }
            } else {
                if (baseBean != null && !StringUtils.isEmpty(baseBean.getOrderId())) {
                    if (!mOutTimeObserver.isOutTiming(baseBean.getOrderId())) {
                        mOrderViewModel.queryOrderResult(baseBean.getOrderId());
                    }
                }
            }
        });
    }

    private void initViewPager() {
        mBinding.setHandler(this);
        mBinding.container.setOffscreenPageLimit(4);
        mBinding.container.setAdapter(new MainFragmentAdapter(getSupportFragmentManager()));

        mBinding.container.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        RxBus.get().post(RxBusAction.MAIN_PAGE_IN_TRAN, "");
                        break;
                    case 3:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        changeBottomStatus();
        initSelector();
        checkPermission();
    }

    private void checkPermission() {
        List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if ((checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)) {
//                permissionsList.add(Manifest.permission.READ_PHONE_STATE);
//            }
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
//            if ((checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
//                permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
//            }
            if ((checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)) {
                permissionsList.add(Manifest.permission.READ_CONTACTS);
            }
            if ((checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)) {
                permissionsList.add(Manifest.permission.ACCESS_NETWORK_STATE);
            }
            if ((checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED)) {
                permissionsList.add(Manifest.permission.WRITE_CONTACTS);
            }
            if ((checkSelfPermission(Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED)) {
                permissionsList.add(Manifest.permission.WRITE_SETTINGS);
            }

            if (permissionsList.size() != 0) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        CODE_FOR_WRITE_PERMISSION);
            } else {

            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //通过requestCode来识别是否同一个请求
        if (requestCode == CODE_FOR_WRITE_PERMISSION) {
            List<String> pression = new ArrayList<>();
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        pression.add(permissions[i]);
                    }
                }
            }

            if (pression.size() > 0) {
                //用户不同意，向用户展示该权限作用
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("请赋予权限，否则APP无法正常使用！")
                            .setPositiveButton("OK", (dialog1, which) ->
                                    MainActivity.this.requestPermissions(pression.toArray(new String[pression.size()]),
                                            CODE_FOR_WRITE_PERMISSION))
                            .setNegativeButton("Cancel", (dialog, which) -> MainActivity.this.requestPermissions(pression.toArray(new String[pression.size()]),
                                    CODE_FOR_WRITE_PERMISSION))
                            .create()
                            .show();
                }
            } else {

            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_home:
                mSelectorIndex = HOME_PAGE;
                break;
            case R.id.ll_disk:
                mSelectorIndex = DISK;
                break;
            case R.id.ll_trans:
                mSelectorIndex = TRANS;
                break;
            case R.id.ll_backup:
                mSelectorIndex = BACK_UP;
                break;
            case R.id.iv_add:
                showUpdataDialog();
                break;
            default:
                break;
        }
        changeBottomStatus();
    }

    private ServerTransOrderViewModel mOrderViewModel;
    private InstructionOutTimeObserver mOutTimeObserver;
    private MessageReceiveManager mMessageReceiveManager;

    private void showUpdataDialog() {
        UploadFileDialog uploadFileDialog = new UploadFileDialog(this);
        uploadFileDialog.setOnClickListener(type -> {

            if (!Constants.isPcOnLine || !Constants.isPcConnecting) {
                ToastUtils.showShort("密夹未在线或者未成功连接通讯！");
                return;
            }
            if (!Constants.SYS_CONTANTS_BEAN.SelectedDiskIsOnLine.get()) {
                ToastUtils.showShort("当前磁盘未在线！");
                return;
            }


            switch (type) {


                case CREATE_FOLDER:
                    CreateNewFolderDialog createNewFolderDialog = new CreateNewFolderDialog(MainActivity.this);
                    createNewFolderDialog.setOnClickSureListener(string -> {
                        JsonObject mainObject = new JsonObject();
                        mainObject.addProperty("userName", AccountHelper.getNickname());
                        mainObject.addProperty("userId", AccountHelper.getUserId());
                        mainObject.addProperty("gsId", Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
                        mainObject.addProperty("gsName", Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());
                        JsonArray jsonArray = new JsonArray();
                        JsonObject fileinfoObject = new JsonObject();
                        fileinfoObject.addProperty("fileCmd", "new");
                        fileinfoObject.addProperty("objType", "floder"); // file or floder
                        fileinfoObject.addProperty("srcDisk", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
                        fileinfoObject.addProperty("srcDiskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
                        fileinfoObject.addProperty("srcObj", DISK_NOW_PAGE_PATH + "/" + string);//path
//                            fileinfoObject.addProperty("dstDisk", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
//                            fileinfoObject.addProperty("dstDiskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
//                            fileinfoObject.addProperty("dstObj", string);
                        jsonArray.add(fileinfoObject);
                        mainObject.add("fileInfo", jsonArray);
                        byte[] data = UdpDataUtils.getDataByte((byte) 0X0B, 0, 0, mainObject.toString());
                        if (Constants.isPcConnecting) {
                            MainActivity.mConnectBinder.send(data);
                            setReviceListener();
                            mOutTimeObserver.addSendInstructionAndTime("0X0B", instruction -> {
                                ToastUtils.showShort("新建文件夹失败!");
                            });
                        } else {
                            showLoading("");
                            severTransOrderPresenter.orderTrans("0X0B", Base64.encodeToString(data, Base64.DEFAULT));
//                            mOrderViewModel.serverTransOrder(Base64.encodeToString(data, Base64.DEFAULT));
                        }
                    });
                    createNewFolderDialog.show();
                    break;
                case UPLOAD_PHOTO:
                    startActivity(new Intent(MainActivity.this, SelectPhotoArrayActivity.class));
                    break;
                case UPLOAD_VIDEO:
                case UPLOAD_FILE:
                case UPLOAD_VOICE:
                    startActivity(new Intent(MainActivity.this, SelectVoiceVideoFileActivity.class).putExtra("type", type));
                    break;
                case UPLOAD_TOTHER:
                    startActivity(new Intent(MainActivity.this, SelectOtherUploadActivity.class));
                    break;
                default:
                    break;
            }
        });
        uploadFileDialog.show();
    }

    @Subscribe(tags = {@Tag(RxBusAction.MAIN_REFRESH)}, thread = EventThread.MAIN_THREAD)
    public void refresh(String str) {
        mHeartBeatViewModel.heartBeat(Communication.getInstance().getLocationPort() + "");
    }


    @Subscribe(tags = {@Tag(RxBusAction.MAIN_OPEN_UPLOAD_DIALOG)}, thread = EventThread.MAIN_THREAD)
    public void openUploadDialog(String type) {
        showUpdataDialog();
    }

    @Subscribe(tags = {@Tag(RxBusAction.MAIN_PAGE_CURRENT)}, thread = EventThread.MAIN_THREAD)
    public void changeCurritem(String index) {
        mSelectorIndex = Integer.parseInt(index);
        if (mSelectorIndex == 2) {
            RxBus.get().post(RxBusAction.TRAN_PAGE_DOWNLOAD, "");
        }
        changeBottomStatus();
    }

    private void changeBottomStatus() {
        mBinding.tvHome.setSelected(mSelectorIndex == HOME_PAGE);
        mBinding.ivHome.setSelected(mSelectorIndex == HOME_PAGE);
        mBinding.tvDisk.setSelected(mSelectorIndex == DISK);
        mBinding.ivDisk.setSelected(mSelectorIndex == DISK);
        mBinding.tvTrans.setSelected(mSelectorIndex == TRANS);
        mBinding.ivTrans.setSelected(mSelectorIndex == TRANS);
        mBinding.tvBackup.setSelected(mSelectorIndex == BACK_UP);
        mBinding.ivBackup.setSelected(mSelectorIndex == BACK_UP);
        initSelector();
    }

    private void initSelector() {
        mBinding.container.setCurrentItem(mSelectorIndex);
    }


    private TransferListFragment mTransferListFragment;


    public class MainFragmentAdapter extends FragmentPagerAdapter implements TransferListener {

        public MainFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return new DiskManagerFragment();
                case 2:
                    if (mTransferListFragment == null) {
                        mTransferListFragment = new TransferListFragment();
                    }
                    return mTransferListFragment;
                case 3:
                    return new BackUpContactsFragment();
                default:
                    break;
            }
            throw new RuntimeException();
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public void send(String data) {
            if (mConnectBinder == null) {
                ToastUtils.showShort("连接失败");
                return;
            }
            mConnectBinder.send(data);
        }
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

//            MiJiaService.ConnectBinder connectBinder = (MiJiaService.ConnectBinder) iBinder;
//            connectBinder.setAddress("192.168.1.145","2238");
//            connectBinder.hole();
//
            mConnectBinder = (MiJiaService.ConnectBinder) iBinder;
//            mConnectBinder.setAddress("192.168.1.145", "2238");
//            boolean isLive = mConnectBinder.getIsLive();
//            if (!isLive) {
//                mConnectBinder.hole();
//            }

            mConnectBinder.serverConnect();

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.i("okhttp","请求一次服务器心跳");
                    mHeartBeatViewModel.heartBeat(Communication.getInstance().getLocationPort() + "");
                    //180000
                }
            }, 0, 60000);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private long p = 0;

    @Override
    public void onBackPressed() {
        if (isBottomGone.get()) {
            isBottomGone.set(false);

            DiskManagerFragment.isOpenSelect.set(false);
            DiskManagerFragment.selectedFileList.clear();
            DiskManagerFragment.selectedNum.set(0);
            DiskManagerFragment.isMiWenOpen.set(false);

            TransferListFragment.isOpenSelect.set(false);
            TransferListFragment.selectType.set(0);
            return;
        }

        if (DiskManagerFragment.isOpenSelect.get()) {
            DiskManagerFragment.isOpenSelect.set(false);
            DiskManagerFragment.selectedFileList.clear();
            DiskManagerFragment.selectedNum.set(0);
            DiskManagerFragment.isMiWenOpen.set(false);
        }

        if (TransferListFragment.isOpenSelect.get()) {
            TransferListFragment.isOpenSelect.set(false);
            TransferListFragment.selectType.set(0);
        }


        int downTaskNumber = DownLoadManager.getInstance().taskRunningNumber();
        if (downTaskNumber >= 1) {
            MessageDialog messageDialog = new MessageDialog(this);
            messageDialog.setMessage("还有" + downTaskNumber + "个任务正在下载中,确定要退出嘛");
            messageDialog.setSureListener(view -> {
                DownLoadManager.getInstance().exit();
                UpLoadManager.getInstance().exit();
                AppUtils.exitApp();
            });
            messageDialog.show();
            return;
        }
        int uploadTaskNumber = UpLoadManager.getInstance().taskRuningNumber();
        if (uploadTaskNumber >= 1) {
            MessageDialog messageDialog = new MessageDialog(this);
            messageDialog.setMessage("还有" + uploadTaskNumber + "个任务正在上传中,确定要退出嘛");
            messageDialog.setSureListener(view -> {
                UpLoadManager.getInstance().exit();
                DownLoadManager.getInstance().exit();
                AppUtils.exitApp();
            });
            messageDialog.show();
            return;
        }


        if (!DiskManagerFragment.isOutPage) {
            RxBus.get().post(RxBusAction.DISK_MANAGER_RETURN, "");
        } else {
            long timeMillis = System.currentTimeMillis();
            if (timeMillis - p < 1500) {
                super.onBackPressed();
            } else {
                p = System.currentTimeMillis();
                ToastUtils.showShort("再按一次退出密夹");
            }

        }


//        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
        ServiceUtils.unbindService(mServiceConnection);
        unregisterReceiver(netWorkStateReceiver);
    }

    @Override
    public void onTransSuccess(String order, byte[] data) {
        dismissLoading();
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
                            if (StringUtils.isEmpty(DISK_NOW_PAGE_PATH)) {
                                RxBus.get().post(RxBusAction.DISK_HOME_REFRESH, "");
                            } else {
                                RxBus.get().post(RxBusAction.DISK_CURRENT_REFRESH, "");
                            }
                            ToastUtils.showShort("新建文件夹成功!");
                        } else {
                            ToastUtils.showShort("新建文件夹失败！");
                        }
                    } else {
                        ToastUtils.showShort("新建文件夹失败！");
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
