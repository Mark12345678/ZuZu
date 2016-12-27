package cn.liaojh.zuzu.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.bean.Goods;
import cn.liaojh.zuzu.bean.GoodsPic;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.http.SpotsCallBack;
import cn.liaojh.zuzu.utils.BitmapUtil;
import cn.liaojh.zuzu.utils.StringAndMap;
import cn.liaojh.zuzu.utils.ToastUtils;

/**
 * Created by Liaojh on 2016/12/26.
 */

public class MasterGoodsDetailFragment extends Fragment implements View.OnClickListener{

    @ViewInject(R.id.master_goods_name)
    TextView master_goods_name;

    @ViewInject(R.id.master_goods_describe)
    TextView master_goods_describe;

    @ViewInject(R.id.master_goods_response)
    TextView master_goods_response;

    @ViewInject(R.id.master_goods_time)
    TextView master_goods_time;

    @ViewInject(R.id.master_goods_price)
    TextView master_goods_price;

    @ViewInject(R.id.master_send)
    ImageView master_send;

    @ViewInject(R.id.master_goods_delete)
    Button master_goods_delete;

    @ViewInject(R.id.master_goodsPic)
    GridView master_goodsPic;

    @ViewInject(R.id.master_back)
    ImageView master_back;

    View view;

    OkHttpHelper okHttpHelper;

    Goods goods;

    ArrayList<HashMap<String, Object>> imageItem;
    HashMap<String , Object> map = new HashMap<String , Object>();
    Bitmap bmp;
    SimpleAdapter simpleAdapter;


    Handler handler;
    public MasterGoodsDetailFragment(Handler handler){
        this.handler = handler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_master_goodsdetaile,container,false);

        ViewUtils.inject(this, view);

        okHttpHelper = OkHttpHelper.getInstance();
        goods = (Goods) getArguments().getSerializable("goods");

        initCridView();

        initView(goods);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.master_goods_name:
                modifyMessage(R.id.master_goods_name);
                break;
            case R.id.master_goods_describe:
                modifyMessage(R.id.master_goods_describe);
                break;
            case R.id.master_goods_response:
                modifyMessage(R.id.master_goods_response);
                break;
            case R.id.master_goods_price:
                modifyMessage(R.id.master_goods_price);
                break;
            case R.id.master_goods_delete:
                requestDeleteGoods(goods);
                break;
            case R.id.master_send:
                requestModifyMessage();
                break;
            case R.id.master_back:
                remoteFragment();
                break;
        }
    }

    /**
     * 发出请求修改信息
     */
    private void requestModifyMessage() {

        String name = master_goods_name.getText().toString();
        String price = master_goods_price.getText().toString();
        String response = master_goods_response.getText().toString();
        String decrease = master_goods_describe.getText().toString();

        Map<String,String> map = new HashMap<String,String>();
        map.put("goodsname",name);
        map.put("price",price);
        map.put("decrease",decrease);
        map.put("response",response);
        map.put("latitude",String.valueOf(goods.getGoodsLatitude()));
        map.put("longitude",String.valueOf(goods.getGoodsLongitude()));

        String json = StringAndMap.tranMapToString(map);

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("goodsId",goods.getId());
        params.put("message",json);

        okHttpHelper.get(Contans.API.MODIFYGOODSMESSAGE, params, new SpotsCallBack<String>(getContext()) {
            @Override
            public void onSuccess(Response response, String s) {
                if(s.equals("1")){
                    ToastUtils.show(getContext(),"修改成功");
                }else if(s.equals("-1")){
                    ToastUtils.show(getContext(),"修改失败");
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(getContext(),"网络出现异常");
            }
        });
    }

    /**
     * 请求删除指定物品
     * @param goods
     */
    private void requestDeleteGoods(Goods goods){

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("goodsId",goods.getId());

        okHttpHelper.get(Contans.API.DELETEGOODS, params, new SpotsCallBack<String>(getActivity()) {
            @Override
            public void onSuccess(Response response, String s) {
                if(s.equals("1")){
                    ToastUtils.show(getContext(),"删除成功");
                    remoteFragment();
                }else if(s.equals("-1")){
                    ToastUtils.show(getContext(),"删除失败");
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(getContext(),"网络异常");
            }
        });

    }

    public void initCridView(){

        bmp = BitmapFactory.decodeResource(getResources(),R.drawable.gridview_addpic);
        imageItem = new ArrayList<HashMap<String, Object>>();
        map.put("itemImage",bmp);
        map.put("pathImage",bmp);
        imageItem.add(map);

        simpleAdapter = new SimpleAdapter(getActivity(),
                imageItem,R.layout.griditem_addpic,
                new String[]{"itemImage"},new int[]{R.id.imageViewItem});

    }

    public void initView(Goods goods){
        master_goods_name.setText(goods.getGoodsName());
        master_goods_name.setOnClickListener(this);
        master_goods_describe.setText("\t\t\t"+goods.getGoodsDescribe());
        master_goods_describe.setOnClickListener(this);
        master_goods_price.setText(String.valueOf(goods.getGoodsPrice()));
        master_goods_price.setOnClickListener(this);
        master_goods_response.setText("\t\t\t"+goods.getGoodsResponsibility());
        master_goods_response.setOnClickListener(this);
        master_goods_time.setText(goods.getGoodsReleaseTime());
        master_goods_time.setOnClickListener(this);
        master_send.setOnClickListener(this);
        master_goods_delete.setOnClickListener(this);

        //获取物品图片
        getGoodsPic(goods.getId(),1);
        master_back.setOnClickListener(this);
    }

    public void getGoodsPic(int goodsId , int picType){

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("goodsId",goodsId);
        params.put("goodsPicType",picType);

        okHttpHelper.get(Contans.API.GETGOODSPIC, params , new SpotsCallBack<List<GoodsPic>>(getActivity()) {

            @Override
            public void onSuccess(Response response, List<GoodsPic> goodsPics) {
                initBitmap(goodsPics);
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                ToastUtils.show(getActivity(),"获取图片出错");
            }
        });
    }

    /**
     *初始化ListMap
     */
    public void initBitmap(List<GoodsPic> goodsPics){

        for(int i = 0 ; i < goodsPics.size() ; i++){
            HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("itemImage","");
            map.put("pathImage",Contans.API.SHOW_GOODS_PHOTO + goodsPics.get(i).getPicUrl());
            imageItem.add(map);
        }

        simpleAdapter = new SimpleAdapter(getActivity(),imageItem,R.layout.griditem_addpic
                ,new String[]{"pathImage"},new int[]{R.id.imageViewItem});
        //接口载入图片
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && data instanceof  Bitmap){
                    ImageView i = (ImageView) view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }else if(view instanceof ImageView && data instanceof  String){
                    ImageView i = (ImageView) view;
                    Picasso.with(getActivity()).load(data.toString()).into(i);
                    Log.i("XX===========",data.toString());
                    return true;
                }
                return false;
            }
        });
        master_goodsPic.setAdapter(simpleAdapter);
        simpleAdapter.notifyDataSetChanged();
    }

    /**
     * 修改信息
     */
    public void modifyMessage(int textViewId){
        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
        final TextView test = (TextView)view.findViewById(textViewId);
        //dlg.setTitle("修改"+test.getText());
        final TextView titileView = new TextView(getActivity());
        titileView.setText("修改"+test.getText());
        titileView.setTextSize(20);
        dlg.setCustomTitle(titileView);
        final EditText editText = new EditText(getActivity());
        editText.setTextColor(Color.parseColor("#eb4f38"));
        dlg.setView(editText);
        dlg.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                test.setText( "\t\t\t" + editText.getText());
                dialogInterface.dismiss();
            }
        });
        dlg.setNegativeButton("取消", null);
        dlg.show();
    }

    public void remoteFragment(){
        handler.sendEmptyMessage(0);
        FragmentManager fmanger =getFragmentManager();
        FragmentTransaction ftran =fmanger.beginTransaction();
        ftran.remove(MasterGoodsDetailFragment.this);
        ftran.commit();
    }


}
