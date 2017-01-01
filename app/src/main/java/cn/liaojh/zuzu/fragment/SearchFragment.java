package cn.liaojh.zuzu.fragment;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjj.MaterialRefreshLayout;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
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
import cn.liaojh.zuzu.adapter.BaseAdapter;
import cn.liaojh.zuzu.adapter.HomeAdapter;
import cn.liaojh.zuzu.adapter.RentAdapter;
import cn.liaojh.zuzu.bean.Goods;
import cn.liaojh.zuzu.bean.HistoryRent;
import cn.liaojh.zuzu.bean.Page;
import cn.liaojh.zuzu.bean.isPayGoods;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.http.SpotsCallBack;
import cn.liaojh.zuzu.utils.Pager;
import cn.liaojh.zuzu.utils.PagerBuilder;
import cn.liaojh.zuzu.utils.ToastUtils;
import cn.liaojh.zuzu.widget.MyToolBar;

/**
 * Created by Liaojh on 2016/12/6.
 */

public class SearchFragment extends Fragment{

    @ViewInject(R.id.search_toolbat)
    private MyToolBar myToolBar;

    @ViewInject(R.id.search_recycleView)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.serach_materialRefreshLayout)
    private MaterialRefreshLayout mRefreshLayout;

    private View view;

    //设置title
    private String title;
    private int type_fragment;

    OkHttpHelper okHttpHelper ;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            initReleaseGoods(ZuZuApplication.getInstance().getUser().getId());
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_search_result,container,false);
        ViewUtils.inject(this, view);
        okHttpHelper = OkHttpHelper.getInstance();
        //获取上一个传递过来的参数
        title = getArguments().getString("title");
        myToolBar.setTitle(title);
        type_fragment = getArguments().getInt(Contans.SEARCHFRAGMENT_TYPE);

        myToolBar.setLeftButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fmanger =getFragmentManager();
                FragmentTransaction ftran =fmanger.beginTransaction();
                ftran.remove(SearchFragment.this);
                ftran.commit();

            }
        });

        initView(view);

        return view;
    }

    //这是搜索页面的
    public void initView(View view){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if(type_fragment == Contans.SEARCH_GOODS) {
            initData(title);
        }else if(type_fragment == Contans.RELEASE_GOODS) {
            if(ZuZuApplication.getInstance().getUser() != null){
                initReleaseGoods(ZuZuApplication.getInstance().getUser().getId());
            }
        }else if(type_fragment == Contans.WAITCOLLECT_GOODS){
            if(ZuZuApplication.getInstance().getUser() != null){
                initWaitCollectGoods(ZuZuApplication.getInstance().getUser().getId());
            }
        }else if(type_fragment == Contans.WAITBACKGOODS){
            if(ZuZuApplication.getInstance().getUser() != null){
                initWaitBack(ZuZuApplication.getInstance().getUser().getId());
            }
        }

    }

    //初始化待收回物品
    private void initWaitBack(int userId) {

        Map<String,Object> prams = new HashMap<String,Object>();
        prams.put("masterId",userId);

        okHttpHelper.get(Contans.API.MASTERSHOWWAITBACK, prams, new SpotsCallBack<List<isPayGoods>>(getActivity()) {
            @Override
            public void onSuccess(Response response, List<isPayGoods> isGoods) {
                serData(isGoods,Contans.WAITBACKGOODS);
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(getContext(),"网络异常");
            }
        });

    }

    //初始化待发货物品
    private void initWaitCollectGoods(int userId) {


       Map<String,Object> param = new HashMap<String,Object>();
        param.put("masteId", userId);
        okHttpHelper.get(Contans.API.MASTERSHOWWAITOUT, param, new SpotsCallBack<List<isPayGoods>>(getActivity()) {
            @Override
            public void onSuccess(Response response, List<isPayGoods> list) {
                serData(list,Contans.WAITCOLLECT_GOODS);
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });

    }

    //初始化可租物品列表
    private void initReleaseGoods(int userId) {

        PagerBuilder builder = new PagerBuilder();
        builder.setUrl(Contans.API.USERRELEASE);
        builder.setCanLoadMore(true);
        builder.setRefreshLayout(mRefreshLayout);
        builder.setOnPageListener(new loadMessage());
        builder.setPageSize(5);
        builder.putParam("usetId",userId);
        builder.build(getContext(),new TypeToken<Page<Goods>>(){}.getType());

        Pager pager = new Pager(builder);
        pager.request();

    }

    //初始化搜索数据
    public void initData(String key){

        PagerBuilder builder = new PagerBuilder();
        builder.setUrl(Contans.API.SEEACHKEY);
        builder.setCanLoadMore(true);
        builder.setRefreshLayout(mRefreshLayout);
        builder.setOnPageListener(new loadMessage());
        builder.setPageSize(5);
        builder.putParam("likeName",key);
        builder.build(getContext(),new TypeToken<Page<Goods>>(){}.getType());

        Pager pager = new Pager(builder);
        pager.request();

    }

    //获取物品数据后，通过这个方法设置显示数据
    public void serData(final List<isPayGoods> list , int type){

        RentAdapter rentAdapter = new RentAdapter(getContext(),list);
        mRecyclerView.setAdapter(rentAdapter);
        //待发货的物品
        if(type == Contans.WAITCOLLECT_GOODS){
            rentAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    initWaitCollcetSelect(list.get(position));
                }
            });
        }
        //待收回的物品
        else if(type == Contans.WAITBACKGOODS) {
            rentAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    initWaitBackSelect(list.get(position));
                }
            });
        }
    }

    //这个类，分别在可租物品和搜索物品中用到
    HomeAdapter mAdapter;
    class loadMessage implements Pager.OnPageListener<Goods>{

        @Override
        public void load(final List<Goods> datas, int totalPage, int totalCount) {

            Collections.reverse(datas);  //按照age降序 23,22

            mAdapter = new HomeAdapter(getActivity(),datas);
            mRecyclerView.setAdapter(mAdapter);
            if(type_fragment == Contans.SEARCH_GOODS){
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
            }else if(type_fragment == Contans.RELEASE_GOODS){
                mAdapter.setOnItemClickListener(new cn.liaojh.zuzu.adapter.BaseAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        initReleaseSelect(datas.get(position));

                    }
                });
            }

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

    //可租物品列表中的，单项点击事件
    public void initReleaseSelect(final Goods goods){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("干哈？");
        builder.setIcon(R.drawable.default_head);
        builder.setCancelable(false);  //不响应back按钮
        builder.setItems(new String[]{"查看物品详情","查看授权用户","删除物品","取消选择"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:      //查看物品详情
                        dialog.dismiss();
                        opeanMasterGoodsDetail(goods);
                        break;

                    case 1:      //添加授权用户
                        FragmentAddAccredit fragment = new FragmentAddAccredit();
                        FragmentManager fmanger =getFragmentManager();
                        //开启一个事务
                        FragmentTransaction ftran =fmanger.beginTransaction();
                        //往Activity中添加fragment
                        ftran.add(R.id.mine_test,fragment);
                        //创建一个bundle对象，往里面设置参数
                        Bundle bundle = new Bundle();
                        bundle.putInt("goodsId", goods.getId());
                        //吧bundle当住参数，设置给fragment
                        fragment.setArguments(bundle);
                        ftran.addToBackStack("");
                        ftran.commit();
                        dialog.dismiss();
                        break;

                    case 2:      //删除添加
                        dialog.dismiss();
                        requestDeleteGoods(goods);
                        handler.sendEmptyMessage(0);
                        break;

                    case 3:      //取消添加
                        dialog.dismiss();
                        break;

                    default:
                        break;
                }
            }
        });
        //显示对话框
        builder.create().show();
    }

    //待发货物品列表中的，单项点击事件
    public void initWaitCollcetSelect(final Goods goods){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("干哈？");
        builder.setIcon(R.drawable.default_head);
        builder.setCancelable(false);  //不响应back按钮
        builder.setItems(new String[]{"查看物品详情","确认发货","取消选择"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:      //查看物品详情
                        dialog.dismiss();
                        opeanMasterGoodsDetail(goods);
                        break;

                    case 1:      //确认发货
                        MasterSureOut(goods);
                        break;

                    case 2:      //取消添加
                        dialog.dismiss();
                        break;

                    default:
                        break;
                }
            }
        });
        //显示对话框
        builder.create().show();
    }

    //待回收物品列表中的，单项点击事件
    public void initWaitBackSelect(final Goods goods){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("干哈？");
        builder.setIcon(R.drawable.default_head);
        builder.setCancelable(false);  //不响应back按钮
        builder.setItems(new String[]{"查看物品详情","确认收回","取消选择"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:      //查看物品详情
                        dialog.dismiss();
                        opeanMasterGoodsDetail(goods);
                        break;

                    case 1:      //确认发货
                        MasterSureBack(goods);
                        break;

                    case 2:      //取消添加
                        dialog.dismiss();
                        break;

                    default:
                        break;
                }
            }
        });
        //显示对话框
        builder.create().show();
    }

    //物主确认已经回收了
    public void MasterSureBack(Goods goods){

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("goodsId",goods.getId());
        params.put("masterId",ZuZuApplication.getInstance().getUser().getId());
        okHttpHelper.get(Contans.API.MASTERSUREWAITBACK, params, new SpotsCallBack<String>(getActivity()) {
            @Override
            public void onSuccess(Response response, String str) {
                if(str.equals("1")){
                    ToastUtils.show(getContext(),"确认成功");
                }else if(str.equals("-1")){
                    ToastUtils.show(getContext(),"确认失败");
                }
                initWaitBack(ZuZuApplication.getInstance().getUser().getId());
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(getContext(),"网络异常");
            }
        });
    }

    //物主确认已经发货了
    public void MasterSureOut(Goods goods){

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("goodsId",goods.getId());
        params.put("masterId",ZuZuApplication.getInstance().getUser().getId());
        okHttpHelper.get(Contans.API.MASTERSUREWAITCOLLECT, params, new SpotsCallBack<String>(getActivity()) {
            @Override
            public void onSuccess(Response response, String str) {
                if(str.equals("1")){
                    ToastUtils.show(getContext(),"确认成功");
                }else if(str.equals("-1")){
                    ToastUtils.show(getContext(),"确认失败");
                }
                initWaitCollectGoods(ZuZuApplication.getInstance().getUser().getId());
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(getContext(),"网络异常");
            }
        });
    }

    //查看物品详情
    public void opeanMasterGoodsDetail(Goods  goods){
            MasterGoodsDetailFragment fragment = new MasterGoodsDetailFragment(handler);
            FragmentManager fmanger =getFragmentManager();
            //开启一个事务
            FragmentTransaction ftran =fmanger.beginTransaction();
            //往Activity中添加fragment
            ftran.add(R.id.meater_search,fragment);
            //创建一个bundle对象，往里面设置参数
            Bundle bundle = new Bundle();
            bundle.putSerializable("goods",goods);
            //吧bundle当住参数，设置给fragment
            fragment.setArguments(bundle);
            ftran.addToBackStack("物主物品详情");
            ftran.commit();
        }

    //删除指定物品
    private void requestDeleteGoods(Goods goods){

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("goodsId",goods.getId());

        okHttpHelper.get(Contans.API.DELETEGOODS, params, new SpotsCallBack<String>(getActivity()) {
            @Override
            public void onSuccess(Response response, String s) {
                if(s.equals("1")){
                    ToastUtils.show(getContext(),"删除成功");
                }else if(s.equals("-1")){
                    ToastUtils.show(getContext(),"删除失败");
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(getContext(),"网络异常");
            }
        });

    }
}
