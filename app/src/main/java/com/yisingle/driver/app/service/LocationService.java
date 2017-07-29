package com.yisingle.driver.app.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.map.library.help.AMapLocationHelper;
import com.yisingle.bind.IGuardAidlInterface;
import com.yisingle.driver.app.event.LocationEvent;

import com.yisingle.driver.app.map.utils.IWifiAutoCloseDelegate;
import com.yisingle.driver.app.map.utils.PowerManagerUtil;
import com.yisingle.driver.app.map.utils.WifiAutoCloseDelegate;
import com.yisingle.driver.app.receiver.LocationReceiver;
import com.yisingle.driver.app.utils.NetUtil;


/**
 * Created by jikun on 17/5/11.
 * 定位Service功能
 */


public class LocationService extends BaseNoticService implements AMapLocationHelper.OnLocationGetListener {


    private AMapLocationHelper aMapLocationHelper;

    private int loctionTime = 10* 1000;

    private final String RECEIVER_ACTION = "com.yisingle.driver.app.service.receiver";


    /**
     * 处理息屏关掉wifi的delegate类
     * //如果因为熄灭屏幕而造成了W定位失败，那么点亮屏幕
     */
    private IWifiAutoCloseDelegate mWifiAutoCloseDelegate = new WifiAutoCloseDelegate();
    /**
     * 记录是否需要对息屏关掉wifi的情况进行处理
     */
    private boolean mIsWifiCloseable = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new GuardAidlImpl();
    }


    class GuardAidlImpl extends IGuardAidlInterface.Stub {

        @Override
        public void connect() throws RemoteException {

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("1", "测试代码：LocationService-------onStartCommand");
        aMapLocationHelper = new AMapLocationHelper(getApplicationContext());
        aMapLocationHelper.setOnLocationGetListener(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("1", "测试代码：LocationService-------onStartCommand");
        applyNotiKeepMech();
        aMapLocationHelper.startLocation(loctionTime);
        if (mWifiAutoCloseDelegate.isUseful(getApplicationContext())) {
            mIsWifiCloseable = true;
            mWifiAutoCloseDelegate.initOnServiceStarted(getApplicationContext());
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unApplyNotiKeepMech();
        aMapLocationHelper.destroyLocation();
        Log.e("1", "测试代码：LocationService-------onDestroy");

    }

    @Override
    public void onLocationGetSuccess(AMapLocation loc) {

        if (null != loc) {
            //由于当前LocationService 属于另一个进程中
            // 所以发送EventBus或者RxBus都没有作用所以只能发送广播来进行定位转发
            //如果在广播中发送EventBus到界面中去
            sendLocation(new LocationEvent(LocationEvent.Code.SUCCESS, loc));

        }

        if (mIsWifiCloseable) {
            mWifiAutoCloseDelegate.onLocateSuccess(getApplicationContext(),
                    PowerManagerUtil.getInstance().isScreenOn(getApplicationContext()),
                    NetUtil.getInstance().isMobileAva(getApplicationContext()));
        }
    }


    @Override
    public void onLocationGetFail(AMapLocation loc) {
        Log.e("测试代码", "测试代码LocationService--onLocationGetFail");
        if (null != loc) {
            sendLocation(new LocationEvent(LocationEvent.Code.FAILED, loc));
        }

        //
        if (mIsWifiCloseable) {
            int code = -99999;
            if (null != loc) {
                code = loc.getErrorCode();
            }
            mWifiAutoCloseDelegate.onLocateFail(getApplicationContext(), code,
                    PowerManagerUtil.getInstance().isScreenOn(getApplicationContext()),
                    NetUtil.getInstance().isWifiCon(getApplicationContext()));
        }

    }

    private void sendLocation(LocationEvent locationEvent) {
        Intent mIntent = new Intent(RECEIVER_ACTION);
        mIntent.putExtra(LocationReceiver.LOCATION, locationEvent);
        //发送广播
        sendBroadcast(mIntent);
    }

    public static void startService(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        context.startService(intent);
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        context.stopService(intent);
    }


}
