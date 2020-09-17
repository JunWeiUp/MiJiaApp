package com.handongkeji.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class CommonUtils {

    /**
     * 返回桌面
     *
     * @param context
     */
    public static void goDeskTop(Context context) {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);

        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(mHomeIntent);
    }


    public static void openSetting(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(intent);
    }

    public static File saveFile(Bitmap bm) {
        String path = Environment.getExternalStorageDirectory().getPath() + "/qy/";
        File dirFile = new File(path);
        String fileName = SystemClock.currentThreadTimeMillis() + ".jpg";
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(path + fileName);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myCaptureFile;
    }

    /**
     * 键盘弹出调整布局不被遮挡
     *
     * @param main
     * @param scroll 不被遮挡的view
     */
    public static void adjustEdit(final View main, final View scroll) {
        main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                main.getWindowVisibleDisplayFrame(rect);
                int mainInvisibleHeight = main.getRootView().getHeight() - rect.bottom;
                if (mainInvisibleHeight > 100) {
                    int[] location = new int[2];
                    scroll.getLocationInWindow(location);
                    int srollHeight = (location[1] + scroll.getHeight()) - rect.bottom;
                    main.scrollTo(0, srollHeight + 50);
                } else {
                    main.scrollTo(0, 0);
                }
            }
        });
    }

    public static void fullScreen(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            View decorView = activity.getWindow().getDecorView();
//                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        } else {

            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }
    }


    public static String toCommaString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        if (!isEmptyList(list)) {
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.get(i)).append(",");
            }
        }
        return sb.toString();
    }


    public static boolean isStringNull(String string) {
        if (TextUtils.isEmpty(string) || "null".equals(string)) {
            return true;
        }
        return false;
    }

    public static boolean isEmptyList(List list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        return false;
    }


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
        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        toast.setText(text);
        toast.show();
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


    public static double getDoubleFromString(String string) {
        double result = 0.0;

        try {
            result = Double.parseDouble(string);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    public static long getLongFromString(String string) {
        long result = 0L;

        try {
            result = Long.parseLong(string);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 设置editText最多输入的小数位数
     * inputType设为：numberDemical
     *
     * @param editText
     * @param pointCount
     */
    public static void setPoint(final EditText editText, final int pointCount) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > pointCount) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + pointCount + 1);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }
                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

    public static boolean isRemindName(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        if (name.contains("[") && name.contains("]")) {
            return true;
        }
        return false;
    }

    public static String getMaskRemindName(String remind) {

        return String.format(Locale.CHINA, "[%s]", remind);
    }

    public static String getParseName(String name) {
        return name.replace("[", "").replace("]", "");
    }

    public static boolean isChinaPhoneLegal(String str)
            throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }


    /**
     * 从source中去除size个元素返回list
     *
     * @param source
     * @param size
     * @param <T>
     * @return
     */
    public static <T> List<T> getSizedList(List<T> source, int size) {
        List<T> result = new ArrayList<>();

        if (isEmptyList(source)) {
            return result;
        }

        for (int i = 0; i < size; i++) {
            if (source.size() > i) {
                result.add(source.get(i));
            }
        }

        return result;
    }


}
