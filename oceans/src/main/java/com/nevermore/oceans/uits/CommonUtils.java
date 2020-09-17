package com.nevermore.oceans.uits;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class CommonUtils {
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * ����ֻ�ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * ����ֻ�ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /*
     * 判断是否有网�?
     * */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private static Toast toast;

    public static void showToast(Context context, CharSequence text) {
        if (context == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        toast.setText(text);
        toast.show();
    }


    /**
     * 字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isStringNull(String str) {
        return TextUtils.isEmpty(str) || "null".equals(str);
    }

    /**
     * list是否为空
     *
     * @param list
     * @return
     */
    public static boolean isEmptyList(List list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        return false;
    }


    public static int getIntFromString(String string) {
        int result = 0;
        if (!isStringNull(string)) {
            try {
                result = Integer.parseInt(string);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public static double getDoubleFromString(String string){
        double result = 0.0;

        try {
            result = Double.parseDouble(string);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    public static long getLongFromString(String string){
        long result = 0L;

        try {
            result = Long.parseLong(string);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    public static String formatNumberPoint(String number, int pointCount) {
        String format = "%." + pointCount + "f";
//         return String.format( "%.2f", number);
        return String.format(format, number);
//        return number;


    }

    public static Bitmap getBitmap(Context context,int drawableId) {
        Bitmap bitmap=null;
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
            Drawable vectorDrawable = context.getResources().getDrawable(drawableId);
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        }else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        }
        return bitmap;
    }
}
