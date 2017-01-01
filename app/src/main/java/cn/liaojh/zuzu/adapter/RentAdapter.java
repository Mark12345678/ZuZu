package cn.liaojh.zuzu.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.bean.isPayGoods;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.http.SimpleCallback;
import cn.liaojh.zuzu.utils.ToastUtils;

/**
 * Created by Liaojh on 2016/12/28.
 */

public class RentAdapter extends SimpleAdapter<isPayGoods> {

    OkHttpHelper okHttpHelper;
    Context context;

    public RentAdapter(Context context, List<isPayGoods> datas) {
        super(context, datas, R.layout.rent_item);

        this.context = context;
        okHttpHelper = OkHttpHelper.getInstance();
    }

    @Override
    public void bindData(final BaseViewHolder holder, isPayGoods isPayGoods) {

        holder.getTextView(R.id.rent_username).setText(isPayGoods.getUser().getPhone());
        holder.getTextView(R.id.rent_goodsname).setText(isPayGoods.getGoodsName());
        holder.getTextView(R.id.rent_price).setText("￥" + isPayGoods.getGoodsPrice());
        holder.getTextView(R.id.rent_describe).setText(isPayGoods.getGoodsDescribe());
        holder.getTextView(R.id.rent_time).setText(isPayGoods.getGoodsReleaseTime());
        if(isPayGoods.getMasterSure() == 1 && isPayGoods.getUsreSure() == 0){
            holder.getImageView(R.id.reng_tag).setImageResource(R.drawable.icon_master_sure);
            holder.getImageView(R.id.reng_tag).setVisibility(View.VISIBLE);
        }else if(isPayGoods.getMasterSure() == 0 && isPayGoods.getUsreSure() == 1){
            holder.getImageView(R.id.reng_tag).setImageResource(R.drawable.icon_user_sure);
            holder.getImageView(R.id.reng_tag).setVisibility(View.VISIBLE);
        }else if(isPayGoods.getMasterSure() == 0 && isPayGoods.getUsreSure() == 0){
            holder.getImageView(R.id.reng_tag).setVisibility(View.GONE);
        }
        Picasso.with(context).load(Contans.API.SHOW_GOODS_PHOTO + "/" + isPayGoods.getStandPic()).into(holder.getImageView(R.id.rent_img));
        //获取物品用户头像
        Map<String , Object> map = new HashMap<String,Object>();
        map.put("phone",isPayGoods.getUser().getPhone());
        okHttpHelper.get(Contans.API.GETUSERHEAD, map, new SimpleCallback<String>(context) {
            @Override
            public void onSuccess(Response response, String str) {
                Picasso.with(context).load(Contans.API.SHOW_PHOTO + "/" + str).into(holder.getImageView(R.id.rent_head));
                System.out.println(Contans.API.SHOW_PHOTO + "/" + str +"==============");
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
    }
}
