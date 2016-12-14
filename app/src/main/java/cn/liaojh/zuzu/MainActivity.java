package cn.liaojh.zuzu;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.liaojh.zuzu.bean.Tab;
import cn.liaojh.zuzu.fragment.BaseFragment;
import cn.liaojh.zuzu.fragment.CategoryFragment;
import cn.liaojh.zuzu.fragment.HomeFragment;
import cn.liaojh.zuzu.fragment.MineFragment;
import cn.liaojh.zuzu.fragment.ReleaseFragment;
import cn.liaojh.zuzu.fragment.RentFragment;
import cn.liaojh.zuzu.utils.AskPermission;
import cn.liaojh.zuzu.utils.ToastUtils;
import cn.liaojh.zuzu.widget.FragmentTabHost;
import cn.liaojh.zuzu.widget.GooeyMenu;

public class MainActivity extends AppActivity implements GooeyMenu.GooeyMenuInterface{

    private GooeyMenu gooey_menu;

    private LayoutInflater mInflater;

    private FragmentTabHost mTabhost;

    private List<Tab> mTabs = new ArrayList<>(5);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //询文照相机权限
        AskPermission.askPremission(MainActivity.this,new String[]{Manifest.permission.CAMERA});

        initTab();
        initView();
    }

    public void initView(){
        gooey_menu = (GooeyMenu) findViewById(R.id.gooey_menu);

        gooey_menu.setOnMenuListener(this);
    }

    //初始化Tab123
    private void initTab() {

        Tab tab_home = new Tab(HomeFragment.class,R.string.home,R.drawable.selector_icon_home);
        Tab tab_category = new Tab(CategoryFragment.class,R.string.catagory,R.drawable.selector_icon_category);
        Tab tab_release = new Tab(ReleaseFragment.class,R.string.release,R.drawable.selector_icon_release);
        Tab tab_rent = new Tab(RentFragment.class,R.string.rent,R.drawable.selector_icon_rent);
        Tab tab_mine = new Tab(MineFragment.class,R.string.mine,R.drawable.selector_icon_mine);

        mTabs.add(tab_home);
        mTabs.add(tab_category);
        mTabs.add(tab_release);
        mTabs.add(tab_rent);
        mTabs.add(tab_mine);

        mInflater = LayoutInflater.from(this);
        mTabhost = (FragmentTabHost) this.findViewById(R.id.tobhost);
        mTabhost.setup(this,getFragmentManager(),R.id.realtabcontent);


        for (int i= 0 ; i < mTabs.size() ; i++){
            TabHost.TabSpec tabSpec = mTabhost.newTabSpec(getString(mTabs.get(i).getTitle()));

            tabSpec.setIndicator(buildIndicator(mTabs.get(i)));

            mTabhost.addTab(tabSpec,mTabs.get(i).getFragment(),null);

        }

        mTabhost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        mTabhost.setCurrentTab(0);

        mTabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

            }
        });

    }


    private View buildIndicator(Tab tab){

        View view =mInflater.inflate(R.layout.tab_indicator,null);
        ImageView img = (ImageView) view.findViewById(R.id.icon_tab);
        TextView text = (TextView) view.findViewById(R.id.txt_indicator);

        img.setBackgroundResource(tab.getIcon());
        text.setText(tab.getTitle());

        return  view;
    }


    @Override
    protected BaseFragment getFirstFragment() {
        return null;
    }

    /***********粘性菜单的监听器***********/
    @Override
    public void menuOpen() {

    }

    @Override
    public void menuClose() {

    }

    @Override
    public void menuItemClicked(int menuNumber) {
        if(ZuZuApplication.getInstance().getUser() != null){
            Intent intent = new Intent(MainActivity.this,ReleaseActivity.class);
            switch (menuNumber){
                case 1:
                    intent.putExtra("category",1);
                    startActivity(intent);
                    break;
                case 2:
                    intent.putExtra("category",2);
                    startActivity(intent);
                    break;
            }
        }
    }

}
