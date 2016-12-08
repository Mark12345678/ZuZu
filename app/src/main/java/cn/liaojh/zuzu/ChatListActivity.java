package cn.liaojh.zuzu;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.liaojh.zuzu.bean.User;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.http.SpotsCallBack;
import cn.liaojh.zuzu.utils.ToastUtils;
import cn.liaojh.zuzu.widget.MyToolBar;
import io.rong.imkit.RongIM;

public class ChatListActivity extends AppCompatActivity {

    ListView listView ;

    OkHttpHelper okHttpHelper ;

    List<User> listUser ;

    MyToolBar myToolBar ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        okHttpHelper = OkHttpHelper.getInstance();

        initView();

        getFriends();

    }

    public void initView(){
        listView = (ListView) findViewById(R.id.my_chatList);
        myToolBar = (MyToolBar) findViewById(R.id.chat_mytoolbat);

        myToolBar.setLeftButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //这是一个测试语句
        myToolBar.setTitle("好友列表");

    }

    public void getFriends(){

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("mineId", ZuZuApplication.getInstance().getUser().getId()+"");

        okHttpHelper.get(Contans.API.FINDFRIENDLIST+"?mineId=" +ZuZuApplication.getInstance().getUser().getId() , new SpotsCallBack<List<User>>(this) {

            @Override
            public void onSuccess(Response response, List<User> users) {
                listUser = users;

                listSetAdapter();
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
    }

    public void listSetAdapter(){
        ChatAdapter chatAdapter = new ChatAdapter();
        listView.setAdapter(chatAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //判断是否连接成功融云服务器
                if(ZuZuApplication.getInstance().isisConnectToIM() == false){
                    ZuZuApplication.getInstance().connect(ZuZuApplication.getInstance().getUser().getTakenIM());
                }

                opeanWindow(i);

            }
        });
    }

    public void opeanWindow(int position){
        //打开聊天窗口
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().startPrivateChat(ChatListActivity.this, listUser.get(position).getId() + "", "title");
        }
    }

    class ChatAdapter extends BaseAdapter{

        private LayoutInflater inflater = LayoutInflater.from(ChatListActivity.this);

        @Override
        public int getCount() {
            return listUser.size();
        }

        @Override
        public Object getItem(int i) {
            return listUser.get(i);
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
                view = inflater.inflate(R.layout.conversation_list_item,null);
                viewHolder.headImage = (ImageView) view.findViewById(R.id.conversationlist_item_head);
                viewHolder.nameTxt = (TextView) view.findViewById(R.id.conversationlist_item_name);
                viewHolder.phoneTxt = (TextView) view.findViewById(R.id.conversationlist_item_phone);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.headImage.setImageResource(R.drawable.default_head);
            viewHolder.phoneTxt.setText(listUser.get(i).getPhone());
            if(!listUser.get(i).getNikeName().equals("") && (listUser.get(i).getNikeName() != null)){
                viewHolder.nameTxt.setText(listUser.get(i).getNikeName());
            }else {

            }

            return view;
        }

        class ViewHolder{
            ImageView headImage;
            TextView nameTxt , phoneTxt;
        }
    }

}
