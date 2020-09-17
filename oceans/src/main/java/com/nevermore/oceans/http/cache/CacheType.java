package com.nevermore.oceans.http.cache;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 缓存类型
 * @ClassName CacheType

 * @PackageName com.nevermore.oceans.http

 * @Create On 2018/2/11 0011   09:20

 * @Site http://www.handongkeji.com

 * @author xuchuanting

 * @Copyrights 2018/2/11 0011 handongkeji All rights reserved.
 */
public class CacheType {


    public static final int CACHE_NONE = 0;//不缓存
    public static final int CACHE_FOR_NO_NET = 1;//请求完用文件缓存，无网的时候用

    @IntDef({CACHE_NONE,CACHE_FOR_NO_NET})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CacheInt{
    }
}
