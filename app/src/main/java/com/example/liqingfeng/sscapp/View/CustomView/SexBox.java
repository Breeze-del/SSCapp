package com.example.liqingfeng.sscapp.View.CustomView;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.liqingfeng.sscapp.R;

/**
 *性别选择器
 * Created by cycycd on 2017/10/25.
 */

public class SexBox extends LinearLayout {
    private boolean man_selected=false;
    private boolean woman_selected=false;
    ImageView manicon,womanicon;
    TextView mantext,womantext;
    public SexBox(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.sex_select,this);

        manicon=(ImageView) findViewById(R.id.man_ic);
        womanicon=(ImageView) findViewById(R.id.woman_ic);
        mantext=(TextView) findViewById(R.id.man_tx);
        womantext=(TextView) findViewById(R.id.woman_tx);

        manicon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (getstatu())
                {
                    case 0:
                    case 2:
                        turnToMan();
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
            }
        });
        womanicon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (getstatu())
                {
                    case 0:
                    case 1:
                        turnToWoman();
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }
            }
        });
    }
    public void turnToWoman()
    {
        manicon.setImageResource(R.drawable.ic_man);
        mantext.setTextColor(Color.rgb(195,195,195));
        womanicon.setImageResource(R.drawable.ic_woman_x);
        womantext.setTextColor(Color.rgb(68,68,68));
        man_selected=false;
        woman_selected=true;
    }
    public void turnToMan()
    {
        manicon.setImageResource(R.drawable.ic_man_x);
        mantext.setTextColor(Color.rgb(68,68,68));
        womanicon.setImageResource(R.drawable.ic_woman);
        womantext.setTextColor(Color.rgb(195,195,195));
        man_selected=true;
        woman_selected=false;
    }
    //返回当前选择状态
    public int getstatu()
    {
        //均未选择
        if (!man_selected&&!woman_selected)
        {
            return 0;
        }
        else if(man_selected&&!woman_selected)   //选男
        {
            return 1;
        }
        else if(!man_selected&&woman_selected)  //选女
        {
            return 2;
        }
        else    //均选择 不可能发生
        {
            return -1;
        }
    }
}
