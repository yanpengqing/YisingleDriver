package com.yisingle.driver.app.mvp.presenter;

import com.yisingle.baselibray.base.BasePresenter;
import com.yisingle.driver.app.data.DriverEntity;
import com.yisingle.driver.app.data.OrderEntity;
import com.yisingle.driver.app.http.ApiService;
import com.yisingle.driver.app.http.RetrofitManager;
import com.yisingle.driver.app.mvp.INewOrder;
import com.yisingle.driver.app.rx.ApiSubscriber;
import com.yisingle.driver.app.rx.RxUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jikun on 17/7/11.
 */

public class NewOrderPresenter extends BasePresenter<INewOrder.INewOrderView> implements INewOrder.INewOrderPresenter {
    public NewOrderPresenter(INewOrder.INewOrderView view) {
        super(view);
    }

    @Override
    public void acceptOrder(int orderId, int type) {
        Map<String, String> params = new HashMap<>();

        params.put("orderId", orderId + "");


        RetrofitManager.getInstance().createService(ApiService.class).acceptOrder(params)
                .compose(RxUtils.apiChildTransformer())
                .subscribe(new ApiSubscriber<OrderEntity>(mView, type) {
                    @Override
                    public void onNext(OrderEntity data) {
                        mView.acceptSuccess(data);
                    }
                });
    }
}
