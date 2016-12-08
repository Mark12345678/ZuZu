package cn.liaojh.zuzu.adapter;

import android.content.Context;

import com.squareup.picasso.Picasso;

import java.util.List;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.bean.Goods;

/**
 * Created by Liaojh on 2016/10/27.
 */

public class HomeAdapter extends SimpleAdapter<Goods>{

    Context context;

    public HomeAdapter(Context context, List<Goods> datas) {
        super(context, datas, R.layout.list_item);
        this.context = context;
    }

    @Override
    public void bindData(BaseViewHolder holder, Goods goods) {
        holder.getTextView(R.id.item_username).setText(goods.getUser().getPhone());
        holder.getTextView(R.id.item_goodsname).setText(goods.getGoodsName());
        holder.getTextView(R.id.item_price).setText("￥"+goods.getGoodsPrice());
        holder.getTextView(R.id.item_describe).setText(goods.getGoodsDescribe());
        Picasso.with(context).load(Contans.API.SHOW_GOODS_PHOTO+"/"+goods.getStandPic()).into(holder.getImageView(R.id.item_img));
    }

}
