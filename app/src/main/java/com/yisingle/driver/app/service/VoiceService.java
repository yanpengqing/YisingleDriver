package com.yisingle.driver.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by jikun on 17/9/26.
 */

public class VoiceService extends Service {
    private Queue<String> queue = new LinkedList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == queue) {
            queue = new LinkedList<>();
        }
        queue.offer(intent.getStringExtra("test"));
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        queue.clear();
    }
}
