package com.nahuo.buyertool.api;

/**
 * 监听结果回调
 *@author liaochong
 *@time 2017/1/18 10:19
*/
public interface MyCallback {
     void returnData(Object object, int requestId);
     void error(int requestId);
}
