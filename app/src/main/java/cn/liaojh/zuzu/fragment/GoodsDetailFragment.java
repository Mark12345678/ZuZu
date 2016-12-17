package cn.liaojh.zuzu.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Response;

import java.util.HashMap;
import java.util.Map;

import cn.liaojh.zuzu.Contans;
import cn.liaojh.zuzu.R;
import cn.liaojh.zuzu.ZuZuApplication;
import cn.liaojh.zuzu.bean.Goods;
import cn.liaojh.zuzu.http.OkHttpHelper;
import cn.liaojh.zuzu.http.SpotsCallBack;
import cn.liaojh.zuzu.utils.ToastUtils;
import cn.liaojh.zuzu.widget.MyToolBar;
import io.rong.imkit.RongIM;

/**
 * Created by Liaojh on 2016/10/29.
 */

public class GoodsDetailFragment extends BaseFragment {

    Goods goods;

    private WebAppInterface mAppInterface;

    @ViewInject(R.id.detail_toolbar)
    MyToolBar detail_toolbar;

    @ViewInject(R.id.detail_webView)
    WebView mWebView;

    OkHttpHelper okHttpHelper ;


    public GoodsDetailFragment(Goods goods){
        this.goods = goods;
    }

    @Override
    public void initView(View view, Bundle savedInstanceState) {
        detail_toolbar.setTitle(goods.getGoodsName());
        detail_toolbar.setLeftButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFragment();
                getActivity().finish();
            }
        });

        okHttpHelper = OkHttpHelper.getInstance();

        initWebView();
    }

    private void initWebView(){
        WebSettings settings = mWebView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(false);
        settings.setAppCacheEnabled(true);

        mWebView.loadUrl(Contans.API.DETAIL+"?goodsId="+goods.getId());

        mAppInterface = new WebAppInterface(getContext());
        mWebView.addJavascriptInterface(mAppInterface,"appInterface");
        mWebView.setWebViewClient(new WC());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_goodsdetail;
    }

    class WC extends WebViewClient {

    }

    class WebAppInterface{

        Context context;

        public WebAppInterface(Context context){
            this.context = context;
        }

        @JavascriptInterface
        public void btnlike(){
            putToLike();
        }

        @JavascriptInterface
        public void talkgoodser(){

            //判断是否连接成功融云服务器
            if(ZuZuApplication.getInstance().isisConnectToIM() == false){
                ZuZuApplication.getInstance().connect(ZuZuApplication.getInstance().getUser().getTakenIM());
            }

            android.support.v7.app.AlertDialog.Builder dlg = new android.support.v7.app.AlertDialog.Builder(mActivity);
            dlg.setTitle("添加好友");
            dlg.setView(R.layout.addfriend_content);
            dlg.setCustomTitle(View.inflate(getContext(),R.layout.addfriend_title,null));
            dlg.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    addFriend(ZuZuApplication.getInstance().getUser().getId() , goods.getUser().getId());
                    opeanWindow();
                }
            });
            dlg.setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    opeanWindow();
                }
            });
            dlg.show();
        }
    }

    public void addFriend(int mineId , int targetId){

        Map<String,Object> params = new HashMap<String,Object>();
        params.put("mineId",mineId);
        params.put("targetId",targetId);

        okHttpHelper.get(Contans.API.ADDFRIEND,params, new SpotsCallBack<String>(getActivity()) {

            @Override
            public void onSuccess(Response response, String s) {
                if(s.equals("1")){
                    ToastUtils.show(getContext(),"成功加为好友");
                }else if(s.equals("2")){
                    ToastUtils.show(getContext(),"你们本来就是好友了！");
                }else {
                    ToastUtils.show(getContext(),"添加好友失败,暂时不能聊天");
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
    }

    public void opeanWindow(){
        //打开聊天窗口
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().startPrivateChat(getContext(), goods.getUser().getId() + "", "title");
        }
    }

    /**
     * 放入喜欢列表
     */
    public void putToLike(){
        Map<String , Object> map = new HashMap<String,Object>();
        map.put("mineId",ZuZuApplication.getInstance().getUser().getId());
        map.put("goodsId",goods.getId());

        okHttpHelper.get(Contans.API.ADDLIKE, map, new SpotsCallBack<String>(getActivity()) {

            @Override
            public void onSuccess(Response response, String s) {
                if(s.equals("1")){
                    ToastUtils.show(getContext(),"成功添加进喜欢列表");
                } else if(s.equals("2")) {
                    ToastUtils.show(getContext(), "你已经添加到喜欢列表了");
                }else {
                    ToastUtils.show(getContext(),s+"s添加失败");
                }
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
    }
}
