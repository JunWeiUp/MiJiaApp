package com.mijia.app.utils;

import android.annotation.SuppressLint;

public class DurationUtils {


    @SuppressLint("DefaultLocale")
    public static String getDuration(long duration) {
        String time;
        duration = duration / 1000;
        if (duration < 60) {

            time = String.format("00:%02d", duration % 60);

        } else if (duration < 3600) {

            time = String.format("%02d:%02d", duration / 60, duration % 60);

        } else {

            time = String.format("%02d:%02d:%02d", duration / 3600, duration % 3600 / 60, duration % 60);

        }
        return time;
    }


}
