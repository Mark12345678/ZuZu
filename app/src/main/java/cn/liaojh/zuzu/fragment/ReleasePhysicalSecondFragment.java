package cn.liaojh.zuzu.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.ZuZuApplication;
import cn.liaojh.zuzu.utils.StringAndMap;
import cn.liaojh.zuzu.utils.ToastUtils;
import cn.liaojh.zuzu.widget.MyToolBar;
import dmax.dialog.SpotsDialog;

/**
 * Created by Liaojh on 2016/10/20.
 */

public class ReleasePhysicalSecondFragment extends BaseFragment{

    @ViewInject(R.id.select_view)
    View select_view;

    @ViewInject(R.id.second_category)
    TextView txt_category;

    @ViewInject(R.id.second_decrease)
    TextView txt_decrease;

    @ViewInject(R.id.response)
    TextView txt_response;

    @ViewInject(R.id.second_map)
    TextView txt_map;

    @ViewInject(R.id.second_price)
    TextView txt_price;

    @ViewInject(R.id.second_sure)
    TextView txt_sure;

    OptionsPickerView pvOptions;

    private static String goodsname = "";
    private  Map<String,String> photoMap ;

    //用于存放类别数据
    private ArrayList<HashMap<String, Object>> imageItem;
    //物品类别
    private int category1;
    private int category2;
    //物品描述
    private String decrease = "";
    private String response = "";
    //物品坐标
    private double longitude = 12.789543;
    private double latitude = 12.789543;
    private float price;

    private ArrayList<String> options1Items = new ArrayList<String>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<ArrayList<String>>();

    private SpotsDialog releaseDialog;

    public ReleasePhysicalSecondFragment(String name, Map<String,String> photoPathMap) {
        photoMap = photoPathMap;
        goodsname = name;
    }

    @ViewInject(R.id.second_toolbat)
    MyToolBar toolBar;

    @Override
    public void initView(View view, Bundle savedInstanceState) {
        initCategoryData();

        //选项选择器
        pvOptions = new OptionsPickerView(getActivity());

        //二级联动效果
        pvOptions.setPicker(options1Items, options2Items, true);

        pvOptions.setTitle("选择类别");
        pvOptions.setCyclic(false);
        pvOptions.setSelectOptions(0, 0);

        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).toString()+"--"
                        + options2Items.get(options1).get(option2).toString();

                //因为数据库的类别id是从1开始的
                category1 = options1 + 1;
                category2 = option2 + 1;

                txt_category.setText(tx);
                select_view.setVisibility(View.GONE);
            }

        });


        toolBar.setTitle("发布物品");
        toolBar.setLeftButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder bulider = new AlertDialog.Builder(getActivity());
                bulider.setTitle("确定要离开？")
                        .setMessage("现在离开将失去之前编译数据")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                removeFragment();

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                bulider.show();

            }
        });

        //选择类型
        txt_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvOptions.show();
            }
        });

        //选择经纬度
        txt_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        releaseDialog = new SpotsDialog(getActivity());
        releaseDialog.setTitle("正在发布");

        txt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                releaseDialog.show();
                //首先验证有没有字段为空的
                if( verifyNotNUll() ){


                    release();

                }

            }
        });

    }

    //参数类型  text/plain
    private final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private final MediaType MEDIA_TYPE_TXT = MediaType.parse("text/plain");

    public void release(){
        List<File> fileList = pathToFile();
        String message = messageToMapString();

        String url = Contans.API.RELEASE;
        OkHttpClient client = new OkHttpClient();
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);

        builder.addFormDataPart("message",message);
        //添加文件参数
        for(int k = 0 ; k < fileList.size() ; k++){
            builder.addFormDataPart("files",fileList.get(k).getPath(), RequestBody.create(MEDIA_TYPE_PNG,fileList.get(k)));
            System.out.println("file,length() = " + fileList.get(k).length());
        }

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url)//地址
                .post(requestBody)//添加请求体
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println("request = " + request.urlString());
                System.out.println("e.getLocalizedMessage() = " + e.getLocalizedMessage());
                releaseDialog.dismiss();
                getActivity().finish();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                System.out.println("response = " + response.body().string());
                releaseDialog.dismiss();
                getActivity().finish();
            }
        });
    }

    //将传过来的图片地址转化为图片文件
    private List<File> pathToFile(){
        Set set = photoMap.keySet();
        List<File> fileList = new ArrayList<File>();

        for(Iterator iter = set.iterator(); iter.hasNext();)
        {
            String key = (String)iter.next();
            String value = (String)photoMap.get(key);
            fileList.add(new File((value)));
        }
        return fileList;
    }

    private String messageToMapString(){
        Map<String , String> messageMap = new HashMap<String,String>();
        messageMap.put("goodsname",goodsname);
        messageMap.put("category1",String.valueOf(category1));
        messageMap.put("category2",String.valueOf(category2));
        messageMap.put("decrease",decrease);
        messageMap.put("response",response);
        messageMap.put("longitude",String.valueOf(longitude));
        messageMap.put("latitude",String.valueOf(latitude));
        messageMap.put("price",String.valueOf(price));
        messageMap.put("userId",String.valueOf(ZuZuApplication.getInstance().getUser().getId()));
        return StringAndMap.tranMapToString(messageMap);
    }

    //验证输入框中，是否存在每个字段为空
    public Boolean verifyNotNUll(){
        decrease = txt_decrease.getText().toString();
        response = txt_response.getText().toString();
        price = Float.valueOf(txt_price.getText().toString());

        if(!goodsname.equals("")  &&  !String.valueOf(category1).equals("") &&
                !decrease.equals("")  && !response.equals("")  && !String.valueOf(longitude).equals("") && !String.valueOf(price).equals("")){
            return true;
        }
        ToastUtils.show(getActivity(),"请完善好所有信息");
        return false;
    }

    //初始化类别的选择框
    public void initCategoryData(){
        options1Items.add(0,"实物");
        options1Items.add(1,"时间");
        options1Items.add(2,"需求");
        ArrayList<String> list1 = new ArrayList<String>();
        list1.add("书籍文具");
        list1.add("运动器材");
        list1.add("衣服鞋子");
        list1.add("生活工具");
        list1.add("植物盆栽");
        list1.add("艺术器材");
        list1.add("其他");
        options2Items.add(0,list1);
        ArrayList<String> list2 = new ArrayList<String>();
        list2.add("交心聊天");
        list2.add("外出活动");
        list2.add("日常闲逛");
        list2.add("教学辅导");
        options2Items.add(1,list2);
        ArrayList<String> list3 = new ArrayList<String>();
        list3.add("物品需求");
        list3.add("时间需求");
        options2Items.add(2,list3);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_releasesecondphysical;
    }


}
