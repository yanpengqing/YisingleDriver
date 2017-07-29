package com.yisingle.driver.app.data;

import android.support.annotation.IntDef;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;

public class OrderEntity implements Serializable {


    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String phoneNum;


    private String startLatitude;


    private String startLongitude;


    private String endLatitude;


    private String endLongitude;


    private String startPlaceName;


    private String endPlaceName;


    private int orderState;

    //订单创建时间
    private long createTime;


    //司机端收到回复时间
    private long driverRelyTime;

    private BigDecimal orderPrice;


    //花费的总时间
    private long costTime;


    private DriverEntity driver;

    private UserEntity user;


    public String getStartPlaceName() {
        return startPlaceName;
    }

    public void setStartPlaceName(String startPlaceName) {
        this.startPlaceName = startPlaceName;
    }

    public String getEndPlaceName() {
        return endPlaceName;
    }

    public void setEndPlaceName(String endPlaceName) {
        this.endPlaceName = endPlaceName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(String startLatitude) {
        this.startLatitude = startLatitude;
    }

    public String getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(String startLongitude) {
        this.startLongitude = startLongitude;
    }

    public String getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(String endLatitude) {
        this.endLatitude = endLatitude;
    }

    public String getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(String endLongitude) {
        this.endLongitude = endLongitude;
    }

    public DriverEntity getDriver() {
        return driver;
    }

    public void setDriver(DriverEntity driver) {
        this.driver = driver;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }


    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getDriverRelyTime() {
        return driverRelyTime;
    }

    public void setDriverRelyTime(long driverRelyTime) {
        this.driverRelyTime = driverRelyTime;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public long getCostTime() {
        return costTime;
    }

    public void setCostTime(long costTime) {
        this.costTime = costTime;
    }

    public
    @OrderState
    int getOrderState() {
        return orderState;
    }

    public void setOrderState(@OrderState int orderState) {
        this.orderState = orderState;
    }


    //添加支持注解的依赖到你的项目中，需要在build.gradle文件中的依赖块中添加：
    //dependencies { compile 'com.android.support:support-annotations:24.2.0' }
    @IntDef({OrderState.WATI_NEW, OrderState.WATI_OLD, OrderState.HAVE_TAKE, OrderState.DRIVER_ARRIVE, OrderState.PASSENGER_IN_CAR, OrderState.PASSENGER_OUT_CAR, OrderState.HAVE_COMPLETE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrderState {

        int WATI_NEW = -1;
        int WATI_OLD = 0;
        int HAVE_TAKE = 1;//订单已接受
        int DRIVER_ARRIVE = 2;//司机已到达
        int PASSENGER_IN_CAR = 3;//乘客已经上车
        int PASSENGER_OUT_CAR = 4;//乘客已下车
        int HAVE_COMPLETE = 5;


    }

    @Override
    public String toString() {
        return "OrderEntity{" +
                "id=" + id +
                ", phoneNum='" + phoneNum + '\'' +
                ", startLatitude='" + startLatitude + '\'' +
                ", startLongitude='" + startLongitude + '\'' +
                ", endLatitude='" + endLatitude + '\'' +
                ", endLongitude='" + endLongitude + '\'' +
                ", startPlaceName='" + startPlaceName + '\'' +
                ", endPlaceName='" + endPlaceName + '\'' +
                ", orderState=" + orderState +
                '}';
    }
}


