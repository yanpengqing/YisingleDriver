package com.yisingle.driver.app.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.RegexUtils;
import com.yisingle.baselibray.base.BaseActivity;
import com.yisingle.driver.app.R;
import com.yisingle.driver.app.data.DriverEntity;
import com.yisingle.driver.app.mvp.IRegister;
import com.yisingle.driver.app.mvp.presenter.RegisterPresenter;
import com.yisingle.driver.app.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jikun on 17/7/27.
 */

public class RegisterActivity extends BaseActivity<RegisterPresenter> implements IRegister.IRegisterView {


    @BindView(R.id.et_phonenum)
    EditText et_phonenum;

    @BindView(R.id.et_password)
    EditText et_password;

    @BindView(R.id.et_confim_password)
    EditText et_confim_password;

    @BindView(R.id.et_user_nanme)
    EditText et_user_nanme;

    @BindView(R.id.bt_register)
    Button bt_register;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_register;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected boolean isregisterEventBus() {
        return false;
    }

    @Override
    protected RegisterPresenter createPresenter() {
        return new RegisterPresenter(this);
    }

    @Override
    public void registerSuccess(DriverEntity data) {

        ToastUtils.show("注册成功");
        finish();

    }

    @OnClick(R.id.bt_register)
    protected void doRegister() {
        String phone = et_phonenum.getText().toString();

        String password = et_password.getText().toString();

        String confimpassword = et_confim_password.getText().toString();

        String username = et_user_nanme.getText().toString();


        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show("请输入账号");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ToastUtils.show("请输入密码");
            return;
        }

        if (TextUtils.isEmpty(confimpassword)) {
            ToastUtils.show("请输入确认密码");
            return;
        }


        if (TextUtils.isEmpty(username)) {
            ToastUtils.show("请输入用户名");
            return;
        }

        if (!password.equals(confimpassword)) {
            ToastUtils.show("两次密码不一致，请重新输入");
            return;
        }


        if (!RegexUtils.isMobileExact(phone)) {
            ToastUtils.show("请输入正确的手机号码");
            return;
        }
        mPresenter.registerDriver(phone, password, username, 0);
    }
}
