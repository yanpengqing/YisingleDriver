package com.yisingle.driver.app.data;

import java.math.BigDecimal;

public class DriverStatisticData {

    private int today_order_count;

    private BigDecimal today_order_gain;


    public DriverStatisticData(int today_order_count, BigDecimal today_order_gain) {
        this.today_order_count = today_order_count;
        this.today_order_gain = today_order_gain;
    }

    public int getToday_order_count() {
        return today_order_count;
    }

    public void setToday_order_count(int today_order_count) {
        this.today_order_count = today_order_count;
    }

    public BigDecimal getToday_order_gain() {
        return today_order_gain;
    }

    public void setToday_order_gain(BigDecimal today_order_gain) {
        this.today_order_gain = today_order_gain;
    }
}