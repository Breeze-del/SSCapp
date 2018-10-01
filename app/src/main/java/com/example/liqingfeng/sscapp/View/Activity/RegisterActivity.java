package com.example.liqingfeng.sscapp.View.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.example.liqingfeng.sscapp.Model.ResponseModel;
import com.example.liqingfeng.sscapp.Presenter.DataConstant;
import com.example.liqingfeng.sscapp.Presenter.UrlConfig;
import com.example.liqingfeng.sscapp.Presenter.Util.FileUtil.FileManager;
import com.example.liqingfeng.sscapp.R;
import com.google.gson.Gson;

public class RegisterActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );
        TextView textView=(TextView)findViewById( R.id.tempShow );
        FileManager fileManager= new FileManager( this, UrlConfig.userInformation );
        String content;
        content=fileManager.readFileData();
        Gson gson=DataConstant.gson;
        ResponseModel responseModel=gson.fromJson( content,ResponseModel.class );
        textView.setText( responseModel.getToken() );
    }
}
