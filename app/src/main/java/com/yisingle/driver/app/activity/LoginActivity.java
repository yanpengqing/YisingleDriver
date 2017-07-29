package com.yisingle.driver.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;

import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SPUtils;
import com.yisingle.baselibray.base.BaseActivity;
import com.yisingle.driver.app.R;
import com.yisingle.driver.app.base.LoginConstant;
import com.yisingle.driver.app.data.DriverEntity;
import com.yisingle.driver.app.data.OrderEntity;
import com.yisingle.driver.app.mvp.ILogin;
import com.yisingle.driver.app.mvp.presenter.LoginPresenter;
import com.yisingle.driver.app.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by jikun on 17/7/7.
 */

public class LoginActivity extends BaseActivity<LoginPresenter> implements ILogin.ILoginView {
    @BindView(R.id.cb_checkbox)
    CheckBox cb_checkbox;

    @BindView(R.id.et_phonenum)
    EditText et_phonenum;

    @BindView(R.id.et_password)
    EditText et_password;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        String userName = SPUtils.getInstance().getString(LoginConstant.LOGIN_USER_NAME);

        String password = SPUtils.getInstance().getString(LoginConstant.LOGIN_PASSWORD);

        et_phonenum.setText(userName);
        et_password.setText(password);

//        cb_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Log.e("测试代码", "测试代码isChecked=" + isChecked);
//                cb_checkbox.setChecked(isChecked);
//            }
//        });

    }

    @Override
    protected boolean isregisterEventBus() {
        return false;
    }

    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter(this);
    }


    @OnClick(R.id.bt_login)
    public void toMain() {
        String phone = et_phonenum.getText().toString();

        String password = et_password.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show("请输入账号");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ToastUtils.show("请输入密码");
            return;
        }

        if (!RegexUtils.isMobileExact(phone)) {
            ToastUtils.show("请输入正确的手机号码");
            return;
        }


        mPresenter.login(phone, password, 0);


    }

    @Override
    public void loginSuccess(DriverEntity entity) {
        SPUtils.getInstance().put(LoginConstant.LOGIN_DRIVER_ID, entity.getId());
        SPUtils.getInstance().put(LoginConstant.LOGIN_USER_NAME, entity.getPhonenum());
        SPUtils.getInstance().put(LoginConstant.LOGIN_PASSWORD, entity.getPassword());
        SPUtils.getInstance().put(LoginConstant.LOGIN_DRIVER_STATE, entity.getState());
        Intent intent = new Intent();
        intent.putExtra("DriverEntity", entity);
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.tv_register)
    public void toRegisterActivity() {
        Intent intent = new Intent();
        intent.setClass(this, RegisterActivity.class);
        startActivity(intent);
    }


}
