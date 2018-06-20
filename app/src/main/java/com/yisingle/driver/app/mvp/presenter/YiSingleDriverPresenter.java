package com.yisingle.driver.app.mvp.presenter;

import android.util.Log;

import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.yisingle.baselibray.base.BasePresenter;
import com.yisingle.driver.app.AppManager;
import com.yisingle.driver.app.activity.NewOrderActivity;
import com.yisingle.driver.app.activity.OrderActivity;
import com.yisingle.driver.app.base.LoginConstant;
import com.yisingle.driver.app.data.DriverEntity;
import com.yisingle.driver.app.data.FindOrderRequest;
import com.yisingle.driver.app.data.OrderEntity;
import com.yisingle.driver.app.data.SocketData;
import com.yisingle.driver.app.data.SocketHeadData;
import com.yisingle.driver.app.http.ApiService;
import com.yisingle.driver.app.http.RetrofitManager;
import com.yisingle.driver.app.mvp.IYiSingleDriver;
import com.yisingle.driver.app.rx.ApiSubscriber;
import com.yisingle.driver.app.rx.RxUtils;
import com.yisingle.driver.app.utils.JsonUtils;
import com.yisingle.driver.app.websocket.WebSocketManager;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;

/**
 * Created by jikun on 17/7/24.
 */

public class YiSingleDriverPresenter extends BasePresenter<IYiSingleDriver.IYiSingleDriverView> implements IYiSingleDriver.IYiSingleDriverPresenter {

    private Subscription subscription;

    private Gson gson = new Gson();


    private String TAG = YiSingleDriverPresenter.class.getSimpleName();

    public YiSingleDriverPresenter(IYiSingleDriver.IYiSingleDriverView view) {
        super(view);
        WebSocketManager.getInstance().setOnWebSocketListener(new WebSocketManager.OnWebSocketListener() {
            @Override
            public void onConnectSuccess() {
                sengHeadBeatDataDelay(2);
            }

            @Override
            public void onConnectFailed() {
                connectWebSocketDelay(2);

            }

            @Override
            public void onDisConnect() {
                connectWebSocketDelay(2);
            }


            @Override
            public void onGetMsg(String respones) {

                try {


                    if (JsonUtils.isGoodJson(respones)) {

                        SocketHeadData headData = gson.fromJson(respones, SocketHeadData.class);
                        switch (headData.getType()) {
                            case SocketData.Type.HEART_BEAT:
                                sengHeadBeatDataDelay(5);
                                break;
                            case SocketData.Type.ORDER_NEW:
                                SocketData<OrderEntity> data = gson.fromJson(respones, new TypeToken<SocketData<OrderEntity>>() {
                                }.getType());
                                mView.findOrderSuccess(data.getResponse());
                                WebSocketManager.getInstance().sendData(respones);//收到消息后把消息返回的服务器告诉服务器我接受到了订单
                                break;
                            case SocketData.Type.PRICIE_ORDER:
                                SocketData<OrderEntity> price = gson.fromJson(respones, new TypeToken<SocketData<OrderEntity>>() {
                                }.getType());
                                //发送价格订单到OrderService
                                EventBus.getDefault().post(price.getResponse());
                                break;
                            default:
                                break;
                        }
                    } else {

                        Logger.e("socket收到数据respones=" + respones + "不合法");

                    }
                } catch (Exception e) {
                    Logger.e("socket收到数据respones=" + respones + "不合法Exception=" + e.toString());
                }
                ;
            }
        });
    }

    @Override
    public void repeatFindOrder() {
        if (null != subscription) {
            subscription.unsubscribe();
        }

        subscription = Observable.interval(10, TimeUnit.SECONDS).filter(aLong -> {
            Log.e("测试代码", "测试代码--" + TAG + "--repeatFindOrder");
            return isShowNewOrder();


        }).subscribe(aLong -> getOrder());

    }

    @Override
    public void connectSocket() {

        WebSocketManager.getInstance().connectWebSocket();
    }

    private void connectWebSocketDelay(int time) {
        Observable.just("").delay(time, TimeUnit.SECONDS).subscribe(
                s -> {
                    WebSocketManager.getInstance().connectWebSocket();
                }
        );
    }

    private void sengHeadBeatDataDelay(int time) {
        Observable.just("").delay(time, TimeUnit.SECONDS).subscribe(
                s -> {
                    WebSocketManager.getInstance().sendHeartbeatData();
                }
        );
    }


    private void getOrder() {

        int driverId = SPUtils.getInstance().getInt(LoginConstant.LOGIN_DRIVER_ID, -99);
        Integer[] states = new Integer[]{OrderEntity.OrderState.WATI_NEW, OrderEntity.OrderState.WATI_OLD};


        FindOrderRequest params1 = new FindOrderRequest(Arrays.asList(states), driverId);
        RetrofitManager.getInstance().createService(ApiService.class).findOrder(params1).compose(RxUtils.apiChildTransformer())
                .subscribe(new ApiSubscriber<OrderEntity>(mView, 0) {
                    @Override
                    public void onError(Throwable e) {
                        Log.e("测试代码", "测试代码---onError" + TAG + "e=" + e.toString());
                    }

                    @Override
                    public void onNext(OrderEntity orderEntity) {
                        if (isShowNewOrder()) {
                            mView.findOrderSuccess(orderEntity);
                        } else {
                            Log.e("测试代码", "测试代码---" + TAG + "已经有新订单");
                        }
                    }
                });


    }


    /**
     * @return
     */
    private boolean isShowNewOrder() {

        int state = SPUtils.getInstance().getInt(LoginConstant.LOGIN_DRIVER_STATE, -1);


        boolean isFrontActivity = (AppManager.ActivityStack.isExists(NewOrderActivity.class)) || (AppManager.ActivityStack.isExists(OrderActivity.class));

        boolean isShow = state == DriverEntity.DriverState.WATI_FOR_ORDER && !isFrontActivity;
        Log.e("测试代码", "测试代码isShowNewOrder=" + isShow);
        return isShow;
    }


    @Override
    public void onDestory() {
        super.onDestory();
        if (null != subscription) {
            subscription.unsubscribe();
        }
        WebSocketManager.getInstance().closeWebSocket();
    }
}
