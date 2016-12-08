package cn.liaojh.zuzu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.liaojh.zuzu.fragment.BaseFragment;
import cn.liaojh.zuzu.fragment.LoginFragment;

public class LoginActivity extends AppActivity {


    @Override
    protected BaseFragment getFirstFragment() {
        return new LoginFragment();
    }

}
