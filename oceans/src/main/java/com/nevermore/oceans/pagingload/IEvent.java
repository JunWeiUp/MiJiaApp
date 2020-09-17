package com.nevermore.oceans.pagingload;

/**
 *
 * @ClassName:IEvent

 * @PackageName:com.bluemobi.wenwanstyle.httprequest

 * @Create On 2017/8/28 0028   13:35

 * @Site:http://www.handongkeji.com

 * @author:xuchuanting

 * @Copyrights 2017/8/28 0028 handongkeji All rights reserved.
 */

public interface IEvent {
    //下拉刷新
    void onPulldown();
    //上拉加载
    void onPullup();
}
