package cn.liaojh.zuzu.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.bean.Page;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.http.SpotsCallBack;

/**
 * Created by Liaojh on 2016/10/27.
 */

public  class Pager<T> {

    public PagerBuilder builder;

    private OkHttpHelper okHttpHelper;



    private int state= Contans.STATE_NORMAL;

    public   Pager(PagerBuilder pagerBuilder){
        this.builder = pagerBuilder;
        okHttpHelper = OkHttpHelper.getInstance();
        initRefreshLayout();
    }



    public void request(){
        requestData();
    }

    public void putParam(String key , Object value){
        builder.params.put(key,value);
    }

    private void initRefreshLayout(){
        builder.mRefreshLayout.setLoadMore(builder.canLoadMore);

        builder.mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                builder.mRefreshLayout.setLoadMore(builder.canLoadMore);
                refresh();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {

                if(builder.pageIndex<builder.totalPage)
                    loadMore();
                else{
                    ToastUtils.show(builder.mContext , "无更多数据啦");
                    materialRefreshLayout.finishRefreshLoadMore();
                    materialRefreshLayout.setLoadMore(false);
                }
            }
        });

    }


    private <T> void showData(List<T> datas, int totalPage , int totalCount){

        if(datas == null || datas.size() <= 0){
           ToastUtils.show(builder.mContext,"加载不到数据");
            return;
        }

        if(Contans.STATE_NORMAL==state){
            if(builder.onPageListener != null){
                builder.onPageListener.load(datas,totalPage,totalCount);
            }
        }

        else  if(Contans.STATE_REFREH==state)   {
            builder.mRefreshLayout.finishRefresh();
            if(builder.onPageListener !=null){
                builder.onPageListener.refresh(datas,totalPage,totalCount);
            }
        }

        else  if(Contans.STATE_MORE == state){
            builder.mRefreshLayout.finishRefreshLoadMore();
            if(builder.onPageListener !=null){
                builder.onPageListener.loadMore(datas,totalPage,totalCount);
            }
        }
    }

    /**
     * 刷新数据
     */
    private void refresh(){

        state=Contans.STATE_REFREH;
        builder.pageIndex = 1;
        builder.totalRecord = 0;
        requestData();
    }

    /**
     * 隐藏数据
     */
    private void loadMore(){

        state=Contans.STATE_MORE;
        builder.pageIndex =++builder.pageIndex;
        requestData();
    }

    /**
     * 构建URL
     * @return
     */
    private String buildUrl(){

        return builder.mUrl +"?"+buildUrlParams();
    }

    private   String buildUrlParams() {


        HashMap<String, Object> map = builder.params;

        map.put("curryPage",builder.pageIndex);
        map.put("pageSize",builder.pageSize);
        map.put("totalRecord",builder.totalRecord);

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0,s.length()-1);
        }
        return s;
    }


    private void requestData(){
        String url = buildUrl();

        Log.i("获取物品数据",url);

        okHttpHelper.get(url, new RequestCallBack(builder.mContext));
    }

    class RequestCallBack<T> extends SpotsCallBack<Page<T>>{


        public RequestCallBack(Context context) {
            super(context);

            super.mType = builder.mType;

        }

        @Override
        public void onError(Response response, int code, Exception e) {


            if(Contans.STATE_REFREH==state)   {
                builder.mRefreshLayout.finishRefresh();
            }
            else  if(Contans.STATE_MORE == state){

                builder.mRefreshLayout.finishRefreshLoadMore();
            }
        }

        @Override
        public void onFailure(Request request, Exception e) {

            Log.i("怎么了6","=====================");

            dismissDialog();
            ToastUtils.show(builder.mContext,"请求失败"+e.getMessage());

            if(Contans.STATE_REFREH == state){
                builder.mRefreshLayout.finishRefresh();
            }
            else  if(Contans.STATE_MORE == state){

                builder.mRefreshLayout.finishRefreshLoadMore();
            }
        }

        @Override
        public void onSuccess(Response response, Page<T> tPage) {

            builder.pageIndex = tPage.getCurryPage();
            builder.pageSize = tPage.getPageSize();
            builder.totalPage = tPage.getTotalPage();
            builder.totalRecord = tPage.getTotalRecord();

            showData(tPage.getList(),tPage.getTotalPage(),tPage.getTotalRecord());
        }


    }

    public interface OnPageListener<T>{

        void load(List<T> datas, int totalPage , int totalCount);

        void refresh(List<T> datas, int totalPage, int totalCount);

        void loadMore(List<T> datas, int totalPage , int totalCount);

    }

}
