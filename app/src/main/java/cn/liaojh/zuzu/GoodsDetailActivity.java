package cn.liaojh.zuzu;

import android.content.Intent;
import android.os.Bundle;

import cn.liaojh.zuzu.bean.Goods;
import cn.liaojh.zuzu.fragment.BaseFragment;
import cn.liaojh.zuzu.fragment.GoodsDetailFragment;

/**
 * Created by Liaojh on 2016/10/30.
 */

public class GoodsDetailActivity extends AppActivity{

    Goods goods;

    GoodsDetailFragment goodsDetailFragment ;

    @Override
    protected BaseFragment getFirstFragment() {
        if(goods != null){
           goodsDetailFragment = new GoodsDetailFragment(goods);
        }
        return goodsDetailFragment;
    }

    @Override
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
        Bundle bundle = intent.getExtras();
        goods = (Goods) bundle.getSerializable("goods");

    }

    @Override
    protected void onDestroy() {
        removeFragment();

        super.onDestroy();
    }
}
