package com.yisingle.driver.app.mvp;

import com.yisingle.baselibray.base.BaseView;
import com.yisingle.driver.app.data.DriverEntity;
import com.yisingle.driver.app.data.DriverStatisticData;
import com.yisingle.driver.app.data.MessageEntity;
import com.yisingle.driver.app.data.OrderEntity;

import java.util.List;

/**
 * 登录页接口
 * Created by yu on 2016/11/2.
 */
public interface IMain {
    interface IMainView extends BaseView {

        void changeDriverStateSuccess(DriverEntity data);

        void findOrderSuccess(OrderEntity orderEntity);

        void findMessageSuccess(List<MessageEntity> messageEntityList);


        void getCountAndMoneySuccess(DriverStatisticData data);

    }

    interface IMainPresenter {
        void changeDriverState(String phonenum, int state, int type);

        void findOrder(int type);


        void findMessage(int type);

        void findCountMoney(String driverId, int type);

    }


}

