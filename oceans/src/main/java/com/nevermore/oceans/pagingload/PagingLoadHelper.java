package com.nevermore.oceans.pagingload;

/**
 * Created by Administrator on 2017/8/28 0028.
 */

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页加载管理辅助类
 *
 * @ClassName:MultiPageLoadHelper
 * @PackageName:com.bluemobi.wenwanstyle.httprequest
 * @Create On 2017/8/28 0028   13:33
 * @Site:http://www.handongkeji.com
 * @author:xuchuanting
 * @Copyrights 2017/8/28 0028 handongkeji All rights reserved.
 */

public class PagingLoadHelper implements IEvent {

    public int currentPage = 1;
    public int pageSize = 10;

    private IRequest mRequest;
    private BasePagingLoadDelegate mDelegate;
    private boolean hasMoreEnable = true;
    private OnNoMoreListener noMoreListener;

    public PagingLoadHelper(@NonNull BasePagingLoadDelegate mDelegate, @NonNull IRequest mRequest) {
        this.mRequest = mRequest;
        this.mDelegate = mDelegate;
        init();
    }

    private void init() {
        mDelegate.setEvent(this);
        mDelegate.setRefreshAndLoadMoreListener();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void start() {
        mDelegate.onStart();
        onPulldown();
    }

    @Override
    public void onPulldown() {
        currentPage = 1;
        mRequest.onRequest(currentPage, pageSize);
    }

    @Override
    public void onPullup() {
        currentPage++;
        mRequest.onRequest(currentPage, pageSize);
    }

    public void setHasMoreEnable(boolean enable) {
        hasMoreEnable = enable;
    }

    public void onComplete(List<?> list) {
        List page = new ArrayList();
        if (list != null) {
            page.addAll(list);
        }
        if (currentPage == 1) {
            mDelegate.onRefreshComplete(page);
        } else {
            mDelegate.onLoadComplete(page);
        }
        if (hasMoreEnable) {

            int dataSize = page == null ? 0 : page.size();
            if (dataSize < pageSize) {
                mDelegate.setHasMore(false);
                if (noMoreListener != null) {
                    noMoreListener.onNoMoreData();
                }
            } else {
                mDelegate.setHasMore(true);
            }

        } else {
            mDelegate.setPullupEnable(false);
        }
    }

    public void setNoMoreListener(OnNoMoreListener noMoreListener) {
        this.noMoreListener = noMoreListener;
    }

    public interface OnNoMoreListener {
        void onNoMoreData();
    }
}
