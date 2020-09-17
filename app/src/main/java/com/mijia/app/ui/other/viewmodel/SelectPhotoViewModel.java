package com.mijia.app.ui.other.viewmodel;

import android.os.Environment;
import android.text.TextUtils;
import android.view.TextureView;

import com.blankj.utilcode.util.StringUtils;
import com.handong.framework.account.AccountHelper;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.bean.UploadRequestBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.socket.UpLoadManager;
import com.mijia.app.socket.UpLoadTask;
import com.mijia.app.utils.file.MediaBean;

import java.io.File;
import java.util.List;

public class SelectPhotoViewModel extends BaseViewModel {

    private UpLoadManager manager = UpLoadManager.getInstance();

    public String path = "";

    public String upLoadImageForList(List<MediaBean> list) {
        String fileNames = "";

        for (MediaBean mediaBean : list) {
            String path = mediaBean.getPath();
            UploadRequestBean uploadRequestBean = new UploadRequestBean();
            uploadRequestBean.setUserName(AccountHelper.getNickname());
            uploadRequestBean.setUserId(AccountHelper.getUserId());
            uploadRequestBean.setGsId(Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
            uploadRequestBean.setGsName(Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());

            uploadRequestBean.setDiskName(Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
            uploadRequestBean.setDiskId(Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());

            File file = new File(path);

            uploadRequestBean.setFullpath(this.path  + file.getName());
            UpLoadTask task = new UpLoadTask(uploadRequestBean, path);
            boolean b = manager.addTask(task);
            if (!b) {
                fileNames += file.getName() + " ";
            }

        }
        return fileNames.trim();
    }

    public void setDestPath(String path) {
        this.path = path;
    }
}
