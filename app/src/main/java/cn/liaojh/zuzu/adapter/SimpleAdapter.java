package cn.liaojh.zuzu.adapter;

import android.content.Context;

import java.util.List;

/**
 * Created by Liaojh on 2016/10/27.
 */

public abstract class SimpleAdapter<T> extends BaseAdapter<T, BaseViewHolder>{

    public SimpleAdapter(Context context, List<T> datas, int mLayoutResId) {
        super(context, datas, mLayoutResId);
    }
}
