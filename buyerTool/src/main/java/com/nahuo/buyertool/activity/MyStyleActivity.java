package com.nahuo.buyertool.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.MyStyleBean;
import com.nahuo.buyertool.Bean.NotiBean;
import com.nahuo.buyertool.Bean.SelBean;
import com.nahuo.buyertool.Bean.SortBean;
import com.nahuo.buyertool.Bean.StyleBean;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.ItemPreviewActivity;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.adapter.MyStyleAdapter;
import com.nahuo.buyertool.api.ApiHelper;
import com.nahuo.buyertool.api.BuyOnlineAPI;
import com.nahuo.buyertool.api.ShopSetAPI;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.dialog.CopyDialog;
import com.nahuo.buyertool.dialog.EditDialog;
import com.nahuo.buyertool.dialog.MyStyleDialog;
import com.nahuo.buyertool.dialog.SortListDialog;
import com.nahuo.buyertool.eventbus.BusEvent;
import com.nahuo.buyertool.eventbus.EventBusId;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.utils.SortUtls;
import com.nahuo.library.controls.AutoCompleteTextViewEx;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.controls.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

import static com.nahuo.buyertool.activity.MyStyleActivity.Step.NORIFI_STEP;
import static com.nahuo.buyertool.activity.MyStyleActivity.Style.ARRAIGNMENT_TYPE;
import static com.nahuo.buyertool.activity.MyStyleActivity.Style.DOWN_SHELVE_TYPE;
import static com.nahuo.buyertool.activity.MyStyleActivity.Style.EDIT_ALL_TYPE;
import static com.nahuo.buyertool.activity.MyStyleActivity.Style.HIDE_TYPE;
import static com.nahuo.buyertool.activity.MyStyleActivity.Style.ON_SHELVE_TYPE;
import static com.nahuo.buyertool.activity.MyStyleActivity.Style.SHOW_TYPE;

/**
 * 我的款式
 *
 * @author James Chen
 * @create time in 2017/7/11 16:37
 */
public class MyStyleActivity extends FragmentActivity implements SortListDialog.PopDialogListener, View.OnClickListener,
        MyStyleDialog.PopDialogListener, EditDialog.PopDialogListener, CopyDialog.PopDialogListener, PullToRefreshListView.OnLoadMoreListener, PullToRefreshListView.OnRefreshListener {
    CheckBox checkbox_all;
    TextView tv_on_shelves, tv_change, tv_search, tv_notif, tv_if_shelves, tv_if_show, tv_show, tv_hide, tv_copy, tv_off_shelf, tv_progress, tv_upload;
    private LoadingDialog mloadingDialog;
    private PullToRefreshListView mRefreshListView;
    int mPageIndex;
    private static final String TAG = "MyStyleActivity";
    MyStyleActivity vThis;
    String keyword = "";
    private int mPageSize = 10;
    MyStyleAdapter adapter;
    private List<MyStyleBean> itemList = new ArrayList<>();
    private String url = "";
    private AutoCompleteTextViewEx mEtSearch;
    private NotiBean bean = null;
    private EventBus mEventBus = EventBus.getDefault();
    private int Total;

    @Override
    public void onGetSortDialogButtonClick(SortBean.ListBean bean, String type, int Sort_type) {
        if (bean != null) {
            switch (Sort_type) {
                case SortUtls.TYPE_SORT_1:
                    statuid = bean.getValue();
                    tv_if_shelves.setText(bean.getText());
                    break;
                case SortUtls.TYPE_SORT_2:
                    isHide = bean.getValue();
                    tv_if_show.setText(bean.getText());
                    break;
            }
            bindItemData(true);
        }
    }


    public enum Step {
        NORIFI_STEP, ALL_OFF_SHELF_ITEMS_STEP
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_style);
        initView();
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.id) {
            case EventBusId.REFERSH_MYSTYLE_FINISH:
                try {
                    if (event.data != null) {
                        UploadBean bean = (UploadBean) event.data;
                        if (adapter != null) {
                            adapter.setItemDataNotify(bean);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //   bindItemData(true);
//                if (mRefreshListView!=null)
//                mRefreshListView.setSelection(0);
                break;
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

    public void getDataRefesh() {
        onRefresh();
        if (mRefreshListView != null)
            mRefreshListView.setSelection(0);
    }

    private class Task extends AsyncTask<Object, Void, Object> {
        private Step mStep;

        public Task(Step step) {
            mStep = step;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                switch (mStep) {
                    case NORIFI_STEP:
                        NotiBean bean = ShopSetAPI.getNotifiInfo(MyStyleActivity.this);
                        return bean;

                }

            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(getApplicationContext(), ((String) result).replace("error:", ""));
            } else {
                switch (mStep) {
                    case NORIFI_STEP:
                        bean = (NotiBean) result;
                        if (bean != null) {
                            url = bean.getUrl();
                            if (!TextUtils.isEmpty(bean.getText())) {
                                tv_notif.setVisibility(View.VISIBLE);
                                tv_notif.setText(bean.getText());
                            } else {
                                tv_notif.setVisibility(View.GONE);

                            }
                        } else {
                            tv_notif.setVisibility(View.GONE);
                        }
                        break;
                }
            }
        }
    }

    public class EditItemsTask extends AsyncTask<Object, Void, Object> {
        List<MyStyleBean> list;
        String oldStr = "";
        String newStr = "";
        String waitDays = "", groupDealCount = "";
        String newQsFlag="";
        public EditItemsTask(List<MyStyleBean> list, String oldStr, String newStr, String waitDays, String groupDealCount,String newQsFlag) {
            this.list = list;
            this.oldStr = oldStr;
            this.newStr = newStr;
            this.waitDays = waitDays;
            this.groupDealCount = groupDealCount;
            this.newQsFlag=newQsFlag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start("修改中...");
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                if (ListUtils.isEmpty(list)) {
                    return "error:商品ID为空";
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    int id = list.get(i).getItemID();
                    sb.append(id + "");
                    if (i < list.size() - 1)
                        sb.append(",");
                }
                ShopSetAPI.updateMyItemBatch(vThis, sb.toString(), oldStr, newStr, waitDays, groupDealCount,newQsFlag);

            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            hideDialog();
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(getApplicationContext(), ((String) result).replace("error:", ""));
            } else {
                ViewHub.showShortToast(vThis, "修改成功");
                getDataRefesh();
            }
        }
    }

    public class CopyItemsTask extends AsyncTask<Object, Void, Object> {
        List<MyStyleBean> list;
        String oldStr = "";
        String newStr = "";

        public CopyItemsTask(List<MyStyleBean> list, String oldStr, String newStr) {
            this.list = list;
            this.oldStr = oldStr;
            this.newStr = newStr;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start("复制中...");
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                if (ListUtils.isEmpty(list)) {
                    return "error:商品ID为空";
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    int id = list.get(i).getItemID();
                    sb.append(id + "");
                    if (i < list.size() - 1)
                        sb.append(",");
                }
                ShopSetAPI.copyItems(vThis, sb.toString(), oldStr, newStr);

            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            hideDialog();
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(getApplicationContext(), ((String) result).replace("error:", ""));
            } else {
                ViewHub.showShortToast(vThis, "复制成功");
                getDataRefesh();
            }
        }
    }

    public class ArraignmentItemsTask extends AsyncTask<Object, Void, Object> {
        List<MyStyleBean> list;
        Style action;

        public ArraignmentItemsTask(List<MyStyleBean> list, Style action) {
            this.list = list;
            this.action = action;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (action == ARRAIGNMENT_TYPE) {
                mloadingDialog.start("提审中...");
            }

        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                if (ListUtils.isEmpty(list)) {
                    return "error:商品ID为空";
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    int id = list.get(i).getItemID();
                    sb.append(id + "");
                    if (i < list.size() - 1)
                        sb.append(",");
                }
                if (action == ARRAIGNMENT_TYPE) {
                    ShopSetAPI.arraignmentOnItems(vThis, sb.toString());
                }


            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            hideDialog();
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(getApplicationContext(), ((String) result).replace("error:", ""));
            } else {
                if (adapter != null) {
                    if (action == ARRAIGNMENT_TYPE) {
                        //提审
                        adapter.setMyStyleMoreArraignment(cList);
                        ViewHub.showLongToast(MyStyleActivity.this, "已经提审成功，请手动刷新");
                    }
                }

            }
        }
    }

    public class OnOrOffShelfItemsTask extends AsyncTask<Object, Void, Object> {
        List<MyStyleBean> list;
        Style action;

        public OnOrOffShelfItemsTask(List<MyStyleBean> list, Style action) {
            this.list = list;
            this.action = action;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (action == ON_SHELVE_TYPE) {
                mloadingDialog.start("上架中...");
            } else {
                mloadingDialog.start("下架中...");
            }

        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                if (ListUtils.isEmpty(list)) {
                    return "error:商品ID为空";
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    int id = list.get(i).getItemID();
                    sb.append(id + "");
                    if (i < list.size() - 1)
                        sb.append(",");
                }
                if (action == ON_SHELVE_TYPE) {
                    ShopSetAPI.onShelfItems(vThis, sb.toString());
                } else {
                    ShopSetAPI.offShelfItems(vThis, sb.toString());
                }


            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            hideDialog();
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(getApplicationContext(), ((String) result).replace("error:", ""));
            } else {
                if (adapter != null) {
                    if (action == ON_SHELVE_TYPE) {
                        //上架
                        adapter.setMyStyleMoreOnShelf(cList);
                    } else {
                        //下架
                        adapter.setMyStyleMoreOffShelf(cList);
                    }
                }

            }
        }
    }

    public class UpdateHideTask extends AsyncTask<Object, Void, Object> {
        boolean is_hide;
        List<MyStyleBean> list;

        public UpdateHideTask(boolean is_hide, List<MyStyleBean> list) {
            this.is_hide = is_hide;
            this.list = list;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start("设置中...");
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                if (ListUtils.isEmpty(list)) {
                    return "error:商品ID为空";
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    int id = list.get(i).getItemID();
                    sb.append(id + "");
                    if (i < list.size() - 1)
                        sb.append(",");
                }
                ShopSetAPI.updateItemHide(vThis, sb.toString(), is_hide);

            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            hideDialog();
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(getApplicationContext(), ((String) result).replace("error:", ""));
            } else {
                if (adapter != null) {
                    adapter.setMyStyleAllHide(cList, is_hide);
                    if (is_hide) {
                        ViewHub.showShortToast(MyStyleActivity.this, "设置隐藏成功");
                    } else {
                        ViewHub.showShortToast(MyStyleActivity.this, "设置显示成功");
                    }
                }

            }
        }
    }

    protected boolean isDialogShowing() {
        return (mloadingDialog != null && mloadingDialog.isShowing());
    }

    protected void hideDialog() {
        if (isDialogShowing()) {
            mloadingDialog.stop();
        }
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
                if (Total > 0) {
                    if (checkbox_all != null)
                        checkbox_all.setText(Html.fromHtml("<font color=\"#6AB5EA\">全部</font><br/>" + "<font color=\"#C60A1E\">" + Total + "款</font>"));
                } else {
                    if (checkbox_all != null)
                        checkbox_all.setText("全部");
                }
                List<MyStyleBean> list = (List<MyStyleBean>) result;
                if (mIsRefresh) {
                    itemList.clear();
                    itemList = list;
                } else {
                    itemList.addAll(list);
                }
                if (!ListUtils.isEmpty(itemList)) {
                    if (checkbox_all != null)
                        checkbox_all.setChecked(false);
                }
                adapter.setData(itemList);
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
//            adapter.mList = itemList;
//            adapter.notifyDataSetChanged();
        }

        @Override
        protected Object doInBackground(Boolean... params) {
            try {
                StyleBean bean = BuyOnlineAPI.getInstance().getMyStyleShopitems(keyword, mPageIndex, mPageSize, statuid + "", isHide + "", PublicData.getCookie(vThis));
                List<MyStyleBean> list = null;
                if (bean != null) {
                    Total = bean.getTotal();
                    list = bean.getDatas();
                }
                //Log.e("doInBackground+result", "" + result);
                return list;
            } catch (Exception ex) {
                Log.e(TAG, "获取列表发生异常");
                ex.printStackTrace();
                return ex.getMessage() == null ? "未知异常" : ex.getMessage();
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
    }

    private void initView() {
        vThis = this;
        mEventBus.registerSticky(this);
        mloadingDialog = new LoadingDialog(this);
        mRefreshListView = (PullToRefreshListView) findViewById(R.id.listview);
        mRefreshListView.setCanLoadMore(true);
        mRefreshListView.setCanRefresh(true);
        mRefreshListView.setMoveToFirstItemAfterRefresh(true);
        mRefreshListView.setOnRefreshListener(this);
        mRefreshListView.setOnLoadListener(this);
        adapter = new MyStyleAdapter(this);
        mRefreshListView.setAdapter(adapter);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        checkbox_all = (CheckBox) findViewById(R.id.checkbox_all);
        checkbox_all.setOnClickListener(this);
        tv_on_shelves = (TextView) findViewById(R.id.tv_on_shelves);
        tv_change = (TextView) findViewById(R.id.tv_change);
        tv_search = (TextView) findViewById(R.id.tv_search);
        tv_if_shelves = (TextView) findViewById(R.id.tv_if_shelves);
        tv_if_show = (TextView) findViewById(R.id.tv_if_show);
        tv_notif = (TextView) findViewById(R.id.tv_notif);
        tv_show = (TextView) findViewById(R.id.tv_show);
        tv_hide = (TextView) findViewById(R.id.tv_hide);
        tv_copy = (TextView) findViewById(R.id.tv_copy);
        tv_off_shelf = (TextView) findViewById(R.id.tv_off_shelf);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        tv_upload = (TextView) findViewById(R.id.tv_upload);
        tv_change.setOnClickListener(this);
        tv_on_shelves.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        tv_if_shelves.setOnClickListener(this);
        tv_if_show.setOnClickListener(this);
        tv_notif.setOnClickListener(this);
        tv_show.setOnClickListener(this);
        tv_hide.setOnClickListener(this);
        tv_copy.setOnClickListener(this);
        tv_off_shelf.setOnClickListener(this);
        tv_progress.setOnClickListener(this);
        tv_upload.setOnClickListener(this);
        mEtSearch = (AutoCompleteTextViewEx) findViewById(R.id.et_search);
        mEtSearch.setOnSearchLogDeleteListener(new AutoCompleteTextViewEx.OnSearchLogDeleteListener() {
            @Override
            public void onSearchLogDeleted(String text) {
                String newChar = SpManager.deleteSearchVendorsHistories(getApplicationContext(), text);
                mEtSearch.populateData(newChar, ",");
                mEtSearch.getFilter().filter(mEtSearch.getText());
            }
        });

        mEtSearch.populateData(SpManager.getSearchVendorsHistories(getApplicationContext()), ",");
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//actionSearch搜索，对应常量EditorInfo.IME_ACTION_SEARCH效果
                    ViewHub.hideKeyboard(MyStyleActivity.this);
                    keyword = mEtSearch.getText().toString();
                    if (TextUtils.isEmpty(keyword)) {
                        ViewHub.setEditError(mEtSearch, "请输入搜索内容");
                        return false;
                    }
                    onRefresh();
                }
                return false;
            }
        });
        statuid = 1;
        tv_if_shelves.setText("已上架");
        List<SortBean> list = SortUtls.getListFilter(vThis);
        if (!ListUtils.isEmpty(list)) {
            for (SortBean bean : list) {
                if (SortUtls.TYPE_ITEM.equals(bean.getType())) {
//                    if (!ListUtils.isEmpty(bean.getListfilter())) {
//                        tv_if_shelves.setText(bean.getListfilter().get(0).getText());
//                    }
                    if (!ListUtils.isEmpty(bean.getListSort())) {
                        tv_if_show.setText(bean.getListSort().get(0).getText());
                    }
                    break;
                }
            }
        }
        new Task(NORIFI_STEP).execute();
        bindItemData(true);

    }

    List<SelBean> statusList = new ArrayList<>();
    List<SelBean> hideList = new ArrayList<>();
    List<MyStyleBean> cList = new ArrayList<>();

    public enum Style {
        SHOW_TYPE, HIDE_TYPE, DOWN_SHELVE_TYPE, COPY_TYPE, ON_SHELVE_TYPE, EDIT_ALL_TYPE, ARRAIGNMENT_TYPE

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkbox_all:
                adapter.setAllCheck(checkbox_all);
                break;
            case R.id.tv_search:
                ViewHub.hideKeyboard(MyStyleActivity.this);
                keyword = mEtSearch.getText().toString();
                if (TextUtils.isEmpty(keyword)) {
                    ViewHub.setEditError(mEtSearch, "请输入搜索内容");
                    return;
                }
                onRefresh();
                break;
            case R.id.tv_if_shelves:
                setStatus();
                break;
            case R.id.tv_if_show:
                setIsHide();
                break;
            case R.id.tv_notif:
                if (bean != null) {
                    url = bean.getUrl();
                    if (!TextUtils.isEmpty(url)) {
                        Intent it = new Intent(getApplicationContext(), ItemPreviewActivity.class);
                        it.putExtra("url", bean.getUrl());
                        it.putExtra("name", "详情");
                        startActivity(it);
                    }
                }

                break;
            case R.id.tv_change:
                //修改
                setPopCheck(EDIT_ALL_TYPE);
                break;
            case R.id.tv_on_shelves:
                //上架
                setPopCheck(ON_SHELVE_TYPE);
                break;
            case R.id.tv_show:
                setPopCheck(SHOW_TYPE);
                break;
            case R.id.tv_hide:
                setPopCheck(HIDE_TYPE);
                break;
            case R.id.tv_off_shelf:
                setPopCheck(DOWN_SHELVE_TYPE);
                break;
            case R.id.tv_copy:
                // setPopCheck(COPY_TYPE);
                setPopCheck(ARRAIGNMENT_TYPE);
                break;
            case R.id.tv_progress:
                startActivity(new Intent(vThis, UploadProgressActivity.class));
                break;
            case R.id.tv_upload:
                Intent vendorIntent = new Intent(vThis, UploadItemActivity.class);
                vendorIntent.putExtra(UploadItemActivity.EXTRA_UPLOAD_TYPE, UploadItemActivity.UPLOAD_TYPE);
                startActivity(vendorIntent);
                break;
        }
    }

    @Override
    public void onCopyDialogButtonClick(MyStyleBean bean, String oldStr, String newStr) {
        new CopyItemsTask(cList, oldStr, newStr).execute();
    }

    @Override
    public void onEditDialogButtonClick(MyStyleBean bean, String oldStr, String newStr, String waitDays, String groupDealCount,String newQsFlag) {
        new EditItemsTask(cList, oldStr, newStr, waitDays, groupDealCount,newQsFlag).execute();
    }

    @Override
    public void onPopDialogButtonClick(Style action, MyStyleBean bean) {
        switch (action) {
            case SHOW_TYPE:
                //ViewHub.showShortToast(this, "显示");
                new UpdateHideTask(false, cList).execute();
                break;
            case HIDE_TYPE:
                // ViewHub.showShortToast(this, "隐藏");
                new UpdateHideTask(true, cList).execute();
                break;
            case DOWN_SHELVE_TYPE:
                // ViewHub.showShortToast(this, "下架");
                new OnOrOffShelfItemsTask(cList, DOWN_SHELVE_TYPE).execute();
                break;
            case ON_SHELVE_TYPE:
                new OnOrOffShelfItemsTask(cList, ON_SHELVE_TYPE).execute();
                break;
            case ARRAIGNMENT_TYPE:
                new ArraignmentItemsTask(cList, ARRAIGNMENT_TYPE).execute();
                break;
        }
    }


    public void setPopCheck(Style action) {
        cList.clear();
        List<MyStyleBean> bean = adapter.getMyStyleCheck();
        if (ListUtils.isEmpty(bean)) {
            ViewHub.showLongToast(this, "请选择商品");
        } else {
            cList.addAll(bean);
            switch (action) {
                case SHOW_TYPE:
                    MyStyleDialog.getInstance(this).setTitle("是否要显示选中的款式").setAction(SHOW_TYPE).setPositive(this).showDialog();
                    break;
                case HIDE_TYPE:
                    MyStyleDialog.getInstance(this).setTitle("是否要隐藏选中的款式").setAction(HIDE_TYPE).setPositive(this).showDialog();
                    break;
                case DOWN_SHELVE_TYPE:
                    MyStyleDialog.getInstance(this).setTitle("是否要下架选中的款式").setAction(DOWN_SHELVE_TYPE).setPositive(this).showDialog();
                    break;
                case COPY_TYPE:
                    CopyDialog.getInstance(this).setPositive(this).showDialog();
                    break;
                case ARRAIGNMENT_TYPE:
                    //提审
                    MyStyleDialog.getInstance(this).setTitle("是否要提审选中的款式").setAction(ARRAIGNMENT_TYPE).setPositive(this).showDialog();

                    break;
                case ON_SHELVE_TYPE:
                    MyStyleDialog.getInstance(this).setTitle("是否要上架选中的款式").setAction(ON_SHELVE_TYPE).setPositive(this).showDialog();
                    break;
                case EDIT_ALL_TYPE:
                    EditDialog.getInstance(this).setPositive(this).showDialog();
                    break;
            }
        }

    }

    int statuid = -1;
    int isHide = -1;

    private void setStatus() {
//        statusList.clear();
//        SelBean bean = new SelBean(-1, "全部");
//        SelBean bean1 = new SelBean(1, "已上架");
//        SelBean bean2 = new SelBean(2, "已下架");
//        SelBean bean3 = new SelBean(10, "在售中");
//        statusList.add(bean);
//        statusList.add(bean1);
//        statusList.add(bean2);
//        statusList.add(bean3);
//        ActionSheetDialog dialog = new ActionSheetDialog(this)
//                .builder()
//                .setTitle("筛选上/下架状态")
//                .setCancelable(true)
//                .setCanceledOnTouchOutside(true);
//        for (SelBean xbean : statusList) {
//            dialog.addSheetItem(xbean.getContent(), ActionSheetDialog.SheetItemColor.Blue
//                    , new ActionSheetDialog.OnSheetItemClickListener() {
//                        @Override
//                        public void onClick(int which) {
//                            //填写事件
//                            statuid = statusList.get(which - 1).getId();
//                            if (statuid == -1) {
//                                tv_if_shelves.setText("全部");
//                            } else {
//                                tv_if_shelves.setText(statusList.get(which - 1).getContent());
//                            }
//                            bindItemData(true);
//                        }
//                    });
//        }
//        dialog.show();
        SortListDialog.getInstance(this).setHasSelId(statuid).setType(SortUtls.TYPE_ITEM, SortUtls.TYPE_SORT_1).setPositive(this).showDialog();
    }

    private void setIsHide() {
//        hideList.clear();
//        SelBean bean = new SelBean("-1", "全部");
//        SelBean bean1 = new SelBean("2", "显示");
//        SelBean bean2 = new SelBean("1", "隐藏");
//        SelBean bean3 = new SelBean("3", "有库存");
//        SelBean bean4 = new SelBean("4", "草稿");
//        SelBean bean5 = new SelBean("5", "审核中");
//        SelBean bean6 = new SelBean("6", "驳回");
//        SelBean bean7 = new SelBean("7", "已通过");
//        hideList.add(bean);
//        hideList.add(bean1);
//        hideList.add(bean2);
//        hideList.add(bean3);
//        hideList.add(bean4);
//        hideList.add(bean5);
//        hideList.add(bean6);
//        hideList.add(bean7);
//        ActionSheetDialog dialog = new ActionSheetDialog(this)
//                .builder()
//                .setTitle("筛选显示/隐藏状态")
//                .setCancelable(true)
//                .setCanceledOnTouchOutside(true);
//        for (SelBean xbean : hideList) {
//            dialog.addSheetItem(xbean.getContent(), ActionSheetDialog.SheetItemColor.Blue
//                    , new ActionSheetDialog.OnSheetItemClickListener() {
//                        @Override
//                        public void onClick(int which) {
//                            //填写事件
//                            isHide = hideList.get(which - 1).getmId();
//                            tv_if_show.setText(hideList.get(which - 1).getContent());
//                            bindItemData(true);
//                        }
//                    });
//        }
//        dialog.show();
        SortListDialog.getInstance(this).setHasSelId(isHide).setType(SortUtls.TYPE_ITEM, SortUtls.TYPE_SORT_2).setPositive(this).showDialog();

    }


    @Override
    public void onRefresh() {
        bindItemData(true);
    }

    @Override
    public void onLoadMore() {
        bindItemData(false);
    }
}
