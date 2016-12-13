package cn.liaojh.zuzu.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import cn.liaojh.zuzu.PhotoActivity;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

/**
 * Created by Administrator on 2016/12/13.
 * 询问用户是否给权限
 */

public class AskPermission {


    /**
     * 询问权限
     * @param activity
     */
    public static void askPremission(Activity activity,String[] quanxian){
        for(int i = 0 ; i < quanxian.length ; i++){
            if(ContextCompat.checkSelfPermission(activity, quanxian[i] )!= PackageManager.PERMISSION_GRANTED ){
                // Should we show an explanation?
                if(ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.CAMERA)){
                    ToastUtils.show(activity,"请到设置，应用权限管理处给应用授权");
                }else{
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(activity,new String[]{ quanxian[i] },PERMISSION_GRANTED);
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        }
    }

}
