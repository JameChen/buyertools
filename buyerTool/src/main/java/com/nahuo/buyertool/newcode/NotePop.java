package com.nahuo.buyertool.newcode;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.CommonListActivity;
import com.nahuo.buyertool.ItemDetailsActivity;
import com.nahuo.buyertool.PicGalleryActivity;
import com.nahuo.buyertool.PurchaseListActivity;
import com.nahuo.buyertool.api.HttpUtils;
import com.nahuo.buyertool.eventbus.BusEvent;
import com.nahuo.buyertool.eventbus.EventBusId;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.model.PurchaseModel;
import com.nahuo.buyertool.model.ShopItemListModel;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.helper.ImageUrlExtends;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.JsonHttpRequestCallback;
import cn.finalteam.okhttpfinal.RequestParams;
import de.greenrobot.event.EventBus;
import okhttp3.Headers;

/**
 * ===============BuyerTool====================
 * author：ChenZhen
 * Email：18620156376@163.com
 * Time : 2016/7/8 10:34
 * Description :已开单页面用来关联采购单的popwindow
 * ===============BuyerTool====================
 */
public class NotePop extends BasePopupWindow {
    @Bind(R.id.tv_select)
    TextView mTvSelect;
    @Bind(R.id.item_detail_purchase_tv1)
    TextView mItemDetailPurchaseTv1;
    @Bind(R.id.item_detail_purchase_tv2)
    TextView mItemDetailPurchaseTv2;
    @Bind(R.id.item_detail_purchase_img)
    ImageView mItemDetailPurchaseImg;
    @Bind(R.id.item_detail_purchase_edit)
    ImageView mItemDetailPurchaseEdit;
    @Bind(R.id.item_detail_purchase_v)
    RelativeLayout mItemDetailPurchaseV;
    @Bind(R.id.item_detail_green_btn)
    TextView mItemDetailGreenBtn;
    @Bind(R.id.work_view)
    LinearLayout mWorkView;
    private DecimalFormat df = new DecimalFormat("#0.00");


    //选择的相关参数信息
    private int size;//共选择的款数
    private int num;//共选择的件数
    private double purchasePrice;//总进价
    private double TotalPrice;//总售价

    private CommonListActivity mActivity;

    private LoadingDialog mloadingDialog;


    public NotePop(Context context) {
        super(context);
        updateData(null);
        updateNote(null);
        setFocusable(false);
        setOutsideTouchable(false);//点击区域外不退出
        mActivity = (CommonListActivity) context;
    }

    @Override
    protected void initAfterViews() {
        //设置大小
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    //更新选择的订单数据
    public void updateData(List<ShopItemListModel> models) {
        size = 0;
        num = 0;
        purchasePrice = 0.00;
        TotalPrice = 0.00;
        if (models == null || models.size() < 1) {
            mTvSelect.setText("已选0款共0件,总进价¥0.00,总售价:¥0.00");
            return;
        }
        size = models.size();
        for (int i = 0; i < models.size(); i++) {
            ShopItemListModel model = models.get(i);
            num += model.getBillingQty();//计算总件数
            purchasePrice += model.getBillingQty() * model.getOriPrice();//计算总进价
            TotalPrice += model.getBillingQty() * model.getPrice();//计算总售价
        }
        mTvSelect.setText("已选" + size + "款共" + num + "件,总进价¥" + df.format(purchasePrice) + ",总售价:¥" + df.format(TotalPrice));
    }

    //更新采购单数据
    public void updateNote(PurchaseModel selectPurchase) {
        if (selectPurchase == null || selectPurchase.getCode().length() <= 0) {
            mItemDetailPurchaseTv1.setText("单号：**********       总金额：¥0.00");
            mItemDetailPurchaseTv2.setText("市场：***     楼层：***      档口：***");
            return;
        }
        mItemDetailPurchaseTv1.setText("单号：" + selectPurchase.getCode() + "       总金额：¥" + df.format(selectPurchase.getTotalMoney()));
        mItemDetailPurchaseTv2.setText("市场：" + selectPurchase.getMarketName() + "     楼层：" + selectPurchase.getFloorName() + "      档口：" + selectPurchase.getStallsName());
    }


    @Override
    protected int getViewId() {
        return R.layout.pop_bookdis;
    }

    @Override
    protected int getAnimId() {
        return R.style.AnimationPopupWindowTop;//底部飞入飞出的动画
    }

    @Override
    protected void showPOpbefore() {
        super.showPOpbefore();
        updateData(null);
        updateNote(mActivity.getSelectPurchase());
    }

    @OnClick({R.id.item_detail_purchase_img, R.id.item_detail_purchase_edit, R.id.item_detail_green_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_detail_purchase_img://查看图片
                if (mActivity.getSelectPurchase() == null) {
                    Toast.makeText(mContext, "请先选择采购单", Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<String> imgs = new ArrayList<String>();
                String url = ImageUrlExtends.getImageUrl(mActivity.getSelectPurchase().getBillPic());
                imgs.add(url);
                Intent intent = new Intent(mContext, PicGalleryActivity.class);
                intent.putExtra(PicGalleryActivity.EXTRA_CUR_POS, 0);
                intent.putStringArrayListExtra(PicGalleryActivity.EXTRA_URLS, imgs);
                mActivity.startActivity(intent);
                break;
            case R.id.item_detail_purchase_edit://选择采购单
                Intent it = new Intent(mContext.getApplicationContext(), PurchaseListActivity.class);
                mActivity.startActivityForResult(it, CommonListActivity.SELECT_ORDERED);
                break;
            case R.id.item_detail_green_btn://关联采购单
                if (size < 1) {
                    Toast.makeText(mContext, "请先选择订单", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mActivity.getSelectPurchase() == null) {
                    Toast.makeText(mContext, "请先选择采购单", Toast.LENGTH_SHORT).show();
                    return;
                }
                Xdialog.actionDialog(mContext, "确认", "请确认开单数量,总价等与采购单明细一致", new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        postDate();
                    }
                });
                break;
        }
    }

    /**
     * 关联采购单网络交互
     */
    private void postDate() {
        String guanlianUrl = HttpUtils.SERVERURL + "buyertool/buyer/BatchBindPurchase";
        RequestParams params = new RequestParams();
        params.addFormDataPart("orderBillJson", beanToJson());//添加参数
        params.addHeader("Cookie", PublicData.getCookie(mContext));//添加cookie
        HttpRequest.post(guanlianUrl, params, new JsonHttpRequestCallback() {
            @Override
            public void onStart() {
                super.onStart();
                if (mloadingDialog == null) mloadingDialog = new LoadingDialog(mContext);
                mloadingDialog.start("关联中");
            }

            @Override
            protected void onSuccess(Headers headers, JSONObject jsonObject) {
                super.onSuccess(headers, jsonObject);
                if (mloadingDialog != null && mloadingDialog.isShowing())
                    mloadingDialog.dismiss();
                if (jsonObject.getBoolean("Result")) {
                    Toast.makeText(mContext, "你的采购单与单据关联成功", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(BusEvent.getEvent(EventBusId.NOTE_POP_F5, ""));//通知activity刷新
                } else {
                    Xdialog.msgDialog(mContext, "失败", "关联失败,请重试");
                }
                Log.i("关联采购单==>", jsonObject.toJSONString());
            }


            @Override
            public void onFailure(int errorCode, String msg) {
                super.onFailure(errorCode, msg);
                Xdialog.msgDialog(mContext, "失败", "关联失败,请重试");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (mloadingDialog != null && mloadingDialog.isShowing())
                    mloadingDialog.dismiss();
            }
        });
    }

    /**
     * 将当前选中的订单和采购单转为json
     *
     * @return
     */
    private String beanToJson() {
        List<PurAndOrdBean> beanList = new ArrayList<>();
        for (ShopItemListModel model : mActivity.getList())
            beanList.add(new PurAndOrdBean(mActivity.getSelectPurchase().getID(), model.getID()));
        return JSON.toJSONString(beanList);
    }
}
