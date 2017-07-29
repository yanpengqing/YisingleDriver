package com.yisingle.driver.app.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yisingle.baselibray.base.BaseService;
import com.yisingle.driver.app.activity.NewOrderActivity;
import com.yisingle.driver.app.data.OrderEntity;
import com.yisingle.driver.app.mvp.IYiSingleDriver;
import com.yisingle.driver.app.mvp.presenter.YiSingleDriverPresenter;
import com.yisingle.driver.app.websocket.WebSocketManager;

import rx.Subscription;


/**
 * Created by jikun on 17/7/10.
 */

public class OrderService extends BaseService<YiSingleDriverPresenter> implements IYiSingleDriver.IYiSingleDriverView {


    private String TAG = OrderService.class.getSimpleName();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("测试代码", "测试代码--" + TAG + "onCreate");

    }

    @Override
    protected boolean isregisterEventBus() {
        return false;
    }

    @Override
    protected YiSingleDriverPresenter createPresenter() {
        return new YiSingleDriverPresenter(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("测试代码", "测试代码--" + TAG + "onStartCommand");
        //mPresenter.repeatFindOrder();
        mPresenter.connectSocket();
        return START_STICKY;
    }


    public static void startService(Context context) {
        Intent intent = new Intent(context, OrderService.class);
        context.startService(intent);
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, OrderService.class);
        context.stopService(intent);
    }


    @Override
    public void findOrderSuccess(OrderEntity orderEntity) {
        Log.e("测试代码", "测试代码---收到订单显示" + TAG + "NewOderActivity");
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), NewOrderActivity.class);
        intent.putExtra("OrderEntity", orderEntity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WebSocketManager.getInstance().closeWebSocket();
    }
}
