package com.yisingle.driver.app.data;

import java.util.List;

/**
 * Created by jikun on 17/7/12.
 */

public class FindOrderRequest {


    private List<Integer> orderState;
    private int driverId;


    public FindOrderRequest(List<Integer> orderState, int driverId) {
        this.orderState = orderState;
        this.driverId = driverId;
    }

    public List<Integer> getOrderState() {
        return orderState;
    }

    public void setOrderState(List<Integer> orderState) {
        this.orderState = orderState;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }
}
