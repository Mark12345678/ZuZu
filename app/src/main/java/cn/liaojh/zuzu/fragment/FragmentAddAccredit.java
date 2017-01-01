package cn.liaojh.zuzu.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.liaojh.zuzu.ChatListActivity;
import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.ZuZuApplication;
import cn.liaojh.zuzu.bean.User;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.http.SimpleCallback;
import cn.liaojh.zuzu.http.SpotsCallBack;
import cn.liaojh.zuzu.utils.ToastUtils;
import cn.liaojh.zuzu.widget.MyToolBar;

/**
 * Created by Liaojh on 2016/12/29.
 */

public class FragmentAddAccredit extends Fragment {

    @ViewInject(R.id.addaccredit_ll)
    LinearLayout addaccredit_ll;

    @ViewInject(R.id.addaccredit_toolbar)
    MyToolBar addaccredit_toolbar;

    @ViewInject(R.id.addaccredit_list)
    ListView addaccredit_list;

    @ViewInject(R.id.addaccredit_edit)
    EditText addaccredit_edit;

    @ViewInject(R.id.addaccredit_sure)
    TextView addaccredit_sure;

    OkHttpHelper okHttpHelper ;

    List<User> listUser = new ArrayList<User>();

    View view;

    int goodsId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_addaccredit,container,false);

        ViewUtils.inject(this, view);
        okHttpHelper = OkHttpHelper.getInstance();
        goodsId = getArguments().getInt("goodsId");

        initView();
        getAccredit();

        return view;
    }

    public void initView(){

        addaccredit_toolbar.setTitle("添加授权");
        addaccredit_toolbar.setLeftButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fmanger =getFragmentManager();
                FragmentTransaction ftran =fmanger.beginTransaction();
                ftran.remove(FragmentAddAccredit.this);
                ftran.commit();
            }
        });

        addaccredit_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAccredit();
            }
        });

        addaccredit_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int list_position, long l) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("干哈？");
                builder.setIcon(R.drawable.default_head);
                builder.setItems(new String[]{"删除用户","确认支付"}, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int dia_position) {
                        if(dia_position == 0 ){
                            deleteAccredit(list_position);
                        }else if(dia_position == 1){
                            masterSurePay(list_position);
                        }

                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

    }

    public void masterSurePay(int list_position){

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("masterId",ZuZuApplication.getInstance().getUser().getId());
        params.put("userPhone",listUser.get(list_position).getPhone());
        params.put("goodsId",goodsId);

        okHttpHelper.get(Contans.API.MASTERSUREPAY, params, new SpotsCallBack<String>(getActivity()) {
            @Override
            public void onSuccess(Response response, String str) {
                if(str.equals("1")){
                    ToastUtils.show(getActivity(),"成功确认");
                    getAccredit();
                }else if(str.equals("-1")){
                    ToastUtils.show(getActivity(),"确认失败");
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(getActivity(),"网络异常");
            }
        });
    }

    public void deleteAccredit(int list_position){

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("masterId",ZuZuApplication.getInstance().getUser().getId());
        params.put("userPhone",listUser.get(list_position).getPhone());
        params.put("goodsId",goodsId);

        okHttpHelper.get(Contans.API.DELETEACCREDIT, params, new SpotsCallBack<String>(getActivity()) {
            @Override
            public void onSuccess(Response response, String str) {
                if(str.equals("1")){
                    ToastUtils.show(getActivity(),"成功删除");
                    getAccredit();
                }else if(str.equals("-1")){
                    ToastUtils.show(getActivity(),"删除失败");
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
    }

    //添加授权用户
    public void addAccredit(){
        String phone = addaccredit_edit.getText().toString();
        if(!phone.equals("")){

            Map<String,Object> params = new HashMap<String,Object>();
            params.put("masterId",ZuZuApplication.getInstance().getUser().getId());
            params.put("userPhone",phone);
            params.put("goodsId",goodsId);

            okHttpHelper.get(Contans.API.ADDACCREDIT, params, new SpotsCallBack<String>(getActivity()) {
                @Override
                public void onSuccess(Response response, String str) {
                    if(str.equals("2")){
                        ToastUtils.show(getActivity(),"你已经授权了这个用户");
                    }else if(str.equals("-1")){
                        ToastUtils.show(getActivity(),"到底有没有这个人");
                    }else if(str.equals("1")){
                        ToastUtils.show(getActivity(),"成功授权");
                    }
                    getAccredit();
                }

                @Override
                public void onError(Response response, int code, Exception e) {

                }
            });

        }else {
            ToastUtils.show(getActivity(),"请填写授权用户的手机号码！");
        }
    }

    //获取授权用户
    public void getAccredit(){
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("masterId", ZuZuApplication.getInstance().getUser().getId());
        params.put("goodsId",goodsId);

        okHttpHelper.get(Contans.API.SHOWACCREDIT, params, new SpotsCallBack<List<User>>(getActivity()) {
            @Override
            public void onSuccess(Response response, List<User> listUser) {
                setData(listUser);
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(getContext(),"error");
            }
        });
    }

    public void setData(List<User> listUser){
        if(listUser.size() >= 1 ){
            addaccredit_ll.setVisibility(View.GONE);
        }
        this.listUser = listUser;
        AddAccreditAdapter adapter = new AddAccreditAdapter(getActivity(),listUser);
        addaccredit_list.setAdapter(adapter);
    }

    class AddAccreditAdapter extends BaseAdapter{

        Context context;
        List<User> listUser;
        LayoutInflater inflater;

        public AddAccreditAdapter(Context context , List<User> listUser){
            this.context = context;
            this.listUser = listUser;
            inflater = LayoutInflater.from(context);
        }

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
            ViewHolder viewHolder = null;
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
            Picasso.with(getActivity()).load(Contans.API.SHOW_PHOTO +"/"+ listUser.get(i).getHeadImageUrl()).into(viewHolder.headImage);
            Log.i("======",Contans.API.SHOW_PHOTO +"/"+ listUser.get(i).getHeadImageUrl());
            //viewHolder.headImage.setImageResource(R.drawable.default_head);
            viewHolder.phoneTxt.setText(listUser.get(i).getPhone());
            if(!listUser.get(i).getNikeName().equals("") && (listUser.get(i).getNikeName() != null)){
                viewHolder.nameTxt.setText(listUser.get(i).getNikeName());
            }else {

            }

            return view;
        }

    }

    class ViewHolder{
        ImageView headImage;
        TextView nameTxt , phoneTxt;
    }
}
