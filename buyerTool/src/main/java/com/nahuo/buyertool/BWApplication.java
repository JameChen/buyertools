package com.nahuo.buyertool;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.nahuo.buyertool.common.Debug;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.eventbus.BusEvent;
import com.nahuo.buyertool.eventbus.EventBusId;
import com.nahuo.buyertool.service.UploadManager;
import com.nahuo.buyertool.utils.SmallVideoUtils;
import com.nahuo.library.toast.ToastUtils;
import com.nahuo.library.toast.style.ToastBlackStyle;
import com.nahuo.live.xiaozhibo.common.utils.TCConstants;
import com.nahuo.live.xiaozhibo.common.utils.TCLog;
import com.nahuo.live.xiaozhibo.login.TCUserMgr;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.rtmp.TXLiveBase;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;
import cn.finalteam.okhttpfinal.Part;
import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;
import okhttp3.Headers;

public class BWApplication extends MultiDexApplication {

    public static int PrinterId = 0;
    private static BWApplication instance;
    private static String UserAgent = "";
    public static String BUYERTOOL_LOGGER = "BUYERTOOL_LOGGER";
    public static BWApplication getInstance() {
        return instance;
    }

    public static List<Activity> pList = new ArrayList<>();
    public static List<Activity> vList = new ArrayList<>();
    private HttpClient httpClient;
    public static Activity currentActivity;

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        BWApplication.currentActivity = currentActivity;
    }

    public void setUserAgent() {
        WebView webView = new WebView(instance);
        WebSettings settings = webView.getSettings();
        UserAgent = settings.getUserAgentString();
        SpManager.setUserAgent(instance, UserAgent);
    }

    public String getUserAgent() {
        if (TextUtils.isEmpty(UserAgent)) {
            UserAgent = SpManager.getUserAgentN(instance);
        }
        return UserAgent;
    }

    public static void addActivity(Activity activity) {
        if (!pList.contains(activity)) {
            pList.add(activity);
        }
    }

    public static void clearActivity() {
        try {
            for (Activity activity : pList) {
                activity.finish();
            }
            pList.clear();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void addVActivity(Activity activity) {
        if (!vList.contains(activity)) {
            vList.add(activity);
        }
    }

    public static void clearVActivity() {
        try {
            for (Activity activity : vList) {
                activity.finish();
            }
            vList.clear();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    private boolean isRunInBackground;
    private int appCount;
    @Override
    public void onCreate() {
        super.onCreate();
        initBackgroundCallBack();
        instance = this;
        ToastUtils.init(this,new ToastBlackStyle());
        initOkHttp();
        initSDK();
        Fresco.initialize(this);
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(2)         // (Optional) How many method line to show. Default 2
                .methodOffset(0)        // (Optional) Hides internal method calls up to offset. Default 5
//                .logStrategy(new LogStrategy() {
//                    @Override
//                    public void log(int priority, String tag, String message) {
//
//                    }
//                }) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag(BUYERTOOL_LOGGER)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return com.nahuo.buyer.tool.BuildConfig.DEBUG;
            }

        });
        try {
            JPushInterface.init(getApplicationContext());
            // JPushInterface.resumePush(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SmallVideoUtils.initSmallVideo();
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果使用到百度地图或者类似启动remote service的第三方库，这个if判断不能少
        if (processAppName == null || processAppName.equals("")) {
            // workaround for baidu location sdk
            // 百度定位sdk，定位服务运行在一个单独的进程，每次定位服务启动的时候，都会调用application::onCreate
            // 创建新的进程。
            // 但环信的sdk只需要在主进程中初始化一次。 这个特殊处理是，如果从pid 找不到对应的processInfo
            // processName，
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

        setUserAgent();
      //  Context context = getApplicationContext();
       // File cacheDir = StorageUtils.getCacheDirectory(context);
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
//                context)
//                .threadPoolSize(5)
//                // default 3
//                .threadPriority(Thread.NORM_PRIORITY - 1)
//                // defaul
//                .tasksProcessingOrder(QueueProcessingType.FIFO)
//                // default
//                .denyCacheImageMultipleSizesInMemory()
//                .memoryCache(new LruMemoryCache(20 * 1024 * 1024))
//                .memoryCacheSize(20 * 1024 * 1024)
//                .memoryCacheSizePercentage(30)
//                // default
//                .discCache(new UnlimitedDiscCache(cacheDir))
//                // default
//                .discCacheSize(50 * 1024 * 1024).discCacheFileCount(100)
//                .discCacheFileNameGenerator(new Md5FileNameGenerator()) // default
//                .imageDownloader(new BaseImageDownloader(context)) // default
//                .imageDecoder(new BaseImageDecoder(true)) // default
//                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
//                .writeDebugLogs().build();
//
//        // Initialize ImageLoader with configuration
//        ImageLoader.getInstance().init(config);

        httpClient = this.createHttpClient();

        Debug.init(this);
        UploadManager.getInstance(this).getThreadPool().setCorePoolSize(1);
    }
    /**
     * 从后台回到前台需要执行的逻辑
     *
     * @param activity
     */
    private void back2App(Activity activity) {
        isRunInBackground = false;
        Log.e("yu","back2App");
        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.BACKGROUND_ACTION,BACK_APP));
    }
    public static  int BACK_APP=1;
    public static  int LEAVE_APP=2;
    /**
     * 离开应用 压入后台或者退出应用
     *
     * @param activity
     */
    private void leaveApp(Activity activity) {
        isRunInBackground = true;
        Log.e("yu","leaveApp");
        EventBus.getDefault().post(BusEvent.getEvent(EventBusId.BACKGROUND_ACTION,LEAVE_APP));
    }

    private void initBackgroundCallBack() {
        registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.e("yu","onActivityCreated");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.e("yu","onActivityStarted");
                appCount++;
                if (isRunInBackground) {
                    //应用从后台回到前台 需要做的操作
                    back2App(activity);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.e("yu","onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.e("yu","onActivityPaused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.e("yu","onActivityStopped");
                appCount--;
                if (appCount == 0) {
                    //应用进入后台 需要做的操作
                    leaveApp(activity);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.e("yu","onActivitySaveInstanceState");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.e("yu","onActivityDestroyed");
            }
        });
    }
    /**
     * 初始化SDK，包括Bugly，IMSDK，RTMPSDK等
     */
    public void initSDK() {
        Log.w("App","xzb_process: app init sdk");
        //启动bugly组件，bugly组件为腾讯提供的用于crash上报和分析的开放组件，如果您不需要该组件，可以自行移除
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setAppVersion(TXLiveBase.getSDKVersionStr());
        CrashReport.initCrashReport(getApplicationContext(), TCConstants.BUGLY_APPID, true, strategy);

        //设置rtmpsdk log回调，将log保存到文件
        TXLiveBase.setListener(new TCLog(getApplicationContext()));
        TXLiveBase.setAppID(TCConstants.appID);

        //初始化httpengine
     //   TCHttpEngine.getInstance().initContext(getApplicationContext());

        TCUserMgr.getInstance().initContext(getApplicationContext());
    }

    /**
     * ===============BuyerTool====================
     * author：ChenZhen
     * Email：18620156376@163.com
     * Time : 2016/7/8 12:51
     * Description : 初始化网络请求框架
     * ===============BuyerTool====================
     */
    private void initOkHttp() {
        List<Part> commomParams = new ArrayList<>();
        Headers commonHeaders = new Headers.Builder().build();
        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder()
                .setCommenParams(commomParams)//设置公共请求参数,例如设备信息,语言版本
                .setCommenHeaders(commonHeaders)//设置公共请求头
                .setTimeout(10000)//设置超时时间为10秒
//                .setInterceptors(interceptorList)
//                .setCache(new Cache(FileUtils.getCacheFile(), 10 * 1024 * 1024))//设置缓存
//                .setCacheStale(new Cache(FileUtils.getCacheFile(), 10 * 1024 * 1024), 60 * 10)
                .setCommenHeaders(new Headers.Builder().add("charset", "UTF-8").build())
                .setDebug(true);
        OkHttpFinal.getInstance().init(builder.build());
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this
                .getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> l = am.getRunningAppProcesses();
        Iterator<RunningAppProcessInfo> i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            RunningAppProcessInfo info = i.next();
            try {
                if (info.pid == pID) {
                    pm.getApplicationLabel(pm.getApplicationInfo(
                            info.processName, PackageManager.GET_META_DATA));
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("BWApplication", e.toString());
            }
        }
        return processName;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        this.shutdownHttpClient();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        this.shutdownHttpClient();
    }

    // 创建HttpClient实例
    private HttpClient createHttpClient() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params,
                HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
        HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
        HttpConnectionParams.setSoTimeout(params, 20 * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        schReg.register(new Scheme("https",
                SSLSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager connMgr = new ThreadSafeClientConnManager(
                params, schReg);

        return new DefaultHttpClient(connMgr, params);
    }

    // 关闭连接管理器并释放资源
    private void shutdownHttpClient() {
        if (httpClient != null && httpClient.getConnectionManager() != null) {
            httpClient.getConnectionManager().shutdown();
        }
    }

    // 对外提供HttpClient实例
    public HttpClient getHttpClient() {
        return httpClient;
    }
}
