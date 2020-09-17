package com.handong.framework.base;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * 懒加载fragment
 *
 * @ClassName:LazyloadFragment
 * @PackageName:com.xianshangjingbiao.app.base
 * @Create On 2018/4/25 0025   17:46
 * @Site:http://www.handongkeji.com
 * @author:zhouhao
 * @Copyrights 2018/4/25 0025 handongkeji All rights reserved.
 */
public abstract class LazyloadFragment<T extends ViewDataBinding,VM extends BaseViewModel> extends BaseFragment<T,VM> {

    public static final int FLAG_USERVISIBLE = 1 << 0;
    public static final int FLAG_VIEWCREATED = 1 << 1;
    public static final int FLAG_FIRSTIN = 1 << 2;  //  第一次初始化标志

    private int flag;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            flag |= FLAG_USERVISIBLE;
        } else {
            flag &= ~FLAG_USERVISIBLE;
        }
        lazyLoad();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    private void lazyLoad() {
        if ((flag & FLAG_USERVISIBLE) == FLAG_USERVISIBLE
                && (flag & FLAG_VIEWCREATED) == FLAG_VIEWCREATED
                && (flag & FLAG_FIRSTIN) != FLAG_FIRSTIN) {
            flag |= FLAG_FIRSTIN;
            onLazyload();
        }
    }

    public abstract void onLazyload();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        flag |= FLAG_VIEWCREATED;
        lazyLoad();
    }

}
