package cn.liaojh.zuzu.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.ZuZuApplication;
import cn.liaojh.zuzu.adapter.HistoryRentAdapter;
import cn.liaojh.zuzu.bean.Goods;
import cn.liaojh.zuzu.bean.HistoryRent;
import cn.liaojh.zuzu.bean.Page;
import cn.liaojh.zuzu.utils.Pager;
import cn.liaojh.zuzu.utils.PagerBuilder;
import cn.liaojh.zuzu.utils.ToastUtils;

/**
 * Created by Liaojh on 2017/1/1.
 */

public class HIstoryRentFragment extends Fragment implements View.OnClickListener{

    @ViewInject(R.id.history_text_in)
    TextView history_text_in;

    @ViewInject(R.id.history_text_out)
    TextView history_text_out;

    @ViewInject(R.id.history_recycleView)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.history_materialRefreshLayout)
    private MaterialRefreshLayout mRefreshLayout;

    View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_history,container,false);
        ViewUtils.inject(this, view);

        getRentOutGoods();

        return view;
    }


    @Override
    public void onClick(View view) {

    }

    public void getRentOutGoods(){

        PagerBuilder builder = new PagerBuilder();
        builder.setUrl(Contans.API.OUTRENTGOODS);
        builder.setCanLoadMore(true);
        builder.setRefreshLayout(mRefreshLayout);
        builder.setOnPageListener(new loadRentOutMessage());
        builder.setPageSize(5);
        builder.putParam("userId", ZuZuApplication.getInstance().getUser().getId());
        builder.build(getContext(),new TypeToken<Page<HistoryRent>>(){}.getType());

        Pager pager = new Pager(builder);
        pager.request();

    }

    HistoryRentAdapter historyRentAdapter;
    class loadRentOutMessage implements Pager.OnPageListener<HistoryRent>{

        @Override
        public void load(List<HistoryRent> datas, int totalPage, int totalCount) {
            historyRentAdapter = new HistoryRentAdapter(getActivity(),datas);
            mRecyclerView.setAdapter(historyRentAdapter);

            ToastUtils.show(getContext(),datas.size()+"");

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

            linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
            mRecyclerView.setLayoutManager(linearLayoutManager);

            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        @Override
        public void refresh(List<HistoryRent> datas, int totalPage, int totalCount) {
            historyRentAdapter.clearData();
            historyRentAdapter.addData(datas);
            mRecyclerView.scrollToPosition(0);
        }

        @Override
        public void loadMore(List<HistoryRent> datas, int totalPage, int totalCount) {
            historyRentAdapter.addData(historyRentAdapter.getmDatas().size(),datas);
            mRecyclerView.scrollToPosition(historyRentAdapter.getmDatas().size());
        }
    }
}
