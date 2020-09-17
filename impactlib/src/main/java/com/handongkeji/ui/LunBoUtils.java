package com.handongkeji.ui;

import android.content.Context;
import android.widget.ImageView;


import com.handongkeji.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class LunBoUtils {
    private BannerViewPager cycleViewPager;
    private List<ImageView> views = new ArrayList<ImageView>();
    private Context context;
    private ArrayList<String> infos;


    public LunBoUtils(BannerViewPager cycleViewPager, Context context, ArrayList<String> infos) {
        this.cycleViewPager = cycleViewPager;
        this.context = context;
        this.infos = infos;
    }

    public void setLunBo() {
        initData();
        setData();
        setViewPagerAndIndicator();
    }

    public void setLunBo(BannerViewPager.OnItemClickListener listener) {
        initData();
        setData(listener);
        setViewPagerAndIndicator();
    }

    //设置轮播图和指示器
    private void setViewPagerAndIndicator() {
        //开始轮播
        cycleViewPager.setWheel(true);
        // 设置轮播时间，默认3000ms
        cycleViewPager.setScrollTime(4000);
        //设置圆点指示图标组居中显示，默认靠右
        cycleViewPager.setIndicatorCenter();
    }

    //    设置数据以及条目点击事件
    private void setData() {
        // 将第一个ImageView添加进来
        if (CommonUtils.isEmptyList(infos)) {
            return;
        }
        views.add(ViewFactory.getImageView(context, infos.get(0)));
        // 在加载数据前设置是否循环
        cycleViewPager.setData(views, infos, new BannerViewPager.OnItemClickListener() {
            @Override
            public void onItemClick(int postion) {
                //Toast.makeText((Activity) context, postion + "", Toast.LENGTH_SHORT).show();
                //轮播的点击事件
            }
        });
    }

    //    设置数据以及条目点击事件
    private void setData(BannerViewPager.OnItemClickListener listener) {
        // 将第一个ImageView添加进来
        if (CommonUtils.isEmptyList(infos)) {
            return;
        }
        views.add(ViewFactory.getImageView(context, infos.get(0)));
        // 在加载数据前设置是否循环
        cycleViewPager.setData(views, infos, listener);
    }

    private void initData() {
//        for (int i = 0; i < imageUrls.length; i++) {
//            infos.add(imageUrls[i]);
//        }
        if (CommonUtils.isEmptyList(infos)) {
            return;
        }

//       将ImageView添加到集合中
        // 将最后一个ImageView添加进来
        views.add(ViewFactory.getImageView(context, infos.get(infos.size() - 1)));
        for (int i = 0; i < infos.size(); i++) {
            views.add(ViewFactory.getImageView(context, infos.get(i)));
        }
    }
}
