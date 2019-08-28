package com.nahuo.buyertool;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.SearchBean;
import com.nahuo.buyertool.Bean.SelBean;
import com.nahuo.buyertool.Bean.SortBean;
import com.nahuo.buyertool.adapter.CommonListAdapter;
import com.nahuo.buyertool.api.ApiHelper;
import com.nahuo.buyertool.api.BuyerToolAPI;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.dialog.SortListDialog;
import com.nahuo.buyertool.eventbus.BusEvent;
import com.nahuo.buyertool.eventbus.EventBusId;
import com.nahuo.buyertool.model.PurchaseModel;
import com.nahuo.buyertool.model.ShopItemListModel;
import com.nahuo.buyertool.newcode.ArrearsPop;
import com.nahuo.buyertool.newcode.NotePop;
import com.nahuo.buyertool.newcode.NoteStorePop;
import com.nahuo.buyertool.newcode.NoteWaitPop;
import com.nahuo.buyertool.newcode.RefreshBean;
import com.nahuo.buyertool.utils.SortUtls;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 小组页面
 *
 * @author nahuo9
 */
public class CommonListActivity extends BaseActivity implements SortListDialog.PopDialogListener, View.OnClickListener, PullToRefreshListView.OnLoadMoreListener, PullToRefreshListView.OnRefreshListener {

    private static final String TAG = "CommonListActivity";
    private CommonListActivity vThis = this;
    private LoadingDialog mloadingDialog;
    private int mPageIndex = 1;
    private int mPageSize = 10;
    private CommonListAdapter adapter;
    private List<ShopItemListModel> itemList = null;

    private Context mContext = this;


    private PullToRefreshListView mRefreshListView;
    private String searchKey="",stallKey="";//搜索的关键字
    private EventBus mEventBus = EventBus.getDefault();

    public static final int SELECT_ORDERED = 75;
    private View view_dd;

    //如果是已开单或者入库单页面,则增加编辑和完成功能()
    private boolean isEdit = false;//是否正在编辑状,默认为否
    private TextView mTextView;

    public List<ShopItemListModel> getList() {
        return mList;
    }

    private List<ShopItemListModel> mList = new ArrayList<>();//存放当前选中的即将关联的订单id
    private NotePop mNotePop;
    private NoteWaitPop mWaitPop;
    private ArrearsPop mArrearPop;
    private NoteStorePop mNoteStorePop;
    int status = -1;
    int sort = -1;
    private ShopItemListModel mShopItem;
    private TextView tv_sort, tv_status;
    int type = 1;
    String Text = "";

    @Override
    public void onGetSortDialogButtonClick(SortBean.ListBean bean, String type, int Sort_type) {
        if (bean != null) {
            switch (Sort_type) {
                case SortUtls.TYPE_SORT_2:
                    sort = bean.getValue();
                    tv_sort.setText(bean.getText());
                    break;
                case SortUtls.TYPE_SORT_1:
                    status = bean.getValue();
                    tv_status.setText(bean.getText());
                    break;
            }
            bindItemData(true);
//            if (sort == -1) {
//                tv_sort.setText("排序");
//            } else {
//                tv_sort.setText(sortList.get(which - 1).getContent());
//
//            }

        }
    }

    public enum ListType {
        待开单,
        已开单,
        入库单,
        欠货单,
        退款单,
        开或欠单,
        异常单
    }

    private ListType mType;
    /*   待开单：paid
       已开单：billing
       欠货单：owes
       入仓单：store
       退款单：refund
       已开单和欠货单：billingandowes
       我的款式：item*/
    private String sType = SortUtls.TYPE_PAID;
    private Dialog writeListDialog;

    @Override
    public void onLoadMore() {
        bindItemData(false);
    }

    @Override
    public void onRefresh() {
        bindItemData(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_common_list);
        mEventBus.registerSticky(this);
        view_dd = findViewById(R.id.view_dd);
        tv_sort = (TextView) findViewById(R.id.tv_sort);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_sort.setVisibility(View.VISIBLE);
        tv_status.setVisibility(View.VISIBLE);
        tv_status.setOnClickListener(this);
        tv_sort.setOnClickListener(this);
        type = getIntent().getIntExtra("type", 1);
        Text = getIntent().getStringExtra("Text");

        switch (type) {
            case 1:
                mType = ListType.待开单;
                sType = SortUtls.TYPE_PAID;
                ((TextView) findViewById(R.id.tv_title)).setText("待开单");
                break;
            case 2:
                mType = ListType.已开单;
                sType = SortUtls.TYPE_BILLING;
                ((TextView) findViewById(R.id.tv_title)).setText("已开单");
                break;
            case 3:
                mType = ListType.入库单;
                sType = SortUtls.TYPE_STORE;
                ((TextView) findViewById(R.id.tv_title)).setText("入仓单");
                break;
            case 4:
                mType = ListType.欠货单;
                sType = SortUtls.TYPE_OWES;
                ((TextView) findViewById(R.id.tv_title)).setText("欠货单");
                break;
            case 5:
                mType = ListType.退款单;
                sType = SortUtls.TYPE_REFUND;
                ((TextView) findViewById(R.id.tv_title)).setText("退款单");
                break;
            case 6:
                mType = ListType.开或欠单;
                sType = SortUtls.TYPE_BILLINGANDOWES;
                // ((TextView) findViewById(R.id.tv_title)).setText("开/欠单");
                Text = "开/欠单";
                break;
            case 7:
                mType = ListType.异常单;
                sType = SortUtls.TYPE_EXCEPTION;
                ((TextView) findViewById(R.id.tv_title)).setText("异常单");
                break;
            default:
                mType = ListType.待开单;
                sType = SortUtls.TYPE_PAID;
                ((TextView) findViewById(R.id.tv_title)).setText("待开单");
                break;
        }
        List<SortBean> list = SortUtls.getListFilter(vThis);
        if (!ListUtils.isEmpty(list)) {
            for (SortBean bean : list) {
                if (sType.equals(bean.getType())) {
                    if (!ListUtils.isEmpty(bean.getListfilter())) {
                        tv_status.setText(bean.getListfilter().get(0).getText());
                    }
                    if (!ListUtils.isEmpty(bean.getListSort())) {
                        tv_sort.setText(bean.getListSort().get(0).getText());
                    }
                    break;
                }
            }
        }
        ((TextView) findViewById(R.id.tv_title)).setText(Text);
        ((TextView) findViewById(R.id.tv_title)).setMaxEms(3);
        mloadingDialog = new LoadingDialog(vThis);
        initView();
//        File cachetop = CacheDirUtil.getCache(getApplicationContext(), "Listpage_list_" + mType);
//        if (cachetop.exists()) {
//            itemList = GsonHelper.jsonToObject(CacheDirUtil.readString(cachetop),
//                    new TypeToken<List<ShopItemListModel>>() {
//                    });
//            adapter.mList = itemList;
//            adapter.notifyDataSetChanged();
//        }
//        else {
        bindItemData(true);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpManager.setStallKeyWord(this,"");
        mEventBus.unregister(this);
        adapter = null;
        if (itemList != null) {
            itemList.clear();
            itemList = null;
        }
        if (writeListDialog != null) {
            if (writeListDialog.isShowing())
                writeListDialog.dismiss();
            writeListDialog = null;
        }
    }

    public void onEventMainThread(BusEvent event) {
       String keyword = "";
        String stall="";
        if (event.data instanceof SearchBean){
            SearchBean bean= (SearchBean) event.data;
            if (bean!=null){
                keyword=bean.getKeyWord();
                stall=bean.getStall_word();
            }
        }
        switch (event.id) {
            case EventBusId.COMMON_LIST_CALCEL_KB:
                RefreshBean data1 = (RefreshBean) event.data;
                int index1 = id2Index(data1.getTempId());
                if (index1 == -1) return;
                if (data1.getBuyNum() >= (itemList.get(index1).getPayQty())) {//完全开单
                    itemList.remove(index1);
                    //补数据并刷新
                    LoadListDataTask loadListDataTask = new LoadListDataTask(false);
                    loadListDataTask.execute(true);
                } else {//部分开单
                    int payQty = itemList.get(index1).getPayQty();
                    itemList.get(index1).setPayQty(payQty - data1.getBuyNum());
                    adapter.notifyDataSetChanged();
                    Log.i("onEventMainThread", "剩余:" + itemList.get(index1).getPayQty() + "开单:" + data1.getBuyNum());
                }
                break;
            case EventBusId.NOTE_POP_F5_DAIKAIDAN://收到待开单页面关联采购单成功的通知,
                RefreshBean data = (RefreshBean) event.data;
                int index = id2Index(data.getTempId());
                if (index == -1) return;
                if (data.getBuyNum() >= (itemList.get(index).getPayQty())) {//完全开单
                    itemList.remove(index);
                    //补数据并刷新
                    LoadListDataTask loadListDataTask = new LoadListDataTask(false);
                    loadListDataTask.execute(true);
                } else {//部分开单
                    int payQty = itemList.get(index).getPayQty();
                    itemList.get(index).setPayQty(payQty - data.getBuyNum());
                    adapter.notifyDataSetChanged();
                    Log.i("onEventMainThread", "剩余:" + itemList.get(index).getPayQty() + "开单:" + data.getBuyNum());
                }

//                bindItemData(true);
                break;
            case EventBusId.COMMON_LIST_RELOAD:
                bindItemData(true);
                break;
            case EventBusId.NOTE_POP_F5://关联成功之后,刷新界面,清空相关数据
                if (mNotePop != null && mNotePop.isShowing()) mNotePop.dismiss();
                if (mNoteStorePop != null && mNoteStorePop.isShowing()) mNoteStorePop.dismiss();
                isEdit = false;
                mTextView.setText("编辑");
                selectPurchase = null;
                mList.clear();
                bindItemData(true);
                break;
            case EventBusId.SEARCH_待开单:
                if (mType == ListType.待开单) {
                    searchKey = keyword;
                    stallKey=stall;
                    bindItemData(true);
                }
                break;
            case EventBusId.SEARCH_已开单:
                if (mType == ListType.已开单) {
                    searchKey = keyword;
                    stallKey=stall;
                    bindItemData(true);
                }
                break;
            case EventBusId.SEARCH_异常单:
                if (mType == ListType.异常单) {
                    searchKey = keyword;
                    stallKey=stall;
                    bindItemData(true);
                }
                break;
            case EventBusId.SEARCH_开或欠单:
                if (mType == ListType.开或欠单) {
                    searchKey = keyword;
                    stallKey=stall;
                    bindItemData(true);
                }
                break;
            case EventBusId.SEARCH_入库单:
                if (mType == ListType.入库单) {
                    searchKey = keyword;
                    stallKey=stall;
                    bindItemData(true);
                }
                break;
            case EventBusId.SEARCH_欠货单:
                if (mType == ListType.欠货单) {
                    searchKey = keyword;
                    stallKey=stall;
                    bindItemData(true);
                }
                break;
            case EventBusId.SEARCH_退款单:
                if (mType == ListType.退款单) {
                    searchKey = keyword;
                    stallKey=stall;
                    bindItemData(true);
                }
                break;

        }
    }

    private int id2Index(int tempId) {
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getID() == tempId)
                return i;
        }
        return -1;
    }

    private void initView() {

        Button backBtn = (Button) findViewById(R.id.titlebar_btnLeft);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        if (mType == ListType.异常单 ||mType == ListType.欠货单 || mType == ListType.已开单 || mType == ListType.入库单 || mType == ListType.待开单) {//在已开单和入库单页面显示编辑和完成
            mTextView = (TextView) findViewById(R.id.tv_right);
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText("编辑");
            mTextView.setOnClickListener(this);
        }

        ImageView mIvSearch = (ImageView) findViewById(R.id.titlebar_icon_loading);
        mIvSearch.setImageResource(R.drawable.find);
        mIvSearch.setOnClickListener(this);
        mIvSearch.setVisibility(View.VISIBLE);

        mRefreshListView = (PullToRefreshListView) findViewById(R.id.id_group_listview);
        initItemAdapter();
        mRefreshListView.setCanLoadMore(true);
        mRefreshListView.setCanRefresh(true);
        mRefreshListView.setMoveToFirstItemAfterRefresh(true);
        mRefreshListView.setOnRefreshListener(this);
        mRefreshListView.setOnLoadListener(this);
        mRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position == 0) {
                        position = 1;
                    }
                    ShopItemListModel model = adapter.getItem(position - 1);
                    if ((mType == ListType.异常单 ||mType == ListType.已开单 || mType == ListType.入库单) && isEdit()) {//已开单并且是编辑状态,并且该项是未关联
                        if (!model.getPurchaseStatus()) {
                            int index = findId(model.getID());
                            if (index < 0) {//还未选入
                                mList.add(model);
                            } else {
                                mList.remove(index);//移除
                            }
                            adapter.updateView(position);//改变当前的选中状态
                            if (mType == ListType.已开单||mType == ListType.异常单) {
                                mNotePop.updateData(mList);//更新pop的当前选中
                            } else {
                                mNoteStorePop.updateData(mList);//更新pop的当前选中
                            }

                        }
                        return;
                    } else if (mType == ListType.待开单 && isEdit()) {
                        int index = findId(model.getID());
                        if (index < 0) {//还未选入
                            mList.add(model);
                        } else {
                            mList.remove(index);//移除
                        }
                        adapter.updateView(position);//改变当前的选中状态
                        mWaitPop.updateData(mList);
                        return;
                    } else if (mType == ListType.欠货单 && isEdit()) {
                        int index = findId(model.getID());
                        if (index < 0) {//还未选入
                            mList.add(model);
                        } else {
                            mList.remove(index);//移除
                        }
                        adapter.updateView(position);//改变当前的选中状态
                        mArrearPop.setAdapter(adapter);
                        mArrearPop.updateData(mList);
                        return;
                    }

                    Intent intent = new Intent(vThis, ItemDetailsActivity.class);
                    intent.putExtra(ItemDetailsActivity.EXTRA_ID, model.getID());
                    intent.putExtra(ItemDetailsActivity.EXTRA_QSID, model.getQsID());
                    if (mType == ListType.开或欠单) {
                        if (model.getStatusID() == 1) {
                            intent.putExtra("type", ListType.已开单);
                        } else {
                            intent.putExtra("type", ListType.欠货单);
                        }
                    } else {
                        intent.putExtra("type", mType);
                    }

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 查找集合中是否已经存在这个id
     *
     * @param id
     * @return
     */
    public int findId(int id) {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getID() == id) {
                return i;
            }
        }
        return -1;
    }

    List<SelBean> statusList = new ArrayList<>();
    List<SelBean> sortList = new ArrayList<>();
    int height;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_btnLeft:
                finish();
                break;
            case R.id.titlebar_icon_loading:
                CommonSearchActivity.launch(this, mType);
                break;
            case R.id.tv_right:
                mTextView.setText(isEdit ? "编辑" : "完成");
                isEdit = !isEdit();
                if (mType == ListType.已开单||mType==ListType.异常单) {
                    if (isEdit()) {//转为编辑状态
                        if (mNotePop == null) mNotePop = new NotePop(this);
                        mNotePop.showAtLocation(mRefreshListView, Gravity.BOTTOM, 0, 0);
                        mNotePop.getContentView().measure(0, 0);
                        height = mNotePop.getContentView().getMeasuredHeight();
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                        view_dd.setLayoutParams(params);
                        view_dd.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view_dd.setVisibility(View.VISIBLE);
                            }
                        }, 450);

                    } else {
                        view_dd.setVisibility(View.GONE);
                        mNotePop.dismiss();
                    }
                } else if (mType == ListType.待开单) {
                    if (isEdit()) {//转为编辑状态
                        if (mWaitPop == null) mWaitPop = new NoteWaitPop(this);
                        mWaitPop.showAtLocation(mRefreshListView, Gravity.BOTTOM, 0, 0);
                        mWaitPop.getContentView().measure(0, 0);
                        height = mWaitPop.getContentView().getMeasuredHeight();
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                        view_dd.setLayoutParams(params);
                        view_dd.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view_dd.setVisibility(View.VISIBLE);
                            }
                        }, 450);

                    } else {
                        mWaitPop.dismiss();
                    }

                } else if (mType == ListType.欠货单) {
                    if (isEdit()) {//转为编辑状态
                        if (mArrearPop == null) mArrearPop = new ArrearsPop(this);
                        mArrearPop.showAtLocation(mRefreshListView, Gravity.BOTTOM, 0, 0);
                        mArrearPop.getContentView().measure(0, 0);
                        height = mArrearPop.getContentView().getMeasuredHeight();
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                        view_dd.setLayoutParams(params);
                        view_dd.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view_dd.setVisibility(View.VISIBLE);
                            }
                        }, 450);
                    } else {
                        mArrearPop.dismiss();
                    }
                } else if (mType == ListType.入库单) {
                    if (isEdit()) {//转为编辑状态
                        if (mNoteStorePop == null) mNoteStorePop = new NoteStorePop(this);
                        mNoteStorePop.showAtLocation(mRefreshListView, Gravity.BOTTOM, 0, 0);
                        mNoteStorePop.getContentView().measure(0, 0);
                        height = mNoteStorePop.getContentView().getMeasuredHeight();
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                        view_dd.setLayoutParams(params);
                        view_dd.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view_dd.setVisibility(View.VISIBLE);
                            }
                        }, 450);
                    } else {
                        mNoteStorePop.dismiss();
                    }
                }
                mList.clear();//每次重置选择状态
                adapter.notifyDataSetChanged();

                break;
            case R.id.tv_status:
                //筛选
                setStatus();
                break;
            case R.id.tv_sort:
                //排序
                setSort();
                break;

        }
    }

    private void setSort() {
//        sortList.clear();
//        SelBean selBean = new SelBean(-1, "默认");
//        SelBean selBean1 = new SelBean(1, "楼层档口归类");
//        SelBean selBean2 = new SelBean(2, "单价高到低");
//        SelBean selBean3 = new SelBean(3, "单价低到高");
//        SelBean selBean4 = new SelBean(4, "数量高到低");
//        SelBean selBean5 = new SelBean(5, "数量低到高");
//        SelBean selBean6 = new SelBean(6, "总金额高到低");
//        SelBean selBean7 = new SelBean(7, "总金额低到高");
//        sortList.add(selBean);
//        sortList.add(selBean1);
//        sortList.add(selBean2);
//        sortList.add(selBean3);
//        sortList.add(selBean4);
//        sortList.add(selBean5);
//        sortList.add(selBean6);
//        sortList.add(selBean7);
//        ActionSheetDialog dialog = new ActionSheetDialog(CommonListActivity.this)
//                .builder()
//                .setTitle("订单排序")
//                .setCancelable(true)
//                .setCanceledOnTouchOutside(true);
//        for (SelBean sbean : sortList) {
//            dialog.addSheetItem(sbean.getContent(), ActionSheetDialog.SheetItemColor.Blue
//                    , new ActionSheetDialog.OnSheetItemClickListener() {
//                        @Override
//                        public void onClick(int which) {
//                            //填写事件
//                            sort = sortList.get(which - 1).getId();
//                            if (sort == -1) {
//                                tv_sort.setText("排序");
//                            } else {
//                                tv_sort.setText(sortList.get(which - 1).getContent());
//
//                            }
//                            bindItemData(true);
//                        }
//                    });
//        }
//        dialog.show();
        SortListDialog.getInstance(this).setHasSelId(sort).setType(sType, SortUtls.TYPE_SORT_2).setPositive(this).showDialog();
    }

    private void setStatus() {
//        statusList.clear();
//        SelBean bean = new SelBean(-1, "全部");
//        SelBean bean1 = new SelBean(1, "已截单");
//        SelBean bean2 = new SelBean(2, "拼货中");
//        SelBean bean3 = new SelBean(3, "已截止入库");
//        SelBean bean11 = new SelBean(11, "未截止入库");
//        SelBean bean12 = new SelBean(12, "昨天截止入库");
//        SelBean bean4 = new SelBean(4, "今天截止入库");
//        SelBean bean5 = new SelBean(5, "明天截止入库");
//        SelBean bean6 = new SelBean(6, "后天截止入库");
//        SelBean bean7 = new SelBean(7, "未关联");
//        SelBean bean8 = new SelBean(8, "已关联");
//        SelBean bean9 = new SelBean(9, "已退款");
//        SelBean bean10 = new SelBean(10, "未退款");
//        SelBean bean13 = new SelBean(13, "买手库");
//        SelBean bean14 = new SelBean(14, "公司库");
//        SelBean ybean = new SelBean(13, "有库存");
//        switch (type) {
//            case 1:
//                //mType = ListType.待开单;
//                statusList.add(bean);
//                statusList.add(ybean);
//                statusList.add(bean1);
//                statusList.add(bean2);
//                statusList.add(bean3);
//                statusList.add(bean11);
//                statusList.add(bean12);
//                statusList.add(bean4);
//                statusList.add(bean5);
//                statusList.add(bean6);
//                break;
//            case 2:
//                //mType = ListType.已开单;
//                statusList.add(bean);
//                statusList.add(bean13);
//                statusList.add(bean14);
//                statusList.add(bean3);
//                statusList.add(bean11);
//                statusList.add(bean12);
//                statusList.add(bean4);
//                statusList.add(bean5);
//                statusList.add(bean6);
//                statusList.add(bean7);
//                statusList.add(bean8);
//                break;
//            case 3:
//                //mType = ListType.入库单;
//                statusList.add(bean);
//                statusList.add(bean13);
//                statusList.add(bean14);
//                statusList.add(bean3);
//                statusList.add(bean4);
//                statusList.add(bean7);
//                statusList.add(bean8);
//                break;
//            case 4:
//                // mType = ListType.欠货单;
//                statusList.add(bean);
//                statusList.add(bean13);
//                statusList.add(bean14);
//                statusList.add(bean3);
//                statusList.add(bean11);
//                statusList.add(bean12);
//                statusList.add(bean4);
//                statusList.add(bean5);
//                statusList.add(bean6);
//                break;
//            case 5:
//                // mType = ListType.退款单;
//                statusList.add(bean);
//                statusList.add(bean13);
//                statusList.add(bean14);
//                statusList.add(bean9);
//                statusList.add(bean10);
//                break;
//            default:
//                //  mType = ListType.待开单;
//                statusList.add(bean);
//                statusList.add(ybean);
//                statusList.add(bean1);
//                statusList.add(bean2);
//                statusList.add(bean3);
//                statusList.add(bean11);
//                statusList.add(bean12);
//                statusList.add(bean4);
//                statusList.add(bean5);
//                statusList.add(bean6);
//                break;
//        }
//        ActionSheetDialog dialog = new ActionSheetDialog(CommonListActivity.this)
//                .builder()
//                .setTitle("筛选订单")
//                .setCancelable(true)
//                .setCanceledOnTouchOutside(true);
//        for (SelBean xbean : statusList) {
//            dialog.addSheetItem(xbean.getContent(), ActionSheetDialog.SheetItemColor.Blue
//                    , new ActionSheetDialog.OnSheetItemClickListener() {
//                        @Override
//                        public void onClick(int which) {
//                            //填写事件
//                            status = statusList.get(which - 1).getId();
//                            if (status == -1) {
//                                tv_status.setText("筛选");
//                            } else {
//                                tv_status.setText(statusList.get(which - 1).getContent());
//                            }
//                            bindItemData(true);
//                        }
//                    });
//        }
//        dialog.show();
        SortListDialog.getInstance(this).setHasSelId(status).setType(sType, SortUtls.TYPE_SORT_1).setPositive(this).showDialog();
    }

    // 初始化数据
    private void initItemAdapter() {
        if (itemList == null)
            itemList = new ArrayList<ShopItemListModel>();

        adapter = new CommonListAdapter(vThis, itemList, mType);
        mRefreshListView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class LoadListDataTask extends AsyncTask<Boolean, Void, Object> {
        private boolean mIsRefresh = false;

        public LoadListDataTask(boolean isRefresh) {
            mIsRefresh = isRefresh;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start(getString(R.string.items_loadData_loading));
        }

        @Override
        protected void onPostExecute(Object result) {
            if (isFinishing()) {
                return;
            }
            mloadingDialog.stop();
            mRefreshListView.onRefreshComplete();
            if (result instanceof String) {
                ViewHub.showLongToast(vThis, (String) result);

                if (result.toString().startsWith("401") || result.toString().startsWith("not_registered")) {
                    ApiHelper.checkResult(result, vThis);
                }
            } else {
                @SuppressWarnings("unchecked")
                List<ShopItemListModel> list = (List<ShopItemListModel>) result;
                if (mIsRefresh) {
                    itemList.clear();
                    itemList = list;
                } else {
                    itemList.addAll(list);
                }
                adapter.mList = itemList;
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            adapter.mList = itemList;
            adapter.notifyDataSetChanged();
        }

        @Override
        protected Object doInBackground(Boolean... params) {
            File cacheFile = null;
//            if (mPageIndex == 1) {
//                cacheFile = CacheDirUtil.getCache(getApplicationContext(), "Listpage_list_" + mType);
//                searchKey = "";
//            }
            try {
                List<ShopItemListModel> result = BuyerToolAPI.getShopList(vThis, mType, searchKey, stallKey,mPageIndex, mPageSize, cacheFile, params[0]
                        , status, sort);
                Log.e("doInBackground+result", "" + result);
                return result;
            } catch (Exception ex) {
                Log.e(TAG, "获取列表发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }
    }

    /**
     * /** 绑定款式列表
     */
    private void bindItemData(boolean isRefresh) {
        if (isRefresh) {
            mPageIndex = 1;
            LoadListDataTask loadListDataTask = new LoadListDataTask(isRefresh);
            loadListDataTask.execute(false);
        } else {
            mPageIndex++;
            LoadListDataTask loadListDataTask = new LoadListDataTask(isRefresh);
            loadListDataTask.execute(false);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    //获取是否正在编辑状态
    public boolean isEdit() {
        return isEdit;
    }

    public PullToRefreshListView getRefreshListView() {
        return mRefreshListView;
    }

    /**
     * 选择关联的采购单  返回数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_ORDERED:
                if (resultCode == PurchaseListActivity.SELECT_ORDERED) {
                    selectPurchase = SpManager.getSELECT_PURCHASE_DATA(this);
                    if (mNotePop != null && mNotePop.isShowing()) {
                        mNotePop.updateNote(selectPurchase);
                    }
                    if (mNoteStorePop != null && mNoteStorePop.isShowing()) {
                        mNoteStorePop.updateNote(selectPurchase);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }

    public PurchaseModel getSelectPurchase() {
        return selectPurchase;
    }

    private PurchaseModel selectPurchase;//当前选中的采购单
}
