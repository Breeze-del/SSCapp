package com.example.liqingfeng.sscapp.View.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;

import android.support.annotation.Nullable;

import android.view.View;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liqingfeng.sscapp.Model.Entity.Param;
import com.example.liqingfeng.sscapp.Model.Entity.ResponseModel;
import com.example.liqingfeng.sscapp.Model.Entity.Users;
import com.example.liqingfeng.sscapp.Model.UrlConfig;

import com.example.liqingfeng.sscapp.Model.UserConstant;
import com.example.liqingfeng.sscapp.Presenter.CheckStatuss;
import com.example.liqingfeng.sscapp.Presenter.Util.ConvertUtil.DataConvertUtil;
import com.example.liqingfeng.sscapp.Presenter.Util.ImageUtil.FastBlurUtils;
import com.example.liqingfeng.sscapp.Presenter.Util.ImageUtil.ImageLoaderUtil;
import com.example.liqingfeng.sscapp.Presenter.Util.OkhttpUtil.RequestManager;
import com.example.liqingfeng.sscapp.R;
import com.example.liqingfeng.sscapp.View.Activity.MainActivity;



public class FriendInfoFragment extends Activity {

    private  String ID;
    private ImageView mBackP;
    private ImageView mHeadP;
    private Bitmap headPicture;
    private TextView musName;
    private TextView musNickName;
    private TextView musSex;
    private TextView musClass;
    private TextView musSign;
    private TextView musAge;
    private TextView musInstitution;
    private String imageUrl;
    private ImageButton backRoom;
    private Users userinfo;

    Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg) {
            if(msg.what==0x123)
            {
                // 接受到消息说明已经完成了信息和头像得获取
                show();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_friendinfo);
        initView();
        loadFragment();
    }

    private void loadFragment() {
        getInfo();
    }

    private void initView() {
        mBackP =(ImageView) findViewById(R.id.friend_h_back);
        mHeadP = (ImageView) findViewById(R.id.friend_h_head);
        musNickName=(TextView) findViewById(R.id.friend_info_nickname);
        musName=(TextView) findViewById(R.id.friend_username);
        musSex=(TextView) findViewById(R.id.friend_sex);
        musClass=(TextView) findViewById(R.id.friend_classid);
        musSign=(TextView) findViewById(R.id.friend_sign);
        musAge=(TextView) findViewById(R.id.friend_userAge);
        musInstitution=(TextView) findViewById(R.id.friend_Institution);
        backRoom = (ImageButton) findViewById(R.id.friend_back_login);
        userinfo = new Users();
        ID = UserConstant.friendID;
        backRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backTO();
            }
        });
    }

    private void getInfo() {
        RequestManager requestManager = RequestManager.getInstance(this);
        requestManager.requestAsyn(UrlConfig.findUserByID, RequestManager.TYPE_GET,
                new Param("userId",ID).end(),
                true, new RequestManager.ReqCallBack<ResponseModel>() {
                    @Override
                    public void onReqSuccess(ResponseModel result) {
                        if (CheckStatuss.CheckStatus(result, getApplicationContext()) == 1) {
                            String status = result.getFromData("usStatus") + "";
                            if (status.equals("1.0")) {
                                // 读取用户信息
                                loadUser(result);
                            } else {
                                Toast.makeText(getApplicationContext(), "用户已被锁定,无法查看信息", Toast.LENGTH_SHORT).show();
                                backTO();
                            }
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                    }
                });
    }

    private void loadUser(ResponseModel result) {
        imageUrl = (String) result.getFromData("usImg");
        new Thread(new Runnable() {
            @Override
            public void run() {
                ImageLoaderUtil imageLoaderUtil = ImageLoaderUtil.getInstance(getApplication());
                headPicture=imageLoaderUtil.loadImageBySyn(UrlConfig.imageBaseUrl+imageUrl);
                handler.sendEmptyMessage(0x123);
            }
        }).start();
        userinfo.setUsName((String) result.getFromData("usName"));
        userinfo.setUsNickname((String) result.getFromData("usNickname"));
        userinfo.setUsClass((String) result.getFromData("usClass"));
        int sex = DataConvertUtil.toInt( result.getFromData("usSex"));
        if (sex == 1) {
            userinfo.setUsSex("男");
        } else if (sex == 2) {
            userinfo.setUsSex("女");
        } else {
            userinfo.setUsSex("null");
        }
        userinfo.setUsSign((String) result.getFromData("usSign"));
        userinfo.setUsAge(DataConvertUtil.doubleToString( (double)result.getFromData("usAge")));
        userinfo.setUsInstitution((String) result.getFromData("usInstitution"));
    }

    private void show(){
        mHeadP.setImageBitmap(headPicture);
        Bitmap or = headPicture;
        or = FastBlurUtils.doBlur(or,20,false);
        mBackP.setImageBitmap(or);
        musNickName.setText(userinfo.getUsName());
        musName.setText(userinfo.getUsNickname());
        musSex.setText(userinfo.getUsSex());
        musClass.setText(userinfo.getUsClass());
        musSign.setText(userinfo.getUsSign());
        musAge.setText(userinfo.getUsAge());
        musInstitution.setText(userinfo.getUsInstitution());
    }
    /**
     * 返回到房间中去
     */
    private void backTO(){
//        FragmentManager fm=getFragmentManager();
//        Fragment fragment=new ChatRoomFragment();
//        fm.beginTransaction().replace( R.id.main_content,fragment ).commit();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
