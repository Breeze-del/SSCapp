package com.example.liqingfeng.sscapp.View.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.liqingfeng.sscapp.Presenter.UrlConfig;
import com.example.liqingfeng.sscapp.Presenter.Util.FileUtil.FileManager;
import com.example.liqingfeng.sscapp.R;

public class RegisterActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );
    }
}
