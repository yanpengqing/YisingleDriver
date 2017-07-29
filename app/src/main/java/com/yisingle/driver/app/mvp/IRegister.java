package com.yisingle.driver.app.mvp;

import com.yisingle.baselibray.base.BaseView;
import com.yisingle.driver.app.data.DriverEntity;
import com.yisingle.driver.app.data.OrderEntity;

/**
 * Created by jikun on 17/7/27.
 */

public interface IRegister {

    interface IRegisterView extends BaseView {

        void registerSuccess(DriverEntity data);


    }

    interface IRegisterPresenter {
        void registerDriver(String phoneNumber, String passWord, String username,int type);


    }
}
