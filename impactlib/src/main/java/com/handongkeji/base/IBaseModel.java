package com.handongkeji.base;

import java.util.Map;

/**
 * Created by Administrator on 2017/10/19 0019.
 */

public interface IBaseModel {


    <T> void request(IResponse<T> response);

}
