package com.example.liqingfeng.sscapp.View.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.liqingfeng.sscapp.Model.Entity.ResponseModel;
import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Model.UserConstant;
import com.example.liqingfeng.sscapp.Presenter.CheckStatuss;
import com.example.liqingfeng.sscapp.Presenter.Util.ImageUtil.ImageUtil;
import com.example.liqingfeng.sscapp.Presenter.Util.OkhttpUtil.RequestManager;
import com.example.liqingfeng.sscapp.R;
import com.example.liqingfeng.sscapp.View.Fragment.SpModelFragment;

import java.util.List;
import java.util.Map;

/**
 * App核心主界面
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;          //界面顶部栏
    private FloatingActionButton fab; //界面底部悬浮按钮
    private DrawerLayout drawer;      //滑动切换导航栏和content
    private NavigationView navigationView;//导航栏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        prepareForSpmodelFragment();
        init();
    }

    /**
     * 初化始界面控件和点击事件
     */
    private void init() {
        toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        //悬浮按钮的点击事件
        fab = (FloatingActionButton) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG )
                        .setAction( "Action", null ).show();
            }
        } );
        //打开关闭导航栏
        drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();
        //导航栏点击事件监听
        navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );
    }

    /**
     * 华东切换导航栏和内容板块
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    /**
     *  将你添加的操作栏的item添加到你的布局中去
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    /**
     *  setting 里面点击事件处理
     * @param item  点击的是哪一个setting
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    /**
     * 导航栏的点击事件
     * @param item 点击的是哪一个导航栏的item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sportslevel) {
            // Handle the camera action
        } else if (id == R.id.nav_sportsgrade) {

        } else if (id == R.id.nav_sportsphoto) {

        } else if (id == R.id.nav_sportsfeedback) {

        } else if (id == R.id.nav_aboutus) {

        } else if (id == R.id.nav_share) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

    /**
     * 打开Fragment的函数
     */
    private void addFragment(Fragment fragment) {
        //添加Fragment
        FragmentManager manager=getFragmentManager();
        //获得Fragment的事务对象并且开启事务
        FragmentTransaction transaction=manager.beginTransaction();
        //调用Fragment中相应的添加Fragment的方法
        transaction.add(R.id.main_content,fragment);
        //提交事务
        transaction.commit();
    }

    private void prepareForSpmodelFragment() {
        RequestManager requestManager =RequestManager.getInstance( this );
        requestManager.requestGetWithoutParam( UrlConfig.SpmodleUrl, true,
                new RequestManager.ReqCallBack<ResponseModel>() {
                    @Override
                    public void onReqSuccess(ResponseModel result) {
                        if(CheckStatuss.CheckStatus( result,getApplicationContext() )==1) {
                            UserConstant.list_sport=(List<Map<String,String>>) result.getData();
                            ImageUtil.Image_down(UserConstant.list_sport);
                            addFragment(new SpModelFragment());
                        } else if (CheckStatuss.CheckStatus( result,getApplicationContext() )==2) {
                            Toast.makeText( getApplicationContext(),"网络出现错误",Toast.LENGTH_SHORT ).show();
                        } else {
                            Intent intent = new Intent( getApplicationContext(), LoginActivity.class );
                            startActivity( intent );
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {

                    }
                } );
    }
}
