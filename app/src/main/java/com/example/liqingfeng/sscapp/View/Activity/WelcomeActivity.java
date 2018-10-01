package com.example.liqingfeng.sscapp.View.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.example.liqingfeng.sscapp.Presenter.ImageManage.Varify;
import com.example.liqingfeng.sscapp.R;

/**
 * 启动界面
 */
public class WelcomeActivity extends Activity{
    public View view;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        view = View.inflate( this, R.layout.activity_welcome,null );
        setContentView( view );
        animStart();
    }

    /**
     * 启动 动画开始
     */
    private void animStart() {
        AlphaAnimation aa = new AlphaAnimation(0.1f,1.0f);
        aa.setDuration(3000);
        view.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation arg0) {
                getHome();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
            @Override
            public void onAnimationStart(Animation animation) {

            }

        });
    }

    /**
     *  将获取的验证码发给登陆界面
     */
    private void getHome(){
        Intent intent = new Intent(WelcomeActivity.this,GuideActivity.class);
        startActivity(intent);
        finish();
    }
}
