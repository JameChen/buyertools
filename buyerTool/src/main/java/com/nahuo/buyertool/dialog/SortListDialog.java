package com.nahuo.buyertool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.AttributeBean;
import com.nahuo.buyertool.Bean.SortBean;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.activity.FullyGridLayoutManager;
import com.nahuo.buyertool.adapter.SortListAdapter;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.utils.SortUtls;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jame on 2017/7/7.
 */

public class SortListDialog extends Dialog implements View.OnClickListener {
    static SortListDialog dialog = null;
    private Activity mActivity;
    View mRootView;
    TextView btn_ok, btn_cancle, tv_title;
    int h, w;
    PopDialogListener mPopDialogListener;
    List<SortBean.ListBean> alist;
    List<Integer> propertyIDS;
    RecyclerView listView;
    List<String> data = new ArrayList<>();
    String type;
    int sort_type;
    SortListAdapter adapter;
    int hasSelId;

    public SortListDialog setHasSelId(int hasSelId) {
        this.hasSelId = hasSelId;
        return this;
    }

    public SortListDialog(@NonNull Activity context) {
        super(context, R.style.popDialog);
        this.mActivity = context;
    }

    int choose_index = 0;

    public static SortListDialog getInstance(Activity activity) {
        if (dialog == null) {
            dialog = new SortListDialog(activity);
        }
        return dialog;
    }

    public SortListDialog setList(List<SortBean.ListBean> list) {
        this.alist = list;
        return this;
    }

    public SortListDialog setPropertyIDS(List<Integer> propertyIDS) {
        this.propertyIDS = propertyIDS;
        return this;
    }


    public SortListDialog setType(String type, int sort_type) {
        this.type = type;
        this.sort_type = sort_type;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        h = mActivity.getResources().getDisplayMetrics().heightPixels;
        w = mActivity.getResources().getDisplayMetrics().widthPixels;
        mRootView = LayoutInflater.from(mActivity).inflate(R.layout.four_list_dialog, null);
        listView = (RecyclerView) mRootView.findViewById(R.id.listview);
        btn_ok = (TextView) mRootView.findViewById(R.id.tv_ok);
        btn_cancle = (TextView) mRootView.findViewById(R.id.tv_cancel);
        tv_title = (TextView) mRootView.findViewById(R.id.tv_title);
        btn_ok.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);
        setContentView(mRootView);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager m = mActivity.getWindowManager();
        Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = d.getWidth(); //宽度设置为屏幕
        p.height = d.getHeight() * 2 / 3;
        dialog.getWindow().setAttributes(p); //设置生效
       /* 待开单：paid
        已开单：billing
        欠货单：owes
        入仓单：store
        退款单：refund
        已开单和欠货单：billingandowes
        我的款式：item*/
        switch (type) {
            case SortUtls.TYPE_BILLING:
            case SortUtls.TYPE_BILLINGANDOWES:
            case SortUtls.TYPE_OWES:
            case SortUtls.TYPE_PAID:
            case SortUtls.TYPE_REFUND:
            case SortUtls.TYPE_STORE:
            case SortUtls.TYPE_EXCEPTION:
                if (sort_type == SortUtls.TYPE_SORT_1) {
                    tv_title.setText("筛选订单");
                } else if (sort_type == SortUtls.TYPE_SORT_2) {
                    tv_title.setText("筛选订单");
                }
                break;
            case SortUtls.TYPE_ITEM:
                if (sort_type == SortUtls.TYPE_SORT_1) {
                    tv_title.setText("筛选款式");
                } else if (sort_type == SortUtls.TYPE_SORT_2) {
                    tv_title.setText("筛选款式");
                }
                break;
        }
        List<SortBean> list = SortUtls.getListFilter(mActivity);
        if (!ListUtils.isEmpty(list)) {
            for (SortBean bean : list) {
                if (type.equals(bean.getType())) {
                    if (sort_type == SortUtls.TYPE_SORT_1) {
                        alist = bean.getListfilter();
                    } else if (sort_type == SortUtls.TYPE_SORT_2) {
                        alist = bean.getListSort();
                    }
                    break;
                }
            }
            if (!ListUtils.isEmpty(alist)) {
                for (SortBean.ListBean bean : alist) {
                    if (hasSelId == bean.getValue()) {
                        bean.isSelect = true;
                    } else {
                        bean.isSelect = false;
                    }
                }
            }
            adapter = new SortListAdapter(mActivity);
            adapter.setPareData(alist);
            FullyGridLayoutManager manager = new FullyGridLayoutManager(mActivity, 2, GridLayoutManager.VERTICAL, false);
            listView.setLayoutManager(manager);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }


    }


    public void showDialog() {
//        if (!TextUtils.isEmpty(content)) {
//            if (tv_title != null)
//                tv_title.setText(content);
//        }
        this.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        dialog = null;
    }

    List<AttributeBean.ChildsBean> childsBeanList;
    List<UploadBean.PropertyListBean> propertyListBeanList;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                dialog = null;
                break;
            case R.id.tv_ok:
                SortBean.ListBean bean = null;
                if (adapter != null) {
                    bean = adapter.getHasCheckData();
                }
                if (mPopDialogListener != null) {
                    if (bean != null) {
                        mPopDialogListener.onGetSortDialogButtonClick(bean, type, sort_type);
                    }
                }
                dismiss();
                dialog = null;
                break;

        }
    }

    public SortListDialog setPositive(PopDialogListener listener) {
        mPopDialogListener = listener;
        return this;
    }

    public interface PopDialogListener {
        void onGetSortDialogButtonClick(SortBean.ListBean bean, String type, int Sort_type);
    }
}
