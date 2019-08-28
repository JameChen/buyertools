package com.nahuo.buyertool.task;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.api.AgentAPI;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.eventbus.BusEvent;
import com.nahuo.buyertool.eventbus.EventBusId;
import com.nahuo.library.controls.LoadingDialog;

import de.greenrobot.event.EventBus;

/**
 * @description 代理店铺
 * @created 2015-1-28 上午10:22:21
 * @author ZZB
 */
public class AgentShopTask extends AsyncTask<Object, Void, Object> {

    private LoadingDialog mDialog;
    private Context mContext;
    private int mUserId;
    private Callback mCallback;
    private int mApplyStatuId;

    public static interface Callback {
        public void onAgentFinished();
    }
    public void setCallback(Callback callback){
        mCallback = callback;
    }
    public AgentShopTask(Context context, int userId, int applyStatuId) {
        mContext = context;
        mDialog = new LoadingDialog(mContext);
        mUserId = userId;
        mApplyStatuId = applyStatuId;
    }

    @Override
    protected void onPreExecute() {
        mDialog.start("代理店铺中...");
    }

    @Override
    protected Object doInBackground(Object... params) {
        try {
            String nextApplyTime = "";
            if(mApplyStatuId == Const.ApplyAgentStatu.REJECT){
                nextApplyTime = AgentAPI.getNextAgentApplyTime(mContext, mUserId);
            }
            if(TextUtils.isEmpty(nextApplyTime)){
                AgentAPI.applyAgent(mContext, mUserId, "关注转代理");
            }else{
                return nextApplyTime;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return "error:" + e.getMessage();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        if (mDialog.isShowing()) {
            mDialog.stop();
        }
        if (result instanceof String && ((String) result).startsWith("error:")) {
            ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));
        } else {
            if(result != null){
                ViewHub.showOkDialog(mContext, "提示", "你申请代理被拒绝，需要" + result.toString() + "后才可以重新申请代理", "OK");
            }else{
                if (mCallback != null) {
                    mCallback.onAgentFinished();
                }
//                Intent serviceIntent = new Intent();
//                serviceIntent.setAction(AllItemActivity.AllItemActivityReloadBroadcaseName);
//                mContext.sendBroadcast(serviceIntent);
                EventBus.getDefault().postSticky(BusEvent.getEvent(EventBusId.AGENT_VENDOR_CHANGED));
                ViewHub.showOkDialog(mContext, "提示", "代理请求已发送，请等待上家通过", "OK");
            }
            
        }
    }
}
