package com.yisingle.driver.app.mvp.presenter;

import android.support.annotation.IntDef;

import com.blankj.utilcode.util.SPUtils;
import com.yisingle.baselibray.base.BasePresenter;
import com.yisingle.driver.app.base.LoginConstant;
import com.yisingle.driver.app.data.DriverEntity;
import com.yisingle.driver.app.data.DriverStatisticData;
import com.yisingle.driver.app.data.FindOrderRequest;
import com.yisingle.driver.app.data.MessageEntity;
import com.yisingle.driver.app.data.OrderEntity;
import com.yisingle.driver.app.http.ApiService;
import com.yisingle.driver.app.http.RetrofitManager;
import com.yisingle.driver.app.mvp.IMain;
import com.yisingle.driver.app.rx.ApiSubscriber;
import com.yisingle.driver.app.rx.RxUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jikun on 17/6/29.
 */

public class MainPresenter extends BasePresenter<IMain.IMainView> implements IMain.IMainPresenter {


    public MainPresenter(IMain.IMainView view) {
        super(view);
    }


    @Override
    public void changeDriverState(String phonenum, @DriverEntity.DriverState int state, int type) {
        Map<String, String> params = new HashMap<>();

        params.put("phonenum", phonenum);
        params.put("state", state + "");


        RetrofitManager.getInstance().createService(ApiService.class).changeDriverState(params)
                .compose(RxUtils.apiChildTransformer())
                .subscribe(new ApiSubscriber<DriverEntity>(mView, type) {
                    @Override
                    public void onNext(DriverEntity data) {
                        mView.changeDriverStateSuccess(data);
                    }
                });
    }

    @Override
    public void findOrder(int type) {
        int driverId = SPUtils.getInstance().getInt(LoginConstant.LOGIN_DRIVER_ID, -99);
        Integer[] states = new Integer[]{OrderEntity.OrderState.HAVE_TAKE,
                OrderEntity.OrderState.DRIVER_ARRIVE,
                OrderEntity.OrderState.PASSENGER_IN_CAR,
                OrderEntity.OrderState.PASSENGER_OUT_CAR};


        FindOrderRequest params = new FindOrderRequest(Arrays.asList(states), driverId);
        RetrofitManager.getInstance().createService(ApiService.class).findOrder(params).compose(RxUtils.apiChildTransformer())
                .subscribe(new ApiSubscriber<OrderEntity>(mView, false, type) {
                    @Override
                    public void onNext(OrderEntity data) {
                        mView.findOrderSuccess(data);
                    }
                });
    }

    @Override
    public void findMessage(int type) {
        Map<String, String> params = new HashMap<>();
        RetrofitManager.getInstance().createService(ApiService.class).findMessage(params)
                .compose(RxUtils.apiChildTransformer())
                .subscribe(new ApiSubscriber<List<MessageEntity>>(mView, type) {
                    @Override
                    public void onNext(List<MessageEntity> list) {
                        mView.findMessageSuccess(list);
                    }
                });
    }

    public void findCountMoney(String driverId, int type) {
        Map<String, String> params = new HashMap<>();
        params.put("driverId", driverId);
        RetrofitManager.getInstance().createService(ApiService.class).getOrderCountAndMoney(params)
                .compose(RxUtils.apiChildTransformer())
                .subscribe(new ApiSubscriber<DriverStatisticData>(mView, type) {
                    @Override
                    public void onNext(DriverStatisticData data) {
                        mView.getCountAndMoneySuccess(data);
                    }
                });

    }

    //添加支持注解的依赖到你的项目中，需要在build.gradle文件中的依赖块中添加：
    //dependencies { compile 'com.android.support:support-annotations:24.2.0' }
    @IntDef({PresenterHttpType.changeDriverState, PresenterHttpType.Order, PresenterHttpType.Message, PresenterHttpType.Count_Money})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PresenterHttpType {

        int changeDriverState = 0;
        int Order = 1;
        int Message = 2;
        int Count_Money = 3;

    }
}



