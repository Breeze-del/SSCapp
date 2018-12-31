package com.example.liqingfeng.sscapp.View.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.liqingfeng.sscapp.Model.Entity.Param;
import com.example.liqingfeng.sscapp.Model.Entity.ResponseModel;
import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Model.UserConstant;
import com.example.liqingfeng.sscapp.Presenter.Util.OkhttpUtil.RequestManager;
import com.example.liqingfeng.sscapp.R;

/**
 *  展示签到和运动记录
 */
public class MysignActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mysign);
        getdata();
    }

    /**
     * 获取数据
     */
    private void getdata() {
        RequestManager requestManager =  RequestManager.getInstance(this);
        ResponseModel result =requestManager.requestGetBySyn(UrlConfig.sportsLogUrl, new Param().append("userId", UserConstant.uesrID+"").end(),
                true);
        System.out.println(result);
    }
}
