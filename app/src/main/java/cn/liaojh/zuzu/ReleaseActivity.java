package cn.liaojh.zuzu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.liaojh.zuzu.fragment.BaseFragment;
import cn.liaojh.zuzu.fragment.ReleasePhysicalFirstFragment;
import cn.liaojh.zuzu.fragment.ReleaseTimeFragment;

public class ReleaseActivity extends AppActivity {

    ReleaseTimeFragment releaseTimeFragment ;
    ReleasePhysicalFirstFragment releasePhysicalFirstFragment;

    @Override
    protected BaseFragment getFirstFragment() {

        releaseTimeFragment = new ReleaseTimeFragment();
        releasePhysicalFirstFragment = new ReleasePhysicalFirstFragment();

        Intent intent=getIntent();

        //获取到底打开那个fragment
        int value=intent.getIntExtra("category",1);

        if(value == 1){
            return releaseTimeFragment;
        }else if(value == 2){
            return releasePhysicalFirstFragment;
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
