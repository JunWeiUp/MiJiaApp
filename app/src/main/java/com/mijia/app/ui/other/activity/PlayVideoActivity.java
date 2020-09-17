package com.mijia.app.ui.other.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.handong.framework.base.BaseActivity;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.R;
import com.mijia.app.constants.Constants;
import com.mijia.app.databinding.ActivityPlayVideoBinding;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class PlayVideoActivity extends BaseActivity<ActivityPlayVideoBinding, BaseViewModel> {

    private String videoPath = "";

    @Override
    public int getLayoutRes() {
        return R.layout.activity_play_video;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        videoPath = getIntent().getStringExtra(Constants.VIDEOPATH);
        mBinding.jcVideoPlay.setUp(videoPath, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
    }

    @Override
    public void onResume() {
        super.onResume();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JCVideoPlayer.releaseAllVideos();
    }
}
