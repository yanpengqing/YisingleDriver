package com.map.library.base;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.model.RouteOverlayOptions;
import com.amap.api.navi.view.OverviewButtonView;
import com.amap.library.R;
import com.amap.library.R2;
import com.map.library.BitMapUtils;
import com.map.library.TTSController;
import com.map.library.listener.AMapNaviAdapterListener;
import com.yisingle.baselibray.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by jikun on 17/6/29.
 */

public abstract class BaseNaviFragment<T extends BasePresenter> extends BaseMapFragment<T> {

    protected AMapNavi mAMapNavi;


    @BindView(R2.id.overviewButtonView)
    OverviewButtonView overviewButtonView;//全览图标


    protected boolean isdestoryNaiv = true;

    protected TTSController mTtsManager;

    protected NaviLatLng mStartLatlng = null;
    protected NaviLatLng mEndLatlng = null;


    public abstract AMapNaviView getNaviView();

    public abstract void initViewsinitNaviViewOver();//初始化普通控件，并且导航控件初始化成功

    @Override
    protected void initViews(Bundle savedInstanceState) {
        super.initViews(savedInstanceState);

        mTtsManager = TTSController.getInstance(getContext());
        mTtsManager.init();

        //
        mAMapNavi = AMapNavi.getInstance(getContext());
        mAMapNavi.addAMapNaviListener(mTtsManager);
        mAMapNavi.addAMapNaviListener(new AMapNaviAdapterListener() {
            @Override
            public void onCalculateRouteSuccess() {
                mAMapNavi.setEmulatorNaviSpeed(600);
                mAMapNavi.startNavi(NaviType.GPS);//m模拟导航
                dimissNaviLoading();
                naviBeginSuccess();

            }

            @Override
            public void onCalculateRouteFailure(int i) {
                dimissNaviLoading();
                naviBeginFailed();

            }
        });

        initNavView(savedInstanceState);//初始化导航View
        initViewsinitNaviViewOver();

    }

    /**
     * @param startLatlng 高德导航坐标 经度
     * @param endLatlng   高德导航坐标 纬度
     */
    protected void startNav(NaviLatLng startLatlng, NaviLatLng endLatlng) {


        dimissNaviLoading();
        List<NaviLatLng> startList = new ArrayList<>();
        List<NaviLatLng> endList = new ArrayList<>();

        mAMapNavi.stopNavi();
        mStartLatlng = startLatlng;//起点，39.942295,116.335891

        mEndLatlng = endLatlng;//终点，39.995576,116.481288

        startList.add(mStartLatlng);
        endList.add(mEndLatlng);
        //再次强调，最后一个参数为true时代表多路径，否则代表单路径
        //congestion：躲避拥堵
        //avoidhightspeed：不走高速
        //cost：避免收费
        // hightspeed：高速优先
        // multiple：多路径
        //不走高速与高速优先不能同时为true。  高速优先与避免收费不能同时为true。
        // strategy = mAMapNavi.strategyConvert(false, false, false, false, isMultipath);
        int strategy = PathPlanningStrategy.DRIVING_SHORTEST_DISTANCE;//重新写参数策略 不使用高德自己的策略

        boolean isPostNavi = mAMapNavi.calculateDriveRoute(startList, endList, null, strategy);

        showNaviLoading();
    }


    /**
     * 初始化NaviView参数
     *
     * @param savedInstanceState
     */
    private void initNavView(Bundle savedInstanceState) {
        if (getNaviView() != null) {
            AMapNaviViewOptions options = getNaviView().getViewOptions();
            if (options != null) {
                options.setReCalculateRouteForTrafficJam(false);//设置前方拥堵时不重新计算路径（只适用于驾车导航，需要联网）

                options.setLayoutVisible(false);//设置导航UI是否显示
                options.setCrossDisplayShow(false);//设置是否显示路口放大图(路口模型图)
                options.setScreenAlwaysBright(true);//设置导航状态下屏幕是否一直开启。
                options.setTrafficBarEnabled(false);//设置路况光柱条是否显示（只适用于驾车导航，需要联网）。
                options.setEndPointBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.restdistance));
                options.setCarBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.navi_car_circle));
                options.setStartPointBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.drive_eagle_eye_navi_start));

                Bitmap bitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.drive_map_icon_preview_portrait_day);

                Bitmap bitmap1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.drive_map_icon_preview_portrait_day_checked);
                overviewButtonView.reDrawBackground(bitmap1, bitmap2);
                getNaviView().setLazyOverviewButtonView(overviewButtonView);
                options.setFourCornersBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.navi_direction_night));

                setRouteLineOptions(options);


//               getNaviView().setViewOptions(options);
            }
            getNaviView().onCreate(savedInstanceState);

            if (getNaviView().getMap() != null) {
                getNaviView().getMap().setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
                    @Override
                    public void onMapLoaded() {
                        getNaviView().getMap().showBuildings(false);
                    }
                });


                getNaviView().getMap().setOnMapClickListener(new AMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                    }
                });

            }
        }
    }


    private void setRouteLineOptions(AMapNaviViewOptions options) {
        RouteOverlayOptions routeOverlayOptions = new RouteOverlayOptions();

        //设置默认的路线纹理位图（未开启路况时）。
        routeOverlayOptions.setNormalRoute(BitMapUtils.getHalfBitMap(this.getResources(), R.drawable.drive_map_lr_default));
        //routeOverlayOptions.setArrowOnTrafficRoute//设置浮于道路上的『小箭头』图标的纹理位图。
        routeOverlayOptions.setUnknownTraffic(BitMapUtils.getHalfBitMap(this.getResources(), R.drawable.drive_map_lr_default));

        //设置畅通路况下的纹理位图
        routeOverlayOptions.setSmoothTraffic(BitMapUtils.getHalfBitMap(this.getResources(), R.drawable.drive_map_lr_green_hl));

        //设置缓慢路况下的纹理位图
        routeOverlayOptions.setSlowTraffic(BitMapUtils.getHalfBitMap(this.getResources(), R.drawable.drive_map_lr_slow_hl));

        //设置拥堵路况下的纹理位图。
        routeOverlayOptions.setJamTraffic(BitMapUtils.getHalfBitMap(this.getResources(), R.drawable.drive_map_lr_bad_hl));

        routeOverlayOptions.setVeryJamTraffic(BitMapUtils.getHalfBitMap(this.getResources(), R.drawable.drive_map_lr_darkred_hl));

        options.setRouteOverlayOptions(routeOverlayOptions);


    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != getNaviView()) {
            getNaviView().onResume();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != getNaviView()) {
            getNaviView().onPause();
        }
        //仅仅是停止你当前在说的这句话，一会到新的路口还是会再说的
        if (null != mTtsManager) {
            mTtsManager.stopSpeaking();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != getNaviView()) {
            getNaviView().onDestroy();
        }
        //since 1.6.0 不再在naviview destroy的时候自动执行AMapNavi.stopNavi();请自行执行
        mAMapNavi.stopNavi();
        if (isdestoryNaiv) {
            mAMapNavi.destroy();
        }

        if (null != mTtsManager) {
            mTtsManager.destroy();
        }
    }


    protected abstract void showNaviLoading();

    protected abstract void dimissNaviLoading();

    protected abstract void naviBeginSuccess();

    protected abstract void naviBeginFailed();

    //定义转向图标的数组
    protected int[] customIconTypes = {R.drawable.sou2, R.drawable.sou3,
            R.drawable.sou4, R.drawable.sou5, R.drawable.sou6, R.drawable.sou7,
            R.drawable.sou8, R.drawable.sou9, R.drawable.sou10,
            R.drawable.sou11, R.drawable.sou12, R.drawable.sou13,
            R.drawable.sou14, R.drawable.sou15, R.drawable.sou16,
            R.drawable.sou17, R.drawable.sou18
    };

    //锁定自车
    public void recoverLockMode() {

        if (null != getNaviView()) {
            getNaviView().recoverLockMode();
        }

    }


}
