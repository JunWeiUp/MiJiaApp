package com.mijia.app.ui.other.activity;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.SeekBar;

import com.blankj.utilcode.util.ToastUtils;
import com.handong.framework.base.BaseActivity;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.R;
import com.mijia.app.bean.VideoVoiceFileBean;
import com.mijia.app.databinding.ActivityPlayMusicBinding;
import com.mijia.app.dialog.DeleteTipsDialog;
import com.mijia.app.utils.DurationUtils;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class PlayMusicActivity extends BaseActivity<ActivityPlayMusicBinding, BaseViewModel> {

    private VideoVoiceFileBean musicBean;

    private MediaPlayer mMediaPlayer;

    private Timer timer;//定时器

    private boolean isSeekbarChaning = false;//互斥变量，防止进度条和定时器冲突。


    @Override
    public int getLayoutRes() {
        return R.layout.activity_play_music;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

        mMediaPlayer = new MediaPlayer();
        musicBean = new VideoVoiceFileBean();

        String path = getIntent().getStringExtra("MusicPath");
        File file = new File(path);
        if (file.exists()) {
            try {
                musicBean.setDisplayName(file.getName());
                musicBean.setPath(path);
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(path); //在获取前，设置文件路径（应该只能是本地路径）
                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                retriever.release(); //释放
                if(!TextUtils.isEmpty(duration)){
                    long dur = Long.parseLong(duration);
                    musicBean.setDuration(dur);
                }
            }catch (RuntimeException e){
                ToastUtils.showShort("无法播放该音频文件！");
            }


        }

        mBinding.tvReturn.setOnClickListener(v -> {
            finish();
        });

        mBinding.ivDelete.setOnClickListener(v -> {
            DeleteTipsDialog deleteTipsDialog = new DeleteTipsDialog(this);
            deleteTipsDialog.setOnClickDelListener(new DeleteTipsDialog.onClickDelListener() {
                @Override
                public void onDel() {

                }
            });
            deleteTipsDialog.show();
        });

        if (musicBean == null) {
            return;
        }

        try {
            mMediaPlayer.setDataSource(musicBean.getPath());
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaPlayer.setOnPreparedListener(mp -> {
            startPlay();
            mBinding.tvMusicName.setText(musicBean.getDisplayName());
            mBinding.tvMusicSize.setText(DurationUtils.getDuration(musicBean.getDuration()));
            mBinding.tvAllTime.setText(DurationUtils.getDuration(musicBean.getDuration()));
            mBinding.tvPositionTime.setText("00:00");
            mBinding.seekbar.setMax((int) musicBean.getDuration());

            timer = new Timer();//时间监听器
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!isSeekbarChaning) {
                        mBinding.seekbar.setProgress(mMediaPlayer.getCurrentPosition());
                    }
                }
            }, 0, 50);

        });


        mMediaPlayer.setOnErrorListener((mediaPlayer, i, i1) -> {
//            ToastUtils.showShort("播放失败！");
            return false;
        });

        mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
            // todo
            mBinding.ivPlayOrPause.setSelected(false);
            mMediaPlayer.seekTo(0);
            mBinding.seekbar.setProgress(0);
        });

        mMediaPlayer.setOnSeekCompleteListener(mediaPlayer -> {
            // todo
            mBinding.seekbar.setProgress(mediaPlayer.getCurrentPosition());
        });


        mBinding.ivPlayOrPause.setOnClickListener(v -> {
            if (mBinding.ivPlayOrPause.isSelected()) {
                pausePlay();
            } else {
                startPlay();
            }
        });

        mBinding.ivBackFiveSecond.setOnClickListener(v -> {
            backFiveSeconds();
        });

        mBinding.ivGoFiveSecond.setOnClickListener(v -> {
            goFiveSeconds();
        });


        //绑定监听器，监听拖动到指定位置
        mBinding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int duration2 = mMediaPlayer.getDuration() / 1000;//获取音乐总时长
                int position = mMediaPlayer.getCurrentPosition();//获取当前播放的位置
                mBinding.tvPositionTime.setText(calculateTime(position / 1000));//开始时间
                mBinding.tvAllTime.setText(calculateTime(duration2));//总时长
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekbarChaning = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekbarChaning = false;
                mMediaPlayer.seekTo(seekBar.getProgress());//在当前位置播放
                mBinding.tvPositionTime.setText(calculateTime(mMediaPlayer.getCurrentPosition() / 1000));
            }
        });


    }

    //计算播放时间
    public String calculateTime(int time) {
        int minute;
        int second;
        if (time > 60) {
            minute = time / 60;
            second = time % 60;
            //分钟再0~9
            if (minute >= 0 && minute < 10) {
                //判断秒
                if (second >= 0 && second < 10) {
                    return "0" + minute + ":" + "0" + second;
                } else {
                    return "0" + minute + ":" + second;
                }
            } else {
                //分钟大于10再判断秒
                if (second >= 0 && second < 10) {
                    return minute + ":" + "0" + second;
                } else {
                    return minute + ":" + second;
                }
            }
        } else if (time < 60) {
            second = time;
            if (second >= 0 && second < 10) {
                return "00:" + "0" + second;
            } else {
                return "00:" + second;
            }
        }
        return null;
    }


    /**
     * 开始 继续播放
     */
    private void startPlay() {
        mMediaPlayer.start();
        mBinding.ivPlayOrPause.setSelected(true);
    }

    /**
     * 暂停播放
     */
    private void pausePlay() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mBinding.ivPlayOrPause.setSelected(false);
            }
        }
    }

    /**
     * 停止播放
     */
    private void stopPlay() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mBinding.ivPlayOrPause.setSelected(false);
        }
    }

    /**
     * 销毁播放器
     */
    private void destoryPlay() {
        if (mMediaPlayer != null) {
            stopPlay();
            mMediaPlayer.release();
        }
    }

    /**
     * 后退五秒
     */
    private void backFiveSeconds() {
        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() - 5000);
    }

    /**
     * 前进五秒
     */
    private void goFiveSeconds() {
        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + 5000);
    }

    @Override
    public void onResume() {
        super.onResume();
        stopPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null) {
            timer.cancel();
        }
        destoryPlay();
    }
}
