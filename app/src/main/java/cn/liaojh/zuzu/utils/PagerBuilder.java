package cn.liaojh.zuzu.utils;

import android.content.Context;

import com.cjj.MaterialRefreshLayout;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by Liaojh on 2016/11/17.
 */

public class PagerBuilder {

    Context mContext;
    Type mType;
    public String mUrl;

    public MaterialRefreshLayout mRefreshLayout;

    public boolean canLoadMore;

    public int totalPage = 0;
    public int pageIndex = 1;
    public int pageSize = 10;
    public int totalRecord = 0;

    public HashMap<String,Object> params = new HashMap<String,Object>(5);

    public Pager.OnPageListener onPageListener;

    public void setUrl(String url){
        this.mUrl = url;

    }

    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    public void setPageSize(int pageSize){
        this.pageSize = pageSize;
    }

    public void putParam(String key, Object value){
        params.put(key,value);
    }

    public void setLoadMore(boolean loadMore){
        this.canLoadMore = loadMore;
    }

    public void setRefreshLayout(MaterialRefreshLayout refreshLayout){

        this.mRefreshLayout = refreshLayout;

    }

    public void setOnPageListener(Pager.OnPageListener onPageListener) {
        this.onPageListener = onPageListener;
    }

    public void build(Context context, Type type){
        this.mType = type;
        this.mContext = context;
        valid();
    }

    private void valid(){

        if(this.mContext==null)
            throw  new RuntimeException("content can't be null");

        if(this.mUrl==null || "".equals(this.mUrl))
            throw  new RuntimeException("url can't be  null");

        if(this.mRefreshLayout==null)
            throw  new RuntimeException("MaterialRefreshLayout can't be  null");
    }
}
