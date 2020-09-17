package com.mijia.app.ui.home.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.handong.framework.account.AccountHelper;
import com.handong.framework.base.BaseActivity;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.MyApp;
import com.mijia.app.R;
import com.mijia.app.bean.DownRowTaskBean;
import com.mijia.app.bean.DownRowTaskBean_;
import com.mijia.app.databinding.ActivitySpaceSettingBinding;
import com.mijia.app.dialog.TipsDialog;
import com.mijia.app.socket.DownLoadManager;
import com.mijia.app.utils.DeleteFileUtil;

import java.io.File;
import java.util.List;

import io.objectbox.Box;

public class SpaceSettingActivity extends BaseActivity<ActivitySpaceSettingBinding, BaseViewModel> {

    private Box<DownRowTaskBean> mBox;
    private DownLoadManager mDownLoadManager = DownLoadManager.getInstance();

    @Override
    public int getLayoutRes() {
        return R.layout.activity_space_setting;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mBox = MyApp.getInstance().getBoxStore().boxFor(DownRowTaskBean.class);
        mBinding.ivReturn.setOnClickListener(v -> {
            finish();
        });

        mBinding.llDelDownloadFile.setOnClickListener(v -> {
            TipsDialog tipsDialog = new TipsDialog(SpaceSettingActivity.this, "是否删除下载文件?");
            tipsDialog.setClickListener(new TipsDialog.OnClickListener() {
                @Override
                public void sure() {

                    Log.i("LocalPath", getSDPath());

                    boolean isdelete = DeleteFileUtil.deleteDirectory(getSDPath() + "/秘夹/" + AccountHelper.getNickname() + AccountHelper.getUserId());
                    if (isdelete) {
                        List<DownRowTaskBean> list = mBox.query().equal(DownRowTaskBean_.currTaskStatus, 2).build().find();
                        mBox.remove(list);

//                        mBox.removeAll();
                        for (String s : mDownLoadManager.getTaskList().keySet()) {

//                            mDownLoadManager.getTaskList().
                            mDownLoadManager.removeTask(s);
                        }

                        ToastUtils.showShort("清理成功！");
                    } else {
                        ToastUtils.showShort("清理失败！");
                    }
                }

                @Override
                public void cancel() {

                }
            });
            tipsDialog.show();
        });

        mBinding.llDelPreviewCache.setOnClickListener(v -> {
            TipsDialog tipsDialog = new TipsDialog(SpaceSettingActivity.this, "是否删除在线预览缓存?");
            tipsDialog.setClickListener(new TipsDialog.OnClickListener() {
                @Override
                public void sure() {
                    Log.i("LocalPath", getApplicationContext().getExternalCacheDir().getPath());
                    boolean isdelete = DeleteFileUtil.deleteDirectory(getApplicationContext().getExternalCacheDir().getPath() + "/秘夹/" + AccountHelper.getNickname() + AccountHelper.getUserId());
                    if (isdelete) {
                        ToastUtils.showShort("清理成功！");
                    } else {
                        ToastUtils.showShort("清理失败！");
                    }
                }

                @Override
                public void cancel() {

                }
            });
            tipsDialog.show();
        });
    }


    public String getSDPath() {
        File sdDir = null;
        //判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
            Log.e("qq", "外部存储可用..." + sdDir.toString());
        }
        return sdDir.toString();
    }


}
