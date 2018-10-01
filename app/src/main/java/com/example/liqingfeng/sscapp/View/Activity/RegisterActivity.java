package com.example.liqingfeng.sscapp.View.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.example.liqingfeng.sscapp.Presenter.UrlConfig;
import com.example.liqingfeng.sscapp.Presenter.Util.FileUtil.FileManager;
import com.example.liqingfeng.sscapp.R;

public class RegisterActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );
        TextView textView=(TextView)findViewById( R.id.tempShow );
        FileManager fileManager= new FileManager( this, UrlConfig.userInformation );
        String content;
        content=fileManager.readFileData();
        textView.setText( content );
    }
}
