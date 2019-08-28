package com.nahuo.buyertool.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand.ENABLE;
import com.gprinter.command.GpCom;
import com.gprinter.command.GpUtils;
import com.gprinter.command.LabelCommand;
import com.gprinter.command.LabelCommand.DIRECTION;
import com.gprinter.command.LabelCommand.EEC;
import com.gprinter.command.LabelCommand.FONTMUL;
import com.gprinter.command.LabelCommand.FONTTYPE;
import com.gprinter.command.LabelCommand.MIRROR;
import com.gprinter.command.LabelCommand.ROTATION;
import com.gprinter.service.GpPrintService;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.BWApplication;
import com.nahuo.buyertool.Bean.PrintBean;
import com.nahuo.buyertool.ViewHub;
import com.nahuo.buyertool.adapter.PrintAdapter;
import com.nahuo.buyertool.api.BuyerToolAPI;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.dialog.PrintDialog;
import com.nahuo.library.controls.LoadingDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class PrintActivity extends AppCompatActivity implements View.OnClickListener, PrintAdapter.PrintListener, PrintDialog.PopDialogListener {
    private TextView back, tv_all_print;
    private ListView listview;
    private String json = "";
    public static String ETRA_PRINT = "ETRA_PRINT";
    public static String ETRA_PRINT_ID = "ETRA_PRINT_ID";
    List<PrintBean> data = new ArrayList<>();
    private LoadingDialog mloadingDialog;
    private GpService mGpService = null;
    private PrinterServiceConnection conn = null;
    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    private int mPrinterIndex = 0;
    Style current_action;
    PrintBean current_bean = null;
    private int id;

    class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("ServiceConnection", "onServiceDisconnected() called");
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
        }
    }

    private void connection() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent(this, GpPrintService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            unbindService(conn); // unBindService
        }
        unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("TAG", action);
            // GpCom.ACTION_DEVICE_REAL_STATUS 为广播的IntentFilter
            if (action.equals(GpCom.ACTION_DEVICE_REAL_STATUS)) {

                // 业务逻辑的请求码，对应哪里查询做什么操作
                int requestCode = intent.getIntExtra(GpCom.EXTRA_PRINTER_REQUEST_CODE, -1);
                // 判断请求码，是则进行业务操作
                if (requestCode == MAIN_QUERY_PRINTER_STATUS) {

                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    String str;
                    if (status == GpCom.STATE_NO_ERR) {
                        str = "打印机正常";
                    } else {
                        str = "打印机 ";
                        if ((byte) (status & GpCom.STATE_OFFLINE) > 0) {
                            str += "脱机";
                        }
                        if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0) {
                            str += "缺纸";
                        }
                        if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0) {
                            str += "打印机开盖";
                        }
                        if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0) {
                            str += "打印机出错";
                        }
                        if ((byte) (status & GpCom.STATE_TIMES_OUT) > 0) {
                            str += "查询超时";
                        }
                    }

                    Toast.makeText(getApplicationContext(), "打印机：" + mPrinterIndex + " 状态：" + str, Toast.LENGTH_SHORT)
                            .show();
                } else if (requestCode == REQUEST_PRINT_LABEL) {
                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    if (status == GpCom.STATE_NO_ERR) {
                        switch (current_action) {
                            case ALL_PRINT:
                                sendSMoreLabel();
                                break;
                            case ITEM_PRINT:
                                sendSingleLabel();
                                break;
                        }
                    } else {
                        ViewHub.showShortToast(PrintActivity.this, "打印机状态显示error，请检查打印机");
                    }
                }
//                else if (requestCode == REQUEST_PRINT_RECEIPT) {
//                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
//                    if (status == GpCom.STATE_NO_ERR) {
//                        sendReceipt();
//                    } else {
//                        Toast.makeText(MainActivity.this, "query printer status error", Toast.LENGTH_SHORT).show();
//                    }
//                }
            }
//            else if (action.equals(GpCom.ACTION_RECEIPT_RESPONSE)) {
//                if (--mTotalCopies > 0) {
//                    sendReceiptWithResponse();
//                }
//            }
//            else if (action.equals(GpCom.ACTION_LABEL_RESPONSE)) {
//                byte[] data = intent.getByteArrayExtra(GpCom.EXTRA_PRINTER_LABEL_RESPONSE);
//                int cnt = intent.getIntExtra(GpCom.EXTRA_PRINTER_LABEL_RESPONSE_CNT, 1);
//                String d = new String(data, 0, cnt);
//                /**
//                 * 这里的d的内容根据RESPONSE_MODE去判断返回的内容去判断是否成功，具体可以查看标签编程手册SET
//                 * RESPONSE指令
//                 * 该sample中实现的是发一张就返回一次,这里返回的是{00,00001}。这里的对应{Status,######,ID}
//                 * 所以我们需要取出STATUS
//                 */
//                Log.d("LABEL RESPONSE", d);
//
//                if (--mTotalCopies > 0 && d.charAt(1) == 0x00) {
//                    sendLabelWithResponse();
//                }
//            }
        }
    };

    private void sendSingleLabel() {
        if (current_bean == null)
            return;
        String scan = current_bean.getScan();
        String first_txt = current_bean.getTxt_1();
        String second_txt = current_bean.getTxt_2();
        String third_txt = current_bean.getTxt_3();
        String four_txt = current_bean.getTxt_4();
        String code = current_bean.getItemcode();
        String DMCode=current_bean.getDMCode();
        String color = current_bean.getColor();
        String size = current_bean.getSize();
        LabelCommand tsc = new LabelCommand();
        tsc.addSize(40, 70); // 设置标签尺寸，按照实际尺寸设置
        tsc.addGap(5); // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addDirection(DIRECTION.BACKWARD, MIRROR.NORMAL);// 设置打印方向
        tsc.addDensity(LabelCommand.DENSITY.DNESITY15);
        tsc.addReference(0, 0);// 设置原点坐标
        tsc.addTear(ENABLE.ON); // 撕纸模式开启
        tsc.addCls();// 清除打印缓冲区
        tsc.addQRCode(280, 30, EEC.LEVEL_Q, 8, LabelCommand.ROTATION.ROTATION_90, scan);
        // 绘制简体中文
        if (!TextUtils.isEmpty(code))
            tsc.addText(100, 70, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_90, FONTMUL.MUL_2, FONTMUL.MUL_2,
                    code);
        if (!TextUtils.isEmpty(DMCode))
            tsc.addText(50, 80, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_90, FONTMUL.MUL_1, FONTMUL.MUL_1,
                    DMCode);
        if (!TextUtils.isEmpty(first_txt))
            tsc.addText(270, 245, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_90, FONTMUL.MUL_1, FONTMUL.MUL_1,
                    first_txt);
        if (!TextUtils.isEmpty(second_txt))
            tsc.addText(235, 245, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_90, FONTMUL.MUL_1, FONTMUL.MUL_1,
                    second_txt);
        if (!TextUtils.isEmpty(third_txt))
            tsc.addText(200, 245, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_90, FONTMUL.MUL_1, FONTMUL.MUL_1,
                    third_txt);
        if (!TextUtils.isEmpty(four_txt))
            tsc.addText(165, 245, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_90, FONTMUL.MUL_1, FONTMUL.MUL_1,
                    four_txt);
        if (!TextUtils.isEmpty(color))
            tsc.addText(120, 245, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_90, FONTMUL.MUL_2, FONTMUL.MUL_2,
                    color);
        if (!TextUtils.isEmpty(size))
            tsc.addText(70, 245, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_90, FONTMUL.MUL_2, FONTMUL.MUL_2,
                    size);
        tsc.addPrint(1, 1); // 打印标签
        tsc.addSound(2, 100); // 打印标签后 蜂鸣器响
        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
        Vector<Byte> datas = tsc.getCommand(); // 发送数据
        byte[] bytes = GpUtils.ByteTo_byte(datas);
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rel;
        try {
            rel = mGpService.sendLabelCommand(mPrinterIndex, str);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                ViewHub.showShortToast(getApplicationContext(), GpCom.getErrorText(r));
            } else {
                if (adapter != null)
                    adapter.reMoveItem(current_bean);
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void sendSMoreLabel() {
        try {
            if (adapter != null) {
                List<PrintBean> list = adapter.getData();
                if (!ListUtils.isEmpty(list)) {
                    for (PrintBean printBean : list) {
                        if (printBean != null) {
                            String scan = printBean.getScan();
                            String first_txt = printBean.getTxt_1();
                            String second_txt = printBean.getTxt_2();
                            String third_txt = printBean.getTxt_3();
                            String four_txt = printBean.getTxt_4();
                            String code = printBean.getItemcode();
                            String color = printBean.getColor();
                            String size = printBean.getSize();
                            String DMCode=printBean.getDMCode();
                            LabelCommand tsc = new LabelCommand();
                            tsc.addSize(40, 70); // 设置标签尺寸，按照实际尺寸设置
                            tsc.addGap(5); // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
                            tsc.addDirection(DIRECTION.BACKWARD, MIRROR.NORMAL);// 设置打印方向
                            tsc.addDensity(LabelCommand.DENSITY.DNESITY15);
                            tsc.addReference(0, 0);// 设置原点坐标
                            tsc.addTear(ENABLE.ON); // 撕纸模式开启
                            tsc.addCls();// 清除打印缓冲区
                            tsc.addQRCode(280, 30, EEC.LEVEL_Q, 8, LabelCommand.ROTATION.ROTATION_90, scan);
                            // 绘制简体中文
                            if (!TextUtils.isEmpty(code))
                                tsc.addText(100, 70, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_90, FONTMUL.MUL_2, FONTMUL.MUL_2,
                                        code);
                            if (!TextUtils.isEmpty(DMCode))
                                tsc.addText(50, 80, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_90, FONTMUL.MUL_1, FONTMUL.MUL_1,
                                        DMCode);
                            if (!TextUtils.isEmpty(first_txt))
                                tsc.addText(270, 245, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_90, FONTMUL.MUL_1, FONTMUL.MUL_1,
                                        first_txt);
                            if (!TextUtils.isEmpty(second_txt))
                                tsc.addText(235, 245, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_90, FONTMUL.MUL_1, FONTMUL.MUL_1,
                                        second_txt);
                            if (!TextUtils.isEmpty(third_txt))
                                tsc.addText(200, 245, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_90, FONTMUL.MUL_1, FONTMUL.MUL_1,
                                        third_txt);
                            if (!TextUtils.isEmpty(four_txt))
                                tsc.addText(165, 245, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_90, FONTMUL.MUL_1, FONTMUL.MUL_1,
                                        four_txt);
                            if (!TextUtils.isEmpty(color))
                                tsc.addText(120, 245, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_90, FONTMUL.MUL_2, FONTMUL.MUL_2,
                                        color);
                            if (!TextUtils.isEmpty(size))
                                tsc.addText(70, 245, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_90, FONTMUL.MUL_2, FONTMUL.MUL_2,
                                        size);
                            tsc.addPrint(1, 1); // 打印标签
                            tsc.addSound(2, 100); // 打印标签后 蜂鸣器响
                            tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
                            Vector<Byte> datas = tsc.getCommand(); // 发送数据
                            byte[] bytes = GpUtils.ByteTo_byte(datas);
                            String str = Base64.encodeToString(bytes, Base64.DEFAULT);
                            int rel;
                            try {
                                rel = mGpService.sendLabelCommand(mPrinterIndex, str);
                                GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
                                if (r != GpCom.ERROR_CODE.SUCCESS) {
                                    ViewHub.showShortToast(getApplicationContext(), GpCom.getErrorText(r));
                                } else {
                                    if (adapter != null)
                                        adapter.reMoveItem(printBean);
                                }
                            } catch (RemoteException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printLabelClicked() {

        try {
            int type = mGpService.getPrinterCommandType(mPrinterIndex);
            if (type == GpCom.LABEL_COMMAND) {
                mGpService.queryPrinterStatus(mPrinterIndex, 1000, REQUEST_PRINT_LABEL);
            } else {
                Toast.makeText(this, "Printer is not label mode", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onImagePrintClick(PrintBean bean) {
        if (bean != null) {
            current_bean = bean;
            // ViewHub.showShortToast(this, bean.getColor());
            current_action = Style.ITEM_PRINT;
            printLabelClicked();
            if (id > 0)
                SpManager.setBlueToolPrintTime(this, id, System.currentTimeMillis());
        }
    }

    @Override
    public void onFinish(int count) {
        try {
            if (count <= 0) {
                ViewHub.showShortToast(this, "款式全部打印完成");
                this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPopDialogButtonClick(Style action, PrintBean bean) {
        if (action == Style.ALL_PRINT) {
            //ViewHub.showShortToast(this, adapter.getData().size() + "");
            current_action = Style.ALL_PRINT;
            printLabelClicked();
            if (id > 0)
                SpManager.setBlueToolPrintTime(this, id, System.currentTimeMillis());
        }
    }

    public enum Step {
        QRCODELIST
    }

    PrintAdapter adapter;
    private static final int REQUEST_PRINT_LABEL = 0xfd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        mPrinterIndex = BWApplication.PrinterId;
        connection();
        // 注册实时状态查询广播
        registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));
        /**
         * 票据模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus()，在打印完成后会接收到
         * action为GpCom.ACTION_DEVICE_STATUS的广播，特别用于连续打印，
         * 可参照该sample中的sendReceiptWithResponse方法与广播中的处理
         **/
        registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_RECEIPT_RESPONSE));
        /**
         * 标签模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus(RESPONSE_MODE mode)
         * ，在打印完成后会接收到，action为GpCom.ACTION_LABEL_RESPONSE的广播，特别用于连续打印，
         * 可参照该sample中的sendLabelWithResponse方法与广播中的处理
         **/
        registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_LABEL_RESPONSE));
        mloadingDialog = new LoadingDialog(this);
        back = (TextView) findViewById(R.id.back);
        back.setOnClickListener(this);
        tv_all_print = (TextView) findViewById(R.id.tv_all_print);
        tv_all_print.setOnClickListener(this);
        listview = (ListView) findViewById(R.id.listview);
        adapter = new PrintAdapter(this);
        // adapter.setData(data);
        adapter.setListener(this);
        listview.setAdapter(adapter);
        Intent intent = getIntent();
        if (intent != null) {
            json = intent.getStringExtra(ETRA_PRINT);
            id = intent.getIntExtra(ETRA_PRINT_ID, -1);
            new Task(Step.QRCODELIST).execute();
        }
    }

    public enum Style {
        ITEM_PRINT, ALL_PRINT
    }

    private class Task extends AsyncTask<Object, Void, Object> {
        private Step mStep;

        public Task(Step step) {
            mStep = step;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mloadingDialog.start("加载数据...");
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                switch (mStep) {
                    case QRCODELIST:
                        List<PrintBean> bean = BuyerToolAPI.getQrcodeList(PrintActivity.this, json);
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
            hideDialog();
            if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(getApplicationContext(), ((String) result).replace("error:", ""));
            } else {
                switch (mStep) {
                    case QRCODELIST:
                        List<PrintBean> list = (List<PrintBean>) result;
                        data = list;
                        if (adapter != null) {
                            adapter.setData(data);
                            adapter.notifyDataSetChanged();
                        }

                        break;
                }
            }
        }
    }

    protected void hideDialog() {
        if (isDialogShowing()) {
            mloadingDialog.stop();
        }
    }

    protected boolean isDialogShowing() {
        return (mloadingDialog != null && mloadingDialog.isShowing());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_all_print:
                PrintDialog.getInstance(this).setTitle("确定要全部打印吗？").setAction(Style.ALL_PRINT).setList(null).setPositive(this).showDialog();

                break;
        }
    }
}
