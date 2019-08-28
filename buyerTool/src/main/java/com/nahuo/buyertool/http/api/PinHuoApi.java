package com.nahuo.buyertool.http.api;


import com.nahuo.buyertool.api.HttpUtils;
import com.nahuo.buyertool.http.response.PinHuoResponse;
import com.nahuo.buyertool.model.PageraModel.BaseInfoList;
import com.nahuo.live.xiaozhibo.model.GoodsBean;
import com.nahuo.live.xiaozhibo.model.LiveDetailBean;
import com.nahuo.live.xiaozhibo.model.LiveListBean;

import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import static com.nahuo.buyertool.api.HttpUtils.SERVERURL_Https_V4;


/**
 * Created by jame on 2018/4/27.
 */

public interface PinHuoApi {
    String HOST = HttpUtils.BASE_Https_URL + HttpUtils.VERSION;
    String HTTP_V4 = SERVERURL_Https_V4;

    /**
     * 拼货列表
     *
     * @param
     */
    @GET("buyertool/live/GetLiveList/")
    Flowable<PinHuoResponse<LiveListBean>> getLiveList(
    );

    /**
     * TX账号
     *
     * @param
     */
    @GET(HTTP_V4 + "user/ImUser/GetTXUserInfo/")
    Flowable<PinHuoResponse<Object>> getTXUserInfo(
            @QueryMap Map<String, Object> map
    );

    /**
     * 拼货详情
     *
     * @param
     */
    @GET("buyertool/live/GetLiveItem/")
    Flowable<PinHuoResponse<LiveDetailBean>> getLiveItem(
            @Query("id") int id
    );

    /**
     * 拼货详情
     *
     * @param
     */
    @GET("buyertool/BuyerV2/GetBaseInfo2/")
    Flowable<PinHuoResponse<BaseInfoList>> GetBaseInfo2(
            @QueryMap Map<String, Object> map
    );

    /**
     * 拼货录制
     *
     * @param
     */
    @GET("buyertool/live/CreateLiveRecord/")
    Flowable<PinHuoResponse<Object>> createLiveRecord(
    );

    /**
     * 拼货结束录制
     *
     * @param
     */
    @GET("buyertool/live/StopLiveRecord/")
    Flowable<PinHuoResponse<Object>> stopLiveRecord(
    );

    /**
     * 设置是否显示商品
     *
     * @param
     */
    @POST("buyertool/live/SetIsShow/")
    Flowable<PinHuoResponse<Object>> setIsShow(
            @Query("id") int id, @Query("isShowItem") boolean isShowItem
    );

    /**
     * 设设置是否允许观众可以开始观看直播
     *
     * @param
     */
    @POST("buyertool/live/SetIsWatch/")
    Flowable<PinHuoResponse<Object>> setIsWatch(
            @Query("id") int id, @Query("isStartWatch") boolean isStartWatch
    );

    /**
     * 拼货详情
     *
     * @param
     */
    @GET("buyertool/live/GetLiveItemAllGoods")
    Flowable<PinHuoResponse<GoodsBean>> getLiveItemAllGoods(
            @Query("id") int id
    );

    /**
     * 、设置哪个商品为试穿中
     *
     * @param
     */
    @POST("buyertool/live/SetOnTry/")
    Flowable<PinHuoResponse<Object>> setOnTry(
            @Query("id") int id, @Query("agentItemID") int agentItemID
    );

    /**
     * 取消试穿中的商品
     *
     * @param
     */
    @POST("buyertool/live/CancelOnTry/")
    Flowable<PinHuoResponse<Object>> cancelOnTry(
            @Query("id") int id, @Query("agentItemID") int agentItemID
    );

    /**
     * 设置已开单为异常件
     *
     * @param
     */
    @POST("buyertool/BuyerV2/SetExceptionalBill")
    Flowable<PinHuoResponse<Object>> setExceptionalBill(
            @QueryMap Map<String, Object> map
    );

    @POST("buyertool/BuyerV2/CancelExceptionalBill")
    Flowable<PinHuoResponse<Object>> cancelExceptionalBill(
            @QueryMap Map<String, Object> map
    );

}
