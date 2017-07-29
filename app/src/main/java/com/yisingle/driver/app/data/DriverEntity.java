package com.yisingle.driver.app.data;


import android.support.annotation.IntDef;

import java.io.Serializable;
import java.lang.annotation.*;


/**
 * Created by jikun on 17/6/26.
 */

public class DriverEntity implements Serializable {


    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    private String driverName;


    private String password;//密码

    private String latitude;//纬度

    private String longitude;//经度


    private String deviceId;//设备id


    private String phonenum;//电话号码

    private int state;

    private int driver_score;


    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDriver_score() {
        return driver_score;
    }

    public void setDriver_score(int driver_score) {
        this.driver_score = driver_score;
    }

    public
    @DriverState
    int getState() {
        return state;
    }

    public void setState(@DriverState int state) {
        this.state = state;
    }


    //添加支持注解的依赖到你的项目中，需要在build.gradle文件中的依赖块中添加：
    //dependencies { compile 'com.android.support:support-annotations:24.2.0' }
    @IntDef({DriverState.WATI_FOR_ORDER, DriverState.SERVICE, DriverState.BREAKDOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DriverState {

        int WATI_FOR_ORDER = 0;//等待订单中
        int SERVICE = 1;//服务中
        int BREAKDOWN = 2;//下线中

    }


}