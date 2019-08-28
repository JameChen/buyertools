package com.nahuo.buyertool;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.Bean.AccountBean;
import com.nahuo.buyertool.Bean.LoginBean;
import com.nahuo.buyertool.api.AccountAPI;
import com.nahuo.buyertool.common.Const;
import com.nahuo.buyertool.common.ListUtils;
import com.nahuo.buyertool.common.SpManager;
import com.nahuo.buyertool.model.PublicData;
import com.nahuo.buyertool.utils.ListDataSave;
import com.nahuo.library.controls.AutoCompleteTextViewEx;
import com.nahuo.library.controls.LoadingDialog;
import com.nahuo.library.helper.FunctionHelper;
import com.nahuo.library.helper.MD5Utils;

import java.util.ArrayList;
import java.util.List;

import ch.ielse.view.SwitchView;

public class LoginActivity extends Activity implements OnClickListener {

    private LoginActivity mContext = this;
    private AutoCompleteTextViewEx edtAccount;
    private EditText edtPassword;
    private Button btnLogin;
    private SwitchView switch_view;
    private LoadingDialog loadingDialog;
    private ListDataSave save;
    List<AccountBean> accountBeanList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        save=new ListDataSave(this, Const.LOGIN_PRE_KEY);
        //BWApplication.getInstance().addActivity(this);
        initView();
    }


    /**
     * 初始化视图
     */
    private void initView() {

        loadingDialog = new LoadingDialog(mContext);
        edtAccount = (AutoCompleteTextViewEx) mContext.findViewById(R.id.login_edtAccount);
        edtPassword = (EditText) mContext.findViewById(R.id.login_edtPassword);
        switch_view = (SwitchView) mContext.findViewById(R.id.switch_view);
        switch_view.toggleSwitch(true);
        btnLogin = (Button) mContext.findViewById(R.id.login_btnLogin);

        edtAccount.setOnSearchLogDeleteListener(new AutoCompleteTextViewEx.OnSearchLogDeleteListener() {
            @Override
            public void onSearchLogDeleted(String text) {
                String newChar = SpManager.deleteLoginAccounts(mContext, text);
                Log.i(getClass().getSimpleName(), "deleteSearchItemHistories:" + newChar);
                edtAccount.populateData(newChar, ",");
                edtAccount.getFilter().filter(edtAccount.getText());
            }
        });


        String username = SpManager.getLoginAccounts(mContext);
        edtAccount.populateData(username, ",");

        btnLogin.setOnClickListener(this);
        findViewById(R.id.img_see_pwd).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btnLogin:
                login();
                break;
            case R.id.img_see_pwd:
                int length = edtPassword.getText().length();
                if (length > 0) {
                    if (edtPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        edtPassword.invalidate();
                        edtPassword.setSelection(length);
                    } else {
                        edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        edtPassword.invalidate();
                        edtPassword.setSelection(edtPassword.getText().length());
                    }
                }
                break;
        }
    }

    /**
     * 登录
     */
    private void login() {
        // 验证用户录入
        if (!validateInput())
            return;
        // 验证网络
        if (!FunctionHelper.CheckNetworkOnline(mContext))
            return;
        // 执行登录操作
        new Task().execute();
    }

    /**
     * 验证用户输入
     */
    private boolean validateInput() {
        String phoneNo = edtAccount.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        // 验证手机号
        if (TextUtils.isEmpty(phoneNo)) {
            Toast.makeText(mContext, R.string.login_edtAccount_empty, Toast.LENGTH_SHORT).show();
            edtAccount.requestFocus();
            return false;
        }
        // 验证密码
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(mContext, R.string.login_edtPassword_empty, Toast.LENGTH_SHORT).show();
            edtPassword.requestFocus();
            return false;
        }
        return true;
    }

    private class Task extends AsyncTask<Object, Void, Object> {

        private String phoneNo, password;

        public Task() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.start(getString(R.string.login_doLogin_loading));
            phoneNo = edtAccount.getText().toString().trim();
            password = edtPassword.getText().toString().trim();

        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                return AccountAPI.getInstance().userNewLogin(phoneNo, password);

            } catch (Exception e) {
                e.printStackTrace();
                return "error:" + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (loadingDialog.isShowing()) {
                loadingDialog.stop();
            }
            if (result instanceof LoginBean) {
                LoginBean resultData = (LoginBean) result;
                if (resultData.isResult()) {
                    if (resultData!=null){
                        AccountBean accountBean=  resultData.getData();
                        if (accountBean!=null)
                            SpManager.setUserId(LoginActivity.this, accountBean.getUserID());
                        if (accountBean!=null&&switch_view.isOpened()){
                            accountBean.setPhoneNo(edtAccount.getText().toString());
                            accountBean.setPwd(MD5Utils.encrypt32bit(edtPassword.getText().toString()));
                            List<AccountBean> list=save.getDataList(Const.LOGIN_PRE_LIST_KEY);
                            accountBeanList.clear();
                            if (list!=null)
                            accountBeanList.addAll(list);
                            if (ListUtils.isEmpty(accountBeanList)){
                                accountBeanList.add(accountBean);
                                save.setDataList(Const.LOGIN_PRE_LIST_KEY,accountBeanList);
                            }else {
                                List<String> ids=new ArrayList<>();
                                for (AccountBean bean:accountBeanList) {
                                    ids.add(bean.getUserID()+"");
                                }
                                if (!ids.contains(accountBean.getUserID()+"")){
                                    accountBeanList.add(accountBean);
                                    save.setDataList(Const.LOGIN_PRE_LIST_KEY,accountBeanList);
                                }else {
                                    for (AccountBean bean:accountBeanList) {
                                        if (bean.getUserID()==accountBean.getUserID()){
                                            bean.setPwd(MD5Utils.encrypt32bit(password));
                                            bean.setPhoneNo(phoneNo);
                                            bean.setUserName(accountBean.getUserName());
                                            bean.setToken(accountBean.getToken());
                                            break;
                                        }
                                    }
                                    save.setDataList(Const.LOGIN_PRE_LIST_KEY,accountBeanList);
                                }
                            }
                        }

                        //Log.d("yu",accountBean.toString());
                    }
                    if (!TextUtils.isEmpty(PublicData.getCookie(mContext))) {
                        SpManager.setCookie(mContext, PublicData.getCookie(mContext));
                    }
                    SpManager.setSELECT_PURCHASE_DATA_Empty(mContext);
                    SpManager.setLoginAccount(mContext, edtAccount.getText().toString());
                    SpManager.setLoginPwd(mContext, MD5Utils.encrypt32bit(edtPassword.getText().toString()));
                    Intent intent = new Intent(mContext, MainAcivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    ViewHub.showLongToast(mContext, ((LoginBean) result).getMessage());
                }
            } else if (result instanceof String && ((String) result).startsWith("error:")) {
                ViewHub.showLongToast(mContext, ((String) result).replace("error:", ""));

            } else {
                ViewHub.showLongToast(mContext, result.toString());
            }

        }

    }
}
