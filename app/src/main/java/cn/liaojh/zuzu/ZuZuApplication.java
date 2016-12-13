package cn.liaojh.zuzu;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;



import cn.liaojh.zuzu.bean.User;
import cn.liaojh.zuzu.utils.ToastUtils;
import cn.liaojh.zuzu.utils.UserLocalData;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.notification.PushNotificationMessage;

/**
 * Created by Liaojh on 2016/10/15.
 */

public class ZuZuApplication extends Application {

    private User user;

    private static ZuZuApplication mInstance = new ZuZuApplication();

    public static ZuZuApplication getInstance(){
        return mInstance;
    }



    public RefWatcher refWatcher = null;

    private Boolean isConnectToIM  = false;

    @Override
    public void onCreate() {
        super.onCreate();
        RongIM.init(this);
        refWatcher = LeakCanary.install(this);
        mInstance = this;
        initUser();
        Fresco.initialize(this);
        RongIM.setOnReceivePushMessageListener(new MyReceivePushMessageListener());
        //connect(user.getTakenIM());
    }

    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {

                return appProcess.processName;
            }
        }
        return null;
    }

    public static RefWatcher getRefWatcher(Context context) {
        ZuZuApplication application = (ZuZuApplication) context
                .getApplicationContext();
        return application.refWatcher;
    }

    public void initUser(){
        this.user = UserLocalData.getUser(this);
    }

    public User getUser(){
        return user;
    }

    public void putUser(User user){
        this.user = user;
        UserLocalData.putUser(this,user);
    }

    public void clearUser(){
        this.user = null;
        UserLocalData.clearUser(this);
    }

    private Intent intent;
    public void putIntent(Intent intent){
        this.intent = intent;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public void jumpToTargetActivity(Context context){

        context.startActivity(intent);
        this.intent =null;
    }

    public Boolean isisConnectToIM(){
        return isConnectToIM;
    }

    public void connect(String token){

        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */

            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {
                    ToastUtils.show(getApplicationContext(),"token出错");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    ToastUtils.show(getApplicationContext(),"随时可以聊天");
                    isConnectToIM = true;
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    ToastUtils.show(getApplicationContext(),"错误");
                }
            });
        }
    }

    private class MyReceivePushMessageListener implements RongIMClient.OnReceivePushMessageListener{

        @Override
        public boolean onReceivePushMessage(PushNotificationMessage pushNotificationMessage) {
            return false;
        }
    }

}
