package com.nahuo.buyertool.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.UploadBean;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.adapter.UploadAdapter;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.db.ToolUploadDbHelper;
import com.nahuo.buyertool.eventbus.BusEvent;
import com.nahuo.buyertool.eventbus.EventBusId;
import com.nahuo.buyertool.service.UploadItemService;
import com.nahuo.buyertool.service.UploadManager;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 上传进度
 *
 * @author James Chen
 * @create time in 2017/7/26:46
 */
public class UploadProgressActivity extends Activity implements View.OnClickListener {
    TextView tvTitle;
    ToolUploadDbHelper toolUploadDbHelper;
    int userId = 0;
    RecyclerView recyclerView;
    UploadAdapter uploadAdapter;
    private EventBus mEventBus = EventBus.getDefault();
    int uploadingCount, uploadFailCount, uploadWaitCount;
    UploadManager uploadManager;
    List<UploadBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_progress);
        mEventBus.register(this);
        uploadManager = UploadItemService.getDownloadManager();
        toolUploadDbHelper = ToolUploadDbHelper.getInstance(this);
        // 标题栏
        TextView btnLeft = (Button) findViewById(R.id.titlebar_btnLeft);
        TextView btnRight = (Button) findViewById(R.id.titlebar_btnRight);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        //tvTitle.setText(R.string.title_activity_uploaditem);
        tvTitle.setText("上传进度");
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnRight.setText("");
        btnLeft.setVisibility(View.VISIBLE);
        btnRight.setVisibility(View.GONE);
        findViewById(R.id.tv_all_stop).setOnClickListener(this);
        findViewById(R.id.tv_look).setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.listview);
        userId = SpManager.getUserId(this);
        uploadAdapter = new UploadAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(uploadAdapter);
        // List<UploadSubBean> list = toolUploadDbHelper.getAllUploadItems(userId);
        list = uploadManager.getAllTask();
        uploadAdapter.setList(list);
        uploadAdapter.notifyDataSetChanged();
        //ViewHub.showShortToast(this, list.size() + "");
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.id) {
            case EventBusId.CHANGE_UPLOADITEM_REFESH_DB:
                list = uploadManager.getAllTask();
                uploadAdapter.setList(list);
                uploadAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEventBus.unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_all_stop:
                uploadManager.stopAllTask();
                if (uploadAdapter != null)
                    uploadAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_look:
                uploadingCount = 0;
                uploadFailCount = 0;
                uploadWaitCount = 0;
                if (!ListUtils.isEmpty(list)) {
                    for (UploadBean bean : list) {
                        if (bean.getUploadStatus().equals(Const.UploadStatus.UPLOADING)) {
                            uploadingCount++;
                        } else if (bean.getUploadStatus().equals(Const.UploadStatus.UPLOAD_FAILED)) {
                            uploadFailCount++;
                        } else if (bean.getUploadStatus().equals(Const.UploadStatus.UPLOAD_WAIT)) {
                            uploadWaitCount++;
                        }
                    }
                }
                ViewHub.showLongToast(this, "待上传任务" + uploadWaitCount + "个" + "\n" + "上传中任务" + uploadingCount + "个" + "\n" + "上传失败任务" + uploadFailCount + "个");
                break;
        }
    }
}
