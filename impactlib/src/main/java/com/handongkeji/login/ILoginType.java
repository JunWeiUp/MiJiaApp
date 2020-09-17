package com.handongkeji.login;

import com.handongkeji.base.IBaseView;

/**
 * 登录，账号密码检查策略
 *
 * @ClassName:ILoginStrategy

 * @PackageName:com.handongkeji.login

 * @Create On 2017/10/19 0019   10:27

 * @Site:http://www.handongkeji.com

 * @author:xuchuanting

 * @Copyrights 2017/10/19 0019 handongkeji All rights reserved.
 */

public interface ILoginType {

    boolean checkAccountPassword(String account, String password, IBaseView baseView);
}
