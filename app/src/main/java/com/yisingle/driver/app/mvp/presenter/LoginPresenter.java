package com.yisingle.driver.app.mvp.presenter;

import com.blankj.utilcode.util.SPUtils;
import com.yisingle.baselibray.base.BasePresenter;
import com.yisingle.driver.app.data.DriverEntity;
import com.yisingle.driver.app.data.FindOrderRequest;
import com.yisingle.driver.app.data.OrderEntity;
import com.yisingle.driver.app.http.ApiService;
import com.yisingle.driver.app.http.RetrofitManager;
import com.yisingle.driver.app.mvp.ILogin;
import com.yisingle.driver.app.rx.ApiSubscriber;
import com.yisingle.driver.app.rx.RxUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by jikun on 17/7/9.
 */

public class LoginPresenter extends BasePresenter<ILogin.ILoginView> implements ILogin.ILoginPresenter {
    public LoginPresenter(ILogin.ILoginView view) {
        super(view);
    }

    @Override
    public void login(String phonenum, String password, int type) {

        Map<String, String> params = new HashMap<>();

        params.put("phonenum", phonenum);
        params.put("password", password);

        RetrofitManager.getInstance().createService(ApiService.class).loginDriver(params)
                .compose(RxUtils.apiChildTransformer())
                .subscribe(new ApiSubscriber<DriverEntity>(mView, type) {
                    @Override
                    public void onNext(DriverEntity data) {
                        mView.loginSuccess(data);
                    }
                });
    }


}
