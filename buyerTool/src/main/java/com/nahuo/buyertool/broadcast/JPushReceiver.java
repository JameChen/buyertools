package com.nahuo.buyertool.broadcast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.nahuo.buyertool.common.BaiduStats;
import com.nahuo.buyertool.eventbus.BusEvent;
import com.nahuo.buyertool.eventbus.EventBusId;
import com.nahuo.buyertool.model.PushMsgModel;
import com.nahuo.library.helper.GsonHelper;
import com.nahuo.live.xiaozhibo.common.utils.TCConstants;
import com.nahuo.live.xiaozhibo.model.JPushGoodsBean;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG                    = "JPushReceiver";

    /**
     * 新代理申请
     */
    public static final String  ACTION_NEW_APPLY_AGENT = "ation_new_apply_agent";
    /**
     * 新订单
     */
    public static final String  ACTION_NEW_ORDER_FLOW  = "ation_new_order_flow";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle==null)
            return;
        Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "接收Registration Id : " + regId);
            // send the Registration Id to your server...
        }
//        else if (JPushInterface.ACTION_UNREGISTER.equals(intent.getAction())) {
//            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
//            Log.d(TAG, "接收UnRegistration Id : " + regId);
//            // send the UnRegistration Id to your server...
//        }
        else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            try {
                if (bundle!=null){
                    String msg_type=  bundle.getString(JPushInterface.EXTRA_MESSAGE);
                    String extra=  bundle.getString(JPushInterface.EXTRA_EXTRA);
                    if (!TextUtils.isEmpty(extra)) {
                        if (msg_type.equals(TCConstants.MESSAGETYPE_LIVESETTRYGOODS)){
                            JPushGoodsBean jGBean = new JPushGoodsBean();
                            JSONObject jsonObject = new JSONObject(extra);
                            int AgentItemID=jsonObject.optInt("AgentItemID");
                            String Cover=jsonObject.optString("Cover");
                            String Price=jsonObject.optString("Price");
                            int liveID=jsonObject.optInt("LiveID");
                            jGBean.setLiveID(liveID);
                            jGBean.setMessageType(msg_type);
                            jGBean.setCover(Cover);
                            jGBean.setPrice(Price);
                            jGBean.setAgentItemID(AgentItemID);
                            EventBus.getDefault().post(BusEvent.getEvent(EventBusId.GOODSBROADCASTRECEIVER_ACTION,jGBean)) ;
                        }else  if (msg_type.equals(TCConstants.MESSAGETYPE_LIVEWATCHCOUNT)){
                            JPushGoodsBean jGBean = new JPushGoodsBean();
                            JSONObject jsonObject = new JSONObject(extra);
                            int WatchCount=jsonObject.optInt("WatchCount");
                            int liveID=jsonObject.optInt("LiveID");
                            jGBean.setLiveID(liveID);
                            jGBean.setMessageType(msg_type);
                            jGBean.setWatchCount(WatchCount);
                            EventBus.getDefault().post(BusEvent.getEvent(EventBusId.GOODSBROADCASTRECEIVER_ACTION,jGBean)) ;
                        }

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            String msgJson = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.d(TAG, "接收到推送下来的通知的  ID: " + notifactionId + "  msgJson:" + msgJson);
            try {
                PushMsgModel msgModel = GsonHelper.jsonToObject(msgJson, PushMsgModel.class);
                if (msgModel.getType().equals("notify")) {
                    if (msgModel.getEvent().equals("new_apply_agent")) {//
                        Intent itNewAgent = new Intent(ACTION_NEW_APPLY_AGENT);
                        context.sendBroadcast(itNewAgent);
                    } else if ("new_order_flow".equals(msgModel.getEvent())) {
                        Intent itNewOrder = new Intent(ACTION_NEW_ORDER_FLOW);
                        context.sendBroadcast(itNewOrder);
                        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.REFRESH_ORDER_MANAGER)) ;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");
            String msgJson = bundle.getString(JPushInterface.EXTRA_EXTRA);
            try {
                PushMsgModel msgModel = GsonHelper.jsonToObject(msgJson, PushMsgModel.class);
                if (msgModel.getType().equals("notify")) {
                    return;
                }
            } catch (Exception e) {
                BaiduStats.log(context, BaiduStats.EventId.PUSH_ERROR, "推送打开失败：" + msgJson);
            }

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            // 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
            // 打开一个网页等..

        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key).toString());
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key).toString());
            }
        }
        return sb.toString();
    }

}
