package cn.liaojh.zuzu.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.ZuZuApplication;
import cn.liaojh.zuzu.bean.User;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.http.SpotsCallBack;
import cn.liaojh.zuzu.utils.CountTimerView;
import cn.liaojh.zuzu.utils.ManifestUtil;
import cn.liaojh.zuzu.utils.ToastUtils;
import cn.liaojh.zuzu.utils.VerifyText;
import cn.liaojh.zuzu.widget.ClearEditText;
import cn.liaojh.zuzu.widget.MyToolBar;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import dmax.dialog.SpotsDialog;

/**
 * Created by Liaojh on 2016/10/18.
 */

public class RegFragment extends  BaseFragment {

    @ViewInject(R.id.reg_toolbar)
    MyToolBar reg_toolbar;

    @ViewInject(R.id.reg_countries)
    TextView reg_countries;

    @ViewInject(R.id.reg_phone)
    ClearEditText reg_phone;

    @ViewInject(R.id.reg_pwd)
    ClearEditText reg_pwd;

    @ViewInject(R.id.reg_code)
    ClearEditText reg_code;

    @ViewInject(R.id.reg_reSend)
    Button reg_reSend;

    @ViewInject(R.id.btn_reg)
    Button btn_reg;

    @ViewInject(R.id.txtCountryCode)
    TextView txtCountryCode;

    OkHttpHelper okHttpHelper;
    SMSEvenHanlder smsEvenHanlder;

    private SpotsDialog dialog;

    @Override
    public void initView(View view, Bundle savedInstanceState) {

        okHttpHelper = OkHttpHelper.getInstance();

        //初始化短信验证接口
        SMSSDK.initSDK(getActivity(), ManifestUtil.getMetaDataValue(getActivity(), "mob_sms_appKey"),
                ManifestUtil.getMetaDataValue(getActivity(),"mob_sms_appSecrect"));


        dialog = new SpotsDialog(getActivity(),"获取验证码ing");

        smsEvenHanlder= new SMSEvenHanlder();
        SMSSDK.registerEventHandler(smsEvenHanlder);
        /**
         * 定义后退事件
         */
        reg_toolbar.setTitle("注册");
        reg_toolbar.setLeftButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFragment();
            }
        });


        /**
         * 定义验证码事件
         */
        reg_reSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CountTimerView timerView = new CountTimerView(reg_reSend);
                timerView.start();

                //先验证一下手机号码是否可用
               if( verifyPhone(reg_phone.getText().toString()) ){
                   String phone = reg_phone.getText().toString().replaceAll("\\s","");
                   String code = txtCountryCode.getText().toString().trim();
                    dialog.setTitle("获取验证码");
                   //not 86   +86
                   SMSSDK.getVerificationCode(code,phone);         //获取验证码
                   dialog.show();
               }
            }
        });

        /**
         * 注册
         */
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //电话号码
                String phone = reg_phone.getText().toString().replaceAll("\\s","");
                String password = reg_pwd.getText().toString();
                //验证码
                String vCode = reg_code.getText().toString();

                //确保信息完整
               if(!phone.equals("") && !password.equals("") && !vCode.equals("")){
                   dialog.setTitle("提交注册信息ing");
                   //提交验证码并注册
                   SMSSDK.submitVerificationCode(txtCountryCode.getText().toString().trim(),phone,
                           vCode);
                   dialog.show();
               }

            }
        });

    }

    /**
     *验证手机号码是否可用
     */
    public Boolean verifyPhone(String phone){

        //如果是手机号码的格式
        if(VerifyText.isMobileNO(phone)){

            //如果还没有注册
            if(!isReg(phone)){
                return true;
            }else {
                ToastUtils.show(getActivity(),"该手机码已经在这里注册过了！！！");
            }

        }else {
            ToastUtils.show(getActivity(),"该手机码为非法手机号码！！！");
        }
        return false;
    }

    boolean isReg;
    /**
     *判断手机号码是否已经注册过了
     */
    public boolean isReg(String phone){
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("phone",phone);
        String url = Contans.API.FINDPHONE;

        okHttpHelper.get(url, params, new SpotsCallBack<String>(getActivity()) {

            @Override
            public void onSuccess(Response response, String s) {
               if(s.equals("0")){         //没有查到该用户
                   isReg = false;
               }else if(s.equals("1")){    //可以查到该用户
                   isReg = true;
               }
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }


        });
        return isReg;
    }

    /**
     * 验证码监听器
     */
    class SMSEvenHanlder extends EventHandler {
        @Override
        public void afterEvent(final int event, final int result, final Object data) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    //提交验证码后
                    if(event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
                        //操作成功
                        if(result == SMSSDK.RESULT_COMPLETE){
                            //注册
                            registered(reg_phone.getText().toString(),reg_pwd.getText().toString());
                        }

                    }

                    // 请求验证码后
                    if(event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //操作成功
                        if(result == SMSSDK.RESULT_COMPLETE){
                            ToastUtils.show(getActivity(),"验证码发送成功 ｡◕‿◕｡");
                        }else {
                            ToastUtils.show(getActivity(),"验证码发送失败 ,,Ծ‸Ծ,, ");
                        }
                    }

                    dialog.dismiss();
                }
            });

        }
    }


    public void registered(final String phone , final String password){
        Map<String , Object> params = new HashMap<String,Object>();
        params.put("phone",phone);
        params.put("password",password);
        okHttpHelper.get(Contans.API.REGITER, params, new SpotsCallBack<String>(getActivity()) {

            @Override
            public void onSuccess(Response response, String s) {
                if(!s.equals("-1") || !s.equals("-2")){
                    ToastUtils.show(getActivity(),"注册成功！");
                    User user = new User();
                    user.setPhone(phone);
                    user.setPassword(password);
                    ZuZuApplication.getInstance().putUser(user);
                    getActivity().finish();
                }
            }
            @Override
            public void onError(Response response, int code, Exception e) {

            }

        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reg;
    }
}
