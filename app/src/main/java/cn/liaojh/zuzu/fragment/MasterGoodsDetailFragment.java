package cn.liaojh.zuzu.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

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

    @ViewInject(R.id.image_send)
    ImageView image_send;

    @ViewInject(R.id.master_goods_delete)
    Button master_goods_delete;

    @ViewInject(R.id.master_goodsPic)
    GridView master_goodsPic;

    View view;

    OkHttpHelper okHttpHelper;

    Goods goods;

    ArrayList<HashMap<String, Object>> imageItem;
    HashMap<String , Object> map = new HashMap<String , Object>();
    Bitmap bmp;
    SimpleAdapter simpleAdapter;

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

                break;
        }
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

        //获取物品图片
        getGoodsPic(goods.getId(),1);

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
                test.setText(editText.getText());
                dialogInterface.dismiss();
            }
        });
        dlg.setNegativeButton("取消", null);
        dlg.show();
    }
}
