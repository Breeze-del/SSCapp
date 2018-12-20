package com.example.liqingfeng.sscapp.View.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.liqingfeng.sscapp.Model.Entity.Param;
import com.example.liqingfeng.sscapp.Model.Entity.ResponseModel;
import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Model.UserConstant;
import com.example.liqingfeng.sscapp.Presenter.Adapter.SpRoomAdapter;
import com.example.liqingfeng.sscapp.Presenter.Util.OkhttpUtil.RequestManager;
import com.example.liqingfeng.sscapp.View.CustomView.RefreshView;
import com.example.liqingfeng.sscapp.R;

import java.util.List;
import java.util.Map;

public class RoomListFragment extends Fragment {
    public ListView listView;
    private View view;
    private RefreshView refreshableView;
    private SpRoomAdapter roomAdapter;
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
        view = inflater.inflate(R.layout.fragment_roomlist,null);
        initList();
        initView();
        refeshData();
        //ListView的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "第" + i + "行", Toast.LENGTH_LONG).show();
            }
        });

        //点击事件 创建
        roomAdapter.setItemcomroomListener(new SpRoomAdapter.onItemcomroomListener() {
            @Override
            public void oncomroomClick(int i) {
                FragmentManager fm=getActivity().getFragmentManager();
                Fragment fragment=new ChatRoomFragment();
                fm.beginTransaction().replace( R.id.main_content,fragment ).commit();
            }
        });
        return view;
    }
    private void initList() {
        if(UserConstant.list_room ==null)
        {
            Log.d("失败", " 全局变量list_map 为空了  需要检查或者重新获取list_map");
        }
    }
    private void initView() {
        refreshableView = (RefreshView) view.findViewById(R.id.refreshableView2);
        roomAdapter=new SpRoomAdapter(getActivity());
        listView=(ListView)view.findViewById(R.id.room_listview);
        listView.setAdapter(roomAdapter);
        refreshableView.setRefreshEnabled(true);
    }
    private void refeshData() {
        refreshableView.setRefreshListener(new RefreshView.RefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String url=UrlConfig.SproomUrl+"?roSportname="+UserConstant.room_Sport_name;
                        RequestManager requestManager =RequestManager.getInstance(getActivity());
                        requestManager.requestGetWithoutParam( url, true,
                                new RequestManager.ReqCallBack<ResponseModel>() {
                                    @Override
                                    public void onReqSuccess(ResponseModel result) {
                                        UserConstant.list_room=(List<Map<String, Object>>)result.getData();
                                        roomAdapter = new SpRoomAdapter(getActivity());
                                        listView.setAdapter(roomAdapter);
                                        handler.sendEmptyMessage(SUCCESS);
                                    }

                                    @Override
                                    public void onReqFailed(String errorMsg) {

                                    }
                                });
                    }
                },1000);
            }
        });
    }
}
