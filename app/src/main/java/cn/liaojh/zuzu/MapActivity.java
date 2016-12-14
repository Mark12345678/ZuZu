package cn.liaojh.zuzu;

import android.Manifest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.bigkoo.pickerview.OptionsPickerView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.liaojh.zuzu.bean.Goods;
import cn.liaojh.zuzu.bean.Page;
import cn.liaojh.zuzu.fragment.GoodsDetailFragment;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.http.SpotsCallBack;
import cn.liaojh.zuzu.utils.AskPermission;
import cn.liaojh.zuzu.utils.CategotyMenuUtil;

public class MapActivity extends BaseActivity implements LocationSource, AMapLocationListener {

    OkHttpHelper okHttpHelpetr;

    List<Goods> goodses;

    ImageView map_category ;

    @ViewInject(R.id.map_detail_all)
    RelativeLayout detail_rl;

    @ViewInject(R.id.map_goods_name)
    TextView map_goods_name;

    @ViewInject(R.id.map_goods_content)
    TextView map_goods_content;

    @ViewInject(R.id.map_detaile_back)
    ImageView map_detaile_back;

    @ViewInject(R.id.map_detail_close)
    ImageView map_detail_close;

    //定义地图控件引用
    MapView mapView = null;
    int category1 = -1;
    int category2 = -1 ;

    AMap aMap = null;

    //类别选择器
    OptionsPickerView pvOptions;

    private UiSettings mUiSettings ;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    private Marker locationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ViewUtils.inject(this);

        AskPermission.askPremission(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS});
        mapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mapView.onCreate(savedInstanceState);

        if(aMap == null){
            initMap();

            initLocation();
        }
        okHttpHelpetr = OkHttpHelper.getInstance();
    }

    public void initMap(){
        aMap = mapView.getMap();
        mUiSettings = aMap.getUiSettings();

        //设置为常规地图
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        aMap.setLocationSource(this);
        mUiSettings.setMyLocationButtonEnabled(true); // 显示默认的定位按钮

        aMap.setMyLocationEnabled(true);// 可触发定位并显示定位层

        //初始化选择按钮
        map_category = (ImageView) findViewById(R.id.id_map_category);
        pvOptions = new OptionsPickerView(MapActivity.this);
        //初始化这个菜单
        pvOptions = CategotyMenuUtil.initCategory(pvOptions);

        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                category1 = options1 + 1;
                category2 = option2 + 1;

                requestGoods(category1,category2);
            }
        });

        map_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.show();
            }
        });

    }

    public void setMark(final List<Goods> goodses){
        this.goodses = goodses;

        //把上面的标记删掉，留下自己的定位标记
        aMap.clear();
        if(locationMarker != null){
            aMap.addMarker(new MarkerOptions().position(locationMarker.getPosition())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker)));

        }
        if(aMap != null && goodses.size() > 0){

            for(int i=0 ; i<goodses.size() ; i++ ){
                LatLng latLng = new LatLng(goodses.get(i).getGoodsLatitude(),goodses.get(i).getGoodsLongitude());
                Marker marker = aMap.addMarker(new MarkerOptions().
                        position(latLng));
                //物品所在的list的位置设置给标记，方便点击时查找
                marker.setObject(i);
            }
        }

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                detail_rl.setVisibility(View.VISIBLE);
                int listId = (int) marker.getObject();

                final Goods goods = goodses.get(listId);

                map_goods_name.setText(goods.getGoodsName());
                map_goods_content.setText(goods.getGoodsDescribe());
                map_detail_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        detail_rl.setVisibility(View.GONE);
                    }
                });

                detail_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startToDetail(goods);
                    }
                });

                return false;
            }
        });
    }

    public void startToDetail(Goods goods){
        //addFragment(new GoodsDetailFragment(goods));
        Intent i = new Intent(MapActivity.this,GoodsDetailActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("goods",goods);
        i.putExtras(b);
        startActivity(i);
    }


    //根据类别获取物品
    public void requestGoods(int category1, int category2){

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("category1",category1 + "");
        params.put("category2",category2 + "");

        okHttpHelpetr.get(Contans.API.CATEGORY, params,new SpotsCallBack<Page<Goods>>(MapActivity.this) {

            @Override
            public void onSuccess(Response response, Page<Goods> goodses) {
                setMark(goodses.getList());
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });

    }

    /**
     * 初始化定位相关东西
     */
    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);

        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        //mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }


    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        //ToastUtils.show(MapActivity.this,"定位ing");
        mLocationClient.startLocation();
    }

    @Override
    public void deactivate() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        removeFragment();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息

                //取出经纬度
                LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                //添加Marker显示定位位置
                if (locationMarker == null) {
                    //如果是空的添加一个新的,icon方法就是设置定位图标，可以自定义
                    locationMarker = aMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker)));
                } else {
                    //已经添加过了，修改位置即可
                    locationMarker.setPosition(latLng);
                    aMap.addMarker(new MarkerOptions().position(locationMarker.getPosition())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker)));
                }

                //然后可以移动到定位点,使用animateCamera就有动画效果
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    //设置这个Activity的布局
    @Override
    protected int getContentViewId() {
        return R.layout.activity_map;
    }

    //设置那个layout可以替换
    @Override
    protected int getFragmentContentId() {
        return R.id.map_frame;
    }
}
