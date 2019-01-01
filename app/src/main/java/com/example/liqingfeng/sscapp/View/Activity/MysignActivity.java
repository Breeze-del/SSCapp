package com.example.liqingfeng.sscapp.View.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liqingfeng.sscapp.Model.Entity.Param;
import com.example.liqingfeng.sscapp.Model.Entity.ResponseModel;
import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Model.UserConstant;
import com.example.liqingfeng.sscapp.Presenter.CheckStatuss;
import com.example.liqingfeng.sscapp.Presenter.Util.ConvertUtil.DataConvertUtil;
import com.example.liqingfeng.sscapp.Presenter.Util.ImageUtil.ImageUtil;
import com.example.liqingfeng.sscapp.Presenter.Util.OkhttpUtil.RequestManager;
import com.example.liqingfeng.sscapp.R;

import java.util.List;
import java.util.Map;

/**
 *  展示签到和运动记录
 */
public class MysignActivity extends Activity {

    private TextView mspoetjilu;
    private TextView msignsport;
    private ResponseModel result;

    Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg) {
            if(msg.what==0x0)
            {
                // 接受到消息说明已经完成了信息和头像得获取
                showSign(result);
                showSprotlog(result);
            }
        };
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mysign);
        initView();
        getdata();
    }


    private void initView() {
        mspoetjilu = (TextView) findViewById(R.id.sportLog);
        msignsport = (TextView) findViewById(R.id.signLog);
    }

    /**
     * 获取数据
     */
    private void getdata() {
        RequestManager requestManager =  RequestManager.getInstance(this);
        requestManager.requestAsyn(UrlConfig.sportsLogUrl, RequestManager.TYPE_GET, new Param().append("userId", UserConstant.uesrID + "").end(),
                true, new RequestManager.ReqCallBack<ResponseModel>() {
                    @Override
                    public void onReqSuccess(ResponseModel yxy) {
                        result = yxy;
                        handler.sendEmptyMessage(0x0);
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {

                    }
                });
    }

    private void showSign(ResponseModel result) {
        StringBuilder builder = new StringBuilder();
        List<Map<String, Object>> sigs= (List<Map<String, Object>>)result.getFromData("signs");
        for(Map<String, Object> map : sigs) {
            builder.append(DataConvertUtil.Time( (double)map.get( "siSigndate" ))).append("            √\n");
        }
        msignsport.setText(builder.toString());
    }

    private void showSprotlog(ResponseModel result) {
        StringBuilder builder = new StringBuilder();
        List<Map<String, Object>> sigs= (List<Map<String, Object>>)result.getFromData("sports");
        for(Map<String, Object> map : sigs) {
            String status;
            if( map.get("spType").toString().equals("1.0")){
                status = "发起了";
            } else {
                status = "跟起了";
            }
            builder.append(DataConvertUtil.Time( (double)map.get( "spSportdate" ))).append("  "+status)
            .append("  "+map.get("spName")+"\n");
        }
        mspoetjilu.setText(builder.toString());
    }

    public void backFrommysin(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     *  签到
     * @param view
     */
    public void signDD(View view) {
        RequestManager requestManager = RequestManager.getInstance(this);
        requestManager.requestAsyn(UrlConfig.signUrl, RequestManager.TYPE_POST_JSON, new Param().append("asd","asd").end(), true,
                new RequestManager.ReqCallBack<ResponseModel>() {
                    @Override
                    public void onReqSuccess(ResponseModel result) {
                        if(CheckStatuss.CheckStatus(result,getApplicationContext()) == 1){
                            if(!(boolean)result.getData()) {
                                Toast.makeText(getApplicationContext(),"您今天已签到",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(),"签到成功",Toast.LENGTH_SHORT).show();
                                getdata();
                            }
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {

                    }
                });
    }

    /**
     * 分享
     */
    public void Fenxiang(View view) {
        Bitmap bitmap=captureScreen(this);
        ImageUtil.saveImageToGallery(this,bitmap);
        Toast.makeText(this,"截图成功，保存相册成功",Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NewApi")
    private Bitmap captureScreen(Activity context) {
        View cv = context.getWindow().getDecorView();
        cv.setDrawingCacheEnabled(true);
        cv.buildDrawingCache();
        Bitmap bmp = cv.getDrawingCache();
        if (bmp == null) {
            return null;
        }
        bmp.setHasAlpha(false);
        bmp.prepareToDraw();
        return bmp;
    }
}
