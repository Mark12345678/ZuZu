package cn.liaojh.zuzu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.cjj.MaterialRefreshLayout;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.GoodsDetailActivity;
import cn.liaojh.zuzu.MapActivity;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.SearchActivity;
import cn.liaojh.zuzu.ZuZuApplication;
import cn.liaojh.zuzu.adapter.BaseAdapter;
import cn.liaojh.zuzu.adapter.HomeAdapter;
import cn.liaojh.zuzu.bean.Banner;
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

//不用理我，我是一行测试语句
public class HomeFragment extends BaseFragment{

    OkHttpHelper okHttpHelper ;

    @ViewInject(R.id.home_toolbar)
    MyToolBar myToolBar;

    HomeAdapter mAdapter;

    @ViewInject(R.id.home_recycleView)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.home_materialRefreshLayout)
    private MaterialRefreshLayout mRefreshLayout;

    private SliderLayout mSliderLayout;

    private ImageView home_map;

    //横幅容器
    List<Banner> mBanner ;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }


    @Override
    public void initView(View view, Bundle savedInstanceState) {

        mSliderLayout = (SliderLayout) view.findViewById(R.id.home_slider);
        okHttpHelper = OkHttpHelper.getInstance();
        home_map = (ImageView) view.findViewById(R.id.home_map);

        myToolBar.setSearchViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),SearchActivity.class);
                startActivity(intent);
            }
        });

        home_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                getActivity().startActivity(intent);
            }
        });

        //请求横幅图片
        requestImages();

        /*Pager pager = Pager.newBuilder()

                .setCanLoadMore(true)
                .setOnPageListener(new loadMessage())
                .setPageSize(5)
                .setRefreshLayout(mRefreshLayout)
                .build(getContext(),new TypeToken<Page<Goods>>(){}.getType());

        pager.request();*/


        PagerBuilder builder = new PagerBuilder();
        builder.setUrl(Contans.API.HOMEDATA);
        builder.setCanLoadMore(true);
        builder.setRefreshLayout(mRefreshLayout);
        builder.setOnPageListener(new loadMessage());
        builder.setPageSize(5);
        builder.build(getContext(),new TypeToken<Page<Goods>>(){}.getType());

        Pager pager = new Pager(builder);
        pager.request();

    }



    @Override
    public void initToolBar() {
        super.initToolBar();
    }

    /**
     * 请求横幅图片
     */
    private  void requestImages(){

        String url = Contans.API.BANNER;

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("type","测试");
        okHttpHelper.get(url, params, new SpotsCallBack<List<Banner>>(getActivity()) {

            @Override
            public void onSuccess(Response response, List<Banner> banners) {

                mBanner = banners;
                initSlider();
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }

        });

    }

    /**
     * 初始化轮播
     */
    public void initSlider(){
        if(mBanner !=null){

            for (Banner banner : mBanner){
                TextSliderView textSliderView = new TextSliderView(this.getActivity());
                textSliderView.image(banner.getImageUrl());
                textSliderView.description(banner.getName());
                textSliderView.setScaleType(BaseSliderView.ScaleType.Fit);
                mSliderLayout.addSlider(textSliderView);

            }
        }

        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

        mSliderLayout.setCustomAnimation(new DescriptionAnimation());
        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.RotateUp);
        mSliderLayout.setDuration(3000);

    }

    @Override
    public void onPause() {
        super.onPause();
        mSliderLayout.stopAutoCycle();
    }

    class loadMessage implements Pager.OnPageListener<Goods>{

        @Override
        public void load(final List<Goods> datas, int totalPage, int totalCount) {

            Collections.reverse(datas);  //按照age降序 23,22

            mAdapter = new HomeAdapter(getActivity(),datas);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
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
            mAdapter.clearData();
            mAdapter.addData(mAdapter.getmDatas().size(),datas);
            mRecyclerView.scrollToPosition(mAdapter.getmDatas().size());
        }
    }


}
