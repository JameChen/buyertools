package com.nahuo.buyertool;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.api.AccountAPI;
import com.nahuo.buyertool.common.BaiduStats;
import com.nahuo.buyertool.common.MediaStoreUtils;
import com.nahuo.buyertool.dialog.TakePhotosDialog;
import com.nahuo.buyertool.eventbus.BusEvent;
import com.nahuo.buyertool.eventbus.EventBusId;
import com.nahuo.buyertool.iosdialog.widget.ActionSheetDialog;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.model.ShopInfoModel;
import com.nahuo.buyertool.utils.ImageUtls;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.helper.FunctionHelper;
import com.nahuo.library.helper.MD5Utils;
import com.nahuo.library.helper.SDCardHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

import de.greenrobot.event.EventBus;

import static android.webkit.WebView.HitTestResult.SRC_ANCHOR_TYPE;


/**
 * ===============BuyerTool====================
 * author：陈振-原有基础改动
 * Email：18620156376@163.com
 * Time : 2016/7/12 13:49
 * Description : 增加js交互可以选择拍照上传图片功能,完善取消选择的机制
 * ===============BuyerTool====================
 */
public class ItemPreviewActivity extends BaseActivity implements OnClickListener, TakePhotosDialog.PopDialogListener {

    private static final String TAG = "ItemPreviewActivity";
    private static final int TAKE_PICTURE = 99;//跳转相机界面的请求码
    private Context mContext;

    private TextView tvTitle;
    private Button btnLeft;
    private ImageView iconLoading;
    private Animation iconLoadingAnimation;
    private WebView webView;

    // 直接展示指定URL
    private String normalUrl;
    private String name;
    // 上次访问的url
    private String mLastUrl;
    // 根据用户ID查询到数据后展示URL
    private String user_id;

    private boolean isBackUrl;                              // 返回标志，如果为true，则表示这个url是因返回调用的，此时不执行那些非网页加载动作

    private String wapItemTimeLine = "";
    private TextView mEmptyView;
    private Button btnLeft2;

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;//5.0的回调
    final static int FILE_SELECTED = 4;
    private LoadingDialog mloadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 设置自定义标题栏
        setContentView(R.layout.activity_itempreview);
        mContext = this;
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_titlebar_default);// 更换自定义标题栏布局
        mloadingDialog = new LoadingDialog(this);
        Intent intent = getIntent();
        if (intent.hasExtra("user_id")) {
            user_id = (String) intent.getStringExtra("user_id");
            if (user_id == null) {
                user_id = "";
            }
        } else {
            user_id = "";
        }
        if (intent.hasExtra("url") && !TextUtils.isEmpty(intent.getStringExtra("url"))) {
            normalUrl = intent.getStringExtra("url");
        } else {
            normalUrl = "";
        }
        if (intent.hasExtra("name")) {
            name = (String) intent.getStringExtra("name");
        } else {
            name = "";
        }

        initView();
        tvTitle.setText(name);
//        tvTitle.setText("地推宝");
        loadWebView();

    }

    private void loadWebView() {
        mEmptyView.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        // 清空webview中的cookie缓存，保证每次获取token都能正常使用cookie记录登录人状态
        CookieManager cm = CookieManager.getInstance();
        cm.removeSessionCookie();
        cm.removeAllCookie();
        webView.loadDataWithBaseURL("", "", "text/html", "utf-8", null);

        if (user_id.length() > 0) { // 根据用户ID查询到数据后展示URL
            // 读取店铺数据，根据domain加载网页
            LoadShopInfoTask lsit = new LoadShopInfoTask();
            lsit.execute((Void) null);
        } else { // 直接展示指定URL
            GetTokenTask gtt = new GetTokenTask(normalUrl);
            gtt.execute((Void) null);
        }
    }

    /**
     * 初始化视图
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        // 标题栏
        tvTitle = (TextView) findViewById(R.id.titlebar_tvTitle);
        btnLeft = (Button) findViewById(R.id.titlebar_btnLeft);
        btnLeft2 = (Button) findViewById(R.id.titlebar_btnLeft2);

        iconLoading = (ImageView) findViewById(R.id.titlebar_icon_loading);
        mEmptyView = (TextView) findViewById(R.id.tv_empty_view);
        mEmptyView.setOnClickListener(this);
        btnLeft.setText("返回");
        btnLeft.setVisibility(View.GONE);

        btnLeft.setOnClickListener(this);
        btnLeft2.setVisibility(View.VISIBLE);
        btnLeft2.setOnClickListener(this);

        // webview
        webView = (WebView) findViewById(R.id.shoppreview_webview);
        WebSettings settings = webView.getSettings();
        String user_agent = settings.getUserAgentString();

        settings.setUserAgentString(user_agent + " Weipu/" + FunctionHelper.GetAppVersion(mContext));
        // webView.getSettings().setBlockNetworkImage(true);
        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "share");
        // // 支持javascript
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //支持自动加载图片
        settings.setLoadsImagesAutomatically(true);
        //允许webview对文件的操作
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setSupportMultipleWindows(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setRenderPriority(RenderPriority.HIGH);
        webView.getSettings().setDomStorageEnabled(true);// 使用LocalStorage则必须打开
        webView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
        webView.setWebViewClient(new CustomeWebViewClient());// 自定义WebViewClient
        webView.setWebChromeClient(new CustomeWebChromeViewClient());
        webView.setDownloadListener(new MyWebViewDownLoadListener());
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                WebView.HitTestResult result = webView.getHitTestResult();
                int hitType = result.getType();
                switch (hitType) {
                    case SRC_ANCHOR_TYPE: {
//                        String text = result.getExtra();
//                        if (!TextUtils.isEmpty(text)){
//                            saveImgUrl = text;
//                            new SaveImage().execute();
//                        }
                        break;
                    }
                }
                return false;
            }
        });
//        webView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                WebView.HitTestResult result = webView.getHitTestResult();
//                int hitType = result.getType();
//                switch (hitType) {
//                    case SRC_ANCHOR_TYPE:
//                    case SRC_IMAGE_ANCHOR_TYPE:
//                    case WebView.HitTestResult.IMAGE_TYPE: {
//                        String text = result.getExtra();
//                        if (!TextUtils.isEmpty(text)) {
//                            saveImgUrl = text;
//                        //    new SaveImage().execute();
//                        }
//                        break;
//                    }
//                }
//                return false;
//            }
//        });

        // 转啊转
        iconLoadingAnimation = AnimationUtils.loadAnimation(mContext, R.anim.loading);
        iconLoadingAnimation.setInterpolator(new

                LinearInterpolator());// 匀速旋转
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            try {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPopDialogButtonClick(int ok_cancel) {
        try {
            switch (ok_cancel) {
                case TakePhotosDialog.BUTTON_CHOOSE_PHOTO:
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");//图片文件,系统自动调用图库和文件管理
                    // intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                    intent.putExtra("outputFormat",
                            Bitmap.CompressFormat.JPEG.toString());
                    startActivityForResult(Intent.createChooser(intent, "选择照片"),
                            FILE_SELECTED);

                    break;
                case TakePhotosDialog.BUTTON_TAKE_PHOTO:
                    //拍照
                    Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(openCameraIntent, TAKE_PICTURE);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义WebChromeClient
     */
    private class CustomeWebChromeViewClient extends WebChromeClient {
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
        }

        @Override
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
            super.onShowCustomView(view, requestedOrientation, callback);
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
        }

        @Override
        public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
            super.onReceivedTouchIconUrl(view, url, precomposed);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            Log.d("yu", "ddd");
            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            Log.d("yu", "ddd");
        } //处理alert弹出框，html 弹框的一种方式

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return true;
        } //处理confirm弹出框

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return true;
        }

        //处理prompt弹出框
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return true;
        }


        // For Android 3.0+

        /**
         * ===============BuyerTool====================
         * author：陈振
         * Email：18620156376@163.com
         * Time : 2016/7/12 15:57
         * Description : 该方法在子线程执行,故需要手动操作线程
         * ===============BuyerTool====================
         */
        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            if (mUploadMessage != null)
                return;
            mUploadMessage = uploadMsg;
            webView.post(new Runnable() {
                @Override
                public void run() {
                    TakePhotosDialog.getInstance(ItemPreviewActivity.this).setPositive(ItemPreviewActivity.this).showDialog();
//                    new MaterialDialog.Builder(mContext)
//                            .title("添加图片")
//                            .items(R.array.select_image)
//                            .itemsCallback(new MaterialDialog.ListCallback() {
//                                @Override
//                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                                    if (which == 0) {//拍照
//                                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                        ((Activity) mContext).startActivityForResult(openCameraIntent, TAKE_PICTURE);
//                                    } else if (which == 1) {//选择照片
//                                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
//                                        intent.setType("image/*");//图片文件,系统自动调用图库和文件管理
//                                        // intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
//                                        intent.putExtra("outputFormat",
//                                                Bitmap.CompressFormat.JPEG.toString());
//                                        ((Activity) mContext).startActivityForResult(Intent.createChooser(intent, "选择照片"),
//                                                FILE_SELECTED);
//                                    }
//                                }
//                            })
//                            .onNegative(new MaterialDialog.SingleButtonCallback() {
//                                @Override
//                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                    resetFileSelect();
//                                }
//                            })
//                            .cancelable(false)
//                            .negativeText("取消")
//                            .show();
                }
            });
        }

        // For Android < 3.0

        public void openFileChooser(ValueCallback uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        // For Android > 4.1.1
        public void openFileChooser(ValueCallback uploadMsg, String acceptType, String capture) {
            openFileChooser(uploadMsg, acceptType);
        }

        // For Android 5.0+
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            mUploadCallbackAboveL = filePathCallback;
            webView.post(new Runnable() {
                @Override
                public void run() {
                    TakePhotosDialog.getInstance(ItemPreviewActivity.this).setPositive(ItemPreviewActivity.this).showDialog();
//                    new MaterialDialog.Builder(mContext)
//                            .title("添加图片")
//                            .items(R.array.select_image)
//                            .itemsCallback(new MaterialDialog.ListCallback() {
//                                @Override
//                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                                    if (which == 0) {//拍照
//                                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                        ((Activity) mContext).startActivityForResult(openCameraIntent, TAKE_PICTURE);
//                                    } else if (which == 1) {//选择照片
//                                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
//                                        intent.setType("image/*");
//                                        // intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
//                                        intent.putExtra("outputFormat",
//                                                Bitmap.CompressFormat.JPEG.toString());
//                                        ((Activity) mContext).startActivityForResult(Intent.createChooser(intent, "选择照片"),
//                                                FILE_SELECTED);
//                                    }
//                                }
//                            })
//                            .onNegative(new MaterialDialog.SingleButtonCallback() {
//                                @Override
//                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                    resetFileSelect();
//                                }
//                            })
//                            .cancelable(false)
//                            .negativeText("取消")
//                            .show();
                }
            });
            return true;
        }
    }

    /**
     * 重置文件选择的js交互
     */

    private void resetFileSelect() {
        if (mUploadCallbackAboveL != null)
            mUploadCallbackAboveL.onReceiveValue(null);
        if (mUploadMessage != null) mUploadMessage.onReceiveValue(null);
        mUploadMessage = null;
        mUploadCallbackAboveL = null;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //用户选择取消的情况下,重置
        try {
            if (resultCode == RESULT_CANCELED) {
                resetFileSelect();
                return;
            }
            switch (requestCode) {

                // Choose a file from the file picker.
                case TAKE_PICTURE://拍照回调
                    getUri4Intent(intent);
                    break;
                case FILE_SELECTED://图片选择回调
                    if (null == mUploadMessage && null == mUploadCallbackAboveL) break;
                    Uri result = intent == null || resultCode != RESULT_OK ? null
                            : intent.getData();

                    if (mUploadCallbackAboveL != null) {
                        onActivityResultAboveL(requestCode, resultCode, intent);
                    } else if (mUploadMessage != null) {
                        mUploadMessage.onReceiveValue(result);
                        mUploadMessage = null;
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK) {
                        String out_trade_id = intent.getStringExtra("out_trade_id");
                        String signParam = "out_trade_id=" + out_trade_id + "648BA66A3411461EB868F6DC6C9C1A3D";
                        String sign = MD5Utils.encrypt32bit(signParam);
                        String redirectUrl = "http://m.shop.weipushop.com/paynotify/agent/paysuccess?out_trade_id="
                                + out_trade_id + "&sign=" + sign;
                        webView.loadUrl(redirectUrl);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过拍照返回的intent回去到uri并上传
     * 该方法可以兼容大部分机型.解决拍照结果为空的问题
     *
     * @param intent
     */
    private void getUri4Intent(Intent intent) {
        Bundle bundle = intent.getExtras();
        Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
        Uri uriImageData;
        if (intent.getData() != null) {
            uriImageData = intent.getData();
        } else {
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null);
            uriImageData = Uri.parse(path);
        }
        if (mUploadCallbackAboveL != null) {
            mUploadCallbackAboveL.onReceiveValue(new Uri[]{uriImageData});
            mUploadCallbackAboveL = null;
        } else if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(uriImageData);
            mUploadMessage = null;
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (mUploadCallbackAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
        return;
    }

    /**
     * 自定义WebViewClient
     */
    private class CustomeWebViewClient extends WebViewClient {
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
        }

//        @Override
//        public void onUnhandledInputEvent(WebView view, InputEvent event) {
//            super.onUnhandledInputEvent(view, event);
//        }

        @Override
        public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
            super.onUnhandledKeyEvent(view, event);
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            // http://twap.admin.nahuo.com/File/129766/20180328093600189.jpg
//            if (url.contains("twap.admin.nahuo.com/File")){
//                webView.loadUrl("javascript:window.share.sharePic(str)");
//            }
        }

        // 点击网页超链接，在web内跳转，不打开系统自带浏览器
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i(TAG, "WebView url:" + url);

            if (url.startsWith("http") || url.startsWith("https") || url.startsWith("data:image/png")) {
                if (url.equals("http://a.myapp.com/h/single.jsp?appid=45592&g_f=990935")
                        || url.equals("http://android.myapp.com/android/popularize/index.jsp?appid=45592&g_f=990935")) {
                    isBackUrl = false;
                    view.loadUrl(normalUrl);
                } else if (url.startsWith("data:image/png") || ( (url.indexOf("b0.upaiyun.com") > 0||(url.indexOf("img.nahuo.com") > 0)||(url.indexOf("img4.nahuo.com") > 0)||(url.indexOf("img3.nahuo.com") > 0)||(url.indexOf("img10.nahuo.com") > 0)) &&
                        (url.toLowerCase().indexOf(".jpg") > 0 || url.toLowerCase().indexOf(".png") > 0 || url.toLowerCase().indexOf(".bmp") > 0 || url.toLowerCase().indexOf(".jpeg") > 0))) {
                    //http://common-img-server.b0.upaiyun.com/system/manage/a5a4e360788fa2b82afc8cc166a83aec.jpg
                    //执行下载
                    saveImgUrl = url;
                    new SaveImage().execute();
                } else if (url.contains("twap.admin.nahuo.com/buyertool/billdetail")) {

                    StringBuffer html = new StringBuffer();
                    html.append("<html>"
                            + "<meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">"
                            + "<head>"
                            + "<style type=\"text/css\">"
                            + ".wp-item-detail-format.wp-item-detail-format table{ max-width:100%;width:auto!important;height:auto!important; }"
                            + "*{margin:0px; padding:0px;word-break:break-all;}"
                            + "</style>" + "</head>" + "<body>"
                            + "<div class=wp-item-detail-format>");
                    html.append("");
                    html.append("</div>");
                    html.append("<script>" +
                            "    function sharePic() {" +
                            "        window.share.sharePic()" +
                            "    }" +
                            "</script>");
                    html.append("</body></html>");
//                    webView.loadDataWithBaseURL(url, null, "text/html",
//                            "UTF-8", null);
                    webView.loadUrl(url);
//                    webView.evaluateJavascript("javascript:sharPic(str)", new ValueCallback<String>() {
//                        @Override
//                        public void onReceiveValue(String value) {
//                            //此处为 js 返回的结果
//                            ViewHub.showShortToast(mContext,"dasd");
//                        }
//                    });
                    return true;

                } else {
                    isBackUrl = false;
                    view.loadUrl(url);

                }
            } else {

                if (isBackUrl) {
                    isBackUrl = true;
                    view.goBack();
                } else {
                    if (url.equals("weipu://main")) {
                    } else {
                        String errorMsg = "无法启动";
                        if (url.contains("mqqwpa://im/chat")) {
                            errorMsg = "您还未安装QQ app，无法聊天";

                            try {
                                Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(in);
                            } catch (Exception e) {
                                Toast.makeText(mContext, errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(mContext, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
            mLastUrl = url;
            return false;
        }

        // 加载网页时，显示加载动画
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.i(TAG, "onPageStarted url:" + url);
            myHandler.sendMessage(myHandler.obtainMessage(LoadingVisible));

        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.d(getClass().getSimpleName(), "onReceivedError");
            // ViewHub.showShortToast(getApplicationContext(), "加载失败....");
            mEmptyView.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);

            // ViewHub.setEmptyView(context, view, emptyText);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        // 加载网页完成时，隐藏加载动画
        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(getClass().getSimpleName(), "onPageFinished url:" + url);
            btnLeft2.setVisibility(webView.canGoBack() ? View.VISIBLE : View.GONE);
            Button rightBtn = (Button) findViewById(R.id.titlebar_btnRight);
            rightBtn.setBackgroundResource(R.drawable.refresh);
            rightBtn.setOnClickListener(ItemPreviewActivity.this);
            rightBtn.setText("");
            int marginRight = FunctionHelper.dip2px(mContext.getResources(), 10);
            int width30 = FunctionHelper.dip2px(mContext.getResources(), 30);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width30, width30);
            lp.setMargins(0, 0, marginRight, 0);
            rightBtn.setLayoutParams(lp);
            rightBtn.setVisibility(View.VISIBLE);

            myHandler.sendMessage(myHandler.obtainMessage(LoadingHidden));

            myHandler.sendMessage(myHandler.obtainMessage(PreviewHidden));
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    myHandler.sendMessage(myHandler.obtainMessage(PreviewShare));
                }
            }).start();
            long costs = System.currentTimeMillis() - timeline;
            wapItemTimeLine = "加载网页结束:" + costs + " | " + wapItemTimeLine;
            timeline = System.currentTimeMillis();
            if ((wapItemTimeLine.length() - wapItemTimeLine.replaceAll("加载网页结束", "").length()) / "加载网页结束".length() == 3) {
                BaiduStats.log(mContext, BaiduStats.EventId.OPEN_WAP_ITEM_TIME, wapItemTimeLine, costs);
            }

            if (url.startsWith("http://pay.nahuo.com/wapwangyin/returnurlApp")) {// 网银支付成功
                EventBus.getDefault().postSticky(BusEvent.getEvent(EventBusId.BANK_PAY_SUCCESS));
                finish();
            }
            if (url.startsWith("http://pay.nahuo.com/wapwangyin/failreturnurlApp")) {// 网银支付失败
                EventBus.getDefault().postSticky(BusEvent.getEvent(EventBusId.BANK_PAY_FAIL));
                finish();
            }
        }
    }

    private static int PreviewShare = 1;
    private static int PreviewShow = 2;
    private static int PreviewHidden = 3;
    private static int LoadingVisible = 4;
    private static int LoadingHidden = 5;

    private static String shareJson;
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Thread.currentThread().setName("PreviewActivity load webview");
            if (msg.what == PreviewShare) {
                // 获取是否有需要分享的内容，有则显示分享按钮，没有则不显示
//                webView.loadUrl("javascript:window.share.shareBegin("
//                        + "document.getElementById('j-weixinShare').value);");
//                webView.loadUrl("javascript:window.share.sharePic("
//                        + "document.getElementById('sharePic').value);");
//                String  js = "var script = document.createElement('script');";
//                js+= "script.type = 'text/javascript';";
//                js+="var child=document.getElementsByTagName('a')[0];";
//                js+="child.onclick=function(){sharePic(str);};";
//               // js+= "function userIdClick(){myObj.getClose();};";
//
//                webView.loadUrl("javascript:" + js);
//                webView.loadUrl("javascript:myObj.showSource(document.getElementsByTagName('p')[0].innerHTML);");
                webView.loadUrl("javascript:(function(){" +
                        "var objs = document.getElementsByTagName(\"img\"); " +
                        "for(var i=0;i<objs.length;i++)  " +
                        "{"
//                      +"objs[i].play();"+
                        + "    objs[i].onclick=function()  " +
                        "    {  "
                        + "        window.share.sharePic(this.src);  " +
                        "    }  " +
                        "}" +
                        "})()");
                super.handleMessage(msg);
            } else if (msg.what == PreviewShow) {
                webView.loadUrl("javascript:window.share.updateView((function bb(){"
                        + "if (document.getElementById('j-weixinChat')!=undefined)"
                        + "{"
                        + "   document.getElementById('j-weixinChat').className=document.getElementById('j-weixinChat').className.replace('hide','');"
                        + "   document.getElementById('j-weixinChat').href='http://weixin-share';"
                        + "}" + "return '1';})()" + ");");

                // 验证一下user-agent
                // webView.loadUrl("javascript:window.share.updateView((function bb(){ return navigator.userAgent.toLowerCase();})()"
                // + ");");
            } else if (msg.what == LoadingVisible) {
                iconLoading.setAnimation(iconLoadingAnimation);
                iconLoadingAnimation.cancel();
                iconLoading.setVisibility(View.VISIBLE);
            } else if (msg.what == LoadingHidden) {
                iconLoading.setAnimation(null);
                iconLoadingAnimation.start();
                iconLoading.setVisibility(View.GONE);
            }
        }

    };

    final class InJavaScriptLocalObj {
        @android.webkit.JavascriptInterface
        public void sharePic(String str) {

            shareItemPic(str);
            // ViewHub.showShortToast(mContext,str);
        }

        @JavascriptInterface
        public void shareBegin(String json) {
            if (json.length() > 0) {
                shareJson = json;
                myHandler.sendMessage(myHandler.obtainMessage(PreviewShow));
            } else {
            }
        }

        @JavascriptInterface
        public void updateView(String text) {
        }
    }

    /**
     * 分享图片
     *
     * @author James Chen
     * @create time in 2018/3/28 11:38
     */

    private void shareItemPic(final String str) {
        if (TextUtils.isEmpty(str)) {
            ViewHub.showShortToast(mContext, "图片不能为空");
        } else {
            ActionSheetDialog dialog = new ActionSheetDialog(this)
                    .builder()
                    .setTitle("分享图片")
                    .setCancelable(true)
                    .setCanceledOnTouchOutside(true);

            for (int i = 0; i < 3; i++) {
                String content;
                if (i == 0) {
                    content = "微信朋友";
                } else if (i == 1) {
                    content = "微信朋友圈";
                } else {
                    content = "下载图片";
                }
                dialog.addSheetItem(content, ActionSheetDialog.SheetItemColor.Blue
                        , new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                //填写事件
                                saveImgUrl = str;
                                switch (which) {
                                    case 1:
                                        if (!ViewHub.isInstallWeChart(mContext)) {
                                            ViewHub.showShortToast(mContext, "您没有安装微信");
                                            return;
                                        }
                                        new ShareSaveImage(1).execute();
                                        break;
                                    case 2:
                                        if (!ViewHub.isInstallWeChart(mContext)) {
                                            ViewHub.showShortToast(mContext, "您没有安装微信");
                                            return;
                                        }
                                        new ShareSaveImage(2).execute();
                                        break;
                                    case 3:
                                        new SaveImage().execute();
                                        break;
                                }
                            }
                        });
            }
            dialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_btnLeft:// 返回首页
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
                break;
            case R.id.titlebar_btnLeft2:// 返回首页
                finish();
                break;
            case R.id.tv_empty_view:
            case R.id.titlebar_btnRight:
                loadWebView();
                break;
        }
    }

    private Date lastShareTime;

    long timeline;

    /**
     * 处理“Back”键，按下此键时，让网页返回上一页面，而不是结束整个Activity
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        String url = webView.getUrl();
        Log.i(TAG, "onKeyDown webView.canGoBack():" + webView.canGoBack() + " url:" + url);
        // 05-04 18:11:46.184: I/ItemPreviewActivity(20617): onKeyDown webView.canGoBack():true
        // url:https://m.wangyin.com/wepay/web/pay
        if ((keyCode == KeyEvent.KEYCODE_BACK)
                && (TextUtils.equals(url, "https://m.wangyin.com/wepay/web/pay") || TextUtils
                .equals(url, "about:blank"))) {
            return super.onKeyDown(keyCode, event);
        }

        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            isBackUrl = true;
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class GetTokenTask extends AsyncTask<Void, Void, String> {
        private String url = "";

        public GetTokenTask(String _url) {
            url = _url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Thread.currentThread().setName("GetTokenTask");
            iconLoading.setVisibility(View.VISIBLE);

            timeline = System.currentTimeMillis();
            // wapItemTimeLine = "start："+timeline+" | ";
        }

        @Override
        protected void onPostExecute(String result) {
            iconLoading.setVisibility(View.GONE);

            isBackUrl = false;

            wapItemTimeLine += "空白耗时:" + String.valueOf(System.currentTimeMillis() - timeline) + " | ";
            timeline = System.currentTimeMillis();
            // url若为http开头则直接加载不需要拼接头部
            // String loadUrl = url.startsWith("http") ? url : ("http://m.shop.weipushop.com/account/tokenlogon?token="
            // + result.replace("\"", "") + "&returnUrl=" + url);
            try {
       /*     if (url.startsWith("http://pay.nahuo.com/wapwangyin/Send?")) {// 网银支付
                webView.loadUrl(url);
            } else */
                /*if (url.startsWith("http://wap.admin.nahuo.com/stock/detail?")) {

                    webView.loadUrl("http://pinhuobuyer.nahuo.com/account/tokenlogon?token=" + result.replace("\"", "")
                            + "&returnUrl=" + URLEncoder.encode(url, "utf-8"));
                } else {
                    //http://pinhuobuyer.nahuo.com/account/tokenlogon?token=
                    webView.loadUrl("http://sso.nahuo.com/account/tokenlogon?token=" + result.replace("\"", "")
                            + "&returnUrl=" + URLEncoder.encode(url, "utf-8"));
                }*/
                webView.loadUrl("http://sso.nahuo.com/account/tokenlogon?token=" + result.replace("\"", "")
                        + "&returnUrl=" + URLEncoder.encode(url, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            // webView.loadUrl("http://sso.nahuo.com/account/tokenlogon?token="
            // + result.replace("\"", "") + "&returnUrl=" + url);

        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                String result = AccountAPI.getInstance().getWebToken(PublicData.getCookie(mContext));
                wapItemTimeLine += "token获取耗时:" + String.valueOf(System.currentTimeMillis() - timeline) + " | ";
                timeline = System.currentTimeMillis();
                return result;
            } catch (Exception ex) {
                Log.e(TAG, "获取token失败");
                ex.printStackTrace();
                return "";
            }
        }

    }

    /**
     * 读取店铺的数据
     */
    private class LoadShopInfoTask extends AsyncTask<Void, Void, Object> {

        @Override
        protected Object doInBackground(Void... params) {
            try {
                ShopInfoModel shopInfo = AccountAPI.getInstance().getShopInfoWithUserID(user_id,
                        PublicData.getCookie(mContext));
                if (shopInfo == null)
                    return "无法获取店铺数据";
                return shopInfo;
            } catch (Exception ex) {
                Log.e(TAG, "无法获取店铺数据");
                ex.printStackTrace();
                return ex.getMessage() == null ? "" : ex.getMessage();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            iconLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            iconLoading.setVisibility(View.GONE);

            if (result instanceof ShopInfoModel) {
                ShopInfoModel shopInfo = (ShopInfoModel) result;

                normalUrl = "http://" + shopInfo.getShopID() + ".weipushop.com";
                // 再次读取token登录后访问URL
                GetTokenTask gtt = new GetTokenTask(normalUrl);
                gtt.execute((Void) null);
            } else {
                Toast.makeText(mContext, result.toString(), Toast.LENGTH_LONG).show();
            }
        }

    }


    public static String downloadDirPath = SDCardHelper.getSDCardRootDirectory()
            + "/buyertool/download";
    private String saveImgUrl = "";

    /***
     * 功能：用线程保存图片
     */
    private class SaveImage extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    ViewHub.showLongToast(mContext, "下载开始...");
//                }
//            });
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mloadingDialog != null)
                        mloadingDialog.start("下载开始...");
                }
            });

        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                File file = new File(downloadDirPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                if (saveImgUrl.contains("data:image/png")) {
                    // file = new File(downloadDirPath + "/" + new Date().getTime()+".png" );
                    Bitmap bitmap = ImageUtls.stringtoBitmap(saveImgUrl);
                    file = ImageUtls.saveFile(bitmap, downloadDirPath, new Date().getTime() + ".png");
                    result = "图片已保存至：" + file.getAbsolutePath();
                    MediaStoreUtils.scanImageFile(mContext, file.getAbsolutePath());
                } else {
                    int idx = saveImgUrl.lastIndexOf(".");
                    String ext = saveImgUrl.substring(idx);
                    file = new File(downloadDirPath + "/" + new Date().getTime() + ext);
                    InputStream inputStream = null;
                    URL url = new URL(saveImgUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(20000);
                    if (conn.getResponseCode() == 200) {
                        inputStream = conn.getInputStream();
                    }
                    byte[] buffer = new byte[4096];
                    int len = 0;
                    FileOutputStream outStream = new FileOutputStream(file);
                    while ((len = inputStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    outStream.close();
                    result = "图片已保存至：" + file.getAbsolutePath();
                    MediaStoreUtils.scanImageFile(mContext, file.getAbsolutePath());
                }

            } catch (Exception e) {
                result = "保存失败！" + e.getLocalizedMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {
            hideDialog();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ViewHub.showLongToast(mContext, result);
                }
            });

        }
    }

    private class ShareSaveImage extends AsyncTask<String, Void, Object> {
        int type = 0;

        ShareSaveImage(int type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ViewHub.showLongToast(mContext, "获取图片...");
//                    }
//                });
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mloadingDialog != null)
                        mloadingDialog.start("获取图片...");
                }
            });

        }

        @Override
        protected Object doInBackground(String... params) {
            String result = "";
            try {
                File file = new File(downloadDirPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                if (saveImgUrl.contains("data:image/png")) {
                    // file = new File(downloadDirPath + "/" + new Date().getTime()+".png" );
                    Bitmap bitmap = ImageUtls.stringtoBitmap(saveImgUrl);
                    file = ImageUtls.saveFile(bitmap, downloadDirPath, new Date().getTime() + ".png");
                    //result = "图片已保存至：" + file.getAbsolutePath();
                    // MediaStoreUtils.scanImageFile(mContext, file.getAbsolutePath());
                } else {
                    int idx = saveImgUrl.lastIndexOf(".");
                    String ext = saveImgUrl.substring(idx);
                    file = new File(downloadDirPath + "/" + new Date().getTime() + ext);
                    InputStream inputStream = null;
                    URL url = new URL(saveImgUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(20000);
                    if (conn.getResponseCode() == 200) {
                        inputStream = conn.getInputStream();
                    }
                    byte[] buffer = new byte[4096];
                    int len = 0;
                    FileOutputStream outStream = new FileOutputStream(file);
                    while ((len = inputStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    outStream.close();
                    // result = "图片已保存至：" + file.getAbsolutePath();
                    // MediaStoreUtils.scanImageFile(mContext, file.getAbsolutePath());
                }
                return file;

            } catch (Exception e) {
                result = "保存失败！" + e.getLocalizedMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(final Object result) {
            // ViewHub.showLongToast(mContext, result);
            hideDialog();
            if (result instanceof File) {
                File file = (File) result;
                if (file != null) {
                    ArrayList<Uri> allList = new ArrayList<>();
                    Uri uri = Uri.fromFile(file);
                    Intent weChatIntent = new Intent();
                    allList.add(uri);
                    if (type == 1) {
                        weChatIntent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI"));
                        weChatIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                        weChatIntent.setType("image/*");
                        weChatIntent.putExtra(Intent.EXTRA_STREAM, allList);
                    } else {
                        weChatIntent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI"));
                        weChatIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                        weChatIntent.setType("image/*");
                        weChatIntent.putExtra(Intent.EXTRA_STREAM, allList);
                        weChatIntent.putExtra("Kdescription", name); //分享描述
                    }
                    startActivity(weChatIntent);
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ViewHub.showLongToast(mContext, result.toString());
                    }
                });

            }
        }
    }

    protected boolean isDialogShowing() {
        return (mloadingDialog != null && mloadingDialog.isShowing());
    }

    protected void hideDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isDialogShowing()) {
                    mloadingDialog.stop();
                }
            }
        });

    }
}
