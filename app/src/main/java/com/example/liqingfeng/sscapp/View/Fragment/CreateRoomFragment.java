package com.example.liqingfeng.sscapp.View.Fragment;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import java.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.liqingfeng.sscapp.Model.Entity.Param;
import com.example.liqingfeng.sscapp.Model.Entity.ResponseModel;
import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Model.UserConstant;
import com.example.liqingfeng.sscapp.Presenter.CheckStatuss;
import com.example.liqingfeng.sscapp.Presenter.Util.OkhttpUtil.RequestManager;
import com.example.liqingfeng.sscapp.R;
import com.example.liqingfeng.sscapp.View.CustomView.ItemGroup;
import com.nostra13.universalimageloader.utils.L;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class CreateRoomFragment extends Fragment implements ItemGroup.ItemOnClickListener  {

    private View view;
    private ItemGroup mlocation;
    private ItemGroup mmaxNum;
    private ItemGroup mstartTime;
    private ItemGroup mendTime;
    private ItemGroup mSportNname;
    private Button okcreate;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String mHour;
    private String mMinute;
    private String time;
    private boolean isStart;
    private boolean isBack = true;

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            String days;
            if (mMonth + 1 < 10) {
                if (mDay < 10) {
                    days = new StringBuffer().append(mYear).append("-").append("0").
                            append(mMonth + 1).append("-").append("0").append(mDay).toString();
                } else {
                    days = new StringBuffer().append(mYear).append("-").append("0").
                            append(mMonth + 1).append("-").append(mDay).toString();
                }

            } else {
                if (mDay < 10) {
                    days = new StringBuffer().append(mYear).append("-").
                            append(mMonth + 1).append("-").append("0").append(mDay).toString();
                } else {
                    days = new StringBuffer().append(mYear).append("-").
                            append(mMonth + 1).append("-").append(mDay).toString();
                }

            }
            time = days;
            setTime();
        }
    };
    private TimePickerDialog.OnTimeSetListener onTimeSetListener= new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            String days;
            if(hourOfDay<24){
                mHour=""+hourOfDay;
            }
            if(minute<24){
                mMinute=""+minute;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=LayoutInflater.from(getActivity()).inflate( R.layout.fragment_createroom,null);
        initView();
        // 获取时间
        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        return view;

    }

    private void initView() {
        mlocation = (ItemGroup) view.findViewById(R.id.create_location);
        mmaxNum = (ItemGroup) view.findViewById(R.id.create_maxnum);
        mstartTime = (ItemGroup) view.findViewById(R.id.create_startTime);
        mendTime = (ItemGroup) view.findViewById(R.id.create_endTime);
        mSportNname = (ItemGroup) view.findViewById(R.id.create_sportName);
        okcreate = (Button) view.findViewById(R.id.create_okcreate);
        okcreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getInfo();
                    // 返回运动模块界面
                    if(isBack) {
                        FragmentManager fm=getActivity().getFragmentManager();
                        Fragment fragment=new SpModelFragment();
                        fm.beginTransaction().replace( R.id.main_content,fragment ).commit();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        mSportNname.setText(UserConstant.room_Sport_name);
        mstartTime.setItemOnClickListener(this);
        mendTime.setItemOnClickListener(this);
    }

    @Override
    public void onItemClick(View v) {
        switch (v.getId()) {
            case R.id.create_startTime:
                new DatePickerDialog(getActivity(), onDateSetListener, mYear, mMonth, mDay).show();
                new TimePickerDialog(getActivity(),onTimeSetListener,0,0,true).show();
                isStart = true;
                break;
            case R.id.create_endTime:
                new DatePickerDialog(getActivity(), onDateSetListener, mYear, mMonth, mDay).show();
                new TimePickerDialog(getActivity(),onTimeSetListener,0,0,true).show();
                isStart = false;
                break;
        }
    }

    /**
     *  获取输入的信息
     */
    private void getInfo() throws ParseException {
        String location = mlocation.getText();
        String maxNum = mmaxNum.getText();
        String trim=Pattern.compile("[0-9]*").matcher(maxNum).replaceAll("").trim();
        if(!trim.equals("")){
            Toast.makeText(getActivity(),"人数只能为数字",Toast.LENGTH_SHORT).show();
            isBack=false;
            return;

        } else {
            isBack = true;
        }
        if(location.equals("")||maxNum.equals("")||mstartTime.getText().equals("")||mendTime.getText().equals("")) {
            Toast.makeText(getActivity(),"输入框不能为空",Toast.LENGTH_SHORT).show();
            isBack = false;
            return;
        }
        long start = aTod(mstartTime.getText());
        long end = aTod(mendTime.getText());
        RequestManager requestManager = RequestManager.getInstance(getActivity());
        requestManager.requestAsyn(UrlConfig.createRoomUrl, RequestManager.TYPE_POST_JSON,
                new Param().append("roSportname", UserConstant.room_Sport_name).append("roStart", start+"")
                        .append("roEnd", end+"").append("roLocation", location).append("roOrinum", maxNum).end(), true,
                new RequestManager.ReqCallBack<ResponseModel>() {
                    @Override
                    public void onReqSuccess(ResponseModel result) {
                        if(CheckStatuss.CheckStatus(result, getActivity()) != 1) {

                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {

                    }
                });
    }

    private void setTime() {
        if(isStart) {
            mstartTime.setText(time+" "+mHour+":"+mMinute);
        } else {
            mendTime.setText(time+" "+mHour+":"+mMinute);
        }
    }

    private long aTod(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        long t;
        t=sdf.parse(time).getTime();
        return t;
    }
}
