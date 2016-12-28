package cn.liaojh.zuzu;

import android.Manifest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
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
import java.util.zip.Inflater;

import cn.liaojh.zuzu.bean.Goods;
import cn.liaojh.zuzu.bean.Page;
import cn.liaojh.zuzu.fragment.GoodsDetailFragment;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.http.SpotsCallBack;
import cn.liaojh.zuzu.utils.AskPermission;
import cn.liaojh.zuzu.utils.CategotyMenuUtil;
import cn.liaojh.zuzu.utils.ToastUtils;

import static cn.liaojh.zuzu.R.id.progress;

public class MapActivity extends BaseActivity implements LocationSource, AMapLocationListener {

    OkHttpHelper okHttpHelpetr;

    List<Goods> goodses;

    ImageView map_category ;

    @ViewInject(R.id.map_detail_all)
    RelativeLayout detail_rl;

    @ViewInject(R.id.map_detaile_back)
    ImageView map_detaile_back;

    @ViewInject(R.id.map_goods_name)
    TextView map_goods_name;

    @ViewInject(R.id.map_goods_content)
    TextView map_goods_content;

    @ViewInject(R.id.map_detail_close)
    ImageView map_detail_close;

    @ViewInject(R.id.map_goods_distance)
    TextView map_goods_distance;

    //定义地图控件引用
    MapView mapView = null;

    AMap aMap = null;

    int lastDistance = 0;

    //类别选择器
    OptionsPickerView pvOptions;

    private UiSettings mUiSettings ;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    //自己当前位置的标记
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


        initMap();

        initLocation();

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

        map_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择种类
                selectCategory();
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

        ToastUtils.show(MapActivity.this,")))");
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
                map_goods_distance.setText("距离你"+getDistance(locationMarker.getPosition(),marker.getPosition())+"m");

                map_detail_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        detail_rl.setVisibility(View.GONE);
                    }
                });

                map_detaile_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startToDetail(goods);
                    }
                });

                return false;
            }
        });
    }

    //获取两点间的距离
    public int getDistance(LatLng startLatlng,LatLng endLatlng){
        int result = (int)AMapUtils.calculateLineDistance(startLatlng, endLatlng);
        return result;
    }

    //跳转到物品详细页面
    public void startToDetail(Goods goods){
        //addFragment(new GoodsDetailFragment(goods));
        Intent i = new Intent(MapActivity.this,GoodsDetailActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("goods",goods);
        i.putExtras(b);
        startActivity(i);
    }

    /**
     *选择类别
     */
    public void selectCategory(){
        AlertDialog.Builder dlg = new AlertDialog.Builder(MapActivity.this);
        View titleView = View.inflate(MapActivity.this,R.layout.addfriend_title,null);
        TextView textView = (TextView)titleView.findViewById(R.id.addFriend);
        textView.setText("筛选范围：");
        dlg.setCustomTitle(titleView);
        View view = LayoutInflater.from(MapActivity.this).inflate(R.layout.activity_select,null);

        final Spinner spinner_category1 = (Spinner) view.findViewById(R.id.spinner_category1);
        final Spinner spinner_category2 = (Spinner) view.findViewById(R.id.spinner_category2);
        final Spinner spinner_distance = (Spinner) view.findViewById(R.id.spinner_distance);
        final TextView txt_category1 = (TextView) view.findViewById(R.id.txt_select_category1);
        final TextView txt_category2 = (TextView) view.findViewById(R.id.txt_select_category2);
        final TextView txt_distance = (TextView) view.findViewById(R.id.txt_select_distance);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                MapActivity.this, R.array.category1, android.R.layout.simple_spinner_dropdown_item);
        spinner_category1.setAdapter(adapter);
        //用于记录类别1,2
        final Map<String,String> pamrams = new HashMap<String,String>();

        spinner_category1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                txt_category1.setText(getResources().getTextArray(R.array.category1)[i]);

                pamrams.put("category1",(i+1)+"");

                switch (i){
                    case 0:
                        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                                MapActivity.this, R.array.category2_1, android.R.layout.simple_spinner_dropdown_item);
                        spinner_category2.setAdapter(adapter1);
                        break;
                    case 1:
                        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                                MapActivity.this, R.array.category2_2, android.R.layout.simple_spinner_dropdown_item);
                        spinner_category2.setAdapter(adapter2);
                        break;
                    case 2:
                        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(
                                MapActivity.this, R.array.category2_3, android.R.layout.simple_spinner_dropdown_item);
                        spinner_category2.setAdapter(adapter3);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_category2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                pamrams.put("category2",(i+1)+"");
                switch (spinner_category1.getSelectedItemPosition()){
                    case 0:
                        txt_category2.setText(getResources().getTextArray(R.array.category2_1)[i]);
                        break;
                    case 1:
                        txt_category2.setText(getResources().getTextArray(R.array.category2_2)[i]);
                        break;
                    case 2:
                        txt_category2.setText(getResources().getTextArray(R.array.category2_3)[i]);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                txt_distance.setText(getResources().getTextArray(R.array.select_distance)[i]);
                pamrams.put("distance",i+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dlg.setView(view);
        dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int[] distances =  getResources().getIntArray(R.array.select_distance_num);
                int size = Integer.valueOf(pamrams.get("distance"));
                int distance = distances[size];
                lastDistance = distance;
                requestGoods(Integer.valueOf(pamrams.get("category1")),Integer.valueOf(pamrams.get("category2")),distance);

            }
        });
        dlg.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dlg.show();

    }


    //根据类别获取物品
    public void requestGoods(int category1, int category2 , int distance){

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("category1",category1 + "");
        params.put("category2",category2 + "");
        params.put("distance",distance + "");
        params.put("myLatitude",locationMarker.getPosition().latitude + "");
        params.put("myLongitude",locationMarker.getPosition().longitude + "");

        okHttpHelpetr.get(Contans.API.FINDBYMAPCATOGERY , params,new SpotsCallBack<List<Goods>>(MapActivity.this) {

            @Override
            public void onSuccess(Response response, List<Goods> goodses) {
                setMark(goodses);
                //ToastUtils.show(MapActivity.this,""+goodses.size());
                if(mLocationClient != null){
                    mLocationClient.startLocation();
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(MapActivity.this,"error");
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
                ToastUtils.show(MapActivity.this,"&&");
                //取出经纬度
                LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());


                aMap.addCircle(new CircleOptions().
                        center(new LatLng(aMapLocation.getLongitude(),aMapLocation.getLatitude())).
                        radius(lastDistance).
                        fillColor(R.color.mediumpurple).
                        strokeColor(R.color.mediumpurple).
                        strokeWidth(15));
                ToastUtils.show(MapActivity.this,""+lastDistance);

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
