package com.nahuo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by jame on 2019/5/15.
 */

public class InputEditView  extends EditText{

    public InputEditView(Context context) {
        super(context);
    }

    public InputEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InputEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public InputEditView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public interface BackListener {
        void back(TextView textView);
    }

    private BackListener listener;

    public void setBackListener(BackListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (listener != null) {
                listener.back(this);
            }
        }
        return false;
    }

}
