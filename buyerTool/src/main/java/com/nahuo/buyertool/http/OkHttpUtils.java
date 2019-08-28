package com.nahuo.buyertool.http;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.nahuo.buyer.tool.BuildConfig;
import com.nahuo.buyertool.BWApplication;
import com.nahuo.buyertool.http.response.PinHuoResponse;
import com.nahuo.buyertool.Bean.UploadRepose;
import com.nahuo.buyertool.common.Constant;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.utils.HttpsUtils;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.helper.FunctionHelper;
import com.nahuo.library.helper.GsonHelper;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.nahuo.buyertool.api.HttpUtils.SERVERURL;
import static com.nahuo.buyertool.api.HttpUtils.SERVERURL_V4;

/**
 * Created by jame on 2017/12/22.
 */
/*public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
        .url(url)
        .post(body)
        .build();
        Response response = client.newCall(request).execute();
        f (response.isSuccessful()) {
        return response.body().string();
        } else {
        throw new IOException("Unexpected code " + response);
        }
        }*/
public class OkHttpUtils {
    private static final byte[] LOCKER = new byte[0];
    private static OkHttpUtils mInstance;
    private static OkHttpClient mOkHttpClient;
    private static OkHttpClient.Builder okNeedCacheBuilder;
    private static OkHttpClient.Builder okNoCacheBuilder;
    public static int CONNECTTIMEOUT = 60;
    public static int READTIMEOUT = 60;
    public static int WRITETIMEOUT = 60;
    public static int CACHE_MAXSIZE = 1024 * 1024 * 50;
    public static int MAXSTALE = 60 * 60 * 24 * 28;
    public static int Error_Code_Exception = 555;
    public static int Error_Call_Exception = 556;
    public static int Error_NUll_Exception = 557;
    public static String Error_Message_Null="对象为空";
    private OkHttpUtils() {
//        okhttp3.OkHttpClient.Builder ClientBuilder = new okhttp3.OkHttpClient.Builder();
//        ClientBuilder.readTimeout(30, TimeUnit.SECONDS);//读取超时
//        ClientBuilder.connectTimeout(10, TimeUnit.SECONDS);//连接超时
//        ClientBuilder.writeTimeout(60, TimeUnit.SECONDS);//写入超时
        mOkHttpClient = provideNoCashClient(provideNoCacheOkHttpBuilder());

    }

    public static OkHttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (LOCKER) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }
    public static String getVersionName(Context mContext){
        String versionName = FunctionHelper.getVersionName(mContext);
        if(!TextUtils.isEmpty(versionName)){
            return versionName.substring(0,3);
        }
        return "";
    }
    public static OkHttpClient.Builder provideNeedCacheOkHttpBuilder() {
        if (okNeedCacheBuilder == null) {
            synchronized (OkHttpUtils.class) {
                if (okNeedCacheBuilder == null)
                    okNeedCacheBuilder = new OkHttpClient.Builder();
            }

        }
        return okNeedCacheBuilder;
    }

    public static OkHttpClient.Builder provideNoCacheOkHttpBuilder() {
        if (okNoCacheBuilder == null) {
            synchronized (OkHttpUtils.class) {
                if (okNoCacheBuilder == null)
                    okNoCacheBuilder = new OkHttpClient.Builder();
            }

        }
        return okNoCacheBuilder;
    }

    private static String Tag = "";

    public static OkHttpClient provideNeedCacheClient(OkHttpClient.Builder builder) {
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    synchronized (HttpLoggingInterceptor.class) {
                        if (TextUtils.isEmpty(message)) return;
                        String s = message.substring(0, 1);
                        //如果收到响应是
                        if ("{".equals(s) || "[".equals(s)) {
                            Logger.t(Tag).json(message);
                        } else {
                            Logger.t(Tag).d(message);
                        }
                    }
                }
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.sslSocketFactory(HttpsUtils.getSslSocketFactory().sSLSocketFactory, HttpsUtils.getSslSocketFactory().trustManager);
//            builder.sslSocketFactory(HttpsUtils.getSslSocketFactory(new InputStream[]{
//                    BWApplication.getInstance().getResources().openRawResource(R.raw.yu)}).sSLSocketFactory,HttpsUtils.getSslSocketFactory(new InputStream[]{
//                    BWApplication.getInstance().getResources().openRawResource(R.raw.yu)}).trustManager);
            builder.addInterceptor(loggingInterceptor);
        }
        File cacheFile = new File(Constant.PATH_CACHE);
        Cache cache = new Cache(cacheFile, CACHE_MAXSIZE);
        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!FunctionHelper.isNetworkConnected(BWApplication.getInstance())) {
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                Response response = chain.proceed(request);
                if (FunctionHelper.isNetworkConnected(BWApplication.getInstance())) {
                    int maxAge = 0;
                    // 有网络时, 不缓存, 最大保存时长为0
                    response.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .removeHeader("Pragma")
                            .build();
                } else {
                    // 无网络时，设置超时为4周
                    int maxStale = MAXSTALE;
                    response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .removeHeader("Pragma")
                            .build();
                }
                return response;
            }
        };
        Interceptor apikey = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response;
                request = request.newBuilder()
                        .header("Cookie", SpManager.getCookie(BWApplication.getInstance()))
                        .header("User-Agent", BWApplication.getInstance().getUserAgent())
                        .build();
                response = chain.proceed(request);
                if (request.url() != null) {
                    String method = request.url().toString();
                    if (method.contains("user/user/register2") || method.contains("user/user/login")
                            || method.contains("user/connect/login")) {
                        // 获取cookie值
                        List<String> headers = response.headers("Set-Cookie");
                        if (!ListUtils.isEmpty(headers)) {
                            for (String header : headers) {
                                String nhStrPrefix = "NaHuo.UserLogin";
                                if (header.toString().contains(nhStrPrefix)) {
                                    // 保存cookie
                                    String cookieFulllStr = header.toString();
                                    // 保存cookie
                                    String cookie = cookieFulllStr.substring(cookieFulllStr.indexOf(nhStrPrefix),
                                            cookieFulllStr.length());
                                    PublicData.setCookieOnlyAtInit(cookie);
                                    if (method.contains("user/user/register2")) {
                                        SpManager.setCookie(BWApplication.getInstance(), cookie);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
//                if (response.code()==504)
//                    throw  new RuntimeException("504");
                return getLogResponse(request, response);
            }
        };
        // 设置统一的请求头部参数
        builder.addInterceptor(apikey);
        //设置缓存
        builder.addNetworkInterceptor(cacheInterceptor);
        builder.addInterceptor(cacheInterceptor);
        builder.cache(cache);
        //设置超时
        builder.connectTimeout(CONNECTTIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(READTIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITETIMEOUT, TimeUnit.SECONDS);
        //builder.connectionPool(new ConnectionPool(5, 1, TimeUnit.SECONDS));
        //错误重连
        builder.retryOnConnectionFailure(true);
        return builder.build();
    }

    public static OkHttpClient provideNoCashClient(OkHttpClient.Builder builder) {
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    synchronized (HttpLoggingInterceptor.class) {
                        if (TextUtils.isEmpty(message)) return;
                        Logger.t(Tag).d(message);
//                        String s = message.substring(0, 1);
//                        //如果收到响应是
//                        if ("{".equals(s) || "[".equals(s)) {
//                            Logger.t(Tag).json(message);
//                        } else {
//                            Logger.t(Tag).d(message);
//                        }
                    }
                }
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            builder.sslSocketFactory(HttpsUtils.getSslSocketFactory(new InputStream[]{
//                    BWApplication.getInstance().getResources().openRawResource(R.raw.yu)}).sSLSocketFactory,HttpsUtils.getSslSocketFactory(new InputStream[]{
//                    BWApplication.getInstance().getResources().openRawResource(R.raw.yu)}).trustManager);
            builder.sslSocketFactory(HttpsUtils.getSslSocketFactory().sSLSocketFactory, HttpsUtils.getSslSocketFactory().trustManager);
            builder.addInterceptor(loggingInterceptor);
        }
//        File cacheFile = new File(Constant.PATH_CACHE);
//        Cache cache = new Cache(cacheFile, CACHE_MAXSIZE);
//        Interceptor cacheInterceptor = new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request request = chain.request();
//                Response response = chain.proceed(request);
//                int maxAge = 0;
//                //  不缓存, 最大保存时长为0
//                response.newBuilder()
//                        .header("Cache-Control", "public, max-age=" + maxAge)
//                        .removeHeader("Pragma")
//                        .build();
//                return response;
//            }
//        };
        Interceptor apikey = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                request = request.newBuilder()
                        .header("Cookie", SpManager.getCookie(BWApplication.getInstance()))
                        .header("User-Agent", BWApplication.getInstance().getUserAgent())
                        .build();
                Response response = chain.proceed(request);
                if (request.url() != null) {
                    String method = request.url().toString();
                    if (method.contains("user/user/register2") || method.contains("user/user/login")
                            || method.contains("user/connect/login")) {
                        // 获取cookie值
                        List<String> headers = response.headers("Set-Cookie");
                        if (!ListUtils.isEmpty(headers)) {
                            for (String header : headers) {
                                String nhStrPrefix = "NaHuo.UserLogin";
                                if (header.toString().contains(nhStrPrefix)) {
                                    // 保存cookie
                                    String cookieFulllStr = header.toString();
                                    // 保存cookie
                                    String cookie = cookieFulllStr.substring(cookieFulllStr.indexOf(nhStrPrefix),
                                            cookieFulllStr.length());
                                    PublicData.setCookieOnlyAtInit(cookie);
                                    if (method.contains("user/user/register2")) {
                                        SpManager.setCookie(BWApplication.getInstance(), cookie);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                return getLogResponse(request, response);
            }
        };
        // 设置统一的请求头部参数
        builder.addInterceptor(apikey);
        //设置缓存
//        builder.addNetworkInterceptor(cacheInterceptor);
//        builder.addInterceptor(cacheInterceptor);
//        builder.cache(cache);
        //设置超时
        builder.connectTimeout(CONNECTTIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(READTIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITETIMEOUT, TimeUnit.SECONDS);
        //builder.connectionPool(new ConnectionPool(5, 1, TimeUnit.SECONDS));
        //错误重连
        builder.retryOnConnectionFailure(true);
        return builder.build();
    }

    @Nullable
    private static Response getLogResponse(Request request, Response response) throws IOException {
        if (BuildConfig.DEBUG) {
            String url = "", body = "", heads = "";
            MediaType mediaType = null;
            if (request != null) {
                if (request.url() != null)
                    url = request.url().toString();
            }
            if (response != null) {
                if (response.body() != null) {
                    mediaType = response.body().contentType();
                    body = response.body().string();
                }
                if (response.headers() != null)
                    heads = response.headers().toString();
            }
            if (mediaType == null) {
                return response;
            } else {
                Logger.t("INFO").i("发送请求=" +
                        url + "\n\n" + heads + "\n" + body);
                return response.newBuilder()
                        .body(ResponseBody.create(mediaType, body))
                        .build();
            }
        } else {
            return response;
        }
    }

    public static String getPostJson(Map<String, Object> params) throws Exception {
        JSONObject jsonObject = new JSONObject();
        // 实际请求的url中对参数顺序没有要求
        for (String k : params.keySet()) {
            // 需要对请求串进行urlencode，由于key都是英文字母，故此处仅对其value进行urlencode
            jsonObject.put(k, params.get(k));
        }
        return jsonObject.toString();
    }

    public static int Post_Media_Type_Json = 1;
    public static int Post_Media_Type_Form = 2;
    public static int POST_TYPE_SYNCHRO = 1;
    public static int POST_TYPE_ASYN = 2;

    public static <T>void post(String TAG,Context context, String method, int post_type, int post_media_type, Map<String, Object> paramsMap, boolean isShowDialog, String dialogMessage,
                            boolean isSynchroThread, final BuyerToolCallback<T> call) {
        Tag = "DEBUG-"+TAG;

        try {
            String url = (method.startsWith("user/") ? SERVERURL_V4 : SERVERURL) + method;
            final OkHttpClient okHttpClient = provideNoCashClient(provideNoCacheOkHttpBuilder());
            RequestBody body = null;
            if (post_media_type == Post_Media_Type_Json) {
                body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), getPostJson(paramsMap));
            } else if (post_media_type == Post_Media_Type_Form) {
                FormBody.Builder formBUilder = new FormBody.Builder();
                for (String key : paramsMap.keySet()) {
                    //追加表单信息
                    formBUilder.add(key, paramsMap.get(key).toString());
                }
                body = formBUilder.build();
            }
            LoadingDialog dialog = null;
            if (isShowDialog) {
                dialog = new LoadingDialog(BWApplication.getInstance());
                dialog.setMessage(dialogMessage);
                showDialog(dialog,call);
            }
            Request.Builder builder = new Request.Builder().url(url);
            final Request request = builder.post(body)
                    .build();
            if (post_type == POST_TYPE_ASYN) {
                okHttpClient.newCall(request).enqueue(new HttpCallback(call,method, new Callback<T>() {
                    @Override
                    public void onSuccess(T data) {
                        if (call != null)
                            call.onSuccess(data);
                    }

                    @Override
                    public void onFailure(String code, final String msg) {
                        // Log.e("yu", code + msg);
                        if (call != null)
                            call.onFailure(code, msg);
                    }
                }, dialog));
            } else if (post_type == POST_TYPE_SYNCHRO) {
                if (isSynchroThread) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Response response = null;
                            try {
                                response = okHttpClient.newCall(request).execute();

                                if (response.isSuccessful()) {
                                    String json = response.body().string();
                                    PinHuoResponse<T> bean = GsonHelper.jsonToMyObject(json, new TypeToken<PinHuoResponse<T>>(){});
                                    if (bean!=null){
                                        if (bean.isResult()){
                                            call.onSuccess(bean.getData());
                                        }else {
                                            if (call != null)
                                                call.onFailure(bean.getCode(), bean.getMessage());
                                        }
                                    }else {
                                        if (call != null)
                                            call.onFailure(String.valueOf(Error_NUll_Exception), Error_Message_Null);
                                    }
                                } else {
                                    if (call != null)
                                        call.onFailure(String.valueOf(response.code()), response.message());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (call != null)
                                    call.onFailure(String.valueOf(Error_Code_Exception), e.getMessage());
                            }
                        }
                    }).start();
                } else {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String json = response.body().string();
                        PinHuoResponse<T> bean = GsonHelper.jsonToMyObject(json, new TypeToken<PinHuoResponse<T>>(){});
                        if (bean!=null){
                            if (bean.isResult()){
                                call.onSuccess(bean.getData());
                            }else {
                                if (call != null)
                                    call.onFailure(bean.getCode(), bean.getMessage());
                            }
                        }else {
                            if (call != null)
                                call.onFailure(String.valueOf(Error_NUll_Exception), Error_Message_Null);
                        }
                    } else {
                        if (call != null)
                            call.onFailure(String.valueOf(response.code()), response.message());
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            if (call != null)
                call.onFailure(String.valueOf(Error_Code_Exception), e.getMessage());
        }
    }
    public static String getParam(Map<String, Object> params) throws UnsupportedEncodingException {
        if (ListUtils.isEmpty(params))
            return  "";
        StringBuilder url = new StringBuilder();
        url.append("?");
        // 实际请求的url中对参数顺序没有要求
        for (String k : params.keySet()) {
            // 需要对请求串进行urlencode，由于key都是英文字母，故此处仅对其value进行urlencode
            url.append(k).append("=").append(URLEncoder.encode(params.get(k).toString(), "UTF-8")).append("&");
        }
        return url.toString().substring(0, url.length() - 1);
    }
    public static  void get(String TAG,Context context,String method, int post_type, Map<String, Object> paramsMap, boolean isShowDialog, String dialogMessage,
                           boolean isSynchroThread, final  BuyerToolCallback call) {
        Tag = "DEBUG-"+TAG;
        try {
            String url = (method.startsWith("user/") ? SERVERURL_V4 : SERVERURL) + method+getParam(paramsMap);
            final OkHttpClient okHttpClient = provideNoCashClient(provideNoCacheOkHttpBuilder());
            LoadingDialog dialog = null;
            if (isShowDialog) {
                dialog = new LoadingDialog(context);
                dialog.setMessage(dialogMessage);
                showDialog(dialog,call);
            }
            Request.Builder builder = new Request.Builder().url(url);
            final Request request = builder.get()
                    .build();
            if (post_type == POST_TYPE_ASYN) {
                okHttpClient.newCall(request).enqueue(new HttpCallback(call,method, new Callback() {
                    @Override
                    public void onSuccess(Object data) {
                        if (call != null)
                            call.onSuccess(data);
                    }

                    @Override
                    public void onFailure(String code, String msg) {
                        if (call != null)
                            call.onFailure(code,msg);

                    }
                }, dialog));
            } else if (post_type == POST_TYPE_SYNCHRO) {
                if (isSynchroThread) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Response response = null;
                            try {
                                response = okHttpClient.newCall(request).execute();

                                if (response.isSuccessful()) {
                                    String json = response.body().string();
                                    PinHuoResponse bean = GsonHelper.jsonToMyObject(json, new TypeToken<PinHuoResponse>(){});
                                    if (bean!=null){
                                        if (bean.isResult()){
                                            call.onSuccess(bean.getData());
                                        }else {
                                            if (call != null)
                                                call.onFailure(bean.getCode(), bean.getMessage());
                                        }
                                    }else {
                                        if (call != null)
                                            call.onFailure(String.valueOf(Error_NUll_Exception), Error_Message_Null);
                                    }
                                } else {
                                    if (call != null)
                                        call.onFailure(String.valueOf(response.code()), response.message());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (call != null)
                                    call.onFailure(String.valueOf(Error_Code_Exception), e.getMessage());
                            }
                        }
                    }).start();
                } else {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String json = response.body().string();
                        PinHuoResponse bean = GsonHelper.jsonToMyObject(json, new TypeToken<PinHuoResponse>(){});
                        if (bean!=null){
                            if (bean.isResult()){
                                call.onSuccess(bean.getData());
                            }else {
                                if (call != null)
                                    call.onFailure(bean.getCode(), bean.getMessage());
                            }
                        }else {
                            if (call != null)
                                call.onFailure(String.valueOf(Error_NUll_Exception), Error_Message_Null);
                        }
                    } else {
                        if (call != null)
                            call.onFailure(String.valueOf(response.code()), response.message());
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            if (call != null)
                call.onFailure(String.valueOf(Error_Code_Exception), e.getMessage());
        }
    }
    public static PinHuoResponse<UploadRepose> uploadEdPic(String method, String json
                                   ) throws Exception {
        String url = (method.startsWith("user/") ? SERVERURL_V4 : SERVERURL) + method;
        if (mOkHttpClient==null)
        mOkHttpClient= provideNoCashClient(provideNoCacheOkHttpBuilder());
        RequestBody body = null;
        body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
//            LoadingDialog dialog=null;
//            if (isShowDialog) {
//                dialog  = new LoadingDialog(BWApplication.getInstance());
//                dialog.setMessage(dialogMessage);
//                showDialog(dialog);
//            }
        Request.Builder builder = new Request.Builder().url(url);
        final Request request = builder.post(body)
                .build();
        Response response = mOkHttpClient.newCall(request).execute();
        String js = response.body().string();
        PinHuoResponse<UploadRepose> data = GsonHelper.jsonToMyObject(js, new TypeToken<PinHuoResponse<UploadRepose>>(){});


        return data;
    }

    public static void showDialog(final LoadingDialog dialog,BuyerToolCallback buyerToolCallback) {
        if (buyerToolCallback!=null)
            buyerToolCallback.showDialog(dialog);

    }

    public static void closeDialog(final LoadingDialog dialog,BuyerToolCallback buyerToolCallback) {

        if (buyerToolCallback!=null)
            buyerToolCallback.closeDialog(dialog);
    }

    public interface BuyerToolCallback<T> {

        /**
         * 登录成功
         */
        void onSuccess(T data);
        /**
         * 登录失败
         *
         * @param code 错误码
         * @param msg  错误信息
         */
        void onFailure(String code, final String msg);
        void showDialog(LoadingDialog dialog);
        void closeDialog(LoadingDialog dialog);

    }

    public interface Callback<T> {

        /**
         * 登录成功
         */
        void onSuccess(T data);

        /**
         * 登录失败
         *
         * @param code 错误码
         * @param msg  错误信息
         */
        void onFailure(String code, final String msg);

    }

    public static  class   HttpCallback implements okhttp3.Callback {
        private Callback  callback;
        private String module;
        private LoadingDialog dialog;
        private BuyerToolCallback toolCallback;
        public HttpCallback(BuyerToolCallback toolCallback,String module, Callback  callback, LoadingDialog dialog) {
            this.callback = callback;
            this.module = module;
            this.dialog = dialog;
            this.toolCallback=toolCallback;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            closeDialog(dialog,toolCallback);
            if (this.callback!=null)
                callback.onFailure(String.valueOf(Error_Call_Exception),e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            closeDialog(dialog,toolCallback);
            if (response.isSuccessful()) {
                String json = response.body().string();
               PinHuoResponse bean = GsonHelper.jsonToMyObject(json, new TypeToken<PinHuoResponse>(){});
                if (bean!=null){
                    if (bean.isResult()){
                        if (this.callback != null)
                        this.callback.onSuccess(bean.getData());
                    }else {
                        if (this.callback != null)
                            this.callback.onFailure(bean.getCode(), bean.getMessage());
                    }
                }else {
                    if (this.callback != null)
                        this.callback.onFailure(String.valueOf(Error_NUll_Exception), Error_Message_Null);
                }
            } else {
                if (this.callback != null)
                    this.callback.onFailure(String.valueOf(response.code()), response.message());
            }
        }


    }

}
