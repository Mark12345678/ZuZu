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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.liaojh.zuzu.PhotoActivity;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.ZuZuApplication;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.utils.BitmapUtil;
import cn.liaojh.zuzu.utils.ToastUtils;
import cn.liaojh.zuzu.widget.MyToolBar;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Liaojh on 2016/10/17.
 */

public class ReleasePhysicalFirstFragment extends BaseFragment{


    private Bitmap bmp;                                            //导入临时图片
    private  ArrayList<HashMap<String, Object>> imageItem;          ////存储Bmp图像
    private  SimpleAdapter simpleAdapter;

    private final int IMAGE_OPEN = 1;      //打开图片标记
    private final int TAKE_PHOTO = 3;       //拍照标记
    private String pathImage;                     //选择图片路径

    @ViewInject(R.id.first_nest)
    TextView txt_next;

    @ViewInject(R.id.first_toolbar)
    MyToolBar toolBar;

    @ViewInject(R.id.first_grid)
    GridView first_grid;

    @ViewInject(R.id.first_name)
    EditText first_name;

    OkHttpHelper okHttpHelper ;

    Map<String,String>  photoPathMap;

    ReleasePhysicalSecondFragment releasePhysicalSecondFragment ;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_releasefirstphysical;
    }



    @Override
    public void initView(View view, Bundle savedInstanceState) {

        okHttpHelper = OkHttpHelper.getInstance();

        photoPathMap = new HashMap<String,String>();

        toolBar.setTitle("发布物品");
        toolBar.setLeftButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        txt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!first_name.getText().toString().equals("") && photoPathMap.size() != 0){

                    releasePhysicalSecondFragment = new ReleasePhysicalSecondFragment(first_name.getText().toString(),photoPathMap);
                    addFragment(releasePhysicalSecondFragment);
                }else {
                    ToastUtils.show(getActivity(),"请填写好信息，再进行下一步！");
                }


            }
        });

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

        /*ArrayList<HashMap<String, Object>> lists = ZuZuApplication.getInstance().getImageItem();
        if(lists != null){
            if( lists.size() >=2 ){
                for(int i=1 ; i<lists.size() ;i++){
                    imageItem.add(lists.get(i));
                }
                ZuZuApplication.getInstance().setImageItem(imageItem);
                simpleAdapter.notifyDataSetChanged();
            }
        }*/

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

        first_grid.setAdapter(simpleAdapter);

        first_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(imageItem.size() == 10){
                    ToastUtils.show(getActivity(),"图片数已经满9张了，不要再来了，雅美蝶~~~");
                }else if(position == 0){
                    ToastUtils.show(getActivity(),"0");
                    AddImageDialog();
                }else {
                    DeleteDialog(position);
                }
            }
        });

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
            first_grid.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
            Log.i("什么鬼",pathImage);
            //刷新后释放防止手机休眠后自动添加
            pathImage = null;
        }
    }


}
