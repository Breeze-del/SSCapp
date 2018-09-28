package com.example.liqingfeng.sscapp.Presenter.BusinessManage;

import android.content.Context;
import android.util.Log;

import com.example.liqingfeng.sscapp.Model.ResponseModel;
import com.example.liqingfeng.sscapp.Presenter.DataConstant;
import com.example.liqingfeng.sscapp.Presenter.UrlConfig;
import com.example.liqingfeng.sscapp.Presenter.UserConstant;
import com.example.liqingfeng.sscapp.Util.OkhttpUtil.RequestManager;

import java.util.HashMap;
import java.util.Map;


public class LoginManage {
    private HashMap<String, String> parems =new HashMap<>(  );

    public double resultForLogin(HashMap<String, String> parems, Context context) {
        final double[] status = new double[1];
        RequestManager requestManager = RequestManager.getInstance( context );
        requestManager.requestAsyn( UrlConfig.loginUrl, 0, parems, new RequestManager.ReqCallBack<ResponseModel>() {
            @Override
            public void onReqSuccess(ResponseModel result) {
                Map<String,Object> map=(Map<String,Object>)result.getData();
                UserConstant.tokenCode=(String) map.get( "token" );
                status[0] = (double) map.get( "status" );
            }

            @Override
            public void onReqFailed(String errorMsg) {
                Log.e("loginFailed",errorMsg);
            }
        } );
        return status[0];
    }
}
