package com.nahuo.buyertool.customview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by jame on 2017/5/10.
 */

public class MyJCVideoPlayerStandard extends JCVideoPlayerStandard {
    public MyJCVideoPlayerStandard(final Context context) {
        super(context);

    }

    @Override
    public void init(final Context context) {
        super.init(context);
        fullscreenButton.setVisibility(GONE);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).finish();
            }
        });
    }

    public MyJCVideoPlayerStandard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
