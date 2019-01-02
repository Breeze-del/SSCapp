package com.example.liqingfeng.sscapp.View.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.liqingfeng.sscapp.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutUsActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        initViews();
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.team)//图片
                .setDescription("道理我都懂，可我就是不听啊")//介绍
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("与我联系")
                .addEmail("562916584@qq.com")//邮箱
                .addWebsite("http://wangzhengyu.cn/sports_back")//网站
                .addGitHub("https://github.com/562916584")//github
                .create();

        relativeLayout.addView(aboutPage);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            default:

        }
        return true;
    }

    private void initViews(){
        relativeLayout= (RelativeLayout) findViewById(R.id.relativeLayout);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
