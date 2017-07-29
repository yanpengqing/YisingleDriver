package com.yisingle.driver.app.mvp;

import com.yisingle.baselibray.base.BaseView;
import com.yisingle.driver.app.data.DriverEntity;
import com.yisingle.driver.app.data.OrderEntity;

/**
 * Created by jikun on 17/7/9.
 */

public interface ILogin {

    interface ILoginView extends BaseView {

        void loginSuccess(DriverEntity entity);





    }

    interface ILoginPresenter {
        void login(String phonenum, String password, int type);


    }
}
