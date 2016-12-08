package cn.liaojh.zuzu.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.ZuZuApplication;
import cn.liaojh.zuzu.bean.User;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.http.SimpleCallback;
import cn.liaojh.zuzu.utils.ToastUtils;
import cn.liaojh.zuzu.utils.VerifyText;
import cn.liaojh.zuzu.widget.ClearEditText;
import cn.liaojh.zuzu.widget.MyToolBar;
import dmax.dialog.SpotsDialog;

/**
 * Created by Liaojh on 2016/10/18.
 */

public class LoginFragment extends  BaseFragment implements View.OnClickListener{

    @ViewInject(R.id.etxt_phone)
    ClearEditText etxt_phone;

    @ViewInject(R.id.etxt_pwd)
    ClearEditText etxt_pwd;

    @ViewInject(R.id.btn_login)
    Button btn_login;

    @ViewInject(R.id.txt_toReg)
    TextView txt_toReg;

    @ViewInject(R.id.txt_forget)
    TextView txt_forget;

    @ViewInject(R.id.login_toolbar)
    MyToolBar login_toolbar;

    OkHttpHelper okHttpHelper ;
    private SpotsDialog dialog;

    @Override
    public void initView(View view, Bundle savedInstanceState) {
        login_toolbar.setTitle("租租登录");
        login_toolbar.setLeftButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        dialog = new SpotsDialog(getActivity(),"提交登录信息ing");

        okHttpHelper = OkHttpHelper.getInstance();

        login_toolbar.setOnClickListener(this);
        etxt_phone.setOnClickListener(this);
        etxt_pwd.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        txt_toReg.setOnClickListener(this);
        txt_forget.setOnClickListener(this);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_toReg:
                addFragment(new RegFragment());
                break;
            case R.id.btn_login:
                login(etxt_phone.getText().toString(),etxt_pwd.getText().toString());
                dialog.show();
                break;
        }
    }

    public void login(String phone , String password){
        String url = Contans.API.LOGIN;
        Map<String,Object> pamars = new HashMap<String,Object>();
        pamars.put("phone",phone);
        pamars.put("password",password);

        if(verifyText(phone,password)){
            okHttpHelper.get(url, pamars, new SimpleCallback<List<User>>(getActivity()) {


                @Override
                public void onSuccess(Response response, List<User> users) {
                    afterLogin(users.get(0));
                    dialog.dismiss();
                }

                @Override
                public void onError(Response response, int code, Exception e) {
                    dialog.dismiss();
                }


            });
        }else {
            ToastUtils.show(getActivity(),"请正确填写登录信息");
        }
    }

    public void afterLogin(User user){
        if(user.getPhone().equals("-2")){
            ToastUtils.show(getActivity(),"用户账号错误");
        }else if(user.getPassword().equals("-2")){
            ToastUtils.show(getActivity(),"密码错误");
        }else if(!user.getPassword().equals("-2") && !user.getPhone().equals("-2")){
            ZuZuApplication zuZuApplication = ZuZuApplication.getInstance();
            zuZuApplication.putUser(user);
            ToastUtils.show(getActivity(),"登录成功");
            getActivity().finish();
        }
    }


    public Boolean verifyText(String phone, String password){
        if(!phone.equals("") && !password.equals("")){
            if(VerifyText.isMobileNO(phone)){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }


}
