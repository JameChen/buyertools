package com.nahuo.buyertool.customview;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;

/**
 * Created by jame on 2017/11/6.
 */

public class MyDatePickerDialog extends DatePickerDialog {

    public MyDatePickerDialog(@NonNull Context context, @Nullable OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }

    public MyDatePickerDialog(@NonNull Context context, @StyleRes int theme, @Nullable OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        super(context, theme, listener, year, monthOfYear, dayOfMonth);
    }

    @Override
    protected void onStop() {
       // super.onStop();
    }
}
