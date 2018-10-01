package com.example.liqingfeng.sscapp.View.Activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liqingfeng.sscapp.Model.Param;
import com.example.liqingfeng.sscapp.Model.ResponseModel;
import com.example.liqingfeng.sscapp.Presenter.CheckStatuss;
import com.example.liqingfeng.sscapp.Presenter.ImageManage.Varify;
import com.example.liqingfeng.sscapp.Presenter.UrlConfig;
import com.example.liqingfeng.sscapp.Presenter.UserConstant;
import com.example.liqingfeng.sscapp.Presenter.Util.AnimUtil.JellyInterpolator;
import com.example.liqingfeng.sscapp.Presenter.Util.FileUtil.FileManager;
import com.example.liqingfeng.sscapp.Presenter.Util.OkhttpUtil.RequestManager;
import com.example.liqingfeng.sscapp.R;
import com.example.liqingfeng.sscapp.View.CustomView.CustomVideoView;

import java.util.Map;


/**
 * 登陆界面
 */
public class LoginActivity extends Activity {
    private TextView mBthLogin;
    private View progress;
    private View mInputLayout;
    private float mWidth, mHeight;
    private LinearLayout mName, mpas, mver;
    private CustomVideoView videoview;
    private ImageView verify_imageview;
    private EditText username, password, verify;
    //验证码字符串
    public String imag_String = "";
    private double loginStatus;
    private String code;//访问验证是否成功
    Param parema = new Param();// 存放用户输入的账号,密码,验证码

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_login );

        init();
    }

    /**
     * 加载login界面所有控件
     */
    private void init() {
        mBthLogin = (TextView) findViewById( R.id.main_btn_login );
        progress = findViewById( R.id.layout_progress );
        mInputLayout = findViewById( R.id.input_layout );
        mName = (LinearLayout) findViewById( R.id.input_layout_name );
        mpas = (LinearLayout) findViewById( R.id.input_layout_psw );
        mver = (LinearLayout) findViewById( R.id.input_ver );
        //三个Edittext输入框 获取输入输出
        username = (EditText) findViewById( R.id.login_username );
        password = (EditText) findViewById( R.id.login_password );
        verify = (EditText) findViewById( R.id.login_verify );
        verify_imageview = (ImageView)findViewById( R.id.verify_image );
        videoview = (CustomVideoView) findViewById( R.id.videoView );

        //获取验证码字符串,并且显示出来
        getVarify();

        //焦点监听
        verify.setOnFocusChangeListener( new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    verify_imageview.setVisibility( View.VISIBLE );
                } else {
                    verify_imageview.setVisibility( View.INVISIBLE );
                }
            }
        } );

        startAnim();
    }

    /**
     * 登陆界面背景动画开始
     */
    private void startAnim() {
        videoview.setVideoURI( Uri.parse( "android.resource://" + getPackageName() + "/" + R.raw.sport ) );
        if(videoview == null) {
            Log.e( "video failed","播放视频出现问题" );
        }
        //播放
        videoview.start();
        //循环播放
        videoview.setOnCompletionListener( new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.start();
            }
        } );
    }

    /**
     * 登陆点击事件
     *
     * @param v 自身View对象
     */
    public void logining(View v) {

        if (username.getText().toString().trim().equals( "" ) || password.getText().toString().trim().equals( "" )
                || verify.getText().toString().trim().equals( "" )) {
            Toast.makeText( LoginActivity.this, "输入不能为空", Toast.LENGTH_SHORT ).show();
        } else {
            //登陆操作
            loginStart();
        }
    }

    private void getLoginerInformation() {
        String usName, usPassword, varify;
        usName = username.getText().toString();
        usPassword = password.getText().toString();
        varify = verify.getText().toString();
        //密码加密
        usPassword += "swust_sport";
        usPassword = android.util.Base64.encodeToString( usPassword.getBytes(),
                android.util.Base64.DEFAULT );
        usPassword = usPassword.replaceAll( "[\\s*\t\n\r]", "" );
        parema.append( "usName", usName );
        parema.append( "usPassword", usPassword );
        parema.append( "code", varify );
    }

    /**
     * 开始登陆入口
     */
    private void loginStart() {
        getLoginerInformation();
        RequestManager requestManager = RequestManager.getInstance( this );
        requestManager.requestAsyn( UrlConfig.loginUrl, RequestManager.TYPE_GET, parema.end(), false, new RequestManager.ReqCallBack<ResponseModel>() {
            @Override
            public void onReqSuccess(ResponseModel result) {
                code = result.getCode();
                if (code.equals( "success" )) {
                    Map<String, Object> data = (Map<String, Object>) result.getData();
                    //文件写入操作
                    saveUserInformation( result, UrlConfig.userInformation, getApplicationContext() );
                    loginStatus = (Double) data.get( "status" );
                    if (loginStatus == 1.0) {
                        UserConstant.tokenCode = result.getToken();
                    }
                    judgeStatus( loginStatus );
                } else if (code.equals( "JWT EXPIRE" )) {
                    judgeStatus( 6.0 );
                }
            }
            @Override
            public void onReqFailed(String errorMsg) {

            }
        } );
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
        } else if (status == 5.0) {
            Toast.makeText( LoginActivity.this, "用户已被锁定 ", Toast.LENGTH_SHORT ).show();
        } else if (status == 0.0) {
            Toast.makeText( LoginActivity.this, "网络连接失败! ", Toast.LENGTH_SHORT ).show();
        } else if (status == 6.0) {
            Toast.makeText( LoginActivity.this, "用户登陆已过期", Toast.LENGTH_SHORT ).show();
        }
    }

    /**
     * 登陆成功后 登陆动画开始
     */
    private void animation_start() {

        // 计算出控件的高与宽
        mWidth = mBthLogin.getMeasuredWidth();
        mHeight = mBthLogin.getMeasuredHeight();
        // 隐藏输入框
        mName.setVisibility( View.INVISIBLE );
        mpas.setVisibility( View.INVISIBLE );
        mver.setVisibility( View.INVISIBLE );
        inputAnimator( mInputLayout, mWidth, mHeight );
    }

    /**
     * 输入框不可见 动画开始
     *
     * @param view
     * @param w
     * @param h
     */
    private void inputAnimator(final View view, float w, float h) {
        AnimatorSet set = new AnimatorSet();
        final ValueAnimator animator = ValueAnimator.ofFloat( 0, w );
        animator.addUpdateListener( new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) animator.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams( params );
            }
        } );
        ObjectAnimator animator2 = ObjectAnimator.ofFloat( mInputLayout, "scaleX", 1f, 0.5f );
        set.setDuration( 500 );
        set.setInterpolator( new AccelerateDecelerateInterpolator() );
        set.playTogether( animator, animator2 );
        set.start();
        set.addListener( new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                //动画结束
                progress.setVisibility( view.VISIBLE );
                progressAnimator( progress );
                mInputLayout.setVisibility( View.INVISIBLE );
            }


            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        } );
    }

    /**
     * 旋转进度条 动画开始
     *
     * @param view
     */
    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat( "scaleX",
                0.5f, 1f );
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat( "scaleY",
                0.5f, 1f );
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder( view,
                animator, animator2 );
        animator3.setDuration( 1000 );
        animator3.setInterpolator( new JellyInterpolator() );
        animator3.start();
        animator3.addListener( new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Intent intent = new Intent( LoginActivity.this, MainActivity.class );
                onStop();
                startActivity( intent );
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        } );
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
     *
     * @param view
     */
    public void register(View view) {
        //得到返回值
        startActivityForResult( new Intent( LoginActivity.this, RegisterActivity.class ), 1 );
    }

    /**
     * 得到注册界面返回的usName用户账户信息
     *
     * @param requestCode 请求码
     * @param resultCode  返回码
     * @param data        返回数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String result = data.getExtras().getString( "result" );
        EditText usname = findViewById( R.id.login_password );
        usname.setText( result );
    }

    /**
     * 绘制验证码
     */
    private void drawverify(String varifyString) {
        Bitmap bitmap = Varify.stringToBitmap( varifyString );
        verify_imageview.setImageBitmap( bitmap );
    }

    /**
     * 点击验证码 刷新验证
     */
    public void redraw(View view) {
        getVarify();
    }

    /**
     * 将用户信息存入文件
     */
    private void saveUserInformation(ResponseModel content, String fileName, Context context) {
        FileManager fileManager = new FileManager( context, fileName );
        fileManager.saveUserInfomation( content );
    }

    /**
     * 获取验证码
     *
     * @return 返回验证码字符串
     */
    private void getVarify() {
        RequestManager requestManager = RequestManager.getInstance( this );
        requestManager.requestGetWithoutParam( UrlConfig.verifyUrl, false, new RequestManager.ReqCallBack<ResponseModel>() {
            @Override
            public void onReqSuccess(ResponseModel result) {
                //得到返回的varify字符串
                imag_String=(String) result.getData();
                drawverify( imag_String );
            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        } );
    }

    /**
     * 点击空白区域隐藏键盘.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
            View v = getCurrentFocus();      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (isShouldHideKeyboard(v, me)) { //判断用户点击的是否是输入框以外的区域
                hideKeyboard(v.getWindowToken());   //收起键盘
            }
        }
        return super.dispatchTouchEvent(me);
    }
    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {  //判断得到的焦点控件是否包含EditText
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],    //得到输入框在屏幕中上下左右的位置
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击位置如果是EditText的区域，忽略它，不收起键盘。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略
        return false;
    }
    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
