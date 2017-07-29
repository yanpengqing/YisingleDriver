package com.yisingle.driver.app.mvp;

import com.yisingle.baselibray.base.BaseView;
import com.yisingle.driver.app.data.OrderEntity;

/**
 * Created by jikun on 17/7/6.
 */

public interface IOrder {

    interface IOrderView extends BaseView {

        void changeOrderStateSuccess(OrderEntity orderEntity);

        void finishDriverSuccess(OrderEntity orderEntity);


    }

    interface IOrderPresenter {
        void changeOrderState(int orderId, int oderState, int type);

        void finishOrder(int orderId, int type);


    }
}
