package com.handongkeji.media;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by Administrator on 2017/7/17 0017.
 */

public class MediaPlayerHelper {

    private MediaPlayer mediaPlayer;
    private static MediaPlayerHelper instance = new MediaPlayerHelper();


    private MediaPlayerHelper() {
    }

    public MediaPlayerHelper getInstance(){
        return instance;
    }

    public void play(String path){
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.prepare();
            mediaPlayer.setDataSource(path);
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void stop(){
        mediaPlayer.release();
        mediaPlayer.stop();
    }

}
