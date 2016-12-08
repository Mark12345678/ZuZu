package cn.liaojh.zuzu.fragment;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.SearchActivity;
import cn.liaojh.zuzu.bean.Goods;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.http.SpotsCallBack;
import cn.liaojh.zuzu.widget.MyToolBar;

/**
 * Created by Liaojh on 2016/12/6.
 */

public class SearchFragment extends Fragment{

    MyToolBar myToolBar;

    View view;

    ListView listView ;

    //设置title
    String title;

    OkHttpHelper okHttpHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_search_result,container,false);

        okHttpHelper = OkHttpHelper.getInstance();

        //获取上一个传递过来的参数
        title = getArguments().getString("title");

        initView(view);

        return view;
    }

    public void initView(View view){
        myToolBar = (MyToolBar) view.findViewById(R.id.search_toolbat);
        myToolBar.setTitle(title);

        listView = (ListView) view.findViewById(R.id.serach_result_list);

        myToolBar.setLeftButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fmanger =getFragmentManager();
                FragmentTransaction ftran =fmanger.beginTransaction();
                ftran.remove(SearchFragment.this);
                ftran.commit();

            }
        });

        initData(title);

    }

    public void setDataToList(List<Goods> listGoods){
        RentAdapter rentAdapter = new RentAdapter(getActivity(),listGoods);
        listView.setAdapter(rentAdapter);
    }


    public void initData(String key){

        if(okHttpHelper != null){
            okHttpHelper = OkHttpHelper.getInstance();
        }

        Map<String,Object> prams = new HashMap<String,Object>();
        prams.put("likeName",key);

        okHttpHelper.get(Contans.API.SEEACHKEY, prams, new SpotsCallBack<List<Goods>>(getActivity()) {

            @Override
            public void onSuccess(Response response, List<Goods> goodses) {
                //ToastUtils.show(SearchActivity.this,goodses.get(0).getGoodsName());

                setDataToList(goodses);
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });


    }


    class RentAdapter extends BaseAdapter {

        private Context context;
        private List<Goods> lists ;
        private LayoutInflater inflater;

        public RentAdapter(Context context , List<Goods> lists){
            this.context = context;
            this.lists = lists;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int i) {
            return lists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            ViewHolder viewHolder ;

            if(view == null){
                viewHolder = new ViewHolder();
                view = inflater.inflate(R.layout.list_item,null);
                viewHolder.imageView = (ImageView) view.findViewById(R.id.item_img);
                viewHolder.txt_name = (TextView) view.findViewById(R.id.item_goodsname);
                viewHolder.txt_desctibe = (TextView) view.findViewById(R.id.item_describe);
                viewHolder.txt_phone = (TextView) view.findViewById(R.id.item_username);
                viewHolder.txt_price = (TextView) view.findViewById(R.id.item_price);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.txt_phone.setText(lists.get(i).getUser().getPhone());
            viewHolder.txt_desctibe.setText(lists.get(i).getGoodsDescribe());
            viewHolder.txt_price.setText("￥"+lists.get(i).getGoodsPrice());
            viewHolder.imageView.setImageResource(R.drawable.default_goods);
            viewHolder.txt_name.setText(lists.get(i).getGoodsName());

            return view;
        }

        class ViewHolder{
            ImageView imageView;
            TextView txt_phone,txt_name,txt_price,txt_desctibe;
        }
    }



}
