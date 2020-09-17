package com.mijia.app.constants;

import com.handong.framework.base.BaseBean;
import com.mijia.app.bean.AdvertisementBean;
import com.mijia.app.bean.EmpowerListBean;
import com.mijia.app.bean.HeartBeatBean;
import com.mijia.app.bean.HomeDataBean;
import com.mijia.app.bean.LoginBean;
import com.mijia.app.bean.SettingBean;
import com.mijia.app.bean.TransOrderBean;
import com.mijia.app.bean.TransOrderResultBean;

import io.reactivex.Observable;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DataService {


    //获取广告图 轮播图
    @GET(Url.ADVERTISEMENT_PHOTO)
    Observable<AdvertisementBean> getAdvertisementPhoto();

    //登录
    @GET(Url.LOGIN)
    Observable<LoginBean> login(@Query("unionId") String unionId);

    //获取首页数据
    @GET(Url.HOMEDATA)
    Observable<HomeDataBean> getHomeData(@Query("userId") String userId);

    //获取首页数据
    @GET(Url.HEARTBEAT)
    Observable<HeartBeatBean> heartBeat(@Query("userId") String userId, @Query("sip") String ip, @Query("sport") String port);

    //设置页面相关
    @GET(Url.SETTINGDATA)
    Observable<SettingBean> settingData();

    //授权列表
    @GET(Url.EMPOWERLIST)
    Observable<EmpowerListBean> getEmpowerlist(@Query("userId") String userId);

    //服务器转发指令
    @GET(Url.SERVERTRANSORDER)
    Observable<TransOrderBean> serverTranOrder(@Query("userId") String userId, @Query("order") String order);

    //查询指令转化结果
    @GET(Url.QUERYORDERRESULT)
    Observable<TransOrderResultBean> queryResult(@Query("orderId") String orderId);
}
