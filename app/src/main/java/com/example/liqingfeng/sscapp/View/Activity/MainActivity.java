package com.example.liqingfeng.sscapp.View.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liqingfeng.sscapp.Model.Entity.Param;
import com.example.liqingfeng.sscapp.Model.Entity.ResponseModel;
import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Model.UserConstant;
import com.example.liqingfeng.sscapp.Presenter.Adapter.SpRoomAdapter;
import com.example.liqingfeng.sscapp.Presenter.CheckStatuss;
import com.example.liqingfeng.sscapp.Presenter.Util.FileUtil.FileManager;
import com.example.liqingfeng.sscapp.Presenter.Util.ImageUtil.ImageLoaderUtil;
import com.example.liqingfeng.sscapp.Presenter.Util.ImageUtil.ImageUtil;
import com.example.liqingfeng.sscapp.Presenter.Util.OkhttpUtil.RequestManager;
import com.example.liqingfeng.sscapp.R;
import com.example.liqingfeng.sscapp.View.CustomView.CircleImageView;
import com.example.liqingfeng.sscapp.View.CustomView.ItemGroup;
import com.example.liqingfeng.sscapp.View.Fragment.RoomListFragment;
import com.example.liqingfeng.sscapp.View.Fragment.SpModelFragment;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * App核心主界面
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;          //界面顶部栏
    //private FloatingActionButton fab; //界面底部悬浮按钮
    private DrawerLayout drawer;      //滑动切换导航栏和content
    private NavigationView navigationView;//导航栏
    // ----------下面定义的是主界面各个点击元素------------
    private CircleImageView headPicture;  // 头像
    private Bitmap mhBitmap;
    private TextView muserName;
    private TextView muserSign;

    Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg) {
            if(msg.what==0x123)
            {
                // 接受到消息说明已经完成了信息和头像得获取
                headPicture.setImageBitmap(mhBitmap);
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        prepareForSpmodelFragment(true);
        init();
    }

    /**
     * 初化始界面控件和点击事件
     */
    private void init() {
        toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        //悬浮按钮的点击事件
//        fab = (FloatingActionButton) findViewById( R.id.fab );
//        fab.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG )
//                        .setAction( "Action", null ).show();
//            }
//        } );
        //打开关闭导航栏
        drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();
        //导航栏点击事件监听
        navigationView = (NavigationView) findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener( this );

        // 绑定控件 注意：想要操作navigationView里的控件，需要先得到view.
        View headView = navigationView.getHeaderView(0);
        muserName =(TextView) headView.findViewById(R.id.main_userName);
        muserSign =(TextView) headView.findViewById(R.id.main_userSign);
        muserName.setText(UserConstant.userNickName);
        muserSign.setText(UserConstant.user_Sign);
        // 绑定头像 并异步更新
        headPicture =(CircleImageView) headView.findViewById(R.id.imageView);
        downPicture();
        // 绑定头像得点击事件
        headPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headPClick();
            }
        });
    }

    /**
     *  根据头像url下载图片，并显示
     */
    private void downPicture() {
        final ImageLoaderUtil imageLoaderUtil = ImageLoaderUtil.getInstance(this);
        if(!UserConstant.user_head_picture.equals("")) {
            //imageLoaderUtil.displayImage(headPicture, UrlConfig.imageBaseUrl+UserConstant.user_head_picture);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mhBitmap = imageLoaderUtil.loadImageBySyn(UrlConfig.imageBaseUrl+UserConstant.user_head_picture);
                    handler.sendEmptyMessage(0x123);
                }
            }).start();
        }
    }

    /**
     *  滑动切换导航栏和内容板块
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
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected( item );
//    }

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
            prepareForSpmodelFragment(false);
        } else if (id == R.id.nav_mysign) {
            // 签到
            Intent intent = new Intent(this, MysignActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
            finish();
        } else if (id == R.id.nav_searchRoom) {
            // 搜索房间
            selectRoom();
        } else if (id == R.id.nav_sendFeedback) {
            // 提交反馈
            sendFeedback();
        } else if (id == R.id.nav_aboutus) {
            // 关于我们
            Intent intent = new Intent(this, AboutUsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
            finish();
        } else if (id == R.id.nav_myRoom) {
            // 我的房间
            requestRoomById(UserConstant.roomID);
        } else if (id == R.id.nav_out) {
            // 退出应用
            signOut();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

    // FragmentManager fm=getActivity().getFragmentManager();
    //            Fragment fragment=new RoomListFragment();
    //            fm.beginTransaction().replace( R.id.main_content,fragment ).commit();
    /**
     * 打开Fragment的函数
     */
    private void addFragment(Fragment fragment, boolean isFirst) {
        //添加Fragment
        FragmentManager manager=getFragmentManager();
        //获得Fragment的事务对象并且开启事务
        FragmentTransaction transaction=manager.beginTransaction();
        if(isFirst) {
            //调用Fragment中相应的添加Fragment的方法
            transaction.add(R.id.main_content,fragment);
            //提交事务
            transaction.commit();
        } else {
            transaction.replace(R.id.main_content, fragment).commit();
        }
    }

    /**
     * 为展示运动模块界面 访问数据做准备
     */
    private void prepareForSpmodelFragment(final boolean isFirst) {
        RequestManager requestManager =RequestManager.getInstance( this );
        requestManager.requestGetWithoutParam( UrlConfig.SpmodleUrl, true,
                new RequestManager.ReqCallBack<ResponseModel>() {
                    @Override
                    public void onReqSuccess(ResponseModel result) {
                        if(CheckStatuss.CheckStatus( result,getApplicationContext() )==1) {
                            UserConstant.list_sport=(List<Map<String,String>>) result.getData();
                            ImageUtil.Image_down(UserConstant.list_sport);
                            addFragment(new SpModelFragment(), isFirst);
                        } else if (CheckStatuss.CheckStatus( result,getApplicationContext() )==2) {
                            Toast.makeText( getApplicationContext(),"网络出现错误",Toast.LENGTH_SHORT ).show();
                        } else {
                            Intent intent = new Intent( getApplicationContext(), LoginActivity.class );
                            startActivity( intent );
                            finish();
                        }
                    }
                    @Override
                    public void onReqFailed(String errorMsg) {

                    }
                } );
    }

    /**
     * 头像点击事件--进入个人信息页面
     * 等待返回最新的个人信息
     */
    public void headPClick() {
        Intent intent = new Intent( this, PersonalActivity.class );
        intent.putExtra("headPicture",mhBitmap);
        startActivityForResult( intent, 0x12 );
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /**
     *  收到回到的信息
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String nickname = data.getExtras().getString( "nickname" );
        String sign = data.getExtras().getString("sign");
        Bitmap headBitmap = data.getParcelableExtra("image");
        muserName.setText(nickname);
        muserSign.setText(sign);
        downPicture();
        headPicture.setImageBitmap(headBitmap);
    }

    /**
     *  退出App
     */
    private void signOut() {
        FileManager fileManager = new FileManager(this, UrlConfig.userInformation);
        fileManager.writeFileData("");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void selectRoom() {
        final View layout = View.inflate(this, R.layout.dialogitem,
                null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择房间");
        builder.setView(layout);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //获得dialog 中 edittext的值
                ItemGroup etListLibraryNote = (ItemGroup) layout.findViewById(R.id.selectRoomId);
                String libraryNote = etListLibraryNote.getText();
                if(libraryNote.equals("")) {
                    Toast.makeText(getApplicationContext(),"搜索房间号不能为空",Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String trim=Pattern.compile("[0-9]*").matcher(libraryNote).replaceAll("").trim();
                    if(!trim.equals("")){
                        Toast.makeText(getApplicationContext(),"只能输入数字",Toast.LENGTH_SHORT).show();
                    } else {
                        requestRoomById(libraryNote);
                    }
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void requestRoomById(String id) {
        String url=UrlConfig.SproomUrl+"?id="+id+"&roStatus=3";
        RequestManager requestManager =RequestManager.getInstance(this);
        requestManager.requestGetWithoutParam( url, true,
                new RequestManager.ReqCallBack<ResponseModel>() {
                    @Override
                    public void onReqSuccess(ResponseModel result) {
                        UserConstant.list_room=(List<Map<String, Object>>)result.getData();
                        if(UserConstant.list_room.size() == 0) {
                            Toast.makeText(getApplicationContext(),"房间已关闭！",Toast.LENGTH_SHORT).show();
                            addFragment(new SpModelFragment() ,false);
                        } else{
                            UserConstant.room_image_path=UserConstant.list_sport.get( 0 ).get( "spRoimg" );
                            FragmentManager fm=getFragmentManager();
                            Fragment fragment=new RoomListFragment();
                            fm.beginTransaction().replace( R.id.main_content,fragment ).commit();
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {

                    }
                });
    }

    private void sendFeedback() {
        final View layout = View.inflate(this, R.layout.sendfeedbackdialog,
                null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提交反馈");
        builder.setView(layout);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //获得dialog 中 edittext的值
                TextView etListLibraryNote = (TextView) layout.findViewById(R.id.feedback);
                String libraryNote = etListLibraryNote.getText().toString();
                // 提交反馈
                send(libraryNote);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void send(String text) {
        RequestManager requestManager = RequestManager.getInstance(this);
        requestManager.requestAsyn(UrlConfig.sendFeedbackByUrl, RequestManager.TYPE_POST_JSON, new Param().append("coContent", text).end(),
                true, new RequestManager.ReqCallBack<ResponseModel>() {
                    @Override
                    public void onReqSuccess(ResponseModel result) {
                        Toast.makeText(getApplicationContext(), "人家知道了，嘤嘤嘤┭┮﹏┭┮",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {

                    }
                });
    }
}
