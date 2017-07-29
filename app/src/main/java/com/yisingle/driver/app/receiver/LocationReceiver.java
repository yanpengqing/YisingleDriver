package com.yisingle.driver.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yisingle.driver.app.event.LocationEvent;

import org.greenrobot.eventbus.EventBus;


/**
 * 定位收取广播器
 */

public class LocationReceiver extends BroadcastReceiver {

    public static String LOCATION = "Loction_event";

    @Override
    public void onReceive(Context context, Intent intent) {
        LocationEvent event = intent.getParcelableExtra(LocationReceiver.LOCATION);

        if (event != null) {
            EventBus.getDefault().post(event);
            Log.e("测试代码", "测试代码LocationReceiver----onReceive" + event.getCode());
        }

    }
}
