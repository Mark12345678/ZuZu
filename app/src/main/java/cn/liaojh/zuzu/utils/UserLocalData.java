package cn.liaojh.zuzu.utils;

import android.content.Context;
import android.text.TextUtils;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.bean.User;

/**
 * Created by Liaojh on 2016/10/17.
 */

public class UserLocalData {

    public static void putUser(Context context, User user){
        String user_json = JSONUtil.toJSON(user);
        PreferencesUtils.putString(context, Contans.USER_JSON,user_json);

    }

    public static User getUser(Context context){
        String user_json = PreferencesUtils.getString(context,Contans.USER_JSON);

        if(!TextUtils.isEmpty(user_json)){

            return  JSONUtil.fromJson(user_json,User.class);
        }
        return  null;
    }

    public static void clearUser(Context context){

        PreferencesUtils.putString(context, Contans.USER_JSON,"");

    }

}
