package com.mijia.app.utils;

public class StringCheckUtils {

    /**
     * 判断是否为汉字
     *
     * @param string
     * @return
     */

    public static boolean isChinese(String string) {
        int n = 0;
        for (int i = 0; i < string.length(); i++) {
            n = (int) string.charAt(i);
            if (!(19968 <= n && n < 40869)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 1.判断字符串是否仅为数字:
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {

        for (int i = str.length(); --i >= 0; ) {

            if (!Character.isDigit(str.charAt(i))) {

                return false;

            }

        }

        return true;

    }


}
