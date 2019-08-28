package com.nahuo.buyertool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.model.ItemShopCategory;

import java.util.ArrayList;

/**
 * Created by jame on 2017/7/7.
 */

public class AccountDialog extends Dialog implements View.OnClickListener {
    static AccountDialog dialog = null;
    private Activity mActivity;
    View mRootView;
    Button btn_ok, btn_cancle;
    int h, w;
    PopDialogListener mPopDialogListener;
    TextView tv_title;
    ArrayList<ItemShopCategory> mSelectedShopCategories = new ArrayList<>();
    public AccountDialog(@NonNull Activity context) {
        super(context, R.style.popDialog);
        this.mActivity = context;
    }

    public static AccountDialog getInstance(Activity activity) {
        if (dialog == null) {
            dialog = new AccountDialog(activity);
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
        mRootView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_shop_account, null);
        btn_ok = (Button) mRootView.findViewById(R.id.btn_ok);
        btn_cancle = (Button) mRootView.findViewById(R.id.btn_cancle);
        tv_title=(TextView)mRootView.findViewById(R.id.title);
        btn_ok.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);
        if (tv_title!=null)
            tv_title.setText(title);
        setContentView(mRootView);
        setCanceledOnTouchOutside(false);
        setCancelable(true);
    }

    String rate = "20";
    String title;
    public AccountDialog setTitle(String title){
        this.title=title;
        return  dialog;
    }

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
                if (mPopDialogListener != null) {
                    mPopDialogListener.onAccountDialogButtonClick();
                }
                dismiss();
                dialog = null;
                break;

        }
    }

    public AccountDialog setPositive(PopDialogListener listener) {
        mPopDialogListener = listener;
        return  this;
    }

    public interface PopDialogListener {
        void onAccountDialogButtonClick();
    }
}
