package com.handongkeji.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/3/2 0002.
 */

public class QFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> titles;


    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    public QFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
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
        if(titles!=null&&titles.size()>=position+1){
            return titles.get(position);
        }

        return super.getPageTitle(position);
    }
}
