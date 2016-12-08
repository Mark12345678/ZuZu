package cn.liaojh.zuzu.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static cn.liaojh.zuzu.utils.FileUtil.getSDPath;

/**
 * Created by Liaojh on 2016/10/20.
 */

public class BitmapUtil {
    
    public static Bitmap pressPicture(Activity activity, String path){
        Bitmap pressBmp = null;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;    //屏幕水平分辨率
        int height = dm.heightPixels;  //屏幕垂直分辨率
        try {
            //Load up the image's dimensions not the image itself
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds = true;
            pressBmp = BitmapFactory.decodeFile(path, bmpFactoryOptions);
            int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
            int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);
            //压缩显示
            if (heightRatio > 1 && widthRatio > 1) {
                if (heightRatio > widthRatio) {
                    bmpFactoryOptions.inSampleSize = heightRatio * 2;
                } else {
                    bmpFactoryOptions.inSampleSize = widthRatio * 2;
                }
            }
            //图像真正解码
            bmpFactoryOptions.inJustDecodeBounds = false;
            pressBmp = BitmapFactory.decodeFile(path, bmpFactoryOptions);

        }catch (Exception e){
            e.printStackTrace();
        }
        return pressBmp;
    }

    public static File saveFile(Bitmap bm, String fileName) throws IOException {
        String path = getSDPath() +"/zuzu/head/";
        File dirFile = new File(path);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(path + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();

        return myCaptureFile;
    }
}
