package com.nahuo.buyertool.api;

import android.content.Context;

public class OtherAPI {

    /**
     * @description 获取短网址
     * @author pj
     */
    public static String getShortUrl(Context context, String url) throws Exception {
        // Map<String, String> params = new HashMap<String, String>();
        // params.put("url", url);
        // return HttpUtils.httpPost("http://dwz.cn/create.php", "", params);
        return HttpUtils.get("http://api.t.sina.com.cn/short_url/shorten.json?source=1939441632&url_long=" + url);
    }

}
