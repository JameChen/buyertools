package com.nahuo.buyertool.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.utils.ImageUtls;

public class PicActivity extends AppCompatActivity {
    public static String ETRA_PIC = "ETRA_PIC";
    private String code = "";
    private Bitmap bitmap = null;
    private ImageView iv;
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);
        iv= (ImageView) findViewById(R.id.iv_pic);
        tv=(TextView) findViewById(R.id.tv);
        findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        try {
            if (intent != null) {
                code = intent.getStringExtra(ETRA_PIC);
                if (!TextUtils.isEmpty(code)) {
                    tv.setText("二维码："+code);
                    bitmap=ImageUtls.encodeAsBitmap(code, 500, 500);
                    if (bitmap!=null){
                        iv.setImageBitmap(bitmap);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
