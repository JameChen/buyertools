package com.nahuo.buyertool.controls;

import android.app.Activity;
import android.app.Dialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.adapter.ColorGridItemAdapter;
import com.nahuo.buyertool.adapter.LabelGridItemAdapter;
import com.nahuo.buyertool.adapter.SizeGridItemAdapter;
import com.nahuo.library.helper.FunctionHelper;

public class ColorSizeSelectMenu extends Dialog implements OnItemClickListener, View.OnClickListener {

    private Activity mActivity;
    private View mRootView;
    private GridView mGridView;
    private TextView mTvTitle;
    private EditText mEtInput;
    private ProgressBar mPbInput;
    private Button mBtnAdd, mBtnOk, mBtnDel;
    private ImageView iv_finish;
    // private List<String> mItems = new ArrayList<String>();
    // private List<Boolean> mSelect = new ArrayList<Boolean>();
    // private OnClickListener mAddListener;
    private ColorSizeOperateCallback mSelectedCallback;
    // private MAdapter mAdapter;
    private String mTitle = "";
    private View mMenuContent;
    private ColorGridItemAdapter mColorAdatper;
    private SizeGridItemAdapter mSizeAdapter;
    private LabelGridItemAdapter mLabelAdapter;
    private CheckBox cb_all;


    private boolean isSelectAll;

    public ColorSizeSelectMenu setSelectAll(boolean selectAll) {
        isSelectAll = selectAll;
        return this;
    }

    public ColorSizeSelectMenu(Activity activity) {
        super(activity, R.style.popDialog);
        this.mActivity = activity;
        initViews();
    }


    private void initViews() {

        mRootView = LayoutInflater.from(mActivity).inflate(R.layout.color_size_select_menu, null);
        mMenuContent = mRootView.findViewById(R.id.menu_content);
        mTvTitle = (TextView) mRootView.findViewById(android.R.id.text1);
        mGridView = (GridView) mRootView.findViewById(android.R.id.list);
        mEtInput = (EditText) mRootView.findViewById(android.R.id.edit);
        mPbInput = (ProgressBar) mRootView.findViewById(android.R.id.progress);
        mBtnAdd = (Button) mRootView.findViewById(android.R.id.button1);
        mBtnDel = (Button) mRootView.findViewById(android.R.id.button2);
        mBtnOk = (Button) mRootView.findViewById(android.R.id.button3);
        iv_finish = (ImageView) mRootView.findViewById(R.id.iv_finish);
        cb_all = (CheckBox) mRootView.findViewById(R.id.cb_all);
        cb_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb_all != null) {
                    if (cb_all.isPressed()) {
                        if (mSelectedCallback != null) {
                            mSelectedCallback.selectAllItems(false);
                        }
                    }
                }
            }
        });
        iv_finish.setOnClickListener(this);
        mGridView.setOnItemClickListener(this);
        mBtnAdd.setOnClickListener(this);
        mBtnDel.setOnClickListener(this);
        mBtnOk.setOnClickListener(this);
        setCanceledOnTouchOutside(true);
//        DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        };
        this.setContentView(mRootView);
        //setOnKeyListener(keylistener);
        setCancelable(true);
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager m = mActivity.getWindowManager();
        Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
        WindowManager.LayoutParams p = this.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = d.getWidth(); //宽度设置为屏幕
        //p.height = d.getHeight() * 4 / 5;
        this.getWindow().setAttributes(p); //设置生效
    }

    public ColorSizeSelectMenu setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    public ColorSizeSelectMenu setInputHint(String hint) {
        mEtInput.setHint(hint);
        return this;
    }

    public void showProgress(boolean show) {
        mPbInput.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    // public ColorSizeSelectMenu setItems(List<String> items) {
    // this.mItems = items;
    // for (int i = 0; i < mItems.size(); i++) {
    // mSelect.add(false);
    // }
    // return this;
    // }

    public ColorSizeSelectMenu setColorAdatper(ColorGridItemAdapter colorAdapter) {
        this.mColorAdatper = colorAdapter;
        return this;
    }

    public ColorGridItemAdapter getColorGridItemAdapter() {
        return mColorAdatper;
    }

    public ColorSizeSelectMenu setSizeAdatper(SizeGridItemAdapter sizeAdapter) {
        this.mSizeAdapter = sizeAdapter;
        return this;
    }

    public SizeGridItemAdapter getSizeGridItemAdapter() {
        return mSizeAdapter;
    }

    public ColorSizeSelectMenu setLabelAdapter(LabelGridItemAdapter labelAdapter) {
        this.mLabelAdapter = labelAdapter;
        return this;
    }

    public LabelGridItemAdapter getLabelAdapter() {
        return mLabelAdapter;
    }

    // public ColorSizeSelectMenu setAddClickListener(View.OnClickListener listener) {
    // mAddListener = listener;
    // return this;
    // }

    public void show(View v) {
//        int[] location = new int[2];
//        v.getLocationOnScreen(location);
//        this.setWidth(LayoutParams.MATCH_PARENT);
//        this.setHeight(LayoutParams.MATCH_PARENT);
//        this.setContentView(mRootView);
//        this.setFocusable(true);
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//        this.setBackgroundDrawable(dw);
//        this.setFocusable(true);

//        mRootView.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                int height = mMenuContent.getTop();
//                int y = (int) event.getY();
//                if (event.getAction() == MotionEvent.ACTION_UP && y < height) {
//                    dismiss();
//                }
//                return true;
//            }
//        });
        if (cb_all != null) {
            cb_all.setChecked(isSelectAll);
        }
        mTvTitle.setText(mTitle);
        if (mColorAdatper != null) {
            mGridView.setAdapter(mColorAdatper);
        } else if (mSizeAdapter != null) {
            mGridView.setAdapter(mSizeAdapter);
        } else if (mLabelAdapter != null) {
            mGridView.setAdapter(mLabelAdapter);
        }
        this.show();
        // else {
        // MAdapter mAdapter = new MAdapter();
        // mGridView.setAdapter(mAdapter);
        // }
//
//        setAnimationStyle(R.style.BottomMenuAnim);
//        // showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1] - getHeight());
//        showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                this.dismiss();
                break;
            case android.R.id.button1:// add
                if (mSelectedCallback != null) {
                    mSelectedCallback.addItem(mEtInput.getText().toString().trim());
                }
                FunctionHelper.hideSoftInput(mEtInput.getWindowToken(), mActivity);
                mEtInput.setText("");
                break;
            case android.R.id.button2:// delete
                if (mSelectedCallback != null) {
                    mSelectedCallback.deleteItems();
                    // List<Integer> ps = new ArrayList<Integer>();
                    // List<String> texts = new ArrayList<String>();
                    // for (int i = mSelect.size() - 1; i >= 0; i--) {
                    // if (mSelect.get(i).booleanValue()) {
                    // ps.add(i);
                    // texts.add(mItems.get(i));
                    // }
                    // }
                    // // int[] intArray = ArrayUtils.toPrimitive(myList.toArray(new Integer[myList.size()]));
                    // int[] positions = new int[ps.size()];
                    // for (int i = 0; i < positions.length; i++) {
                    // positions[i] = ps.get(i).intValue();
                    // }
                    // mSelectedCallback.deleteItems(positions, (String[])texts.toArray(new String[texts.size()]));
                }
                break;
            case android.R.id.button3:// ok
                if (mSelectedCallback != null) {
                    mSelectedCallback.selectedItems();
                    // List<Integer> ps = new ArrayList<Integer>();
                    // List<String> texts = new ArrayList<String>();
                    // for (int i = mSelect.size() - 1; i >= 0; i--) {
                    // if (mSelect.get(i).booleanValue()) {
                    // ps.add(i);
                    // texts.add(mItems.get(i));
                    // }
                    // }
                    // // int[] intArray = ArrayUtils.toPrimitive(myList.toArray(new Integer[myList.size()]));
                    // int[] positions = new int[ps.size()];
                    // for (int i = 0; i < positions.length; i++) {
                    // positions[i] = ps.get(i).intValue();
                    // }
                    // mSelectedCallback.selectedItems(positions, (String[])texts.toArray(new String[texts.size()]));
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    public ColorSizeSelectMenu setOperateCallback(ColorSizeOperateCallback callback) {
        this.mSelectedCallback = callback;
        return this;
    }

    // public void addItem(String item) {
    // mItems.add(item);
    // mSelect.add(false);
    // // mAdapter.notifyDataSetChanged();
    // }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    public interface ColorSizeOperateCallback {

        void addItem(String text);

        void deleteItems();

        void selectedItems();

        void selectAllItems(boolean isSelect);
    }

    // private class MAdapter extends BaseAdapter {
    // @Override
    // public int getCount() {
    // return mItems.size();
    // }
    //
    // @Override
    // public Object getItem(int position) {
    // return mItems.get(position);
    // }
    //
    // @Override
    // public long getItemId(int position) {
    // return position;
    // }
    //
    // @Override
    // public View getView(final int position, View convertView, ViewGroup parent) {
    // ViewHolder holder;
    // if (null == convertView) {
    // convertView = LayoutInflater.from(mActivity).inflate(R.layout.color_size_select_menu_item, null);
    // holder = new ViewHolder();
    // holder.checkBox = (CheckBox)convertView.findViewById(android.R.id.checkbox);
    // convertView.setTag(holder);
    // } else {
    // holder = (ViewHolder)convertView.getTag();
    // }
    // holder.checkBox.setText(mItems.get(position));
    // holder.checkBox.setChecked(mSelect.get(position).booleanValue());
    //
    // holder.checkBox.setOnClickListener(new OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // CheckBox cb = (CheckBox)v;
    // mSelect.set(position, cb.isChecked());
    // // cb.setChecked(!cb.isChecked());
    // }
    // });
    // return convertView;
    // }
    // }
    // private final static class ViewHolder {
    // private CheckBox checkBox;
    // }
}
