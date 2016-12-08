package cn.liaojh.zuzu.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lidroid.xutils.ViewUtils;
import com.squareup.leakcanary.RefWatcher;

import cn.liaojh.zuzu.BaseActivity;
import cn.liaojh.zuzu.ZuZuApplication;

/**
 * Created by Liaojh on 2016/10/15.
 */

public abstract class BaseFragment extends Fragment{

    protected BaseActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(getLayoutId(), container, false);

        RefWatcher refWatcher = ZuZuApplication.getRefWatcher(getContext());

        refWatcher.watch(this);

        ViewUtils.inject(this, view);

        initToolBar();

        initView(view,savedInstanceState);

        return view;
    }

    public void  initToolBar(){

    }

    public abstract void initView(View view, Bundle savedInstanceState);

    public void startActivity(Intent intent, boolean isNeedLogin){

    }

    //获取布局文件ID
    protected abstract int getLayoutId();

    //获取宿主Activity
    protected BaseActivity getHoldingActivity() {
        return mActivity;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (BaseActivity) activity;
    }

    //添加fragment
    protected void addFragment(BaseFragment fragment) {
        if (null != fragment) {
            getHoldingActivity().addFragment(fragment);
        }
    }

    //移除fragment
    protected void removeFragment() {
        getHoldingActivity().removeFragment();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
    }
}
