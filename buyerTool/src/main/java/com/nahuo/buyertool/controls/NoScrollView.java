package com.nahuo.buyertool.controls;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

/**
 * Created by jame on 2018/7/26.
 */

public class NoScrollView extends NestedScrollView {
    private boolean noscroll = false;

    public void setNoscroll(boolean noscroll) {
        this.noscroll = noscroll;
    }

    public NoScrollView(Context context) {
        super(context);
    }

    public NoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return super.computeScrollDeltaToGetChildRectOnScreen(rect);
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        return noscroll;
    }
}
