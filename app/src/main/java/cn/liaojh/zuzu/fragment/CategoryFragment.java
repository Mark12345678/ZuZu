package cn.liaojh.zuzu.fragment;

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
import java.util.List;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.R;
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
    MaterialRefreshLayout category_materialRefreshLayout;

    @ViewInject(R.id.category_recycleView)
    RecyclerView category_recycleView;

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
        /*Pager pager = Pager.newBuilder()
                .setUrl(Contans.API.CATEGORY)
                .setCanLoadMore(false)
                .setOnPageListener(new loadMessage())
                .putParam("category1",category1 + "")
                .putParam("category2",category2 + "")
                .setPageSize(1000)
                .setRefreshLayout(category_materialRefreshLayout)
                .build(getActivity(),new TypeToken<Page<Goods>>(){}.getType());

        pager.request();*/

        PagerBuilder builder = new PagerBuilder();
        builder.setUrl(Contans.API.CATEGORY);
        builder.setCanLoadMore(false);
        builder.setRefreshLayout(category_materialRefreshLayout);
        builder.putParam("category1",category1 + "");
        builder.putParam("category2",category2 + "");
        builder.setOnPageListener(new loadMessage());
        builder.setPageSize(1000);
        builder.build(getContext(),new TypeToken<Page<Goods>>(){}.getType());

        Pager pager = new Pager(builder);
        pager.request();

    }


    class loadMessage implements Pager.OnPageListener<Goods>{
        @Override
        public void load(List<Goods> datas, int totalPage, int totalCount) {
            Log.i("XXXXXXXXXXXXXXXXXXXXX",datas.get(0).getGoodsName());
            mAdapter = new HomeAdapter(getActivity(),datas);
            category_recycleView.setAdapter(mAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

            linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
            category_recycleView.setLayoutManager(linearLayoutManager);

            category_recycleView.setItemAnimator(new DefaultItemAnimator());
        }

        @Override
        public void refresh(List<Goods> datas, int totalPage, int totalCount) {
            mAdapter.clearData();
            mAdapter.addData(datas);
            category_recycleView.scrollToPosition(0);
        }

        @Override
        public void loadMore(List<Goods> datas, int totalPage, int totalCount) {

        }
    }


}
