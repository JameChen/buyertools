package com.nahuo.buyertool.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.PrintBean;
import com.nahuo.buyertool.activity.PrintActivity;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.dialog.PrintDialog;

/**
 * Created by jame on 2017/10/30.
 */

public class PrintAdapter extends MyBaseAdapter<PrintBean> implements PrintDialog.PopDialogListener {
    Activity activity;
    PrintListener listener;

    public void setListener(PrintListener listener) {
        this.listener = listener;
    }

    public PrintAdapter(Activity context) {

        super(context);
        activity=context;
    }
    public void reMoveItem(PrintBean printBean){
        if (!ListUtils.isEmpty(mdata)){
            for (int i=0;i<mdata.size();i++) {
                if (printBean.getTime().equals(mdata.get(i).getTime())){
                    mdata.remove(i);
                }
            }
            notifyDataSetChanged();
            if (listener!=null)
                listener.onFinish(mdata.size());
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_print_list, parent, false);
            holder = new ViewHolder();
            holder.item_text_color_size = (TextView) convertView.findViewById(R.id.item_text_color_size);
            holder.item_text_code = (TextView) convertView.findViewById(R.id.item_text_code);
            holder.item_text_1 = (TextView) convertView.findViewById(R.id.item_text_1);
            holder.item_text_2 = (TextView) convertView.findViewById(R.id.item_text_2);
            holder.item_text_3 = (TextView) convertView.findViewById(R.id.item_text_3);
            holder.item_text_4 = (TextView) convertView.findViewById(R.id.item_text_4);
            holder.item_cover = (ImageView) convertView.findViewById(R.id.item_cover);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PrintBean bean = mdata.get(position);
        //bean.setName("萨达s所f大所%大df所$g大所多g撒  大所 多g撒多撒多撒gff多撒多d撒大所多f撒多撒gg多撒多撒多撒多撒多撒多撒多所的撒的撒的撒");
        holder.item_text_color_size.setText(bean.getColor() + "\n" + bean.getSize());
        holder.item_text_code.setText(bean.getItemcode()+"\n"+bean.getDMCode());
        holder.item_text_1.setText(bean.getTxt_1() + "");
        holder.item_text_2.setText(bean.getTxt_2() + "");
        holder.item_text_3.setText(bean.getTxt_3() + "");
        holder.item_text_4.setText(bean.getTxt_4() + "");
        if (!TextUtils.isEmpty(bean.getScan())) {
            holder.item_cover.setImageBitmap(encodeAsBitmap(bean.getScan()));
        }else {
            holder.item_cover.setImageResource(R.drawable.empty_photo);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintBean bean = mdata.get(position);
                PrintDialog.getInstance(activity).setTitle("是否打印该款式").setAction(PrintActivity.Style.ITEM_PRINT).setList(bean).setPositive(PrintAdapter.this).showDialog();
            }
        });
        return convertView;
    }

    Bitmap encodeAsBitmap(String str) {
        Bitmap bitmap = null;
        BitMatrix result = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            result = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, 200, 200);
            // 使用 ZXing Android Embedded 要写的代码
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(result);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException iae) { // ?
            return null;
        }
        return bitmap;
    }

    @Override
    public void onPopDialogButtonClick(PrintActivity.Style action, PrintBean bean) {
        if (action==PrintActivity.Style.ITEM_PRINT){
            if (listener!=null)
                listener.onImagePrintClick(bean);
        }
    }
    public interface PrintListener {
        void onImagePrintClick( PrintBean bean);
        void onFinish(int count);
    }
    private static class ViewHolder {
        private TextView item_text_color_size, item_text_code, item_text_1, item_text_2, item_text_3,item_text_4;
        private ImageView item_cover;
    }
}
