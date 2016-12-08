package cn.liaojh.zuzu.fragment;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.Response;

import java.util.List;
import java.util.Map;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.ZuZuApplication;
import cn.liaojh.zuzu.bean.Goods;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.http.SpotsCallBack;
import cn.liaojh.zuzu.utils.ToastUtils;
import cn.liaojh.zuzu.widget.MyToolBar;


/**
 * Created by Liaojh on 2016/10/15.
 */

public class RentFragment extends BaseFragment {

    MyToolBar rent_mytoolbat;
    ListView listView ;
    //分别是喜欢，待收，待还
    TextView textView1,textView2,textView3;

    OkHttpHelper okHttpHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rent;
    }

    @Override
    public void initView(View view, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        initView(view);
        okHttpHelper = OkHttpHelper.getInstance();
        if(ZuZuApplication.getInstance().getUser() != null) {
            getLikeGoods();
        }
    }

    public void initView(View view){
        rent_mytoolbat = (MyToolBar) view.findViewById(R.id.rent_mytoolbat);
        rent_mytoolbat.setTitle("我的租单");
        textView1 = (TextView) view.findViewById(R.id.text1);
        textView2 = (TextView) view.findViewById(R.id.text2);
        textView3 = (TextView) view.findViewById(R.id.text3);
        listView = (ListView) view.findViewById(R.id.rent_list);
    }

    public void getLikeGoods(){

        okHttpHelper.get(Contans.API.FINDLIKE + "?mineId=" + ZuZuApplication.getInstance().getUser().getId(), new SpotsCallBack<List<Goods>>(getActivity()) {
            @Override
            public void onSuccess(Response response, List<Goods> goodses) {
                RentAdapter rentAdapter = new RentAdapter(getActivity(),goodses);
                listView.setAdapter(rentAdapter);
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });

    }

    class RentAdapter extends BaseAdapter{

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
