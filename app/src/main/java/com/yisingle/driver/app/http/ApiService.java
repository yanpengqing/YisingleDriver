package com.yisingle.driver.app.http;


import com.yisingle.driver.app.data.DriverEntity;
import com.yisingle.driver.app.data.DriverStatisticData;
import com.yisingle.driver.app.data.FindOrderRequest;
import com.yisingle.driver.app.data.MessageEntity;
import com.yisingle.driver.app.data.OrderEntity;

import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface ApiService {


    // 注册接口
    @POST("yisingle/driver/register.action")
    Observable<HttpResult<DriverEntity>> registerDriver(@Body Map<String, String> params);

    // 登陆接口
    @POST("yisingle/driver/login.action")
    Observable<HttpResult<DriverEntity>> loginDriver(@Body Map<String, String> params);


    @POST("yisingle/driver/changeDriverState.action")
    Observable<HttpResult<DriverEntity>> changeDriverState(@Body Map<String, String> params);


    @POST("yisingle/driver/findDriverOrder")
    Observable<HttpResult<OrderEntity>> findOrder(@Body FindOrderRequest params);

    @POST("yisingle/driver/acceptDriverOrder.action")
    Observable<HttpResult<OrderEntity>> acceptOrder(@Body Map<String, String> params);

    @POST("yisingle/driver/finishDriverOrder.action")
    Observable<HttpResult<OrderEntity>> finishDriverOrder(@Body Map<String, String> params);

    @POST("yisingle/driver/changeDriverOrderState.action")
    Observable<HttpResult<OrderEntity>> changeDriverOrderState(@Body Map<String, String> params);


    @POST("yisingle/driver/message/findMessage.action")
    Observable<HttpResult<List<MessageEntity>>> findMessage(@Body Map<String, String> params);

    @POST("yisingle/driver/getTodayStatistics.action")
    Observable<HttpResult<DriverStatisticData>> getOrderCountAndMoney(@Body Map<String, String> params);


}