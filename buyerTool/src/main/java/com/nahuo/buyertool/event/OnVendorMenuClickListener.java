package com.nahuo.buyertool.event;

import android.view.View;

public interface OnVendorMenuClickListener {
    void onChangeRateClick(View v, int position, float newPriceRate);
    void onUnJoinClick(View v, int position);
    void onReceiveAccountClick(View v, int position);
    void onItemClick(View v, int position);
}
