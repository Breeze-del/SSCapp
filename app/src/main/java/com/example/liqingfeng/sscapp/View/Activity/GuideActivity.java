package com.example.liqingfeng.sscapp.View.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.liqingfeng.sscapp.Model.Entity.Param;
import com.example.liqingfeng.sscapp.Model.Entity.ResponseModel;
import com.example.liqingfeng.sscapp.Presenter.CheckStatuss;
import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Model.UserConstant;
import com.example.liqingfeng.sscapp.Presenter.Util.ConvertUtil.DataConvertUtil;
import com.example.liqingfeng.sscapp.Presenter.Util.FileUtil.FileManager;
import com.example.liqingfeng.sscapp.Presenter.Util.OkhttpUtil.RequestManager;
import com.example.liqingfeng.sscapp.R;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends Activity {
    private ViewPager vpGuide;
    private Button btnStart;
    private LinearLayout llIndicator;
    private ImageView ivIndicator;

    private List<ImageView> imgs;
    private int[] imgIds = new int[]{R.drawable.bg_guide5, R.drawable.bg_guide2, R.drawable.bg_guide3,
            R.drawable.bg_guide4};

    private int pointDis; // 指示器的间距

    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_guide );
        judgeStartAccess();
        init();
        startAnim();
        // 点击按钮，进入主Activity
        btnStart.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( GuideActivity.this, LoginActivity.class );
                startActivity( intent );
                //进入动画
                overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
                //退出动画
                //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                finish(); // 销毁当前Activity
            }
        } );
    }

    /**
     * 开始pager
     */
    private void startAnim() {
        vpGuide.setAdapter( new MyPagerAdapter( imgs ) );
        // 计算两个圆点之间的距离
        // measure-->layout(确定位置)-->draw（onCreate方法执行结束后才会执行此流程）
        // 视图树：监听layout方法结束的事件，位置确定好之后再获取圆点的间距
        ivIndicator.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    // layout方法执行后回调此方法
                    @Override
                    public void onGlobalLayout() {
                        pointDis = llIndicator.getChildAt( 1 ).getLeft()
                                - llIndicator.getChildAt( 0 ).getLeft();
                        // 移除，避免重复回调
                        ivIndicator.getViewTreeObserver().removeGlobalOnLayoutListener( this );
                    }
                } );
        vpGuide.setOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == imgs.size() - 1) {
                    btnStart.setVisibility( View.VISIBLE );
                    AlphaAnimation anim = new AlphaAnimation( 0, 1 );
                    anim.setDuration( 1500 );
                    btnStart.setAnimation( anim );
                } else {
                    btnStart.setVisibility( View.INVISIBLE );
                }
            }

            // position：当前位置；positionOffset：偏移量百分比；positionOffsetPixels：偏移量像素
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                int des = (int) (pointDis * positionOffset) + position
                        * pointDis;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivIndicator
                        .getLayoutParams();
                params.leftMargin = des;
                ivIndicator.setLayoutParams( params );
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        } );
    }

    /**
     * 初化始控件和数据
     */
    private void init() {
        vpGuide = (ViewPager) this.findViewById( R.id.vp_guide );
        btnStart = (Button) this.findViewById( R.id.btn_start );
        btnStart.setVisibility( View.INVISIBLE );
        llIndicator = (LinearLayout) this.findViewById( R.id.ll_indicator );
        ivIndicator = (ImageView) this.findViewById( R.id.iv_indicator_selected );

        initData();
    }

    /**
     * 初化始数据
     */
    private void initData() {
        imgs = new ArrayList<ImageView>();
        for (int i = 0; i < imgIds.length; i++) {
            ImageView img = new ImageView( this );
            img.setBackgroundResource( imgIds[i] );
            imgs.add( img );

            // 初始化指示器
            ImageView indicator = new ImageView( this );
            indicator.setImageResource( R.mipmap.ic_shape_point_normal );

            // 设置左边距
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT );
            if (i > 0) { // 从第二个开始
                params.leftMargin = 10;
            }
            llIndicator.addView( indicator, params );
        }
    }

    /**
     * 判断登陆状态
     */
    private void judgeToken() {
        FileManager fileManager = new FileManager( this, UrlConfig.userInformation );
        String userImf = fileManager.readFileData();
        //用户信息 还未存入文件
        if (userImf == null || userImf.equals( "" )) {
            Intent intent = new Intent( GuideActivity.this, LoginActivity.class );
            startActivity( intent );
            overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        } else {
            ResponseModel responseModel = CheckStatuss.gson.fromJson( userImf, ResponseModel.class );
            // 获取全局用户信息
            UserConstant.userNickName = (String) responseModel.getFromData("usNickname");
            try {
                UserConstant.tokenCode=responseModel.getToken();
            }catch (Exception e) {
                UserConstant.tokenCode = "123";
            }
            UserConstant.user_Sign = (String) responseModel.getFromData("usSign");
            UserConstant.uesrID = DataConvertUtil.toInt( responseModel.getFromData( "id" ) );
            //检测Token是否失效
            RequestManager requestManager = RequestManager.getInstance( this );
            ResponseModel responseModel1 = requestManager.requestGetBySyn( UrlConfig.findUserByID,
                    new Param( "userId", UserConstant.uesrID + "" ).end(),
                    true );
            if(  CheckStatuss.CheckStatus( responseModel1,this ) == 1) {
                String status = responseModel1.getFromData( "usStatus" ) + "";
                if (status.equals( "1.0" )) {
                    // 最新头像地址 昵称 个性签名
                    UserConstant.user_head_picture = (String) responseModel1.getFromData("usImg");
                    UserConstant.userNickName = (String) responseModel1.getFromData("usNickname");
                    UserConstant.user_Sign = (String) responseModel1.getFromData("usSign");
                    UserConstant.roomID= ""+new Double(""+responseModel1.getFromData("usRoomid")).intValue();
                    Intent intent = new Intent( GuideActivity.this, MainActivity.class );
                    startActivity( intent );
                } else {
                    Toast.makeText( GuideActivity.this, "用户已被锁定,请联系管理员", Toast.LENGTH_SHORT ).show();
                    Intent intent = new Intent( GuideActivity.this, LoginActivity.class );
                    startActivity( intent );
                    overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                }
            } else {
                Intent intent = new Intent( GuideActivity.this, LoginActivity.class );
                startActivity( intent );
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
            }
        }
        // 销毁引导Activity
        finish();
    }

    /**
     * 判断是否是第一次打开app
     * 第一次打开app会跳转到引导页
     */
    private void judgeStartAccess() {
        sharedPreferences = getSharedPreferences( "count", MODE_PRIVATE );
        int count = sharedPreferences.getInt( "count", 0 );
        Log.d( "print", String.valueOf( count ) );
        //判断程序是第几次运行，如果是第一次运行则跳转到引导页面
        if (count != 0) {
            judgeToken();
            this.finish();
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //存入数据
        if (count == 0) {
            count = 1;
        }
        editor.putInt( "count", count );
        // /提交修改
        editor.commit();
    }

    /**
     * 内部类 图片加载
     */
    class MyPagerAdapter extends PagerAdapter {
        private List<ImageView> imgs;

        public MyPagerAdapter(List<ImageView> imgs) {
            this.imgs = imgs;
        }

        // item的个数
        @Override
        public int getCount() {
            return imgs.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        // 初始化item布局
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = imgs.get( position );
            container.addView( view );
            return view;
        }

        // 销毁item
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView( imgs.get( position ) );
        }

    }
}
