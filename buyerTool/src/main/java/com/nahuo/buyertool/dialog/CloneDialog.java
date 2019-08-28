package com.nahuo.buyertool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.model.ItemShopCategory;

import java.util.ArrayList;

/**
 * Created by jame on 2017/7/7.
 */

public class CloneDialog extends Dialog implements View.OnClickListener {
    static CloneDialog dialog = null;
    private Activity mActivity;
    View mRootView;
    TextView tv_category;
    Button btn_ok, btn_cancle;
    int h, w;
    EditText et_add_rate;
    PopDialogListener mPopDialogListener;
    ArrayList<ItemShopCategory> mSelectedShopCategories = new ArrayList<>();
    int shopcartId;
    public CloneDialog(@NonNull Activity context) {
        super(context, R.style.popDialog);
        this.mActivity = context;
    }

    public static CloneDialog getInstance(Activity activity) {
        if (dialog == null) {
            dialog = new CloneDialog(activity);
        }
        return dialog;
    }

    public void setList(ArrayList<ItemShopCategory> mSelectedShopCategories) {
        this.mSelectedShopCategories = mSelectedShopCategories;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        h = mActivity.getResources().getDisplayMetrics().heightPixels;
        w = mActivity.getResources().getDisplayMetrics().widthPixels;
        mRootView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_shop_clone, null);
        btn_ok = (Button) mRootView.findViewById(R.id.btn_ok);
        btn_cancle = (Button) mRootView.findViewById(R.id.btn_cancle);
        btn_ok.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);
        setContentView(mRootView);
        setCanceledOnTouchOutside(false);
        setCancelable(true);
    }

    String rate = "20";

    public void showDialog() {
//        if (ListUtils.isEmpty(mSelectedShopCategories)) {
//            if (tv_category != null)
//                tv_category.setText("默认分类");
//        } else {
//            if (tv_category != null)
//                tv_category.setText(mSelectedShopCategories.get(0).getName());
//        }
        this.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.tv_category:
//                Intent shopCatIntent = new Intent(mActivity, SelectItemShopCategoryActivity.class);
//                shopCatIntent.putExtra(SelectItemShopCategoryActivity.EXTRA_SELECTED_CATS, mSelectedShopCategories);
//                mActivity.startActivityForResult(shopCatIntent, REQUEST_SELECT_ITEM_SHOP_CAT);
//                break;
            case R.id.btn_cancle:
                dismiss();
                dialog = null;
                break;
            case R.id.btn_ok:
                if (et_add_rate != null)
                    rate = et_add_rate.getText().toString().trim();
                if (mPopDialogListener != null) {
                    if (ListUtils.isEmpty(mSelectedShopCategories)){
                       shopcartId=0;
                    }else {
                        shopcartId=mSelectedShopCategories.get(0).getId();
                    }
                    mPopDialogListener.onCloneDialogButtonClick(shopcartId, rate);
                }
                dismiss();
                dialog = null;
                break;

        }
    }

    public CloneDialog setPositive(PopDialogListener listener) {
        mPopDialogListener = listener;
        return  this;
    }

    public interface PopDialogListener {
        void onCloneDialogButtonClick(int shopCats, String rate);
    }
}
