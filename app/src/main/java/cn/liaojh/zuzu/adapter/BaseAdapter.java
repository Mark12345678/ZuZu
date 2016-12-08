package cn.liaojh.zuzu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Liaojh on 2016/10/27.
 */

public abstract class BaseAdapter<T, H extends BaseViewHolder> extends RecyclerView.Adapter<BaseViewHolder>{

    protected List<T> mDatas;
    protected LayoutInflater inflater;
    protected Context context;

    public int mLayoutResId;

    public OnItemClickListener listener;

    public interface OnItemClickListener{
        void onClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public BaseAdapter(Context context, List<T> datas , int mLayoutResId){
        this.mDatas = datas;
        this.context = context;
        this.mLayoutResId = mLayoutResId;

        inflater = LayoutInflater.from(context);

    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(mLayoutResId,null,false);

        return new BaseViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

        T t = getItem(position);

        bindData(holder,t);

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public T getItem(int position){
        return mDatas.get(position);
    }

    public List<T> getmDatas(){
        return mDatas;
    }

    public void clearData(){
        mDatas.clear();
        notifyItemRangeRemoved(0,mDatas.size());
    }

    public void addData(List<T> datas){

        addData(0,datas);

    }

    public void addData(int position , List<T> datas){

        if(datas != null && datas.size() > 0){
            mDatas.addAll(datas);
            notifyItemChanged(position,mDatas.size());
        }

    }

    public void refreshData(List<T> list){
        if(list != null && list.size() > 0){
            clearData();
            int size = list.size();
            for(int i=0; i<size; i++){
                mDatas.add(i,list.get(i));
                notifyItemInserted(i);
            }
        }
    }

    public void loadMoreData(List<T> list){
        if(list != null && list.size() > 0){
            int size = list.size();
            int begin = mDatas.size();
            for(int i=0; i<size; i++){
                mDatas.add(list.get(i));
                notifyItemInserted(i+begin);
            }
        }
    }

    public abstract void bindData(BaseViewHolder holder,T t);


}
