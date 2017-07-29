package com.yisingle.driver.app.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.yisingle.baselibray.base.BaseActivity;
import com.yisingle.baselibray.baseadapter.RecyclerAdapter;
import com.yisingle.baselibray.baseadapter.viewholder.RecyclerViewHolder;
import com.yisingle.driver.app.R;
import com.yisingle.driver.app.base.LoginConstant;
import com.yisingle.driver.app.data.DriverEntity;
import com.yisingle.driver.app.data.DriverStatisticData;
import com.yisingle.driver.app.data.MessageEntity;
import com.yisingle.driver.app.data.OrderEntity;
import com.yisingle.driver.app.event.DriverStateEvent;
import com.yisingle.driver.app.mvp.IMain;
import com.yisingle.driver.app.mvp.presenter.MainPresenter;
import com.yisingle.driver.app.service.GuardService;
import com.yisingle.driver.app.service.LocationService;
import com.yisingle.driver.app.service.OrderService;
import com.yisingle.driver.app.utils.ToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class MainActivity extends BaseActivity<MainPresenter> implements IMain.IMainView {


    @BindView(R.id.main_recyclerView)
    RecyclerView recyclerView;

    private RecyclerAdapter<MessageEntity> adapter;

    @BindView(R.id.tv_login_info)
    TextView tv_login_info;

    @BindView(R.id.iv_loading)
    ImageView iv_loading;

    @BindView(R.id.tv_money)
    TextView tv_money;

    @BindView(R.id.tv_count_order)
    TextView tv_count_order;

    @BindView(R.id.tv_socre)
    TextView tv_socre;

    private DriverEntity currentdriverEntity;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {


        initRecyclerView();

        startLocationService();
        currentdriverEntity = (DriverEntity) getIntent().getSerializableExtra("DriverEntity");


        successView(currentdriverEntity);

        OrderService.startService(getApplicationContext());


        if (currentdriverEntity.getState() == DriverEntity.DriverState.SERVICE) {
            mPresenter.findOrder(MainPresenter.PresenterHttpType.Order);
        }
        mPresenter.findMessage(MainPresenter.PresenterHttpType.Message);

        mPresenter.findCountMoney(currentdriverEntity.getId() + "", MainPresenter.PresenterHttpType.Message);

    }

    @Override
    protected boolean isregisterEventBus() {
        return true;
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        List<MessageEntity> data = new ArrayList<>();

        adapter = new RecyclerAdapter<MessageEntity>(data, R.layout.adapter_main_info) {
            @Override
            protected void onBindData(RecyclerViewHolder holder, int position, MessageEntity item) {

                holder.setText(R.id.tv_info_title, item.getTitle());
                holder.setText(R.id.tv_time, TimeUtils.millis2String(item.getTime()));
                holder.setText(R.id.tv_info, item.getContent());
            }
        };
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationService();

        OrderService.stopService(getApplicationContext());
    }


    @Override
    public void showLoading(int type) {
        switch (type) {
            case MainPresenter.PresenterHttpType.changeDriverState:
                showLoadingView();
                break;
            case MainPresenter.PresenterHttpType.Order:
                showLoadingDialog();
                break;
            case MainPresenter.PresenterHttpType.Message:
                //dimisLoadingDialog();
                break;

        }

    }

    @Override
    public void dismissLoading(int type) {
        switch (type) {
            case MainPresenter.PresenterHttpType.changeDriverState:
                dimissLoadingView();
                break;
            case MainPresenter.PresenterHttpType.Order:
                dimisLoadingDialog();
                break;
            case MainPresenter.PresenterHttpType.Message:
                //dimisLoadingDialog();
                break;


        }

    }

    private void showLoadingView() {
        tv_login_info.setVisibility(View.GONE);

        iv_loading.setVisibility(View.VISIBLE);
        startAnimationDrawable(iv_loading);

    }


    private void dimissLoadingView() {

        tv_login_info.setVisibility(View.VISIBLE);
        iv_loading.setVisibility(View.GONE);
        stopAnimationDrawable(iv_loading);
        SPUtils.getInstance().put(LoginConstant.LOGIN_DRIVER_STATE, currentdriverEntity.getState());

    }

    private void successView(DriverEntity driverEntity) {
        String info = "";
        SPUtils.getInstance().put(LoginConstant.LOGIN_DRIVER_STATE, driverEntity.getState());
        tv_socre.setText(driverEntity.getDriver_score() + "分");
        switch (driverEntity.getState()) {
            case DriverEntity.DriverState.WATI_FOR_ORDER:
                info = "下线";
                Log.e("测试代码", "测试代码" + "等待订单中");
                break;
            case DriverEntity.DriverState.SERVICE:
                info = "服务中";
                Log.e("测试代码", "测试代码" + "服务中");
                break;
            case DriverEntity.DriverState.BREAKDOWN:
                info = "出车";
                Log.e("测试代码", "测试代码" + "下线");
                break;
        }

        tv_login_info.setVisibility(View.VISIBLE);
        tv_login_info.setText(info);
    }

    public void startAnimationDrawable(ImageView imageView) {

        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        if (!animationDrawable.isRunning()) {
            animationDrawable.start();
        }
    }

    public void stopAnimationDrawable(ImageView imageView) {

        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        if (animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }

    private void startLocationService() {
        GuardService.startService(getApplicationContext());
        LocationService.startService(getApplicationContext());
    }

    private void stopLocationService() {
        GuardService.stopService(getApplicationContext());
        LocationService.stopService(getApplicationContext());
    }


    @OnClick(R.id.bt_start)
    public void startchangeState() {

        int state = DriverEntity.DriverState.BREAKDOWN;
        SPUtils.getInstance().put(LoginConstant.LOGIN_DRIVER_STATE, state);
        switch (currentdriverEntity.getState()) {
            case DriverEntity.DriverState.BREAKDOWN:
                state = DriverEntity.DriverState.WATI_FOR_ORDER;
                mPresenter.changeDriverState(currentdriverEntity.getPhonenum(), state, 0);
                break;

            case DriverEntity.DriverState.WATI_FOR_ORDER:
                state = DriverEntity.DriverState.BREAKDOWN;
                mPresenter.changeDriverState(currentdriverEntity.getPhonenum(), state, 0);
                break;
            case DriverEntity.DriverState.SERVICE:
                mPresenter.findOrder(0);
                break;
        }


    }

    @Override
    public void changeDriverStateSuccess(DriverEntity data) {
        currentdriverEntity = data;
        successView(data);

    }

    @Override
    public void findOrderSuccess(OrderEntity orderEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("你有一个订单未结束");
        builder.setMessage("请完成这个订单");
        builder.setCancelable(false);
        builder.setNegativeButton("确定", (dialog, which) -> {
            Intent intent = new Intent();
            intent.putExtra("OrderEntity", orderEntity);
            intent.setClass(this, OrderActivity.class);
            startActivity(intent);
        });
//        builder.setPositiveButton("取消", (dialog, which) -> {
//
//        });
        builder.show();
    }

    @Override
    public void findMessageSuccess(List<MessageEntity> messageEntityList) {
        adapter.refreshWithNewData(messageEntityList);
    }

    @Override
    public void getCountAndMoneySuccess(DriverStatisticData data) {

        tv_money.setText(data.getToday_order_gain() + "元");

        tv_count_order.setText(data.getToday_order_count() + "单");

    }


    @OnClick(R.id.bt_setting)
    protected void doSetting() {
        ToastUtils.show("现在是派单模式");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DriverStateEvent event) {

        Log.e("测试代码", "测试代码+onMessageEvent");
        DriverEntity driverEntity = event.getDriverEntity();
        if (driverEntity != null) {
            currentdriverEntity = driverEntity;
            SPUtils.getInstance().put(LoginConstant.LOGIN_DRIVER_STATE, driverEntity.getState());
            successView(driverEntity);
        }
    }

    public void noIml(View view) {
        ToastUtils.show("暂未实现该功能");
    }


}
