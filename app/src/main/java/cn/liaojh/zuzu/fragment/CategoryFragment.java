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
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.cjj.MaterialRefreshLayout;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.GoodsDetailActivity;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.ZuZuApplication;
import cn.liaojh.zuzu.adapter.BaseAdapter;
import cn.liaojh.zuzu.adapter.HomeAdapter;
import cn.liaojh.zuzu.bean.Goods;
import cn.liaojh.zuzu.bean.Page;
import cn.liaojh.zuzu.utils.CategotyMenuUtil;
import cn.liaojh.zuzu.utils.Pager;
import cn.liaojh.zuzu.utils.PagerBuilder;
import cn.liaojh.zuzu.utils.ToastUtils;
import cn.liaojh.zuzu.widget.MyToolBar;

/**
 * Created by Liaojh on 2016/10/15.
 */

public class CategoryFragment extends BaseFragment{

    @ViewInject(R.id.category_materialRefreshLayout)
    private MaterialRefreshLayout mRefreshLayout;

    @ViewInject(R.id.category_recycleView)
    private RecyclerView category_recycleView;

    @ViewInject(R.id.category_toolbar)
    MyToolBar myToolBar;

    @ViewInject(R.id.category_distance)
    TextView distance;

    @ViewInject(R.id.category_category)
    TextView category;

    @ViewInject(R.id.category_time)
    TextView time;

    //类别选择器
    OptionsPickerView pvOptions;

    private int category1 = -1 ;
    private int category2 = -1 ;

    HomeAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_category;
    }

    @Override
    public void initView(View view, Bundle savedInstanceState) {
        initCategory();

        //选项选择器
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvOptions.show();
            }
        });

    }

    public void initCategory(){
        ArrayList<String> options1Items = new ArrayList<String>();
        ArrayList<ArrayList<String>> options2Items = new ArrayList<ArrayList<String>>();

        pvOptions = new OptionsPickerView(getActivity());

        //初始化这个菜单
        pvOptions = CategotyMenuUtil.initCategory(pvOptions);

        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                category1 = options1 + 1;
                category2 = option2 + 1;
                requestGoods();
            }
        });
    }

    public void requestGoods(){

        /*PagerBuilder builder = new PagerBuilder();
        builder.setUrl(Contans.API.CATEGORY);
        builder.setCanLoadMore(false);
        builder.setRefreshLayout(category_materialRefreshLayout);
        builder.putParam("category1",category1 + "");
        builder.putParam("category2",category2 + "");
        builder.setOnPageListener(new loadMessage());
        builder.setPageSize(1000);
        builder.build(getContext(),new TypeToken<Page<Goods>>(){}.getType());

        Pager pager = new Pager(builder);
        pager.request();*/

        PagerBuilder builder = new PagerBuilder();
        builder.setUrl(Contans.API.FINDLIKE);
        builder.setCanLoadMore(true);
        builder.setRefreshLayout(mRefreshLayout);
        builder.setOnPageListener(new loadMessage());
        builder.setPageSize(5);
        builder.putParam("category1",category1 + "");
        builder.putParam("category2",category2 + "");
        builder.build(getContext(),new TypeToken<Page<Goods>>(){}.getType());
        builder.putParam("mineId", ZuZuApplication.getInstance().getUser().getId());

        Pager pager = new Pager(builder);
        pager.request();

    }


    class loadMessage implements Pager.OnPageListener<Goods>{

        @Override
        public void load(final List<Goods> datas, int totalPage, int totalCount) {

            Collections.reverse(datas);  //按照age降序 23,22

            mAdapter = new HomeAdapter(getActivity(),datas);
            category_recycleView.setAdapter(mAdapter);
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
            category_recycleView.setLayoutManager(linearLayoutManager);

            category_recycleView.setItemAnimator(new DefaultItemAnimator());
        }

        @Override
        public void refresh(List<Goods> datas, int totalPage, int totalCount) {
            Collections.reverse(datas);  //按照age降序 23,22
            mAdapter.clearData();
            mAdapter.addData(datas);
            category_recycleView.scrollToPosition(0);
        }

        @Override
        public void loadMore(List<Goods> datas, int totalPage, int totalCount) {
            Collections.reverse(datas);  //按照age降序 23,22
            //mAdapter.clearData();
            mAdapter.addData(mAdapter.getmDatas().size(),datas);
            category_recycleView.scrollToPosition(mAdapter.getmDatas().size());
        }
    }


}
