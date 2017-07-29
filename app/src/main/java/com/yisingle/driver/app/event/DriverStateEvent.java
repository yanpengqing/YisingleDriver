package com.yisingle.driver.app.event;

import com.yisingle.driver.app.data.DriverEntity;

/**
 * Created by jikun on 17/7/13.
 */

public class DriverStateEvent {
    private DriverEntity driverEntity;

    public DriverStateEvent(DriverEntity driverEntity) {
        this.driverEntity = driverEntity;
    }

    public DriverEntity getDriverEntity() {
        return driverEntity;
    }

    public void setDriverEntity(DriverEntity driverEntity) {
        this.driverEntity = driverEntity;
    }
}
