package com.yisingle.driver.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.yisingle.baselibray.base.BaseActivity;
import com.yisingle.baselibray.base.BasePresenter;
import com.yisingle.driver.app.R;


import butterknife.BindView;

/**
 * Created by jikun on 17/7/19.
 */

public class TestActivity extends BaseActivity {



    @BindView(R.id.tv_socketInfo)
    TextView tv_socketInfo;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_test;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {


    }

    @Override
    protected boolean isregisterEventBus() {
        return false;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    public void link(View view) {

    }

    public void sendjson(View view) {

    }


    public void send(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
