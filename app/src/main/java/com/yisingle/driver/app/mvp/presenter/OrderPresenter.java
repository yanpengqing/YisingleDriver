package com.yisingle.driver.app.mvp.presenter;

import com.yisingle.baselibray.base.BasePresenter;
import com.yisingle.driver.app.data.OrderEntity;
import com.yisingle.driver.app.http.ApiService;
import com.yisingle.driver.app.http.RetrofitManager;
import com.yisingle.driver.app.mvp.IOrder;
import com.yisingle.driver.app.rx.ApiSubscriber;
import com.yisingle.driver.app.rx.RxUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by jikun on 17/7/5.
 */

public class OrderPresenter extends BasePresenter<IOrder.IOrderView> implements IOrder.IOrderPresenter {


    public OrderPresenter(IOrder.IOrderView view) {
        super(view);
    }


    @Override
    public void changeOrderState(int orderId, int oderState, int type) {
        Map<String, String> params = new HashMap<>();

        params.put("orderId", orderId + "");
        params.put("oderState", oderState + "");


        RetrofitManager.getInstance().createService(ApiService.class).changeDriverOrderState(params)
                .compose(RxUtils.apiChildTransformer())
                .subscribe(new ApiSubscriber<OrderEntity>(mView, type) {
                    @Override
                    public void onNext(OrderEntity data) {
                        mView.changeOrderStateSuccess(data);
                    }
                });
    }

    @Override
    public void finishOrder(int orderId, int type) {
        Map<String, String> params = new HashMap<>();

        params.put("orderId", orderId + "");


        RetrofitManager.getInstance().createService(ApiService.class).finishDriverOrder(params)
                .compose(RxUtils.apiChildTransformer())
                .subscribe(new ApiSubscriber<OrderEntity>(mView, type) {
                    @Override
                    public void onNext(OrderEntity data) {
                        mView.finishDriverSuccess(data);
                    }
                });
    }
}
