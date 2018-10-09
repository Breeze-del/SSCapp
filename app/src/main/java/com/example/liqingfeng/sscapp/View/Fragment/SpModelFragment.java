package com.example.liqingfeng.sscapp.View.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.liqingfeng.sscapp.Model.Entity.ResponseModel;
import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Model.UserConstant;
import com.example.liqingfeng.sscapp.Presenter.Adapter.SpModelAdapter;
import com.example.liqingfeng.sscapp.Presenter.CheckStatuss;
import com.example.liqingfeng.sscapp.Presenter.Util.ImageUtil.ImageUtil;
import com.example.liqingfeng.sscapp.Presenter.Util.OkhttpUtil.RequestManager;
import com.example.liqingfeng.sscapp.R;
import com.example.liqingfeng.sscapp.View.Activity.LoginActivity;
import com.example.liqingfeng.sscapp.View.CustomView.RefreshView;

import java.util.List;
import java.util.Map;

/**
 * 运动模块Fragment
 */
public class SpModelFragment extends Fragment implements SpModelAdapter.InnerItemOnclickListener {
    //控制点击次数 200代表表大范围为200个运动模块
    public int[] times=new int[200];
    private ListView listView;
    private View view;
    private RefreshView refreshableView;
    private SpModelAdapter sportsAdapter;
    final int SUCCESS = 1;
    final int FAILED = 0;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    refreshableView.finishRefresh(true);
                    break;
                case FAILED:
                    refreshableView.finishRefresh(false);
                    break;
                default:
                    break;
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=LayoutInflater.from(getActivity()).inflate( R.layout.fragment_spmodel,null);
        judegeList();
        initView();
        refreshdata();

        //ListView item的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Button join_bt=view.findViewById( R.id.join_bt );
                Button sta_bt=view.findViewById( R.id.stablish_bt );
                times[i]++;
                if(times[i]<2)
                {
                    join_bt.setVisibility( view.VISIBLE );
                    sta_bt.setVisibility( view.VISIBLE );
                }
                if(times[i]==2)
                {
                    join_bt.setVisibility( view.INVISIBLE );
                    sta_bt.setVisibility( view.INVISIBLE );
                    times[i]=0;
                }
                //Toast.makeText(getActivity(), "Click item" + i, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    /**
     * 判断全局的运动模块List是否为空
     */
    private void judegeList() {
        for(int i=0;i<200;i++)
        {
            times[i]=0;
        }
        if(UserConstant.list_sport==null)
        {
            Log.d("失败", " 全局变量list_map 为空了  需要检查或者重新获取list_map");
        }
    }

    /**
     * 初化始界面控件
     */
    private void initView() {
        refreshableView = (RefreshView) view.findViewById(R.id.refreshableView1);
        this.listView = (ListView) view.findViewById(R.id.spmodel_listview);
        sportsAdapter = new SpModelAdapter(getActivity());
        sportsAdapter.setOnInnerItemOnClickListener( this );
        listView.setAdapter(sportsAdapter);
        refreshableView.setRefreshEnabled(true);
    }

    /**
     * 动态刷新Listview
     * 刷新事件1s
     */
    private void refreshdata() {
        refreshableView.setRefreshListener( new RefreshView.RefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed( new Runnable() {
                    @Override
                    public void run() {
                        RequestManager requestManager =RequestManager.getInstance( getActivity() );
                        requestManager.requestGetWithoutParam( UrlConfig.SpmodleUrl, true,
                                new RequestManager.ReqCallBack<ResponseModel>() {
                            @Override
                            public void onReqSuccess(ResponseModel result) {
                                if(CheckStatuss.CheckStatus( result,getActivity() )==1) {
                                    UserConstant.list_sport=(List<Map<String,String>>) result.getData();
                                    ImageUtil.Image_down(UserConstant.list_sport);
                                    sportsAdapter.postion=-1;
                                    sportsAdapter.notifyDataSetChanged();
//                                    sportsAdapter = new SpModelAdapter(getActivity());
//                                    listView.setAdapter(sportsAdapter);
                                      handler.sendEmptyMessage(SUCCESS);
                                } else if (CheckStatuss.CheckStatus( result,getActivity() )==2) {
                                    Toast.makeText( getActivity(),"网络出现错误",Toast.LENGTH_SHORT ).show();
                                } else {
                                    Intent intent = new Intent( getActivity(), LoginActivity.class );
                                    startActivity( intent );
                                    getActivity().finish();
                                }
                            }

                            @Override
                            public void onReqFailed(String errorMsg) {

                            }
                        } );
                    }
                },1000 );
            }
        } );
    }
    private void requestRooms(String url) {
        RequestManager requestManager =RequestManager.getInstance( getActivity() );
        requestManager.requestGetWithoutParam( url, true,
                new RequestManager.ReqCallBack<ResponseModel>() {
            @Override
            public void onReqSuccess(ResponseModel result){
                if(CheckStatuss.CheckStatus( result,getActivity() )==1) {
                    UserConstant.list_room=(List<Map<String,Object>>) result.getData();
                    //打开新的Fragment
                    FragmentManager fm=getActivity().getFragmentManager();
                    Fragment fragment=new RoomListFragment();
                    fm.beginTransaction().replace( R.id.main_content,fragment ).commit();
                } else if (CheckStatuss.CheckStatus( result,getActivity() )==2) {
                    Toast.makeText( getActivity(),"网络出现错误",Toast.LENGTH_SHORT ).show();
                } else {
                    Intent intent = new Intent( getActivity(), LoginActivity.class );
                    startActivity( intent );
                    getActivity().finish();
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        } );
    }

    @Override
    public void itemClick(View v) {
        int pos;
        pos=(Integer)v.getTag();
        switch (v.getId()) {
            case R.id.join_bt:
                UserConstant.room_Sport_name=UserConstant.list_sport.get( pos ).get( "spName" );
                String url=UrlConfig.SproomUrl+"?roSportname="+UserConstant.room_Sport_name;
                UserConstant.room_image_path=UserConstant.list_sport.get( pos ).get( "spRoimg" );
                requestRooms( url );
            case R.id.stablish_bt:
        }
    }
}
