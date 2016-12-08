package cn.liaojh.zuzu.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.liaojh.zuzu.ChatListActivity;
import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.LoginActivity;
import cn.liaojh.zuzu.PhotoActivity;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.ZuZuApplication;
import cn.liaojh.zuzu.bean.User;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.http.SpotsCallBack;
import cn.liaojh.zuzu.utils.BitmapUtil;
import cn.liaojh.zuzu.utils.ToastUtils;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Liaojh on 2016/10/15.
 */

public class MineFragment extends BaseFragment implements View.OnClickListener{

    public final int LOGIN_TAB = 1;

    private final int IMAGE_OPEN = 1;      //打开图片标记
    private final int TAKE_PHOTO = 3;       //拍照标记
    private final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    @ViewInject(R.id.img_head)
    CircleImageView headView;

    @ViewInject(R.id.txt_chat)
    TextView txt_chat;

    @ViewInject(R.id.btn_logout)
    Button btn_logout;

    @ViewInject(R.id.txt_username)
    TextView txt_username;

    OkHttpHelper okHttpHelper;

    SpotsDialog releaseDialog;

    final ZuZuApplication zuZuApplication = ZuZuApplication.getInstance();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void initView(View view, Bundle savedInstanceState) {

        okHttpHelper = OkHttpHelper.getInstance();
        headView.setOnClickListener(this);
        //设置用户头像
        setUserHead(headView);
        txt_chat.setOnClickListener(this);

        if(zuZuApplication.getUser() != null){
            User user = zuZuApplication.getUser();
            btn_logout.setVisibility(View.VISIBLE);
            txt_username.setText(user.getPhone());
        }

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zuZuApplication.clearUser();
                btn_logout.setVisibility(View.GONE);
                txt_username.setText("请先登录");
            }
        });

    }


    private void setUserHead(ImageView view){
        if(ZuZuApplication.getInstance().getUser() != null){
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("phone",ZuZuApplication.getInstance().getUser().getPhone());

            final ImageView head = view;
            okHttpHelper.get(Contans.API.GETUSERHEAD, params, new SpotsCallBack<String>(getActivity()) {
                @Override
                public void onSuccess(Response response, String url) {
                    //Picasso.with(getActivity()).load(url).into(view);
                    setUserHeadView(Contans.API.SHOW_PHOTO+"/"+url);
                }

                @Override
                public void onError(Response response, int code, Exception e) {

                }

                public void setUserHeadView(String url) {
                    Picasso.with(getActivity()).load(url).into(head);
                }

            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_head:
                //点击头像时，如果没有登录的话，则进去登录界面
                if(zuZuApplication.getUser() == null){
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent,LOGIN_TAB);

                }else {
                    //如果已经登录，则可以选择上传图片
                    AddImageDialog();

                }
                break;
            case R.id.txt_chat:
                //ToastUtils.show(getContext(),"123");
                Intent intent = new Intent(getActivity(), ChatListActivity.class);
                startActivity(intent);

                break;
            default:
                break;
        }
    }

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


    private String pathImage;                     //选择图片路径
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == LOGIN_TAB){
            User user = zuZuApplication.getUser();
            if (user != null){
                btn_logout.setVisibility(View.VISIBLE);
                txt_username.setText(user.getPhone());
            }
        }

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
                showPhoto(pathImage);

            }

        }else if( resultCode == TAKE_PHOTO){
            Log.i("拍照后回调回来的图片路径","=========================="+data.getStringExtra("photoPath"));
            pathImage = data.getStringExtra("photoPath");
            showPhoto(pathImage);
        }
    }

    public void showPhoto(String pathImage){
        if(!TextUtils.isEmpty(pathImage)) {
            Bitmap addbmp = BitmapUtil.pressPicture(getActivity(), pathImage);
            headView.setImageBitmap(addbmp);
            release(addbmp);
        }
    }

    public void release(Bitmap bitmap){

        releaseDialog = new SpotsDialog(getActivity());
        releaseDialog.setTitle("正在上传......");
        releaseDialog.show();

        String url = Contans.API.UPDATEHEAD;
        OkHttpClient client = new OkHttpClient();
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);

        builder.addFormDataPart("phone",ZuZuApplication.getInstance().getUser().getPhone());

        File file= null;
        try {
            file = BitmapUtil.saveFile(bitmap,ZuZuApplication.getInstance().getUser().getPhone()+".jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        builder.addFormDataPart("head",file.getPath(), RequestBody.create(MEDIA_TYPE_PNG,file));

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
            }

            @Override
            public void onResponse(Response response) throws IOException {
                System.out.println("response = " + response.body().string());
                releaseDialog.dismiss();
            }
        });
    }
}
