package cn.liaojh.zuzu.adapter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

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
import cn.liaojh.zuzu.http.SpotsCallBack;
import cn.liaojh.zuzu.utils.ToastUtils;

/**
 * Created by Liaojh on 2016/10/27.
 */

public class HomeAdapter extends SimpleAdapter<Goods>{

    OkHttpHelper okHttpHelper;
    Context context;
    private double latitude=0.0;
    private double longitude =0.0;

    public HomeAdapter(Context context, List<Goods> datas) {
        super(context, datas, R.layout.list_item);
        this.context = context;
        okHttpHelper = OkHttpHelper.getInstance();
        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            if(context.checkSelfPermission(Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED) {

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location != null){

                }

            }else{
                //
            }
        }else{
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 0,locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null){
                latitude = location.getLatitude(); //经度
                longitude = location.getLongitude(); //纬度
            }
        }
    }

    @Override
    public void bindData(BaseViewHolder holder, final Goods goods) {
        holder.getTextView(R.id.item_username).setText(goods.getUser().getPhone());
        holder.getTextView(R.id.item_goodsname).setText(goods.getGoodsName());
        holder.getTextView(R.id.item_price).setText("￥"+goods.getGoodsPrice());
        holder.getTextView(R.id.item_describe).setText(goods.getGoodsDescribe());
        Picasso.with(context).load(Contans.API.SHOW_GOODS_PHOTO+"/"+goods.getStandPic()).into(holder.getImageView(R.id.item_img));

        LatLng start = new LatLng(latitude,longitude);
        LatLng end = new LatLng(goods.getGoodsLatitude(),goods.getGoodsLongitude());

        holder.getTextView(R.id.home_like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                putToLike(goods);
            }
        });

        holder.getTextView(R.id.home_distance).setText(getDistance(start,end)+"m");

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
