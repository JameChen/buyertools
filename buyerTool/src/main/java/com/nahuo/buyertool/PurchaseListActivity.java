package com.nahuo.buyertool;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.adapter.PurchaseListAdapter;
import com.nahuo.buyertool.api.ApiHelper;
import com.nahuo.buyertool.api.BuyerToolAPI;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.eventbus.BusEvent;
import com.nahuo.buyertool.eventbus.EventBusId;
import com.nahuo.buyertool.model.PurchaseModel;
import com.nahuo.buyertool.model.StallsSearchModel;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 选择采购单页面
 */
public class PurchaseListActivity extends BaseActivity implements View.OnClickListener, PullToRefreshListView.OnLoadMoreListener, PullToRefreshListView.OnRefreshListener {

    private static final String TAG = "PurchaseListActivity";
    public static final int SELECT_ORDERED = 1;
    public static final String SELECT_ORDER_DATA = "PurchaseListActivity.selectOrder.data";
    private PurchaseListActivity vThis = this;
    private LoadingDialog mloadingDialog;
    private int mPageIndex = 1;
    private int mPageSize = 20;
    private PurchaseListAdapter adapter;
    private List<StallsSearchModel> searchDatas = null;
    private List<PurchaseModel> itemList = null;
    private PurchaseModel selectData;
    private PullToRefreshListView mRefreshListView;
    private EventBus mEventBus = EventBus.getDefault();

    private StallsSearchModel selectMarket;
    private StallsSearchModel.FloorModel selectFloor;
    private StallsSearchModel.StallsModel selectDK;
    private String selectTime = "";
    private TextView tvMarket, tvFloor, tvDK, tvTime;

    //采购单增加关键字筛选
    private String mKeyWord = "";
    private EditText keyEditText;

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
        setContentView(R.layout.activity_purchase_list);
        mEventBus.registerSticky(this);

        selectData = SpManager.getSELECT_PURCHASE_DATA(vThis);
        mloadingDialog = new LoadingDialog(vThis);
        initView();
        bindItemData(true);
        //读取搜索数据
        new LoadStallsTask().execute((Void) null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
        adapter = null;
        if (itemList != null) {
            itemList.clear();
            itemList = null;
        }
        if (mloadingDialog != null) {
            if (mloadingDialog.isShowing())
                mloadingDialog.dismiss();
            mloadingDialog = null;
        }
    }

    public void onEventMainThread(BusEvent event) {
        String keyword = event.data.toString();
        switch (event.id) {
            case EventBusId.COMMON_LIST_RELOAD:
                bindItemData(true);
                break;
        }
    }

    private void initView() {

        Button backBtn = (Button) findViewById(R.id.titlebar_btnLeft);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);

        TextView mIvOK = (TextView) findViewById(R.id.titlebar_btnRight);
        mIvOK.setOnClickListener(this);
        mIvOK.setVisibility(View.VISIBLE);
        mIvOK.setText("确定");

        ((TextView) findViewById(R.id.titlebar_tvTitle)).setText("选择采购单");

        //搜索层
        keyEditText = (EditText) findViewById(R.id.purchase_search_keyword);
        tvMarket = (TextView) findViewById(R.id.purchase_search_market);
        findViewById(R.id.purchase_search_market).setOnClickListener(this);
        tvFloor = (TextView) findViewById(R.id.purchase_search_floor);
        findViewById(R.id.purchase_search_floor).setOnClickListener(this);
        tvDK = (TextView) findViewById(R.id.purchase_search_stalls);
        findViewById(R.id.purchase_search_stalls).setOnClickListener(this);
        tvTime = (TextView) findViewById(R.id.purchase_search_time);
        findViewById(R.id.purchase_search_time).setOnClickListener(this);
        findViewById(R.id.purchase_search_btn).setOnClickListener(this);

        //列表
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
                if (position == 0) {
                    position = 1;
                }
                PurchaseModel model = adapter.getItem(position - 1);

                selectData = model;
                SpManager.setSELECT_PURCHASE_DATA(vThis, model);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_btnLeft:
                finish();
                break;
            case R.id.titlebar_btnRight:
                if (selectData == null) {
                    ViewHub.showLongToast(vThis, "请选择一个采购单");
                    return;
                }
                Intent it = new Intent();
                it.putExtra(SELECT_ORDER_DATA, selectData);
                setResult(SELECT_ORDERED, it);
                finish();
                break;
            case R.id.purchase_search_btn:
                bindItemData(true);
                break;
            case R.id.purchase_search_market: {
                final String items[] = new String[searchDatas.size()];
                int selectIndex = 0;
                for (int i = 0; i < searchDatas.size(); i++) {
                    items[i] = searchDatas.get(i).getName();
                    if (selectMarket != null && searchDatas.get(i).getID() == selectMarket.getID()) {
                        selectIndex = i;
                    }
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("选择市场");
                builder.setSingleChoiceItems(items, selectIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectMarket = searchDatas.get(which);
                        selectFloor = null;
                        selectDK = null;
                        reflushSearchData();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (selectMarket == null) {
                            if (searchDatas.size() > 0) {
                                selectMarket = searchDatas.get(0);
                            }
                        }
                        dialog.dismiss();
                        reflushSearchData();
                    }
                });
                builder.create().show();
                break;
            }
            case R.id.purchase_search_floor: {
                if (selectMarket != null) {
                    final String items[] = new String[selectMarket.getFloorList().size()];
                    int selectIndex = 0;
                    for (int i = 0; i < selectMarket.getFloorList().size(); i++) {
                        items[i] = selectMarket.getFloorList().get(i).getName();
                        if (selectFloor != null && selectMarket.getFloorList().get(i).getID() == selectFloor.getID()) {
                            selectIndex = i;
                        }
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("选择楼层");
                    builder.setSingleChoiceItems(items, selectIndex, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectFloor = selectMarket.getFloorList().get(which);
                            selectDK = null;
                            reflushSearchData();
                        }
                    });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (selectFloor == null) {
                                if (selectMarket.getFloorList().size() > 0) {
                                    selectFloor = selectMarket.getFloorList().get(0);
                                }
                            }
                            reflushSearchData();
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
                break;
            }
            case R.id.purchase_search_stalls: {
                if (selectFloor != null) {
                    final String items[] = new String[selectFloor.getStallsList().size()];
                    int selectIndex = 0;
                    for (int i = 0; i < selectFloor.getStallsList().size(); i++) {
                        items[i] = selectFloor.getStallsList().get(i).getName();
                        if (selectDK != null && selectFloor.getStallsList().get(i).getID() == selectDK.getID()) {
                            selectIndex = i;
                        }
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("选择档口");
                    builder.setSingleChoiceItems(items, selectIndex, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectDK = selectFloor.getStallsList().get(which);
                            reflushSearchData();
                        }
                    });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (selectDK == null) {
                                if (selectFloor.getStallsList().size() > 0) {
                                    selectDK = selectFloor.getStallsList().get(0);
                                }
                            }
                            dialog.dismiss();
                            reflushSearchData();
                        }
                    });
                    builder.create().show();
                }
                break;
            }
            case R.id.purchase_search_time: {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(vThis, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        selectTime = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        reflushSearchData();
                    }
                }, calendar.get(Calendar.YEAR), calendar
                        .get(Calendar.MONTH), calendar
                        .get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
            }
        }
    }

    private void reflushSearchData() {
        if (selectMarket != null) {
            tvMarket.setText(selectMarket.getName());
        }
        if (selectFloor != null) {
            tvFloor.setText(selectFloor.getName());
        }
        if (selectDK != null) {
            tvDK.setText(selectDK.getName());
        }
        if (selectTime.length() > 0) {
            tvTime.setText(selectTime);
        }
    }

    // 初始化数据
    private void initItemAdapter() {
        if (itemList == null)
            itemList = new ArrayList<PurchaseModel>();

        adapter = new PurchaseListAdapter(vThis, itemList);
        mRefreshListView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class LoadListDataTask extends AsyncTask<Void, Void, Object> {
        private boolean mIsRefresh = false;

        public LoadListDataTask(boolean isRefresh) {
            mIsRefresh = isRefresh;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start(getString(R.string.items_loadData_loading));
            mKeyWord = keyEditText.getText().toString().replaceAll(" ", "");//获取关键字数据
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
                List<PurchaseModel> list = (List<PurchaseModel>) result;
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
        protected Object doInBackground(Void... params) {
            try {
                List<PurchaseModel> result = BuyerToolAPI.getPurchaseList(vThis,
                        selectTime,
                        selectMarket == null ? -1 : selectMarket.getID(),
                        selectFloor == null ? -1 : selectFloor.getID(),
                        selectDK == null ? -1 : selectDK.getID(),
                        mPageIndex, mPageSize, mKeyWord);
                return result;
            } catch (Exception ex) {
                Log.e(TAG, "获取列表发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
        }
    }

    public class LoadStallsTask extends AsyncTask<Void, Void, String> {

        public LoadStallsTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start(getString(R.string.items_loadData_loading));
        }

        @Override
        protected void onPostExecute(String a) {
            if (isFinishing()) {
                return;
            }
            mloadingDialog.stop();
            mRefreshListView.onRefreshComplete();
//            loadedSearchStalls();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                searchDatas = BuyerToolAPI.getStallsList(vThis);
            } catch (Exception ex) {
                Log.e(TAG, "获取档口列表发生异常");
                ex.printStackTrace();
            }
            return "";
        }
    }

    /**
     * /** 绑定款式列表
     */
    private void bindItemData(boolean isRefresh) {
        if (isRefresh) {
            mPageIndex = 1;
        } else {
            mPageIndex++;
        }
        LoadListDataTask loadListDataTask = new LoadListDataTask(isRefresh);
        loadListDataTask.execute((Void) null);

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


}
