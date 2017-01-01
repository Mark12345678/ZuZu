package cn.liaojh.zuzu.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Response;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.GoodsDetailActivity;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.ZuZuApplication;
import cn.liaojh.zuzu.adapter.HomeAdapter;
import cn.liaojh.zuzu.adapter.RentAdapter;
import cn.liaojh.zuzu.bean.Goods;
import cn.liaojh.zuzu.bean.Page;
import cn.liaojh.zuzu.bean.isPayGoods;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.http.SpotsCallBack;
import cn.liaojh.zuzu.utils.Pager;
import cn.liaojh.zuzu.utils.PagerBuilder;
import cn.liaojh.zuzu.utils.ToastUtils;
import cn.liaojh.zuzu.widget.MyToolBar;


/**
 * Created by Liaojh on 2016/10/15.
 */

public class RentFragment extends BaseFragment implements View.OnClickListener{

    OkHttpHelper okHttpHelper;

    @ViewInject(R.id.rent_mytoolbat)
    private MyToolBar rent_mytoolbat;

    //喜欢列表
    @ViewInject(R.id.rentfragment_like)
    private TextView rentfragment_like;

    //已授权列表
    @ViewInject(R.id.rentfragment_accredit)
    private TextView rentfragment_accredit;

    //待收列表
    @ViewInject(R.id.rentfragment_waitcollect)
    private TextView rentfragment_waitcollect;

    //待还列表
    @ViewInject(R.id.rentfragment_waitback)
    private TextView rentfragment_waitback;

    @ViewInject(R.id.rent_recycleView)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.rent_materialRefreshLayout)
    private MaterialRefreshLayout mRefreshLayout;

    @Override
    public void initView(View view, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        okHttpHelper = OkHttpHelper.getInstance();
        rent_mytoolbat.setTitle("我的租单");

        rentfragment_like.setOnClickListener(this);
        rentfragment_accredit.setOnClickListener(this);
        rentfragment_waitcollect.setOnClickListener(this);
        rentfragment_waitback.setOnClickListener(this);

        //初始化数据,默认显示喜欢列表
        rentfragment_like.setTextColor(getResources().getColor(R.color.colorPrimary));
        if(ZuZuApplication.getInstance().getUser() != null){
            getLikeGoods();
        }

    }

    //用户获取待还物品
    public void getWaitBackGoods(){
        mRecyclerView.setAdapter(null);

        Map<String,Object> prams = new HashMap<String,Object>();
        prams.put("userId",ZuZuApplication.getInstance().getUser().getId());

        okHttpHelper.get(Contans.API.GOODSWAITBACK, prams, new SpotsCallBack<List<isPayGoods>>(getActivity()) {
            @Override
            public void onSuccess(Response response, List<isPayGoods> isGoods) {
                setData(isGoods,3);
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(getContext(),"网络异常");
            }
        });

    }

    //用户获取待收物品
    public void getWaitCollectGoods(){

        mRecyclerView.setAdapter(null);

        Map<String,Object> prams = new HashMap<String,Object>();
        prams.put("userId",ZuZuApplication.getInstance().getUser().getId());

        okHttpHelper.get(Contans.API.SHOWWAITCOLLECT, prams, new SpotsCallBack<List<isPayGoods>>(getActivity()) {
            @Override
            public void onSuccess(Response response, List<isPayGoods> isGoods) {
                setData(isGoods,2);
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(getContext(),"网络异常");
            }
        });
    }

    //用户获取授权物品
    public void getAccreditGodos(){
        mRecyclerView.setAdapter(null);

        Map<String,Object> prams = new HashMap<String,Object>();
        prams.put("userId",ZuZuApplication.getInstance().getUser().getId());

        okHttpHelper.get(Contans.API.BYACCREDITSHOW, prams, new SpotsCallBack<List<isPayGoods>>(getActivity()) {
            @Override
            public void onSuccess(Response response, List<isPayGoods> isGoods) {
                setData(isGoods,1);
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(getContext(),"网络异常");
            }
        });

    }

    //当获取了数据之后，通过这个方法来把数据填入
    public void setData(final List<isPayGoods> isGoods, final int typePosition){
        RentAdapter rentAdapter = new RentAdapter(getContext(),isGoods);
        mRecyclerView.setAdapter(rentAdapter);
        rentAdapter.setOnItemClickListener(new cn.liaojh.zuzu.adapter.BaseAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
               switch (typePosition){
                   case 1:
                       AccreditItemOnClickDialog(isGoods,position);
                       break;
                   case 2:
                       WaitCollectItemOnClickDialog(isGoods,position);
                       break;
                   case 3:
                       WaitBackItemOnClickDialog(isGoods,position);
                       break;
               }
            }
        });
    }

    //待还物品列表中的单项点击事件
    private void WaitBackItemOnClickDialog(final List<isPayGoods> isGoods, final int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("你要干哈？");
        builder.setIcon(R.drawable.default_head);
        builder.setCancelable(false);  //不响应back按钮
        builder.setItems(new String[]{"查看详情","确认已还","取消选择"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        Goods goods = isGoods.get(position);
                        bundle.putSerializable("goods",goods);
                        intent.putExtras(bundle);
                        intent.setClass(getActivity(), GoodsDetailActivity.class);
                        startActivity(intent);
                        break;

                    case 1:
                        Map<String,Object> params = new HashMap<String, Object>();
                        params.put("userId",ZuZuApplication.getInstance().getUser().getId());
                        params.put("goodsId",isGoods.get(position).getId());
                        okHttpHelper.get(Contans.API.USERSUREWAITBACK, params, new SpotsCallBack<String>(getActivity()) {
                            @Override
                            public void onSuccess(Response response, String s) {
                                if(s.equals("1")){
                                    ToastUtils.show(getActivity(),"确认成功");
                                }else if(s.equals("-1")){
                                    ToastUtils.show(getActivity(),"确认失败");
                                }else if(s.equals("2")){
                                    ToastUtils.show(getActivity(),"迟一步了，已经有人付款了");
                                }
                                getWaitCollectGoods();
                            }

                            @Override
                            public void onError(Response response, int code, Exception e) {
                                ToastUtils.show(getActivity(),"网络异常");
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        });
        //显示对话框
        builder.create().show();

    }

    //待收物品列表中的单项点击事件
    private void WaitCollectItemOnClickDialog(final List<isPayGoods> isGoods, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("你要干哈？");
        builder.setIcon(R.drawable.default_head);
        builder.setCancelable(false);  //不响应back按钮
        builder.setItems(new String[]{"查看详情","确认收货","取消选择"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        Goods goods = isGoods.get(position);
                        bundle.putSerializable("goods",goods);
                        intent.putExtras(bundle);
                        intent.setClass(getActivity(), GoodsDetailActivity.class);
                        startActivity(intent);
                        break;

                    case 1:
                        Map<String,Object> params = new HashMap<String, Object>();
                        params.put("userId",ZuZuApplication.getInstance().getUser().getId());
                        params.put("goodsId",isGoods.get(position).getId());
                        okHttpHelper.get(Contans.API.USERSUREWAITCOLLECT, params, new SpotsCallBack<String>(getActivity()) {
                            @Override
                            public void onSuccess(Response response, String s) {
                                if(s.equals("1")){
                                    ToastUtils.show(getActivity(),"确认成功");
                                }else if(s.equals("-1")){
                                    ToastUtils.show(getActivity(),"确认失败");
                                }else if(s.equals("2")){
                                    ToastUtils.show(getActivity(),"迟一步了，已经有人付款了");
                                }
                                getWaitCollectGoods();
                            }

                            @Override
                            public void onError(Response response, int code, Exception e) {
                                ToastUtils.show(getActivity(),"网络异常");
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        });
        //显示对话框
        builder.create().show();
    }

    //授权物品列表中的单项点击事件
    private void AccreditItemOnClickDialog(final List<isPayGoods> isGoods, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("你要干哈？");
        builder.setIcon(R.drawable.default_head);
        builder.setCancelable(false);  //不响应back按钮
        builder.setItems(new String[]{"查看详情","确认付款","取消选择"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        Goods goods = isGoods.get(position);
                        bundle.putSerializable("goods",goods);
                        intent.putExtras(bundle);
                        intent.setClass(getActivity(), GoodsDetailActivity.class);
                        startActivity(intent);
                        break;

                    case 1:
                        Map<String,Object> params = new HashMap<String, Object>();
                        params.put("userId",ZuZuApplication.getInstance().getUser().getId());
                        params.put("goodsId",isGoods.get(position).getId());
                        okHttpHelper.get(Contans.API.USERPAYSURE, params, new SpotsCallBack<String>(getActivity()) {
                            @Override
                            public void onSuccess(Response response, String s) {
                                if(s.equals("1")){
                                    ToastUtils.show(getActivity(),"确认付款成功");
                                }else if(s.equals("-1")){
                                    ToastUtils.show(getActivity(),"添加失败");
                                }else if(s.equals("2")){
                                    ToastUtils.show(getActivity(),"迟一步了，已经有人付款了");
                                }
                                getAccreditGodos();
                            }

                            @Override
                            public void onError(Response response, int code, Exception e) {
                                ToastUtils.show(getActivity(),"网络异常");
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        });
        //显示对话框
        builder.create().show();
    }

    //用户获取喜欢物品列表
    public void getLikeGoods(){
        //清空之前的内容
        mRecyclerView.setAdapter(null);

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

    @Override
    public void onClick(View view) {

        if(ZuZuApplication.getInstance().getUser() != null) {
            switch (view.getId()){
                case  R.id.rentfragment_like :
                    ChangTitileColor(R.id.rentfragment_like);
                    getLikeGoods();
                    break;
                case  R.id.rentfragment_accredit :
                    ChangTitileColor(R.id.rentfragment_accredit);
                    getAccreditGodos();
                    break;
                case  R.id.rentfragment_waitcollect :
                    ChangTitileColor(R.id.rentfragment_waitcollect);
                    getWaitCollectGoods();
                    break;
                case  R.id.rentfragment_waitback :
                    ChangTitileColor( R.id.rentfragment_waitback);
                    getWaitBackGoods();
                    break;
            }
        }
    }

    //设置选择类别后的颜色变化
    public void ChangTitileColor(int viewId){
        switch (viewId){
            case R.id.rentfragment_like:
                rentfragment_like.setTextColor(getResources().getColor(R.color.colorPrimary));
                rentfragment_accredit.setTextColor(getResources().getColor(R.color.gray));
                rentfragment_waitcollect.setTextColor(getResources().getColor(R.color.gray));
                rentfragment_waitback.setTextColor(getResources().getColor(R.color.gray));
                break;
            case R.id.rentfragment_accredit:
                rentfragment_like.setTextColor(getResources().getColor(R.color.gray));
                rentfragment_accredit.setTextColor(getResources().getColor(R.color.colorPrimary));
                rentfragment_waitcollect.setTextColor(getResources().getColor(R.color.gray));
                rentfragment_waitback.setTextColor(getResources().getColor(R.color.gray));
                break;
            case R.id.rentfragment_waitcollect:
                rentfragment_like.setTextColor(getResources().getColor(R.color.gray));
                rentfragment_accredit.setTextColor(getResources().getColor(R.color.gray));
                rentfragment_waitcollect.setTextColor(getResources().getColor(R.color.colorPrimary));
                rentfragment_waitback.setTextColor(getResources().getColor(R.color.gray));
                break;
            case R.id.rentfragment_waitback:
                rentfragment_like.setTextColor(getResources().getColor(R.color.gray));
                rentfragment_accredit.setTextColor(getResources().getColor(R.color.gray));
                rentfragment_waitcollect.setTextColor(getResources().getColor(R.color.gray));
                rentfragment_waitback.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
        }
    }

    //这个是专门为喜欢列表设置的上拉刷新，下拉加载
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

    //设置布局
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rent;
    }
}
