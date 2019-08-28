package com.nahuo.buyertool.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.UploadBean.ExtendPropertyTypeListV2Bean.ExtendPropertyListBean;
import com.nahuo.buyertool.common.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jame on 2017/12/18.
 */

public class ExtendPropertyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>implements View.OnTouchListener,View.OnFocusChangeListener {
    Context context;
    public static final int TYPE_SIMPE = 0;
    public static final int TYPE_ET = 1;
    int type;
    Listener mListener;
    private boolean IsValue;
    private boolean IsMore;

    public void setMore(boolean more) {
        IsMore = more;
    }

    public void setValue(boolean value) {
        IsValue = value;
    }

    public ExtendPropertyAdapter(Context context) {
        this.context = context;
    }

    List<ExtendPropertyListBean> pareList = new ArrayList<>();

    public void setType(int type) {
        this.type = type;
    }

    public void setPareData(List<ExtendPropertyListBean> data) {
        this.pareList = data;
    }

    public void setSinIsSelect(ExtendPropertyListBean item) {
        if (item != null && !ListUtils.isEmpty(pareList)) {
            for (ExtendPropertyListBean bean : pareList) {
                if (bean.getID() == item.getID()) {
                    bean.isSelect = !bean.isSelect;
                } else {
                    bean.isSelect = false;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setMutltIsSelect(ExtendPropertyListBean item) {
        if (item != null && !ListUtils.isEmpty(pareList)) {
            for (ExtendPropertyListBean bean : pareList) {
                if (bean.getID() == item.getID()) {
                    bean.isSelect = !bean.isSelect;
                }
            }
        }
        notifyDataSetChanged();
    }


    public ExtendPropertyListBean getHasCheckData() {
        ExtendPropertyListBean fourListBean = null;
        if (!ListUtils.isEmpty(pareList)) {
            for (ExtendPropertyListBean bean : pareList) {
                if (bean.isSelect) {
                    fourListBean = bean;
                }
            }
        }
        return fourListBean;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SIMPE) {
            return new HolderSimple(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false));
        } else {
            return new HolderEt(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_et, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (IsValue) {
            return TYPE_ET;
        } else {
            return TYPE_SIMPE;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HolderSimple) {
            bindTypeSimple((HolderSimple) holder, position);
        } else if (holder instanceof HolderEt) {
            bindTypeEt((HolderEt) holder, position);
        }

    }

    private void bindTypeSimple(HolderSimple holder, int position) {
        View itemView = holder.itemView;
        ExtendPropertyListBean bean = pareList.get(position);
        if (bean != null) {
            holder.textView.setText(bean.getName());
            if (bean.isSelect) {
                holder.textView.setTextColor(context.getResources().getColor(R.color.white));
                holder.textView.setBackground(context.getResources().getDrawable(R.drawable.item_regent_press));
            } else {
                holder.textView.setBackground(context.getResources().getDrawable(R.drawable.item_regent_normal));
                holder.textView.setTextColor(context.getResources().getColor(R.color.item_text));
            }
        }

        itemView.setOnClickListener(new OnItemClickListener(position, IsMore));
    }
    private int selectedEditTextPosition = -1;
    private void bindTypeEt(HolderEt holder,final int position) {
        View itemView = holder.itemView;
        final ExtendPropertyListBean bean = pareList.get(position);
        if (bean != null) {
            holder.textView.setText(bean.getName());
            if (bean.isSelect) {
                holder.textView.setTextColor(context.getResources().getColor(R.color.white));
                holder.textView.setBackground(context.getResources().getDrawable(R.drawable.item_regent_press));
            } else {
                holder.textView.setBackground(context.getResources().getDrawable(R.drawable.item_regent_normal));
                holder.textView.setTextColor(context.getResources().getColor(R.color.item_text));
            }
            holder.editView.setOnTouchListener(this);
            holder.editView.setOnFocusChangeListener(this);
            holder.editView.setTag(position);
            if (TextUtils.isEmpty(bean.getValue())) {
                holder.editView.setHint("请输入");
                holder.editView.setText("");
            } else {
                holder.editView.setText(bean.getValue());
            }
            holder.editView.setSelection(holder.editView.length());
            if (selectedEditTextPosition != -1 && position == selectedEditTextPosition) { // 保证每个时刻只有一个EditText能获取到焦点
                holder.editView.requestFocus();
            } else {
                holder.editView.clearFocus();
            }

        }
        holder.textView.setOnClickListener(new OnItemClickListener(position, IsMore));
    }

    private TextWatcher mTextWatcher = new EditTextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (selectedEditTextPosition != -1) {
                String text = s.toString();
                ExtendPropertyListBean bean = pareList.get(selectedEditTextPosition);
                bean.setValue(text);
            }
        }
    };

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText editText = (EditText) v;
        if (hasFocus) {
            editText.addTextChangedListener(mTextWatcher);
        } else {
            editText.removeTextChangedListener(mTextWatcher);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            EditText editText = (EditText) v;
            selectedEditTextPosition = (int) editText.getTag();
        }
        return false;
    }

    public class  EditTextWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
    public class HolderSimple extends RecyclerView.ViewHolder {
        TextView textView;

        public HolderSimple(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }

    public class HolderEt extends RecyclerView.ViewHolder {
        TextView textView;
        EditText editView;

        public HolderEt(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            editView = (EditText) itemView.findViewById(R.id.editView_item);
        }
    }

    private class OnItemClickListener implements View.OnClickListener {
        private int mPos;
        private boolean IsMore;

        public OnItemClickListener(int pos, boolean IsMore) {
            mPos = pos;
            this.IsMore = IsMore;
        }

        @Override
        public void onClick(View v) {
            ExtendPropertyListBean pItem = pareList.get(mPos);
            if (IsMore) {
                setMutltIsSelect(pItem);
            } else {
                setSinIsSelect(pItem);
            }
            if (mListener != null)
                mListener.onExtendItemClick(pItem, IsMore);
        }
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        void onExtendItemClick(ExtendPropertyListBean item, boolean IsMore);
    }

    @Override
    public int getItemCount() {
        return pareList.size();
    }
}
