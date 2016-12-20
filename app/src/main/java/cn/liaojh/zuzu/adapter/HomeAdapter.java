package cn.liaojh.zuzu.adapter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.ZuZuApplication;
import cn.liaojh.zuzu.bean.Goods;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.http.SimpleCallback;
import cn.liaojh.zuzu.http.SpotsCallBack;
import cn.liaojh.zuzu.utils.ToastUtils;

/**
 * Created by Liaojh on 2016/10/27.
 */

public class HomeAdapter extends SimpleAdapter<Goods> {

    OkHttpHelper okHttpHelper;
    Context context;
    double myLatitude;
    double myLongitude;
    //声明AMapLocationClient类对象
    AMapLocationClient mLocationClient = null;

    public HomeAdapter(Context context, List<Goods> datas) {
        super(context, datas, R.layout.list_item);
        if(mLocationClient == null){
            initAMAP();
        }
        this.context = context;
        okHttpHelper = OkHttpHelper.getInstance();
    }

    @Override
    public void bindData(final BaseViewHolder holder, final Goods goods) {
        holder.getTextView(R.id.item_username).setText(goods.getUser().getPhone());
        holder.getTextView(R.id.item_goodsname).setText(goods.getGoodsName());
        holder.getTextView(R.id.item_price).setText("￥" + goods.getGoodsPrice());
        holder.getTextView(R.id.item_describe).setText(goods.getGoodsDescribe());
        Picasso.with(context).load(Contans.API.SHOW_GOODS_PHOTO + "/" + goods.getStandPic()).into(holder.getImageView(R.id.item_img));
        holder.getTextView(R.id.item_time).setText(goods.getGoodsReleaseTime());
        //获取物品用户头像
        Map<String , Object> map = new HashMap<String,Object>();
        map.put("phone",goods.getUser().getPhone());
        okHttpHelper.get(Contans.API.GETUSERHEAD, map, new SimpleCallback<String>(context) {
            @Override
            public void onSuccess(Response response, String str) {
                Picasso.with(context).load(Contans.API.SHOW_PHOTO + "/" + str).into(holder.getImageView(R.id.item_head));
                System.out.println(Contans.API.SHOW_PHOTO + "/" + str +"==============");
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });

        LatLng end = new LatLng(goods.getGoodsLatitude(), goods.getGoodsLongitude());
        //LatLng start = new LatLng(getLocation().getLatitude(),getLocation().getLongitude());
        LatLng start = new LatLng(myLatitude,myLongitude);
        holder.getTextView(R.id.home_distance).setText(getDistance(start, end) + "m");

    }


    public Location getLocation() {// 获取Location通过LocationManger获取！
        LocationManager locManger = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location loc = locManger.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) {
            loc = locManger.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return loc;
    }

    public void initAMAP(){

        //声明定位回调监听器
        AMapLocationListener mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                //ToastUtils.show(context,""+"getLatitude"+aMapLocation.getLatitude()+"getLongitude"+aMapLocation.getLongitude());
                myLatitude = aMapLocation.getLatitude();
                myLongitude = aMapLocation.getLongitude();
            }
        };
        //初始化定位
        mLocationClient = new AMapLocationClient(ZuZuApplication.getInstance().getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //声明AMapLocationClientOption对象
         AMapLocationClientOption mLocationOption = null;
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
         mLocationClient.startLocation();
    }

    //获取两点间的距离
    public int getDistance(LatLng startLatlng, LatLng endLatlng){
        int result = (int) AMapUtils.calculateLineDistance(startLatlng, endLatlng);
        return result;
    }

    /**
     * 放入喜欢列表
     */
    public void putToLike(Goods goods){
        Map<String , Object> map = new HashMap<String,Object>();
        map.put("mineId", ZuZuApplication.getInstance().getUser().getId());
        map.put("goodsId",goods.getId());

        okHttpHelper.get(Contans.API.ADDLIKE, map, new SpotsCallBack<String>(context) {

            @Override
            public void onSuccess(Response response, String s) {
                if(s.equals("1")){
                    ToastUtils.show(context,"成功添加进喜欢列表");
                }else if(s.equals("2")) {
                    ToastUtils.show(context, "你已经添加到喜欢列表了");
                }else {
                    ToastUtils.show(context,"添加失败");
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
    }


}
