package com.nahuo.live.xiaozhibo.mainui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.base.BaseAppCompatActivity;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.common.Utils;
import com.nahuo.buyertool.http.CommonSubscriber;
import com.nahuo.buyertool.http.HttpManager;
import com.nahuo.buyertool.http.RxUtil;
import com.nahuo.buyertool.http.response.PinHuoResponse;
import com.nahuo.live.xiaozhibo.adpater.LiveListAdapter;
import com.nahuo.live.xiaozhibo.common.TCLiveRoomMgr;
import com.nahuo.live.xiaozhibo.common.utils.TCConstants;
import com.nahuo.live.xiaozhibo.model.LiveDetailBean;
import com.nahuo.live.xiaozhibo.model.LiveListBean;
import com.nahuo.live.xiaozhibo.model.ParLiveListBean;
import com.nahuo.live.xiaozhibo.play.TCVodPlayerActivity;
import com.nahuo.live.xiaozhibo.push.camera.TCLivePublisherActivity;

import java.util.ArrayList;
import java.util.List;

public class LiveListActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private TextView tvTitle;
    public String TAG = LiveListActivity.class.getSimpleName();
    private LiveListActivity Vthis = this;
    private SwipeRefreshLayout refresh_layout;
    private RecyclerView recyclerView;
    private LiveListAdapter adapter;
    private int mBitrateType = TCConstants.BITRATE_NORMAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_list);
        Vthis = this;
//        BaseRoom.mHttpRequest.destroyRoom("RoomID_47474_426188_3", SpManager.getIdentifier(Vthis), new HttpRequests.OnResponseCallback<HttpResponse>() {
//            @Override
//            public void onResponse(int retcode, @Nullable String retmsg, @Nullable HttpResponse data) {
//                Log.e("yu",retmsg+"---"+data.message);
//            }
//        });
        Button btnLeft = (Button) findViewById(R.id.titlebar_btnLeft);
        tvTitle = (TextView) findViewById(R.id.titlebar_tvTitle);
        tvTitle.setText(R.string.title_activity_live_list);
        btnLeft.setVisibility(View.VISIBLE);
        btnLeft.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(Vthis));
        refresh_layout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refresh_layout.setRefreshing(true);
        // 设置下拉进度的背景颜色，默认就是白色的
        refresh_layout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        // refresh_layout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        refresh_layout.setColorSchemeResources(R.color.colorAccent, R.color.lightcolorAccent, android.R.color.holo_blue_dark, android.R.color.holo_blue_light);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshData();
            }
        });
        refresh_layout.setRefreshing(true);

        adapter = new LiveListAdapter(Vthis);
        adapter.setListener(new LiveListAdapter.LiveOnClickListener() {
            @Override
            public void onClick(LiveListBean.SubLiveListBean sub) {
                getLiveItem(sub);
            }
        });
        recyclerView.setAdapter(adapter);
        getLiveList();
    }

    private void setRefreshFalse() {
        if (refresh_layout != null)
            refresh_layout.setRefreshing(false);
    }

    private void onRefreshData() {
        getLiveList();
    }

    /**
     * 获取列表
     *
     * @author James Chen
     * @create time in 2018/5/11 16:34
     */
    private void getLiveList() {
        addSubscribe(HttpManager.getInstance().getPinHuoNoCacheApi(TAG
        ).getLiveList()
                .compose(RxUtil.<PinHuoResponse<LiveListBean>>rxSchedulerHelper())
                .compose(RxUtil.<LiveListBean>handleResult())
                .subscribeWith(new CommonSubscriber<LiveListBean>(Vthis, true, R.string.loading) {
                    @Override
                    public void onNext(LiveListBean bean) {
                        super.onNext(bean);
                        setRefreshFalse();
                        ArrayList<MultiItemEntity> res = new ArrayList<>();

                        if (bean != null) {
                            if (!ListUtils.isEmpty(bean.getProcessLiveList())) {
                                ParLiveListBean parLiveListBean = new ParLiveListBean();
                                parLiveListBean.setTitle("开播中");
                                setSubLiveList(bean.getProcessLiveList(), LiveListBean.Type_ProcessLiveList);
                                parLiveListBean.setSubItems(bean.getProcessLiveList());
                                res.add(parLiveListBean);
                            }
                            if (!ListUtils.isEmpty(bean.getPreviewLiveList())) {
                                ParLiveListBean parLiveListBean = new ParLiveListBean();
                                parLiveListBean.setTitle("即将开始");
                                setSubLiveList(bean.getPreviewLiveList(), LiveListBean.Type_PreviewLiveList);
                                parLiveListBean.setSubItems(bean.getPreviewLiveList());
                                res.add(parLiveListBean);
                            }
                            if (!ListUtils.isEmpty(bean.getOverLiveList())) {
                                ParLiveListBean parLiveListBean = new ParLiveListBean();
                                parLiveListBean.setTitle("精彩回放");
                                setSubLiveList(bean.getOverLiveList(), LiveListBean.Type_OverLiveList);
                                parLiveListBean.setSubItems(bean.getOverLiveList());
                                res.add(parLiveListBean);
                            }
                        }
                        if (adapter != null) {
                            adapter.setNewData(res);
                            adapter.expandAll();
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setRefreshFalse();
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        setRefreshFalse();
                    }
                }));
    }

    /**
     * 获取列表
     *
     * @author James Chen
     * @create time in 2018/5/11 16:34
     */
    private void getLiveItem(final LiveListBean.SubLiveListBean sub) {
        if (sub == null) {
            ViewHub.showShortToast(Vthis, "信息为空");
            return;
        }
        addSubscribe(HttpManager.getInstance().getPinHuoNoCacheApi(TAG
        ).getLiveItem(sub.getID())
                .compose(RxUtil.<PinHuoResponse<LiveDetailBean>>rxSchedulerHelper())
                .compose(RxUtil.<LiveDetailBean>handleResult())
                .subscribeWith(new CommonSubscriber<LiveDetailBean>(Vthis, true, R.string.loading) {
                    @Override
                    public void onNext(LiveDetailBean bean) {
                        super.onNext(bean);
                        if (bean == null) {
                            ViewHub.showShortToast(Vthis, "直播场次详细为空");
                            return;
                        }
                        String AnchorUserName = bean.getAnchorUserName();
                        int AnchorUserID=bean.getAnchorUserID();
                        String RoomID = bean.getRoomID();
                        String Title = bean.getTitle();
                        int WatchCount = bean.getWatchCount();
                        String LiveUrl = bean.getLiveUrl();
                        switch (sub.getType()) {
                            case LiveListBean.Type_ProcessLiveList:
                                if (Utils.isHasTxIdentifier(Vthis)) {
                                    TCLiveRoomMgr.getLiveRoom().setRoomList(bean);
                                    Intent intent = new Intent(Vthis, TCLivePublisherActivity.class);
                                    intent.putExtra(TCConstants.ROOM_TITLE,
                                            "");
                                    intent.putExtra(TCConstants.LIVE_ITEM,bean);
                                    intent.putExtra(TCConstants.GROUP_ID, RoomID);
                                    intent.putExtra(TCConstants.USER_ID, SpManager.getIdentifier(Vthis));
                                    intent.putExtra(TCConstants.USER_NICK, AnchorUserName);
                                    intent.putExtra(TCConstants.USER_HEADPIC, Const.getShopLogo(AnchorUserID));
                                    intent.putExtra(TCConstants.COVER_PIC, Const.getShopLogo(AnchorUserID));
//                        intent.putExtra(TCConstants.SCR_ORIENTATION, mOrientation);
                                    intent.putExtra(TCConstants.BITRATE, mBitrateType);
                                    intent.putExtra(TCConstants.USER_LOC,
                                            "");
                                    //intent.putExtra(TCConstants.SHARE_PLATFORM, mShare_meidia);
                                    startActivity(intent);
                                } else {
                                    ViewHub.showShortToast(Vthis, "直播账号为空");
                                }
                                break;
                            case LiveListBean.Type_PreviewLiveList:
                                if (sub.isCanWatch()) {
                                    TCLiveRoomMgr.getLiveRoom().setRoomList(bean);
                                    Intent intent = new Intent(Vthis, TCLivePublisherActivity.class);
                                    intent.putExtra(TCConstants.ROOM_TITLE,
                                            "");
                                    intent.putExtra(TCConstants.LIVE_ITEM,bean);
                                    intent.putExtra(TCConstants.GROUP_ID, RoomID);
                                    intent.putExtra(TCConstants.USER_ID, SpManager.getIdentifier(Vthis));
                                    intent.putExtra(TCConstants.USER_NICK, AnchorUserName);
                                    intent.putExtra(TCConstants.USER_HEADPIC, Const.getShopLogo(AnchorUserID));
                                    intent.putExtra(TCConstants.COVER_PIC, Const.getShopLogo(AnchorUserID));
//                        intent.putExtra(TCConstants.SCR_ORIENTATION, mOrientation);
                                    intent.putExtra(TCConstants.BITRATE, mBitrateType);
                                    intent.putExtra(TCConstants.USER_LOC,
                                            "");
                                    //intent.putExtra(TCConstants.SHARE_PLATFORM, mShare_meidia);
                                    startActivity(intent);
                                } else {
                                    ViewHub.showLongToast(Vthis, sub.getMsg());
                                }
                                break;
                            case LiveListBean.Type_OverLiveList:
                                if (Utils.isHasTxIdentifier(Vthis)) {
                                    Intent intent = new Intent(Vthis, TCVodPlayerActivity.class);
                                    // Intent intent = new Intent(Vthis, TCLivePlayerActivity.class);
                                    // intent.putExtra(TCConstants.PLAY_URL, TextUtils.isEmpty(item.hlsPlayUrl) ? item.playurl : item.hlsPlayUrl);
                                    intent.putExtra(TCConstants.PLAY_URL, LiveUrl);
                                    intent.putExtra(TCConstants.LIVE_ITEM,bean);
                                    intent.putExtra(TCConstants.PUSHER_ID, SpManager.getIdentifier(Vthis));
                                    intent.putExtra(TCConstants.PUSHER_NAME, AnchorUserName);
                                    intent.putExtra(TCConstants.PUSHER_AVATAR, Const.getShopLogo(AnchorUserID));
                                    intent.putExtra(TCConstants.HEART_COUNT, "" + 0);
                                    intent.putExtra(TCConstants.MEMBER_COUNT, "" + WatchCount);
                                    intent.putExtra(TCConstants.GROUP_ID, RoomID);
                                    intent.putExtra(TCConstants.PLAY_TYPE, false);
                                    intent.putExtra(TCConstants.FILE_ID, "");
                                    intent.putExtra(TCConstants.COVER_PIC, "");
                                    intent.putExtra(TCConstants.TIMESTAMP, "");
                                    intent.putExtra(TCConstants.ROOM_TITLE, Title);
                                    //startActivityForResult(intent, START_LIVE_PLAY);
                                    startActivity(intent);
                                } else {
                                    ViewHub.showLongToast(Vthis, "直播账号为空");
                                }
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }
                }));
    }

    private void setSubLiveList(List<LiveListBean.SubLiveListBean> liveList, int type) {
        if (!ListUtils.isEmpty(liveList)) {
            for (LiveListBean.SubLiveListBean bean : liveList) {
                bean.setType(type);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_btnLeft:
                finish();
                break;
        }
    }
}
