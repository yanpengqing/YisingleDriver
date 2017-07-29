package com.yisingle.driver.app.mvp;

import com.yisingle.baselibray.base.BaseView;

import com.yisingle.driver.app.data.OrderEntity;

/**
 * Created by jikun on 17/7/11.
 */

public interface INewOrder {

    interface INewOrderView extends BaseView {

        void acceptSuccess(OrderEntity data);


    }

    interface INewOrderPresenter {
        void acceptOrder(int orderId, int type);


    }

}
