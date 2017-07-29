package com.yisingle.driver.app.mvp;

import com.yisingle.baselibray.base.BaseView;
import com.yisingle.driver.app.data.OrderEntity;

/**
 * Created by jikun on 17/7/24.
 */

public interface IYiSingleDriver {

    interface IYiSingleDriverView extends BaseView {


        void findOrderSuccess(OrderEntity orderEntity);


    }

    interface IYiSingleDriverPresenter {


        void repeatFindOrder();

        void connectSocket();

    }
}
