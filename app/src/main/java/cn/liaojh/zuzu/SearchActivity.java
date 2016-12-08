package cn.liaojh.zuzu;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.liaojh.zuzu.bean.Goods;
import cn.liaojh.zuzu.fragment.ChatListFragment;
import cn.liaojh.zuzu.fragment.SearchFragment;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.http.SimpleCallback;
import cn.liaojh.zuzu.http.SpotsCallBack;
import cn.liaojh.zuzu.utils.DBManagerHistory;
import cn.liaojh.zuzu.utils.ToastUtils;

public class SearchActivity extends AppCompatActivity {

    SearchView txt_search ;

    //历史记录
    ListView search_hoistory;
    //搜索提示
    ListView seatch_prompt;

    OkHttpHelper okHttpHelper;

    DBManagerHistory dbManagerHistory;

    List<String> listHistory = null;

    TextView clear_history,show_more_history;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        okHttpHelper = OkHttpHelper.getInstance();
        dbManagerHistory = new DBManagerHistory(SearchActivity.this);
        initView();

    }

    public void initView(){
        txt_search = (SearchView) findViewById(R.id.search_text);
        search_hoistory = (ListView) findViewById(R.id.search_list);
        seatch_prompt = (ListView) findViewById(R.id.search_prompt);
        txt_search.setSubmitButtonEnabled(true);
        clear_history = (TextView) findViewById(R.id.clear_history);
        show_more_history = (TextView) findViewById(R.id.show_more_history);

        //初始化时，获取搜索历史
        if(ZuZuApplication.getInstance().getUser() != null){
            listHistory = dbManagerHistory.query(ZuZuApplication.getInstance().getUser().getPhone());
            if(listHistory.size() > 10){
                listHistory.subList(0,10);
            }
        }


        txt_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {

                //获取搜索框中的内容
                String keyName = txt_search.getQuery().toString();

                findByKey(query);

                if(seatch_prompt.getAdapter() != null){
                    seatch_prompt.setAdapter(null);
                }

                if(ZuZuApplication.getInstance().getUser() != null){
                    //向数据库中存入搜索记录
                    dbManagerHistory.add(ZuZuApplication.getInstance().getUser().getPhone(),query);
                }


                SearchFragment fragment = new SearchFragment();
                FragmentManager fmanger =getFragmentManager();
                //开启一个事务
                FragmentTransaction ftran =fmanger.beginTransaction();
                //往Activity中添加fragment
                ftran.add(R.id.serach_frag,fragment);
                //创建一个bundle对象，往里面设置参数
                Bundle bundle = new Bundle();
                bundle.putString("title",query);
                //吧bundle当住参数，设置给fragment
                fragment.setArguments(bundle);
                ftran.addToBackStack("goodsList");
                ftran.commit();

                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {

                if(!newText.equals("")){
                    String[] str = getAjaxDate(newText);
                }else {
                    if(seatch_prompt.getAdapter() != null){
                        seatch_prompt.setAdapter(null);
                    }
                }
                return false;
            }
        });

        seatch_prompt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //设置点击item后,将值赋给搜索栏
                txt_search.setQuery(listName[i],false);
                //取消listView
                if(seatch_prompt.getAdapter() != null){
                    seatch_prompt.setAdapter(null);
                }
            }
        });


        //设置历史记录中的每一项的点击事件
        if(listHistory != null && listHistory.size() > 0){
            final String[] history = new String[listHistory.size()];
            for(int i = 0 ; i < listHistory.size() ; i++){
                history[i] = listHistory.get(i);
            }
            HistoryAdapter historyAdapter = new HistoryAdapter(SearchActivity.this, history);
            search_hoistory.setAdapter(historyAdapter);

            search_hoistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    txt_search.setQuery(history[i],false);
                }
            });

            //如果是有历史数据的话，那我们就显示清除数据，显示更多数据按钮
            clear_history.setVisibility(View.VISIBLE);
            show_more_history.setVisibility(View.VISIBLE);

            clear_history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //清除数据库中的数据
                    dbManagerHistory.clearAll(ZuZuApplication.getInstance().getUser().getPhone());
                    search_hoistory.setAdapter(null);
                    //把清除历史按钮和显示更多历史按钮设置看不见
                    clear_history.setVisibility(View.GONE);
                    show_more_history.setVisibility(View.GONE);
                }
            });

            show_more_history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listHistory = dbManagerHistory.query(ZuZuApplication.getInstance().getUser().getPhone());
                    search_hoistory.setAdapter(null);
                    final String[] history = new String[listHistory.size()];
                    for(int i = 0 ; i < listHistory.size() ; i++){
                        history[i] = listHistory.get(i);
                    }
                    HistoryAdapter historyAdapter = new HistoryAdapter(SearchActivity.this, history);
                    search_hoistory.setAdapter(historyAdapter);
                    //ToastUtils.show(SearchActivity.this,listHistory.size()+listHistory.get(1)+listHistory.get(2)+listHistory.get(3)+listHistory.get(4));
                    search_hoistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            txt_search.setQuery(history[i],false);
                        }
                    });
                }
            });

        }
    }


    /**
     *仿Ajax功能，传入一个参数，实时和后台进行交互
     * 返回该查询后的搜索提示
     */
    public String[] getAjaxDate(String subStr){

        Map<String,Object> prams = new HashMap<String,Object>();
        prams.put("subName",subStr);

        okHttpHelper.get(Contans.API.SERACHAJAX ,prams , new SimpleCallback<String>(SearchActivity.this) {

            @Override
            public void onSuccess(Response response, String s) {
                setWebDate(s);
                System.out.println(s+"======================2");
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });

        String[] listName = getWebData().split(" ");

        return listName;

    }

    String[] listName = null;
    String webData = "";
    public void setWebDate(String data){
        webData = data;
        listName = data.split(" ");
        HistoryAdapter promptAdpater = new HistoryAdapter(SearchActivity.this,listName);
        if(seatch_prompt.getAdapter() != null){
            seatch_prompt.setAdapter(null);
        }

        if(listName != null && listName.length != 0){
            seatch_prompt.setAdapter(promptAdpater);
        }

    }

    public String getWebData() {
        return webData;
    }

    List<Goods> listGoods = null;
    public void setListGoods(List<Goods> list){
        listGoods = list;
    }
    public List<Goods> getListGoods(){
        return listGoods;
    }


    /**
     * 根据关键字，进行搜索
     */
    public List<Goods> findByKey(String key){

        if(okHttpHelper != null){
            okHttpHelper = OkHttpHelper.getInstance();
        }

        Map<String,Object> prams = new HashMap<String,Object>();
        prams.put("likeName",key);

        okHttpHelper.get(Contans.API.SEEACHKEY, prams, new SpotsCallBack<List<Goods>>(SearchActivity.this) {

            @Override
            public void onSuccess(Response response, List<Goods> goodses) {
                setListGoods(goodses);
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
        return getListGoods();
    }


    class HistoryAdapter extends BaseAdapter{

        private Context context;

        private LayoutInflater inflater;

        String[] str = new String[]{};


        public HistoryAdapter(Context context ,String[] str){
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.str = str;
        }


        @Override
        public int getCount() {
            return str.length;
        }

        @Override
        public Object getItem(int i) {
            return str[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if(view == null){
                viewHolder = new ViewHolder();
                view = inflater.inflate(R.layout.history_adapter,null);
                viewHolder.txt_name = (TextView) view.findViewById(R.id.history_item);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }
                viewHolder.txt_name.setText(str[position]);

            return view;
        }

        class ViewHolder {
            TextView txt_name;
        }

    }

}
