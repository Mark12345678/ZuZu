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

        if(type_fragment == Contans.SEARCH_GOODS) {
            initData(title);
        }else if(type_fragment == Contans.RELEASE_GOODS) {
            if(ZuZuApplication.getInstance().getUser() != null){
                initReleaseGoods(ZuZuApplication.getInstance().getUser().getId());
            }
        }

    }

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


    public void initReleaseSelect(final Goods goods){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("干哈？");
        builder.setIcon(R.drawable.default_head);
        builder.setCancelable(false);  //不响应back按钮
        builder.setItems(new String[]{"查看物品详情","添加授权用户","删除物品","取消选择图片"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:      //查看物品详情
                        dialog.dismiss();
                        opeanMasterGoodsDetail(goods);
                        break;

                    case 1:      //添加授权用户

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


    /**
     * 请求删除指定物品
     * @param goods
     */
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
