package cn.liaojh.zuzu.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.PhotoActivity;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.ZuZuApplication;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.utils.BitmapUtil;
import cn.liaojh.zuzu.utils.StringAndMap;
import cn.liaojh.zuzu.utils.ToastUtils;
import cn.liaojh.zuzu.widget.MyToolBar;
import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Liaojh on 2016/10/17.
 */

public class ReleaseTimeFragment extends BaseFragment{

    @ViewInject(R.id.time_category)
    TextView time_category;

    @ViewInject(R.id.time_grid)
    GridView time_grid;

    @ViewInject(R.id.time_decrease)
    TextView time_decrease;

    @ViewInject(R.id.time_price)
    TextView time_price;

    @ViewInject(R.id.time_sure)
    TextView time_sure;

    @ViewInject(R.id.time_toolbar)
    MyToolBar time_toolbar;

    @ViewInject(R.id.time_map)
    TextView time_map;

    OkHttpHelper okHttpHelper ;

    //联级菜单
    OptionsPickerView pvOptions;
    //用于存放种类
    private ArrayList<String> options1Items = new ArrayList<String>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<ArrayList<String>>();
    //物品类别
    private int category1;
    private int category2;

    private double latitude = 0.0;
    private double longitude = 0.0;

    private Bitmap bmp;                                            //导入临时图片
    private  ArrayList<HashMap<String, Object>> imageItem;          ////存储Bmp图像
    private SimpleAdapter simpleAdapter;

    SpotsDialog releaseDialog;

    Map<String,String> photoPathMap;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_releasetime;
    }

    @Override
    public void initView(View view, Bundle savedInstanceState) {

        okHttpHelper = OkHttpHelper.getInstance();

        time_toolbar.setTitle("发布时间");

        photoPathMap = new HashMap<String,String>();

        //后退一步
        time_toolbar.setLeftButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        initCategoryMuen();

        initGridView();

        //选择类型
        time_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvOptions.show();
            }
        });

        releaseDialog = new SpotsDialog(getActivity());
        releaseDialog.setTitle("正在发布");

        time_sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(validate()){
                    releaseDialog.show();
                    release();
                }
            }
        });

    }

    //参数类型  text/plain
    private final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private final MediaType MEDIA_TYPE_TXT = MediaType.parse("text/plain");



    public void release(){
        //第一步，先将路径转化为文件形式
        List<File> fileList = pathToFile(photoPathMap);
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

    private String messageToMapString(){
        Map<String , String> messageMap = new HashMap<String,String>();
        messageMap.put("goodsname",options2Items.get(1).get(category2-1));
        messageMap.put("category1",String.valueOf(category1));
        messageMap.put("category2",String.valueOf(category2));
        messageMap.put("decrease",time_decrease.getText().toString());
        messageMap.put("response","");
        messageMap.put("longitude",String.valueOf(longitude));
        messageMap.put("latitude",String.valueOf(latitude));
        messageMap.put("price",time_price.getText().toString());
        messageMap.put("userId",String.valueOf(ZuZuApplication.getInstance().getUser().getId()));
        return StringAndMap.tranMapToString(messageMap);
    }

    //将传过来的图片地址转化为图片文件
    private List<File> pathToFile(Map<String,String> photoMap){
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
    /**
     * 判断信息是否存在空的
     * @return
     */
    public Boolean validate(){
        String category = time_category.getText().toString();
        String decrease = time_decrease.getText().toString();
        String price = time_price.getText().toString();
        if(!category.equals("") && !decrease.equals("") && !price.equals("")){
            if(validateNum(price)){
                return true;
            }
        }else {
            ToastUtils.show(getActivity(),"请确认信息填写完整！");
        }
        return false;
    }

    public Boolean validateNum(String value){
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 初始化GridView
     */
    public void initGridView(){
        /**
         * 添加默认加号的图片
         */
        bmp = BitmapFactory.decodeResource(getResources(),R.drawable.gridview_addpic);
        imageItem = new ArrayList<HashMap<String, Object>>();
        HashMap<String , Object> map = new HashMap<String , Object>();
        map.put("itemImage",bmp);
        map.put("pathImage","add_pic");
        imageItem.add(map);
        simpleAdapter = new SimpleAdapter(getActivity(),
                imageItem,R.layout.griditem_addpic,
                new String[]{"itemImage"},new int[]{R.id.imageViewItem});

        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(view instanceof ImageView && data instanceof Bitmap ){
                    ImageView i = (ImageView) view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });

        time_grid.setAdapter(simpleAdapter);

        time_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(imageItem.size() == 4){
                    ToastUtils.show(getActivity(),"图片数已经满3张了，不要再来了，雅美蝶~~~");
                }else if(position == 0){
                    ToastUtils.show(getActivity(),"0");
                    AddImageDialog();
                }else {
                    DeleteDialog(position);
                }
            }
        });
    }

    private final int IMAGE_OPEN = 1;      //打开图片标记
    private final int TAKE_PHOTO = 3;       //拍照标记
    private String pathImage;                     //选择图片路径
    /**
     * 添加图片
     */
    private void AddImageDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("添加图片");
        builder.setIcon(R.drawable.default_head);
        builder.setCancelable(false);  //不响应back按钮
        builder.setItems(new String[]{"本地相册选择","手机相机添加","取消选择图片"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:      //本地相册
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, IMAGE_OPEN);
                        //通过onResume()刷新数据
                        break;

                    case 1:      //手机相机
                        dialog.dismiss();
                        Intent intent2 = new Intent(getActivity(),
                                PhotoActivity.class);
                        startActivityForResult(intent2,TAKE_PHOTO);
                        break;

                    case 2:      //取消添加
                        dialog.dismiss();
                        break;

                    default:
                        break;
                }
            }
        });
        //显示对话框
        builder.create().show();
    }

    /**
     * 删除position中的图片
     * @param position
     */
    private void DeleteDialog(final int position){
        AlertDialog.Builder bulider = new AlertDialog.Builder(getActivity());
        bulider.setTitle("提示")
                .setMessage("确定要删除？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        photoPathMap.remove(imageItem.size()+"");

                        imageItem.remove(position);
                        simpleAdapter.notifyDataSetChanged();
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

    /**
     *获取图片路径 ，响应startActivityForResult
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //打开图库
        if(resultCode == RESULT_OK && requestCode == IMAGE_OPEN){
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())){
                //查询选择图片
                Cursor cursor = getActivity().getContentResolver().query(
                        uri,
                        new String[] {MediaStore.Images.Media.DATA},
                        null,
                        null,
                        null );

                //返回 没找到选择图片
                if(null == cursor){
                    return;
                }
                //光标移动至开头，获取图片路径
                cursor.moveToFirst();
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                pathImage = path;
                showPhoto();

            }

        }else if( resultCode == TAKE_PHOTO){
            Log.i("拍照后回调回来的图片路径","=========================="+data.getStringExtra("photoPath"));
            pathImage = data.getStringExtra("photoPath");
            showPhoto();
        }
    }

    /**
     * 将图片显示在GridView中
     */

    public void showPhoto(){

        //适配器动态显示图片
        if(!TextUtils.isEmpty(pathImage)){
            //Bitmap addbmp = BitmapFactory.decodeFile(pathImage);

            Bitmap addbmp = BitmapUtil.pressPicture(getActivity(),pathImage);

            HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("itemImage",addbmp);
            map.put("pathImage",pathImage);

            photoPathMap.put(imageItem.size()+"",pathImage);

            imageItem.add(map);

            simpleAdapter = new SimpleAdapter(getActivity(),imageItem,R.layout.griditem_addpic
                    ,new String[]{"itemImage"},new int[]{R.id.imageViewItem});
            //接口载入图片
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    if(view instanceof ImageView && data instanceof  Bitmap){
                        ImageView i = (ImageView) view;
                        i.setImageBitmap((Bitmap) data);
                        return true;
                    }
                    return false;
                }
            });
            time_grid.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
            Log.i("什么鬼",pathImage);
            //刷新后释放防止手机休眠后自动添加
            pathImage = null;
        }
    }

    /**
     * 初始化选择菜单
     */
    public void initCategoryMuen(){
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

                if(category1 == 2) {
                    time_category.setText(tx);
                }else {
                    ToastUtils.show(getActivity(),"请确认你发布的类别！");
                }

            }
        });
    }

    //初始化类别的选择框里面的内容
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
}
