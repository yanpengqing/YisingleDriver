package com.yisingle.driver.app.mvp.presenter;

import com.yisingle.baselibray.base.BasePresenter;
import com.yisingle.driver.app.data.DriverEntity;
import com.yisingle.driver.app.http.ApiService;
import com.yisingle.driver.app.http.RetrofitManager;
import com.yisingle.driver.app.mvp.IRegister;
import com.yisingle.driver.app.rx.ApiSubscriber;
import com.yisingle.driver.app.rx.RxUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jikun on 17/7/27.
 */

public class RegisterPresenter extends BasePresenter<IRegister.IRegisterView> implements IRegister.IRegisterPresenter {
    public RegisterPresenter(IRegister.IRegisterView view) {
        super(view);
    }

    @Override
    public void registerDriver(String phoneNumber, String passWord, String username, int type) {
        Map<String, String> params = new HashMap<>();

        params.put("phonenum", phoneNumber);
        params.put("password", passWord);
        params.put("driverName", username);

        RetrofitManager.getInstance().createService(ApiService.class).registerDriver(params)
                .compose(RxUtils.apiChildTransformer())
                .subscribe(new ApiSubscriber<DriverEntity>(mView, type) {
                    @Override
                    public void onNext(DriverEntity data) {
                        mView.registerSuccess(data);
                    }
                });
    }
}
