package cn.liaojh.zuzu.adapter;

import android.content.Context;

import com.squareup.picasso.Picasso;

import java.util.List;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.bean.HistoryRent;
import cn.liaojh.zuzu.http.OkHttpHelper;

/**
 * Created by Liaojh on 2017/1/1.
 */

public class HistoryRentAdapter extends SimpleAdapter<HistoryRent> {

    Context context;
    OkHttpHelper okHttpHelper;

    public HistoryRentAdapter(Context context, List<HistoryRent> datas) {
        super(context, datas, R.layout.history_item);
        this.context = context;
    }

    @Override
    public void bindData(BaseViewHolder holder, HistoryRent historyRent) {
       holder.getTextView(R.id.history_goodsname).setText(historyRent.getGoods().getGoodsName());
        holder.getTextView(R.id.history_price).setText("￥" + historyRent.getGoods().getGoodsPrice());
        holder.getTextView(R.id.history_describe).setText(historyRent.getGoods().getGoodsDescribe());
        holder.getTextView(R.id.history_user_phone).setText(historyRent.getUserPhone());
        holder.getTextView(R.id.history_master_phone).setText(historyRent.getMasterPhone());
        Picasso.with(context).load(Contans.API.SHOW_GOODS_PHOTO + "/" + historyRent.getGoods().getStandPic()).into(holder.getImageView(R.id.history_img));
    }


}
