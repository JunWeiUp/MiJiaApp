package com.handong.framework.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/30 0030.
 */

public class LazyFPagerAdapter extends LazyFragmentAdapter {
    private List<Fragment> fragmentList;
    private List<String> titles = new ArrayList<>();

    public LazyFPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        fragmentList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList==null?null:fragmentList.get(position);
    }

    @Override
    public int getCount() {


        return fragmentList==null?0:fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(titles!=null&&titles.size()>position){
            return titles.get(position);
        }

        return super.getPageTitle(position);
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }


}
