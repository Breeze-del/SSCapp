package com.example.liqingfeng.sscapp.View.Activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.liqingfeng.sscapp.Presenter.BusinessManage.LoginManage;
import com.example.liqingfeng.sscapp.Presenter.DataConstant;
import com.example.liqingfeng.sscapp.Presenter.ImageManage.Varify;
import com.example.liqingfeng.sscapp.R;
import com.example.liqingfeng.sscapp.Util.AnimUtil.JellyInterpolator;

import com.example.liqingfeng.sscapp.View.CustomView.CustomVideoView;

import java.util.HashMap;


/**
 * 登陆界面
 */
public class LoginActivity extends Activity{
    private TextView mBthLogin;
    private View progress;
    private View mInputLayout;
    private float mWidth, mHeight;
    private LinearLayout mName, mpas,mver;
    private CustomVideoView videoview;
    private ImageView verify_imageview;
    private EditText username,password,verify;
    //登陆接口
    private String url,url_login;
    //验证码字符串
    public String imag_String;
    public String json;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);    //设置全屏
        this.requestWindowFeature( Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView( R.layout.activity_login );

        judgeStartAccess();
        init();
    }

    /**
     * 判断是否是第一次打开app
     * 第一次打开app会跳转到引导页
     */
    private void judgeStartAccess() {
        sharedPreferences = getSharedPreferences("count",MODE_PRIVATE);
        int count = sharedPreferences.getInt("count",0);
        Log.d("print", String.valueOf(count));
        //判断程序是第几次运行，如果是第一次运行则跳转到引导页面
        if (count == 0){
            Intent intent = new Intent(  );
            intent.setClass(getApplicationContext(), WelcomeActivity.class);
            startActivity(intent);
            this.finish();
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //存入数据
        editor.putInt("count",++count);
        //提交修改
        editor.commit();
    }

    /**
     * 加载login界面所有控件
     */
    private void init() {
        //获取验证码字符串,并且显示出来
        Intent intent=getIntent();
        if(DataConstant.varifyCode.equals( "" )) {
            Varify.getVarify( this );
        }
        imag_String=DataConstant.varifyCode;
        drawverify();

        mBthLogin = (TextView) findViewById(R.id.main_btn_login);
        progress = findViewById(R.id.layout_progress);
        mInputLayout = findViewById(R.id.input_layout);
        mName = (LinearLayout) findViewById(R.id.input_layout_name);
        mpas = (LinearLayout) findViewById(R.id.input_layout_psw);
        mver=(LinearLayout)findViewById(R.id.input_ver);
        //三个Edittext输入框 获取输入输出
        username=(EditText) findViewById(R.id.input_username);
        password=(EditText) findViewById(R.id.input_password);
        verify=(EditText) findViewById(R.id.input_verify);

        videoview = (CustomVideoView) findViewById(R.id.vidoview);
        startAnim();
    }

    /**
     *登陆界面背景动画开始
     */
    private void startAnim() {
        videoview.setVideoURI( Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.sport));

        //播放
        videoview.start();
        //循环播放
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.start();
            }
        });

        //焦点监听
        verify.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    verify_imageview.setVisibility(View.VISIBLE);
                }
                else
                {
                    verify_imageview.setVisibility( View.INVISIBLE );
                }
            }
        } );
    }

    /**
     * 登陆点击事件
     * @param v 自身View对象
     */
    public void logining(View v) {

        if(username.getText().toString().trim().equals("")||password.getText().toString().trim().equals("")
                ||verify.getText().toString().trim().equals(""))
        {
            Toast.makeText(LoginActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
        }
        else {
            //登陆操作
            loginStart();
        }
    }

    /**
     * 开始登陆入口
     */
    private void loginStart() {
        String usName,usPassword,code;
        usName=username.getText().toString();
        usPassword=password.getText().toString();
        code=verify.getText().toString();
        //密码加密
        usPassword += "swust_sport";
        usPassword = android.util.Base64.encodeToString(usPassword.getBytes(),
                android.util.Base64.DEFAULT);
        usPassword = usPassword.replaceAll("[\\s*\t\n\r]", "");
        HashMap<String, String> parema = new HashMap<>(  );
        parema.put( "usName",usName );
        parema.put( "usPassword",usPassword );
        parema.put( "code",code );
        LoginManage loginManage=new LoginManage();
        double status=loginManage.resultForLogin( parema,this );
        judgeStatus( status );
    }
    /**
     * Toast提示信息
     */
    private void judgeStatus(double status) {
        if (status == 1.0) {
            Toast.makeText( LoginActivity.this, "登陆成功 ", Toast.LENGTH_SHORT ).show();
            animation_start();

        } else if (status == 2.0) {
            Toast.makeText( LoginActivity.this, "用户不存在 ", Toast.LENGTH_SHORT ).show();

        } else if (status == 3.0) {
            Toast.makeText( LoginActivity.this, "密码错误 ", Toast.LENGTH_SHORT ).show();

        } else if (status == 4.0) {
            Toast.makeText( LoginActivity.this, "验证码错误 ", Toast.LENGTH_SHORT ).show();
            ;

        } else if (status == 5.0) {
            Toast.makeText( LoginActivity.this, "用户已被锁定 ", Toast.LENGTH_SHORT ).show();

        } else if (status == 0.0) {
            Toast.makeText( LoginActivity.this, "网络连接失败! ", Toast.LENGTH_SHORT ).show();
        }
    }

    /**
     * 登陆成功后 登陆动画开始
     */
    private void animation_start()
    {

        // 计算出控件的高与宽
        mWidth = mBthLogin.getMeasuredWidth();
        mHeight = mBthLogin.getMeasuredHeight();
        // 隐藏输入框
        mName.setVisibility(View.INVISIBLE);
        mpas.setVisibility(View.INVISIBLE);
        mver.setVisibility(View.INVISIBLE);
        inputAnimator(mInputLayout, mWidth, mHeight);
    }

    /**
     *输入框不可见 动画开始
     * @param view
     * @param w
     * @param h
     */
    private void inputAnimator(final View view, float w, float h) {
        AnimatorSet set=new AnimatorSet();
        final ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) animator.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });
        ObjectAnimator animator2=ObjectAnimator.ofFloat(mInputLayout,"scaleX",1f,0.5f);
        set.setDuration(500);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator,animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                //动画结束
                progress.setVisibility(view.VISIBLE);
                progressAnimator(progress);
                mInputLayout.setVisibility(View.INVISIBLE);
            }


            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    /**
     * 旋转进度条 动画开始
     * @param view
     */
    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
                animator, animator2);
        animator3.setDuration(1000);
        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();
        animator3.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                onStop();
                startActivity(intent);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    /**
     * 返回界面重新开始播放背景Video
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        init();
    }

    /**
     * 防止切屏或者退出 video一直播放
     */
    @Override
    protected void onStop() {
        super.onStop();
        videoview.stopPlayback();
    }

    /**
     * 开启注册界面
     * @param view
     */
    public void register(View view)
    {
        //得到返回值
        startActivityForResult(new Intent(LoginActivity.this,RegisterActivity.class),1);
    }

    /**
     * 得到注册界面返回的usName用户账户信息
     * @param requestCode 请求码
     * @param resultCode  返回码
     * @param data        返回数据
     */
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        String result=data.getExtras().getString("result");
        EditText usname=findViewById(R.id.input_username);
        usname.setText(result);
    }
    /**
     * 绘制验证码
     */
    private void drawverify(){
        Bitmap bitmap= Varify.stringToBitmap(imag_String);
        verify_imageview=findViewById(R.id.verify_image);
        verify_imageview.setImageBitmap(bitmap);
    }

    /**
     * 点击验证码 刷新验证
     * @param view
     */
    public void redraw(View view)
    {
        Varify.getVarify( this );
        imag_String = DataConstant.varifyCode;
        drawverify();
    }

}
