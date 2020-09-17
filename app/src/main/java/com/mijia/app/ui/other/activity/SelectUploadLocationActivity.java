package com.mijia.app.ui.other.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.handong.framework.account.AccountHelper;
import com.handong.framework.base.BaseActivity;
import com.handong.framework.base.BaseViewModel;
import com.hwangjr.rxbus.RxBus;
import com.mijia.app.MainActivity;
import com.mijia.app.MyApp;
import com.mijia.app.R;
import com.mijia.app.bean.FileBean;
import com.mijia.app.bean.SimpleBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.constants.RxBusAction;
import com.mijia.app.constants.Sys;
import com.mijia.app.databinding.ActivitySelectUploadLocationBinding;
import com.mijia.app.presenter.OnServerTransListener;
import com.mijia.app.presenter.SeverTransOrderPresenter;
import com.mijia.app.service.ReciveNewFloderOrderListener;
import com.mijia.app.socket.InstructionOutTimeObserver;
import com.mijia.app.socket.MessageReceiveManager;
import com.mijia.app.socket.UdpDataUtils;
import com.mijia.app.sys.FileBoxUtils;
import com.mijia.app.ui.disk.adapter.DiskFileListAdapter;
import com.mijia.app.ui.other.dialog.CreateNewFolderDialog;
import com.mijia.app.viewmodel.ServerTransOrderViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.objectbox.Box;

import static com.mijia.app.constants.Sys.TRANS;
import static com.mijia.app.socket.InstructionOutTimeObserver.getInstance;

public class SelectUploadLocationActivity extends BaseActivity<ActivitySelectUploadLocationBinding, BaseViewModel> implements OnServerTransListener {

    public static final String SelectLocationType = "SELECTLOCATIONTYPE";
    public static final int Upload = 0; //上传
    public static final int Move = 1; // 移动
    public static final int Copy = 2; //复制
    public static final int BackUp = 3; // 备份
    private int type = 0;

    private DiskFileListAdapter mDiskFileListAdapter;
    private PathTitleAdapter mPathTitleAdapter;

    public static final ObservableField<String> midun = new ObservableField<>();
    public static final ObservableField<String> disk = new ObservableField<>();
    public static final ObservableField<String> midunId = new ObservableField<>();
    public static final ObservableField<String> diskId = new ObservableField<>();

    //    private String selectedPath = Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get()+"/"+Constants.SYS_CONTANTS_BEAN.SelectedDisk.get();
    private String selectedPath = "";

    private List<String> pathList = new ArrayList<>();

    private InstructionOutTimeObserver mOutTimeObserver;
    private MessageReceiveManager mMessageReceiveManager;

    private ServerTransOrderViewModel mOrderViewModel;

    private SeverTransOrderPresenter severTransOrderPresenter;


    private String createName = "";

    @Override
    public int getLayoutRes() {
        return R.layout.activity_select_upload_location;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        midun.set(Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());
        disk.set(Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
        midunId.set(Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
        diskId.set(Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
        mBinding.setHandler(this);
        type = getIntent().getIntExtra(SelectLocationType, 0);
        severTransOrderPresenter = new SeverTransOrderPresenter(this);
        initType();

        mBinding.tvCreateFolder.setOnClickListener(v -> {
            CreateNewFolderDialog createNewFolderDialog = new CreateNewFolderDialog(this);
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
                fileinfoObject.addProperty("srcObj", selectedPath + "/" + string);//path
//                            fileinfoObject.addProperty("dstDisk", Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
//                            fileinfoObject.addProperty("dstDiskId", Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
//                            fileinfoObject.addProperty("dstObj", string);
                jsonArray.add(fileinfoObject);
                mainObject.add("fileInfo", jsonArray);
                byte[] data = UdpDataUtils.getDataByte((byte) 0X0B, 0, 0, mainObject.toString());
                if (Constants.isPcConnecting) {
                    createName = selectedPath + "/" + string;
                    MainActivity.mConnectBinder.send(data);
                    initDataObserver();
                    mOutTimeObserver.addSendInstructionAndTime("0X0B", instruction -> {
                        ToastUtils.showShort("新建文件夹失败!");
                    });
                } else {
                    showLoading("");
                    severTransOrderPresenter.orderTrans("0X0B",Base64.encodeToString(data, Base64.DEFAULT));
//                    mOrderViewModel.serverTransOrder(Base64.encodeToString(data, Base64.DEFAULT));
                }

            });

            createNewFolderDialog.show();
        });

//        mBinding.tvSure.setOnClickListener(v -> {
//            RxBus.get().post(RxBusAction.MAIN_PAGE_CURRENT, TRANS + "");
//            startActivity(new Intent(SelectUploadLocationActivity.this, MainActivity.class));
//            finish();
//        });


        mBinding.tvCancel.setOnClickListener(v -> {
            finish();
        });
        pathList.add(Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());
        pathList.add(Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());

        initRecycler();
        initPathRecycler();

        mBinding.smartview.setOnRefreshListener(refreshlayout -> {
            refresh();
            mBinding.smartview.finishRefresh();
        });
        mBinding.smartview.autoRefresh();
        mBinding.tvSure.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(Sys.SELECT_PATH, selectedPath+"/");
            setResult(Activity.RESULT_OK, intent);
            finish();
        });

        mBinding.ivReturn.setOnClickListener(v -> {
            selectedPath = selectedPath.substring(0, selectedPath.lastIndexOf("/"));
            pathList = pathList.subList(0, pathList.size() - 1);
            refresh();
        });

        mOutTimeObserver = getInstance();
        mMessageReceiveManager = MessageReceiveManager.getInstance();
        mOrderViewModel = new ServerTransOrderViewModel();
        //数据监听
    }

    private void initDataObserver() {

        mMessageReceiveManager.setmReciveNewFloderOrderListener(packet -> {
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


                       FileBoxUtils. createNewFloder(createName);
                        runOnUiThread(() -> refresh());
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

    /**
     * 刷新
     */
    private void refresh() {
        List<FileBean> fileBeanList = FileBoxUtils.getFloderByPath(selectedPath);
        if (fileBeanList.size() == 0) {
            mBinding.layoutNoData.setVisibility(View.VISIBLE);
        } else {
            mBinding.layoutNoData.setVisibility(View.GONE);
        }
        mDiskFileListAdapter.replaceData(fileBeanList);
        pathRefresh();
        if (pathList.size() > 2) {
            mBinding.ivReturn.setVisibility(View.VISIBLE);
        } else {
            mBinding.ivReturn.setVisibility(View.GONE);
        }
    }



    /**
     * 路径标题adapter
     */
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

    /**
     * 路径title初始化
     */
    private void initPathRecycler() {
        mPathTitleAdapter = new PathTitleAdapter(R.layout.item_path_title);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(SelectUploadLocationActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBinding.recyclerPath.setLayoutManager(mLayoutManager);
        mBinding.recyclerPath.setAdapter(mPathTitleAdapter);
        pathRefresh();

        mPathTitleAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (position == 0) {
                if (type == BackUp || type == Upload) {
                    startActivity(new Intent(SelectUploadLocationActivity.this, SelectMiDunActivity.class));
                }
            } else if (position == 1) {
                if (type == BackUp || type == Upload) {
                    startActivity(new Intent(SelectUploadLocationActivity.this, SelectDiskActivity.class));
                }
            } else {
                StringBuilder path = new StringBuilder();
                for (int i = 2; i <= position; i++) {
                    path.append("/").append(mPathTitleAdapter.getItem(i));
                }
                selectedPath = path.toString();
                int index = pathList.indexOf(mPathTitleAdapter.getItem(position)) + 1;
                pathList = pathList.subList(0, index);
                refresh();
            }

        });
    }

    private void pathRefresh() {
        mPathTitleAdapter.replaceData(pathList);
    }

    private void initRecycler() {
        mDiskFileListAdapter = new DiskFileListAdapter(R.layout.item_file_list);
        mDiskFileListAdapter.setHideSelectIcon(true);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(SelectUploadLocationActivity.this));
        mBinding.recycler.setAdapter(mDiskFileListAdapter);
        mDiskFileListAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.ll_item_view:
                    String floderPath = mDiskFileListAdapter.getData().get(position).getName();
                    selectedPath = floderPath;
                    pathList.add(floderPath.substring(floderPath.lastIndexOf("/") + 1));
                    refresh();
                    break;
                default:
                    break;
            }
        });
//        mDiskFileListAdapter.replaceData(Arrays.asList(new SimpleBean("测试文件夹一"), new SimpleBean("测试文件夹二"), new SimpleBean("测试文件三")));
    }

    private void initType() {
        switch (type) {
            case Upload:
                mBinding.tvTitle.setText("选择上传位置");
                break;
            case Move:
                mBinding.tvTitle.setText("选择移动位置");
                break;
            case Copy:
                mBinding.tvTitle.setText("选择复制位置");
                break;
            case BackUp:
                mBinding.tvTitle.setText("选择备份位置");
                break;
            default:
                break;
        }
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
                            ToastUtils.showShort("新建文件夹成功!");
                            FileBoxUtils. createNewFloder(createName);

                            runOnUiThread(() -> refresh());
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
