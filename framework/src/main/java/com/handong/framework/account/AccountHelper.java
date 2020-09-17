package com.handong.framework.account;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;

public class AccountHelper {

    //    用户信息
    private static final String SP_NAME = "AccountHelper";
    private static final String SP_TOKEN = "token";
    private static final String SP_NICKNAME = "nickname";
    private static final String SP_AVATAR = "avatar";
    private static final String SP_GENDER = "gender"; // 性别
    private static final String SP_USERID = "userid";
    private static final String SP_SIGN = "sign";

    private static final AccountHelper INSTANCE = new AccountHelper();

    public static AccountHelper getInstance() {
        return INSTANCE;
    }

    public static boolean isLogin() {
        return !TextUtils.isEmpty(getToken());
    }

    public static void login(String token, String userId, String gender, String avatar, String nickname) {
        SPUtils.getInstance(SP_NAME).put(SP_TOKEN, token);
        SPUtils.getInstance(SP_NAME).put(SP_USERID, userId);
        SPUtils.getInstance(SP_NAME).put(SP_GENDER, gender);
        SPUtils.getInstance(SP_NAME).put(SP_AVATAR, avatar);
        SPUtils.getInstance(SP_NAME).put(SP_NICKNAME, nickname);
    }

    public static void login(String userId, String avater, String nickName, String sign) {
        SPUtils.getInstance(SP_NAME).put(SP_USERID, userId);
        SPUtils.getInstance(SP_NAME).put(SP_AVATAR, avater);
        SPUtils.getInstance(SP_NAME).put(SP_NICKNAME, nickName);
        SPUtils.getInstance(SP_NAME).put(SP_SIGN, sign);
    }

    public static String getSign() {
        return SPUtils.getInstance(SP_NAME).getString(SP_SIGN);
    }


    public static String getToken() {
        return SPUtils.getInstance(SP_NAME).getString(SP_TOKEN);
    }

    public static String getUserId() {
        return SPUtils.getInstance(SP_NAME).getString(SP_USERID);
    }

    public static void setUserId() {
        SPUtils.getInstance(SP_NAME).put(SP_USERID, "");
    }

    public static String getGender() {
        return SPUtils.getInstance(SP_NAME).getString(SP_GENDER);
    }

    public static void setGender(String gender) {
        SPUtils.getInstance(SP_NAME).put(SP_GENDER, gender);
    }

    public static String getAvatar() {
        return SPUtils.getInstance(SP_NAME).getString(SP_AVATAR);
    }

    public static void setAvatar(String avatar) {
        SPUtils.getInstance(SP_NAME).put(SP_AVATAR, avatar);
    }

    public static String getNickname() {
        return SPUtils.getInstance(SP_NAME).getString(SP_NICKNAME);
    }

    public static void setNickname(String nickname) {
        SPUtils.getInstance(SP_NAME).put(SP_NICKNAME, nickname);
    }

    public static void logout() {
        login(null, null, null, null, null);

    }

    public static boolean shouldLogin(Context context) {
        if (!isLogin()) {
            context.startActivity(new Intent(context.getPackageName() + ".com.action.login"));
            return true;
        }
        return false;
    }


}