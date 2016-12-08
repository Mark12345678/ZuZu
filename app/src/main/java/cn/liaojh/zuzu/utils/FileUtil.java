package cn.liaojh.zuzu.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by Liaojh on 2016/10/23.
 */

public class FileUtil {

    public static File BitmapTOFile(Bitmap bitmap,String fileName)throws IOException {

        String path = getSDPath() +"/zuzu/";
        File dirFile = new File(path);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }

        File myCaptureFile = new File(path + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return myCaptureFile;
    }


    public static String getSDPath(){
        return Environment.getExternalStorageDirectory()+"";
    }
}
