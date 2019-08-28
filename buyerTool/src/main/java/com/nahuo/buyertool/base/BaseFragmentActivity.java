package com.nahuo.buyertool.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;

import com.baidu.mobstat.StatService;
import com.nahuo.buyertool.BWApplication;
import com.nahuo.buyertool.MainAcivity;
import com.nahuo.live.xiaozhibo.common.utils.TCConstants;
import com.nahuo.live.xiaozhibo.mainui.LiveListActivity;
import com.nahuo.live.xiaozhibo.play.TCLivePlayerActivity;
import com.nahuo.live.xiaozhibo.play.TCVodPlayerActivity;
import com.nahuo.live.xiaozhibo.push.camera.TCLivePublisherActivity;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by jame on 2018/3/28.
 */

public class BaseFragmentActivity extends FragmentActivity {
    protected CompositeDisposable mCompositeDisposable;

    protected void unSubscribe() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }
    protected Activity currentActivity=this;
    //被踢下线广播监听
    private LocalBroadcastManager mLocalBroadcatManager;
    private BroadcastReceiver mExitBroadcastReceiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity=this;
        if (!this.getClass().getSimpleName().equals(MainAcivity.class.getSimpleName())
                & !this.getClass().getSimpleName().equals(LiveListActivity.class.getSimpleName())
                &  !this.getClass().getSimpleName().equals(TCVodPlayerActivity.class.getSimpleName())
                &  !this.getClass().getSimpleName().equals(TCLivePublisherActivity.class.getSimpleName()
        )&  !this.getClass().getSimpleName().equals(TCLivePlayerActivity.class.getSimpleName())) {
            BWApplication.addVActivity(this);
        }
        BWApplication.setCurrentActivity(this);
        mLocalBroadcatManager = LocalBroadcastManager.getInstance(this);
        mExitBroadcastReceiver = new ExitBroadcastRecevier(currentActivity);
        mLocalBroadcatManager.registerReceiver(mExitBroadcastReceiver, new IntentFilter(TCConstants.EXIT_APP));
    }

//
//    /**
//     * 获取订单退款信息
//     *
//     * @author James Chen
//     * @create time in 2018/5/9 14:31
//     */
//    public void getOrderItemForRefund(final Context context, final int oid) {
//        if (oid < 0)
//            return;
//        addSubscribe(HttpManager.getInstance().getPinHuoNetCacheApi("getOrderItemForRefund")
//                .getOrderItemForRefund(oid).compose(RxUtil.<PinHuoResponse<ReFundBean>>rxSchedulerHelper())
//                .compose(RxUtil.<ReFundBean>handleResult()).subscribeWith(new CommonSubscriber<ReFundBean>(context, true, R.string.loading) {
//                    @Override
//                    public void onNext(final ReFundBean reFundBean) {
//                        super.onNext(reFundBean);
//                        if (reFundBean != null) {
//                            String styledText;
//                            if (TextUtils.isEmpty(reFundBean.getCoin())) {
//                                styledText = "已付商品货款：<font color='red'>¥" + reFundBean.getProductAmount() + "</font>，分摊运费：<font color='red'>¥"
//                                        + reFundBean.getPostFee() + "</font>，总计可退：<font color='#09F709'>¥" + reFundBean.getTotalRefundAmount() + "</font>。";
//                            } else {
//                               styledText = "已付商品货款：<font color='red'>¥" + reFundBean.getProductAmount() +  "</font>，换货币：<font color='red'>¥" +reFundBean.getCoin()+ "</font>，分摊运费：<font color='red'>¥"
//                                        + reFundBean.getPostFee() + "</font>，总计可退：<font color='#09F709'>¥" + reFundBean.getTotalRefundAmount() + "</font>。";
//                            }
//                            ReFundOderDialog dialog = new ReFundOderDialog((Activity) context);
//                            dialog.setHasTittle(true).setMessage(Html.fromHtml(styledText))
//                                    .setPositive("确认退款", new ReFundOderDialog.PopDialogListener() {
//                                        @Override
//                                        public void onPopDialogButtonClick(int which, ReFundOderDialog oderDialog) {
//                                            buyerApplyRefund(context, oid, oderDialog);
//                                        }
//                                    }).setNegative("我再想想", null).show();
//                        }
//                    }
//                }));
//    }
//
//    /**
//     * 订单退款
//     *
//     * @author James Chen
//     * @create time in 2018/5/9 14:31
//     */
//    public void buyerApplyRefund(final Context context, int oid, final ReFundOderDialog oderDialog) {
//        if (oid < 0)
//            return;
//        Map<String, Object> params = new HashMap<>();
//        params.put("orderId", oid + "");
//        params.put("refundWithProduct", false);
//        params.put("refundType", 1);
//        params.put("refundAmount", 0);
//        params.put("refundReason", "");
//        addSubscribe(HttpManager.getInstance().getPinHuoNetCacheApi("buyerApplyRefund")
//                .buyerApplyRefund(params).compose(RxUtil.<PinHuoResponse<Object>>rxSchedulerHelper())
//                .compose(RxUtil.<Object>handleResult()).subscribeWith(new CommonSubscriber<Object>(context, true, R.string.loading_refund) {
//                    @Override
//                    public void onNext(Object reFundBean) {
//                        super.onNext(reFundBean);
//                        ViewHub.showShortToast(context, "退款成功！");
//                        if (oderDialog != null)
//                            oderDialog.dismiss();
//                        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.REFUND_BUYER_AGRESS, "ok"));
//                    }
//                }));
//    }

    protected void addSubscribe(Disposable subscription) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(subscription);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unSubscribe();
        if (mLocalBroadcatManager!=null) {
            if (mExitBroadcastReceiver!=null)
                mLocalBroadcatManager.unregisterReceiver(mExitBroadcastReceiver);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
        JPushInterface.onPause(this);
       // UMengTestUtls.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentActivity=this;
        StatService.onResume(this);
        JPushInterface.onResume(this);
      //  UMengTestUtls.onResume(this);
    }
}
