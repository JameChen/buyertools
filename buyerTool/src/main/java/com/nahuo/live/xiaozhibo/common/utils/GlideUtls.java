package com.nahuo.live.xiaozhibo.common.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by jame on 2019/4/26.
 */

public class GlideUtls {
    public static void load(Context context, int resourceId, String url, ImageView imageView){
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(resourceId);
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(imageView);
    }
    public static void loadRoundedCorners(Context context, int resourceId, String url, ImageView imageView){
        //设置图片圆角角度
        RoundedCorners roundedCorners= new RoundedCorners(16);
//通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(300, 300)
                .placeholder(resourceId);
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(imageView);
    }
}
