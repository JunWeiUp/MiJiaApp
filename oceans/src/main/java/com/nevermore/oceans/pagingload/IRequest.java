package com.nevermore.oceans.pagingload;

/**
 * Created by Administrator on 2017/8/28 0028.
 */

public interface IRequest {

    void onRequest(int currentPage, int pageSize);
}
