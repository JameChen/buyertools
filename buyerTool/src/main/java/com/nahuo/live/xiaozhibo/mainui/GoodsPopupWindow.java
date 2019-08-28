package com.nahuo.live.xiaozhibo.mainui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.luck.picture.lib.tools.ScreenUtils;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.live.xiaozhibo.adpater.GoodsAdapter;
import com.nahuo.live.xiaozhibo.model.GoodsBean;

/**
 * Created by jame on 2019/5/10.
 */

public class GoodsPopupWindow extends PopupWindow {
    private View conentView;
    private RecyclerView recyclerView;
    private GoodsBean goodsBean;
    private TextView tv_goods_count;
    private int count;
    private GoodsAdapter goodsAdapter;
    private int Type;
    private GoodsTryOnOnClick goodsTryOnOnClick;
    private GoodsOnItemClick onItemClick;

    public void setOnItemClick(GoodsOnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setGoodsTryOnOnClick(GoodsTryOnOnClick goodsTryOnOnClick) {
        this.goodsTryOnOnClick = goodsTryOnOnClick;
    }

    public GoodsPopupWindow(Context context, GoodsBean goodsBean, int Type) {
        this.goodsBean = goodsBean;
        this.Type = Type;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.goods_pop_menu, null);
        recyclerView = (RecyclerView) conentView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        goodsAdapter = new GoodsAdapter(context);
        goodsAdapter.setType(this.Type);
        goodsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (onItemClick != null) {
                    onItemClick.OnItemClick((GoodsBean.GoodsListBean)adapter.getData().get(position), position);
                }
            }
        });
        goodsAdapter.setTryOnItemOnClick(new GoodsAdapter.TryOnItemOnClick() {
            @Override
            public void OnClick(GoodsBean.GoodsListBean item, int position, boolean isTryOn) {
                if (goodsTryOnOnClick != null)
                    goodsTryOnOnClick.OnClick(item, position, isTryOn);
            }
        });
        recyclerView.setAdapter(goodsAdapter);
        int h = ScreenUtils.getScreenHeight(context);
        int w = ScreenUtils.getScreenWidth(context);
// 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
// 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w - 50);
// 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(h * 5 / 7);
// 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
// 刷新状态
        this.update();
// 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
// mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
// 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.popwin_anim_style);
        conentView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = conentView.findViewById(R.id.recyclerView).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
        if (this.goodsBean != null) {
            count = this.goodsBean.getGoodsCount();
            if (ListUtils.isEmpty(this.goodsBean.getGoodsList())) {
                goodsAdapter.setNewData(null);
            } else {
                goodsAdapter.setNewData(this.goodsBean.getGoodsList());
            }
        }
        tv_goods_count = (TextView) conentView.findViewById(R.id.tv_goods_count);
        tv_goods_count.setText(count + "");
    }

    public void noTifyList(int pos, boolean tryON) {
        if (goodsBean != null) {
            if (!ListUtils.isEmpty(goodsBean.getGoodsList())) {
                for (int i = 0; i < goodsBean.getGoodsList().size(); i++) {
                    GoodsBean.GoodsListBean bean = goodsBean.getGoodsList().get(i);
                    if (tryON) {
                        if (i == pos) {
                            bean.setTryOn(true);
                        } else {
                            bean.setTryOn(false);
                        }
                    } else {
                        bean.setTryOn(false);
                    }
                }
            }
            if (goodsAdapter != null)
                goodsAdapter.notifyDataSetChanged();
        }
    }

    public interface GoodsTryOnOnClick {
        void OnClick(GoodsBean.GoodsListBean item, int position, boolean isTryOn);
    }

    public interface GoodsOnItemClick {

        void OnItemClick(GoodsBean.GoodsListBean item, int position);
    }
//    /**
//     * 显示popupWindow
//     *
//     * @param parent
//     */
//    public void showPopupWindow(View parent) {
//        if (!this.isShowing()) {
//// 以下拉方式显示popupwindow
//            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 18);
//        } else {
//            this.dismiss();
//        }
//    }
}