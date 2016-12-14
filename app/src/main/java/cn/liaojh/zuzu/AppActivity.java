package cn.liaojh.zuzu;

import android.content.Intent;
import android.os.Bundle;

import com.squareup.leakcanary.RefWatcher;

import cn.liaojh.zuzu.fragment.BaseFragment;

/**
 * Created by Liaojh on 2016/10/17.
 */

public abstract  class AppActivity extends BaseActivity{

    //获取第一个fragment
    protected abstract BaseFragment getFirstFragment();

    //获取Intent
    protected void handleIntent(Intent intent) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        RefWatcher refWatcher = ZuZuApplication.getRefWatcher(this);
        refWatcher.watch(this);
        if (null != getIntent()) {
            handleIntent(getIntent());
        }
        //避免重复添加Fragment
        if (0 == getFragmentManager().getBackStackEntryCount()) {
            BaseFragment firstFragment = getFirstFragment();
            if (null != firstFragment) {
                replaceFragment(firstFragment);
            }
        }

    }

    //获取fragment容器
    @Override
    protected int getContentViewId() {
        return R.layout.activity_base;
    }

    //获取可以替换fragment的Id
    @Override
    protected int getFragmentContentId() {
        return R.id.fragment_container;
    }
}
