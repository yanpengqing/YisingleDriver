package com.yisingle.driver.app.activity;


import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.map.library.DistanceUtils;
import com.map.library.data.RouteNaviSettingData;
import com.map.library.fragment.NaviFragment;
import com.map.library.help.AMapLocationHelper;
import com.yisingle.baselibray.base.BaseActivity;
import com.yisingle.baselibray.utils.TimeDisUtils;
import com.yisingle.driver.app.R;
import com.yisingle.driver.app.data.OrderEntity;
import com.yisingle.driver.app.event.DriverStateEvent;
import com.yisingle.driver.app.mvp.IOrder;
import com.yisingle.driver.app.mvp.presenter.OrderPresenter;
import com.yisingle.driver.app.utils.ToastUtils;
import com.yisingle.driver.app.view.ScrollSwithViewButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;


public class OrderActivity extends BaseActivity<OrderPresenter> implements IOrder.IOrderView {

    NaviFragment naviFragment;

    OrderEntity currentOrderEntity;


    @BindView(R.id.scrollSwithViewButton)
    ScrollSwithViewButton scrollSwithViewButton;


    @BindView(R.id.tv_start_name)
    TextView tv_start_name;

    @BindView(R.id.tv_end_name)
    TextView tv_end_name;

    @BindView(R.id.tv_phoneNum)
    TextView tv_phoneNum;


    @BindView(R.id.tv_place_name)
    TextView tv_place_name;

    @BindView(R.id.tv_order_distance_time)
    TextView tv_order_distance_time;

    @BindView(R.id.tv_count_time)
    TextView tv_count_time;

    @BindView(R.id.view_count_time_line)
    View view_count_time_line;


    @BindView(R.id.rl_order_info)
    RelativeLayout rl_order_info;

    @BindView(R.id.rl_order)
    RelativeLayout rl_order;

    @BindView(R.id.title)
    View title;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_order;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        currentOrderEntity = (OrderEntity) getIntent().getSerializableExtra("OrderEntity");


        tv_start_name.setText(currentOrderEntity.getStartPlaceName());
        tv_end_name.setText(currentOrderEntity.getEndPlaceName());

        tv_phoneNum.setText(currentOrderEntity.getPhoneNum());


        //Log.e("测试代码", "测试代码:" + currentOrderEntity.toString());
        if (null == savedInstanceState) {

            RouteNaviSettingData data = buildRouteNaviSettingData(currentOrderEntity);
            naviFragment = NaviFragment.newInstance(data);
            naviFragment.setCallBack(new NaviFragment.OnNaviFragmentCallBack() {
                @Override
                public void onCloseNaviButton() {
                    showRouteView(currentOrderEntity);
                }

                @Override
                public void onDrawRouteBack(DrivePath drivePath) {
                    String showtime = TimeDisUtils.changeSectoMin((int) drivePath.getDuration()) + "分钟";    //将预估的时间转换为显示的时间

                    float distance = drivePath.getDistance();
                    if (distance < 500) {
                        distance = 500;
                    }
                    String distanceInfo = DistanceUtils.getDistance((int) distance);

                    tv_order_distance_time.setText("剩余" + distanceInfo + "       " + showtime);
                }
            });
            loadRootFragment(R.id.frame_navi_layout, naviFragment);
        }
        reshOrderView(currentOrderEntity);


        scrollSwithViewButton.setOnSrollSwichListener(new ScrollSwithViewButton.OnSrollSwichListener() {
            @Override
            public void onSrollingMoreThanCritical() {

            }

            @Override
            public void onSrollingLessThanCriticalX() {

            }

            @Override
            public void onSlideFinishSuccess() {

                switch (currentOrderEntity.getOrderState()) {
                    case OrderEntity.OrderState.HAVE_TAKE:
                        mPresenter.changeOrderState(currentOrderEntity.getId(), OrderEntity.OrderState.DRIVER_ARRIVE, 0);
                        break;
                    case OrderEntity.OrderState.DRIVER_ARRIVE:
                        mPresenter.changeOrderState(currentOrderEntity.getId(), OrderEntity.OrderState.PASSENGER_IN_CAR, 0);
                        break;
                    case OrderEntity.OrderState.PASSENGER_IN_CAR:
                        mPresenter.changeOrderState(currentOrderEntity.getId(), OrderEntity.OrderState.PASSENGER_OUT_CAR, 0);
                        break;
                    case OrderEntity.OrderState.PASSENGER_OUT_CAR:
                        mPresenter.finishOrder(currentOrderEntity.getId(), 0);
                        break;
                    default:
                        break;

                }

            }

            @Override
            public void onSlideFinishCancel() {

            }
        });

    }

    @Override
    protected boolean isregisterEventBus() {
        return true;
    }

    @Override
    protected OrderPresenter createPresenter() {
        return new OrderPresenter(this);
    }


    private RouteNaviSettingData buildRouteNaviSettingData(OrderEntity orderEntity) {
        Double startLatitude = Double.parseDouble(orderEntity.getStartLatitude());
        Double startLongitude = Double.parseDouble(orderEntity.getStartLongitude());
        Double endLatitude = Double.parseDouble(orderEntity.getEndLatitude());

        Double endLongitude = Double.parseDouble(orderEntity.getEndLongitude());

        LatLonPoint startLatLonPoint = new LatLonPoint(startLatitude, startLongitude);

        LatLonPoint endLatLonPoint = new LatLonPoint(endLatitude, endLongitude);


        RouteNaviSettingData settingData = null;
        switch (orderEntity.getOrderState()) {

            case OrderEntity.OrderState.WATI_NEW:
            case OrderEntity.OrderState.WATI_OLD:

                settingData = RouteNaviSettingData.createOneCarToStartRouteData(startLatLonPoint);


                break;
            case OrderEntity.OrderState.HAVE_TAKE:

                settingData = RouteNaviSettingData.createOneCarToStartRouteData(startLatLonPoint);


                break;
            case OrderEntity.OrderState.DRIVER_ARRIVE:
            case OrderEntity.OrderState.PASSENGER_IN_CAR:
            case OrderEntity.OrderState.PASSENGER_OUT_CAR:
                settingData = RouteNaviSettingData.createOneStartToEndRouteData(startLatLonPoint, endLatLonPoint);
                break;
            default:
                break;

        }
        return settingData;
    }

    private void showRouteView(OrderEntity orderEntity) {


        title.setVisibility(View.VISIBLE);
        rl_order_info.setVisibility(View.VISIBLE);
        rl_order.setVisibility(View.VISIBLE);

        RouteNaviSettingData data = buildRouteNaviSettingData(orderEntity);
        naviFragment.showRoute(data);

    }


    private void reshOrderView(OrderEntity orderEntity) {
        tv_count_time.setVisibility(View.GONE);
        view_count_time_line.setVisibility(View.GONE);
        switch (orderEntity.getOrderState()) {
            case OrderEntity.OrderState.HAVE_TAKE:
                scrollSwithViewButton.setText("已到达乘客起点");
                reshTitleData("去接乘客");
                reshTopOrderView(orderEntity.getStartPlaceName());
                tv_count_time.setVisibility(View.VISIBLE);
                view_count_time_line.setVisibility(View.VISIBLE);

                break;
            case OrderEntity.OrderState.DRIVER_ARRIVE:
                scrollSwithViewButton.setText("乘客已上车" + getMoneyAndTimeShowInfo());
                reshTitleData("等待乘客上车");
                reshTopOrderView(orderEntity.getEndPlaceName());


                break;
            case OrderEntity.OrderState.PASSENGER_IN_CAR:
                scrollSwithViewButton.setText("到达目的地" + getMoneyAndTimeShowInfo());
                reshTitleData("行程中");
                reshTopOrderView(orderEntity.getEndPlaceName());

                break;
            case OrderEntity.OrderState.PASSENGER_OUT_CAR:
                scrollSwithViewButton.setText("结束订单" + getMoneyAndTimeShowInfo());
                reshTitleData("乘客已下车");
                reshTopOrderView(orderEntity.getEndPlaceName());

                break;
            default:
                break;

        }
    }


    private void reshTopOrderView(String placeName) {


        if (rl_order.getVisibility() == View.GONE || rl_order.getVisibility() == View.GONE) {
            rl_order.setVisibility(View.VISIBLE);
        }

        SpannableStringBuilder placeNameBuilder = new SpanUtils()
                .append("去")
                .append(placeName).setForegroundColor(getResources().getColor(R.color.text_orange_color)).create();
        tv_place_name.setText(placeNameBuilder);


        tv_order_distance_time.setText("");

        SpannableStringBuilder titleBuilder = new SpanUtils()
                .append("请")
                .append("5分钟01秒").setForegroundColor(getResources().getColor(R.color.text_orange_color))
                .append("内到达乘客上车点").create();
        tv_count_time.setText(titleBuilder);
    }

    @Override
    public Resources getResources() {

        //为了解决高德导航的继承AppCompatActivity要崩溃的BUG
        // 要重写getResources方法如下
        return getBaseContext().getResources();
    }

    @Override
    public void changeOrderStateSuccess(OrderEntity orderEntity) {
        currentOrderEntity = orderEntity;
        reshOrderView(currentOrderEntity);
        showRouteView(currentOrderEntity);
    }

    @Override
    public void finishDriverSuccess(OrderEntity orderEntity) {
        EventBus.getDefault().post(new DriverStateEvent(orderEntity.getDriver()));
        finish();
    }

    @OnClick(R.id.bt_open_navi)
    protected void beginNavi() {


        title.setVisibility(View.GONE);
        rl_order_info.setVisibility(View.GONE);
        rl_order.setVisibility(View.GONE);
        Double startLatitude = Double.parseDouble(currentOrderEntity.getStartLatitude());
        Double startLongitude = Double.parseDouble(currentOrderEntity.getStartLongitude());
        Double endLatitude = Double.parseDouble(currentOrderEntity.getEndLatitude());

        Double endLongitude = Double.parseDouble(currentOrderEntity.getEndLongitude());

        AMapLocation aMapLocation = AMapLocationHelper.getLastKnownLocation(getApplicationContext());
        NaviLatLng locationLatlng = new NaviLatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        NaviLatLng startNaviLatLng = new NaviLatLng(startLatitude, startLongitude);
        NaviLatLng endNaviLatLng = new NaviLatLng(endLatitude, endLongitude);


        switch (currentOrderEntity.getOrderState()) {
            case OrderEntity.OrderState.HAVE_TAKE:
                naviFragment.startNav(locationLatlng, startNaviLatLng);
                break;
            case OrderEntity.OrderState.DRIVER_ARRIVE:
                naviFragment.startNav(startNaviLatLng, endNaviLatLng);
                break;
            case OrderEntity.OrderState.PASSENGER_IN_CAR:
                naviFragment.startNav(startNaviLatLng, endNaviLatLng);
                break;
            case OrderEntity.OrderState.PASSENGER_OUT_CAR:
                naviFragment.startNav(startNaviLatLng, endNaviLatLng);
                break;
            default:
                break;

        }


    }


    @OnClick(R.id.iv_message)
    protected void doMessage() {
        ToastUtils.show("暂未实现");
    }

    @OnClick(R.id.iv_phone)
    protected void docall() {
        PhoneUtils.call(currentOrderEntity.getPhoneNum());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentOrderEntity = null;
    }

    private void reshTitleData(String center) {

        View view = findViewById(R.id.title);
        Button bt_left = (Button) view.findViewById(R.id.bt_left);
        bt_left.setText("");

        Button bt_logo = (Button) view.findViewById(R.id.bt_logo);
        bt_logo.setText(center);

        Button bt_right = (Button) view.findViewById(R.id.bt_right);
        bt_right.setText("行程详情");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OrderEntity event) {
        if (currentOrderEntity != null) {
            currentOrderEntity = event;
            switch (currentOrderEntity.getOrderState()) {
                case OrderEntity.OrderState.DRIVER_ARRIVE:
                    scrollSwithViewButton.setText("乘客已上车" + getMoneyAndTimeShowInfo());
                    break;
                case OrderEntity.OrderState.PASSENGER_IN_CAR:
                    scrollSwithViewButton.setText("到达目的地" + getMoneyAndTimeShowInfo());
                    break;
                case OrderEntity.OrderState.PASSENGER_OUT_CAR:
                    scrollSwithViewButton.setText("结束订单" + getMoneyAndTimeShowInfo());
                    break;
                default:
                    break;
            }
        }

        /* Do something */
    }

    private String getMoneyAndTimeShowInfo() {
        if (currentOrderEntity != null) {
            String info = " " + currentOrderEntity.getOrderPrice() + "元";

            return info;
        } else {
            return "";
        }

    }
}


