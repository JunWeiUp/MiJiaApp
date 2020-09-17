package com.mijia.app.ui.other.viewmodel;

import com.blankj.utilcode.util.StringUtils;
import com.handong.framework.account.AccountHelper;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.bean.UploadRequestBean;
import com.mijia.app.bean.VideoVoiceFileBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.socket.UpLoadManager;
import com.mijia.app.socket.UpLoadTask;

import java.io.File;
import java.util.List;

public class SelectVoiceVideoFileViewModel extends BaseViewModel {


    private UpLoadManager manager = UpLoadManager.getInstance();
    public String mDestPath = "";

    public String upLoadList(List<VideoVoiceFileBean> list) {
        String s = "";
        for (VideoVoiceFileBean itemValue : list) {
            String path = itemValue.getPath();
            UploadRequestBean uploadRequestBean = new UploadRequestBean();
            uploadRequestBean.setUserName(AccountHelper.getNickname());
            uploadRequestBean.setUserId(AccountHelper.getUserId());
            uploadRequestBean.setGsId(Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get());
            uploadRequestBean.setGsName(Constants.SYS_CONTANTS_BEAN.SelectedMiDun.get());

            uploadRequestBean.setDiskName(Constants.SYS_CONTANTS_BEAN.SelectedDisk.get());
            uploadRequestBean.setDiskId(Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get());
            File file = new File(path);
            uploadRequestBean.setFullpath(mDestPath  + file.getName());
            UpLoadTask task = new UpLoadTask(uploadRequestBean, path);
            boolean b = manager.addTask(task);
            if (!b) {
                s += file.getName() + " ";
            }
        }

        return s.trim();
    }

    public void setDestPath(String destPath) {
        mDestPath = destPath;
    }
}
