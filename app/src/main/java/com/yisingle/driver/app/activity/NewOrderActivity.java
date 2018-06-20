package com.yisingle.driver.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.yisingle.baselibray.base.BaseActivity;
import com.yisingle.driver.app.R;
import com.yisingle.driver.app.data.OrderEntity;
import com.yisingle.driver.app.event.DriverStateEvent;
import com.yisingle.driver.app.mvp.INewOrder;
import com.yisingle.driver.app.mvp.presenter.NewOrderPresenter;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jikun on 17/7/7.
 */

public class NewOrderActivity extends BaseActivity<NewOrderPresenter> implements INewOrder.INewOrderView {

    private Subscription subscription;


    private Long totalTime = 30L;

    @BindView(R.id.bt_start)
    Button bt_start;

    @BindView(R.id.tv_start_name)
    TextView tv_start_name;

    @BindView(R.id.tv_end_name)
    TextView tv_end_name;

    @BindView(R.id.tv_time_title)
    TextView tv_time_title;

    @BindView(R.id.tv_time)
    TextView tv_time;

    OrderEntity currentOrderEntity;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_new_order;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        currentOrderEntity = (OrderEntity) getIntent().getSerializableExtra("OrderEntity");

        tv_start_name.setText(currentOrderEntity.getStartPlaceName());
        tv_end_name.setText(currentOrderEntity.getEndPlaceName());


        String littleTime = TimeUtils.millis2String(currentOrderEntity.getCreateTime(), new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()));

        tv_time_title.setText(littleTime);

        String time = TimeUtils.millis2String(currentOrderEntity.getCreateTime(), new SimpleDateFormat("HH:mm",Locale.getDefault()));

        tv_time.setText(time);
        startCountTime();

    }

    @Override
    protected boolean isregisterEventBus() {
        return false;
    }

    @Override
    protected NewOrderPresenter createPresenter() {
        return new NewOrderPresenter(this);
    }


    private void startCountTime() {
        subscription = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        totalTime = 29 - aLong;
                        Log.e("测试代码", "测试代码时间倒计时" + totalTime);
                        bt_start.setText("接单\n" + totalTime + "秒");
                        if (totalTime == 0) {
                            finish();
                        }
                    }
                });
    }


    @OnClick(R.id.bt_start)
    public void acceptOrder() {
        mPresenter.acceptOrder(currentOrderEntity.getId(), 0);
    }

    private void stopCountTime() {
        if (null != subscription) {
            subscription.unsubscribe();
        }

    }

    @OnClick(R.id.ib_close)
    public void close() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCountTime();
    }


    @Override
    public void acceptSuccess(OrderEntity data) {

        Intent intent = new Intent();
        intent.setClass(this, OrderActivity.class);
        intent.putExtra("OrderEntity", data);
        startActivity(intent);
        EventBus.getDefault().post(new DriverStateEvent(data.getDriver()));
        finish();

    }
}
