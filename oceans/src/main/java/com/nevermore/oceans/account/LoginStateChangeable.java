package com.nevermore.oceans.account;

/**
 *
 * 登录退出状态监听
 * @ClassName LoginStateChangeable

 * @PackageName com.nevermore.oceans.global.account

 * @Create On 2018/2/1 0001   17:17

 * @Site http://www.handongkeji.com

 * @author xuchuanting

 * @Copyrights 2018/2/1 0001 handongkeji All rights reserved.
 */

public interface LoginStateChangeable {

    /**
     * 登录
     */
    void onLogin();

    /**
     * 退出
     */
    void onLogout();
}
