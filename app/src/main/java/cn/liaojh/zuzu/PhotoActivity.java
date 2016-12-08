package cn.liaojh.zuzu;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.squareup.leakcanary.RefWatcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cn.liaojh.zuzu.utils.ToastUtils;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class PhotoActivity extends AppCompatActivity {

    private final int TAKE_PHOTO = 3;
    private View layout;
    private Camera camera;
    private Camera.Parameters parameters = null;
    private WindowManager mWindowManager;

    Bundle bundle = null;  //声明一个Bundle对象，用来存储数据

    private int screenWidth;
    private int screenHeight;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        quan();
        RefWatcher refWatcher = ZuZuApplication.getRefWatcher(this);
        refWatcher.watch(this);

        intent = getIntent();

        SurfaceView surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().setFixedSize(176, 144);//设置Surface分辨率
        surfaceView.getHolder().setKeepScreenOn(true); //设置常亮
        surfaceView.getHolder().addCallback(new SurfaceCallback()); //为SurView的句柄添加一个回调函数
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.show(PhotoActivity.this,"对焦功能正在拼命开发中.....");
            }
        });
    }

    /**
     * 按钮被点击触发的事件
     * @param view
     */
    public void btnOnclick(View view){
        if(camera != null){
            switch (view.getId()){
                case R.id.takePhoto:
                    camera.takePicture(null,null,new MyPictureCallback());
                    break;
                default:
                    break;
            }
        }
    }

    private final class MyPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            try {
                bundle = new Bundle();
                bundle.putByteArray("bytes",bytes);
                String photoPath = saveToSDCard(bytes);
                Toast.makeText(getApplicationContext(),"已保存入SD卡",Toast.LENGTH_SHORT).show();
                Log.i("拍照图片：",photoPath);
                intent.putExtra("photoPath", photoPath);
                setResult(TAKE_PHOTO,intent);
                finish();
                camera.startPreview();    //拍完照后重新开始预览
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String saveToSDCard(byte[] data) throws IOException{
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); //格式化时间
        String filename = format.format(date) + ".jpeg";
        File fileFolder = new File(Environment.getExternalStorageDirectory()+ "/zuzu/");

        if(!fileFolder.exists()){
            fileFolder.mkdir();
        }
        File jpgFile = new File(fileFolder,filename);
        FileOutputStream outputStream = new FileOutputStream(jpgFile);
        outputStream.write(data);
        outputStream.close();
        return jpgFile.getAbsolutePath();
    }


    private class SurfaceCallback implements SurfaceHolder.Callback {

        // 开始拍照时调用该方法
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            //quan();
            try{
                camera = Camera.open();  //打开摄像头

                camera.setDisplayOrientation(90);
                camera.setPreviewDisplay(surfaceHolder);// 设置用于显示拍照影像的SurfaceHolder对象
                camera.setDisplayOrientation(getPreviewDegree(PhotoActivity.this));
                //camera.startPreview(); //开始预览

            }catch (Exception e){
                camera.release();
                camera = null;
                e.printStackTrace();
            }

        }

        // 拍照状态变化时调用该方法
        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

            initCamera();
            camera.setParameters(parameters);
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    initCamera();
                    camera.cancelAutoFocus();
                }
            });

            camera.startPreview();
            List<Camera.Size> size = parameters.getSupportedPreviewSizes();
            for(int i = 0 ; i < size.size() ; i++){
                Log.i("你妈逼","width:"+size.get(i).width + " height:"+size.get(i).height);
            }


        }

        public void initCamera(){
            parameters = camera.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setJpegQuality(100);
            parameters.getSupportedPictureSizes();
            parameters.setPreviewSize(720,480);  //这里面的参数只能是几个特定的参数，否则会报错.(176*144,320*240,352*288,480*360,640*480)
            parameters.setPictureSize(720,480);
            camera.cancelAutoFocus();
        }

        // 停止拍照时调用该方法
        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if (camera != null) {
                camera.release(); // 释放照相机
                camera = null;
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                layout.setVisibility(ViewGroup.VISIBLE);
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_CAMERA:  // 按下拍照按钮
                if(camera != null && event.getRepeatCount() == 0){
                    camera.takePicture(null,null,new MyPictureCallback());
                }
        }
        return super.onKeyDown(keyCode,event);
    }

    // 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
    public static int getPreviewDegree(Activity activity) {
        // 获得手机的方向
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }

    public void quan(){
        if(ContextCompat.checkSelfPermission(PhotoActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            // Should we show an explanation?
            if(ActivityCompat.shouldShowRequestPermissionRationale(PhotoActivity.this,Manifest.permission.CAMERA)){
                ToastUtils.show(PhotoActivity.this,"你不给这个权限，你将不能拍照");
            }else{
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(PhotoActivity.this,new String[]{Manifest.permission.CAMERA},PERMISSION_GRANTED);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    /**
     * 最小预览界面的分辨率
     */
    private static final int MIN_PREVIEW_PIXELS = 480 * 320;
    /**
     * 最大宽高比差
     */
    private static final double MAX_ASPECT_DISTORTION = 0.15;
    /**
     * 找出最适合的预览界面分辨率
     *
     * @return
     */
    private Camera.Size findBestPreviewResolution() {
        Camera.Parameters cameraParameters = camera.getParameters();
        Camera.Size defaultPreviewResolution = cameraParameters.getPreviewSize();

        List<Camera.Size> rawSupportedSizes = cameraParameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            return defaultPreviewResolution;
        }

        // 按照分辨率从大到小排序
        List<Camera.Size> supportedPreviewResolutions = new ArrayList<Camera.Size>(rawSupportedSizes);
        Collections.sort(supportedPreviewResolutions, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                int aPixels = a.height * a.width;
                int bPixels = b.height * b.width;
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });

        StringBuilder previewResolutionSb = new StringBuilder();
        for (Camera.Size supportedPreviewResolution : supportedPreviewResolutions) {
            previewResolutionSb.append(supportedPreviewResolution.width).append('x').append(supportedPreviewResolution.height)
                    .append(' ');
        }


        // 移除不符合条件的分辨率
        double screenAspectRatio = (double) screenWidth
                / screenHeight;
        Iterator<Camera.Size> it = supportedPreviewResolutions.iterator();
        while (it.hasNext()) {
            Camera.Size supportedPreviewResolution = it.next();
            int width = supportedPreviewResolution.width;
            int height = supportedPreviewResolution.height;

            // 移除低于下限的分辨率，尽可能取高分辨率
            if (width * height < MIN_PREVIEW_PIXELS) {
                it.remove();
                continue;
            }

            // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
            // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
            // 因此这里要先交换然preview宽高比后在比较
            boolean isCandidatePortrait = width > height;
            int maybeFlippedWidth = isCandidatePortrait ? height : width;
            int maybeFlippedHeight = isCandidatePortrait ? width : height;
            double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            if (distortion > MAX_ASPECT_DISTORTION) {
                it.remove();
                continue;
            }

            // 找到与屏幕分辨率完全匹配的预览界面分辨率直接返回
            if (maybeFlippedWidth == screenWidth
                    && maybeFlippedHeight == screenHeight) {
                return supportedPreviewResolution;
            }
        }


        // 如果没有找到合适的，并且还有候选的像素，则设置其中最大比例的，对于配置比较低的机器不太合适
        if (!supportedPreviewResolutions.isEmpty()) {
            Camera.Size largestPreview = supportedPreviewResolutions.get(0);
            return largestPreview;
        }


        // 没有找到合适的，就返回默认的

        return defaultPreviewResolution;
    }

}
