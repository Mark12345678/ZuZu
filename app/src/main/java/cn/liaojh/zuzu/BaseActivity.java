package cn.liaojh.zuzu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import cn.liaojh.zuzu.fragment.BaseFragment;

/**
 * Created by Liaojh on 2016/10/15.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected static final String TAG = BaseActivity.class.getSimpleName();

    //布局文件ID
    protected abstract int getContentViewId();

    //布局中Fragment的ID
    protected abstract int getFragmentContentId();

    //添加fragment
    public void addFragment(BaseFragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(getFragmentContentId(), fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    //移除fragment
    public void removeFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    //返回键返回事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
