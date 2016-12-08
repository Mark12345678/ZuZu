package cn.liaojh.zuzu.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import android.support.v7.widget.TintTypedArray;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.liaojh.zuzu.R;


public class MyToolBar extends Toolbar {

    private LayoutInflater mInflater;

    private View mView;
    private TextView mTextTitle;
    private TextView mSearchView;
    private Button mRightButton;
    private TextView txt_left;
    private TextView txt_reght;
    private ImageView img_back;

    public MyToolBar(Context context) {
       this(context,null);
    }

    public MyToolBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
        setContentInsetsRelative(10,10);

        if(attrs !=null) {
            final TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs,
                    R.styleable.MyToolBar, defStyleAttr, 0);


            final Drawable rightIcon = a.getDrawable(R.styleable.MyToolBar_rightButtonIcon);
            if (rightIcon != null) {
                //setNavigationIcon(navIcon);
                setRightButtonIcon(rightIcon);
            }

            boolean isShowSearchView = a.getBoolean(R.styleable.MyToolBar_isShowSearchView,false);
            if(isShowSearchView){
                showSearchView();
                hideTitleView();
            }

            boolean isShowRelative = a.getBoolean(R.styleable.MyToolBar_isShowRelative,false);
            if(isShowRelative){
                boolean isShowleft = a.getBoolean(R.styleable.MyToolBar_isShowleftNavitive,false);
                if(isShowleft){
                    showLeftNavitive();
                }
                boolean isShowRegiht = a.getBoolean(R.styleable.MyToolBar_isShowRightNavitive,false);
                if(isShowRegiht){
                    showRegihtNavitive();
                }
            }

            boolean isShowLeftButton = a.getBoolean(R.styleable.MyToolBar_isShowLeftButton,false);
            if(isShowLeftButton
                    ){
                showLeftButton();
            }

            CharSequence rightButtonText = a.getText(R.styleable.MyToolBar_rightButtonText);
            if(rightButtonText !=null){
                setRightButtonText(rightButtonText);
            }

            a.recycle();
        }

    }

    private void showLeftButton() {
        initView();

        img_back.setVisibility(VISIBLE);

    }

    private void showLeftNavitive() {
        initView();

           txt_left.setVisibility(VISIBLE);
           txt_reght.setVisibility(VISIBLE);
           txt_left.setBackgroundColor(getResources().getColor(R.color.white));
           txt_reght.setBackgroundColor(getResources().getColor(R.color.gray));

    }

    public void showRegihtNavitive() {
        initView();
        txt_reght.setVisibility(VISIBLE);
        txt_left.setVisibility(VISIBLE);
        txt_left.setBackgroundColor(getResources().getColor(R.color.gray));
        txt_reght.setBackgroundColor(getResources().getColor(R.color.white));
    }

    private void initView() {

        if(mView == null) {

            mInflater = LayoutInflater.from(getContext());
            mView = mInflater.inflate(R.layout.toolbar, null);

            mTextTitle = (TextView) mView.findViewById(R.id.toolbar_title);
            mSearchView = (TextView) mView.findViewById(R.id.toolbar_searchview);
            mRightButton = (Button) mView.findViewById(R.id.toolbar_rightButton);
            txt_left = (TextView) mView.findViewById(R.id.navitive_left);
            txt_reght = (TextView) mView.findViewById(R.id.navitive_regith);
            img_back = (ImageView) mView.findViewById(R.id.toolbar_back);

            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);

            addView(mView, lp);
        }else {
            mTextTitle = (TextView) mView.findViewById(R.id.toolbar_title);
            mSearchView = (TextView) mView.findViewById(R.id.toolbar_searchview);
            mRightButton = (Button) mView.findViewById(R.id.toolbar_rightButton);
            txt_left = (TextView) mView.findViewById(R.id.navitive_left);
            txt_reght = (TextView) mView.findViewById(R.id.navitive_regith);
            img_back = (ImageView) mView.findViewById(R.id.toolbar_back);
        }



    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void  setRightButtonIcon(Drawable icon){

        if(mRightButton !=null){

            mRightButton.setBackground(icon);
            mRightButton.setVisibility(VISIBLE);
        }

    }

    public void  setRightButtonIcon(int icon){

        setRightButtonIcon(getResources().getDrawable(icon));
    }



    public  void setSearchViewOnClickListener(OnClickListener li){

        mSearchView.setOnClickListener(li);
    }

    public  void setRightButtonOnClickListener(OnClickListener li){

        mRightButton.setOnClickListener(li);
    }

    /**
     * 为左边的图标设置监听器
     * @param li
     */
    public void setLeftButtonListener(OnClickListener li){
        if(img_back != null){
            img_back.setOnClickListener(li);
        }
    }

    public void setRightButtonText(CharSequence text){
        mRightButton.setText(text);
        mRightButton.setVisibility(VISIBLE);
    }

    public void setRightButtonText(int id){
        setRightButtonText(getResources().getString(id));
    }



    public Button getRightButton(){

        return this.mRightButton;
    }



    @Override
    public void setTitle(int resId) {

        setTitle(getContext().getText(resId));
    }

    @Override
    public void setTitle(CharSequence title) {

        initView();
        if(mTextTitle !=null) {
            mTextTitle.setText(title);
            showTitleView();
        }

    }


    public  void showSearchView(){

        if(mSearchView !=null)
            mSearchView.setVisibility(VISIBLE);

    }

    public String getSeacherText(){
        if(mSearchView != null){
            return mSearchView.getText().toString();
        }
        return "";
    }


    public void hideSearchView(){
        if(mSearchView !=null)
            mSearchView.setVisibility(GONE);
    }

    public void showTitleView(){
        if(mTextTitle !=null)
            mTextTitle.setVisibility(VISIBLE);
    }


    public void hideTitleView() {
        if (mTextTitle != null)
            mTextTitle.setVisibility(GONE);

    }

//
//    private void ensureRightButtonView() {
//        if (mRightImageButton == null) {
//            mRightImageButton = new ImageButton(getContext(), null,
//                    android.support.v7.appcompat.R.attr.toolbarNavigationButtonStyle);
//            final LayoutParams lp = generateDefaultLayoutParams();
//            lp.gravity = GravityCompat.START | (Gravity.VERTICAL_GRAVITY_MASK);
//            mRightImageButton.setLayoutParams(lp);
//        }
//    }


}
