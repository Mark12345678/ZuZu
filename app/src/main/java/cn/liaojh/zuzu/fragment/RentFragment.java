package cn.liaojh.zuzu.fragment;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Response;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.GoodsDetailActivity;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.ZuZuApplication;
import cn.liaojh.zuzu.adapter.HomeAdapter;
import cn.liaojh.zuzu.bean.Goods;
import cn.liaojh.zuzu.bean.Page;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.http.SpotsCallBack;
import cn.liaojh.zuzu.utils.Pager;
import cn.liaojh.zuzu.utils.PagerBuilder;
import cn.liaojh.zuzu.utils.ToastUtils;
import cn.liaojh.zuzu.widget.MyToolBar;


/**
 * Created by Liaojh on 2016/10/15.
 */

public class RentFragment extends BaseFragment {

    OkHttpHelper okHttpHelper;

    @ViewInject(R.id.rent_mytoolbat)
    private MyToolBar rent_mytoolbat;

    @ViewInject(R.id.text1)
    private TextView textView1;

    @ViewInject(R.id.text2)
    private TextView textView2;

    @ViewInject(R.id.text3)
    private TextView textView3;

    @ViewInject(R.id.rent_recycleView)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.rent_materialRefreshLayout)
    private MaterialRefreshLayout mRefreshLayout;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rent;
    }

    @Override
    public void initView(View view, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        okHttpHelper = OkHttpHelper.getInstance();

        rent_mytoolbat.setTitle("我的租单");
        if(ZuZuApplication.getInstance().getUser() != null) {
            getLikeGoods();
        }
    }

    public void getLikeGoods(){

        PagerBuilder builder = new PagerBuilder();
        builder.setUrl(Contans.API.FINDLIKE);
        builder.setCanLoadMore(true);
        builder.setRefreshLayout(mRefreshLayout);
        builder.setOnPageListener(new loadMessage());
        builder.setPageSize(5);
        builder.build(getContext(),new TypeToken<Page<Goods>>(){}.getType());
        builder.putParam("mineId",ZuZuApplication.getInstance().getUser().getId());

        Pager pager = new Pager(builder);
        pager.request();

    }

    HomeAdapter mAdapter;
    class loadMessage implements Pager.OnPageListener<Goods>{

        @Override
        public void load(final List<Goods> datas, int totalPage, int totalCount) {

            Collections.reverse(datas);  //按照age降序 23,22

            mAdapter = new HomeAdapter(getActivity(),datas);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new cn.liaojh.zuzu.adapter.BaseAdapter.OnItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("goods",datas.get(position));
                    intent.putExtras(bundle);
                    intent.setClass(getActivity(), GoodsDetailActivity.class);
                    startActivity(intent);

                }
            });

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

            linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
            mRecyclerView.setLayoutManager(linearLayoutManager);

            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        @Override
        public void refresh(List<Goods> datas, int totalPage, int totalCount) {
            Collections.reverse(datas);  //按照age降序 23,22
            mAdapter.clearData();
            mAdapter.addData(datas);
            mRecyclerView.scrollToPosition(0);
        }

        @Override
        public void loadMore(List<Goods> datas, int totalPage, int totalCount) {
            Collections.reverse(datas);  //按照age降序 23,22
            //mAdapter.clearData();
            mAdapter.addData(mAdapter.getmDatas().size(),datas);
            mRecyclerView.scrollToPosition(mAdapter.getmDatas().size());
        }
    }

}
