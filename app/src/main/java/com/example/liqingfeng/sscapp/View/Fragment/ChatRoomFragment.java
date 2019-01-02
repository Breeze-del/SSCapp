package com.example.liqingfeng.sscapp.View.Fragment;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.liqingfeng.sscapp.Model.Entity.Param;
import com.example.liqingfeng.sscapp.Model.Entity.PersonsChat;
import com.example.liqingfeng.sscapp.Model.Entity.ResponseModel;
import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Model.UserConstant;
import com.example.liqingfeng.sscapp.Presenter.Adapter.NewChatroomAdapter;
import com.example.liqingfeng.sscapp.Presenter.CheckStatuss;
import com.example.liqingfeng.sscapp.Presenter.Util.OkhttpUtil.RequestManager;
import com.example.liqingfeng.sscapp.R;
import com.example.liqingfeng.sscapp.View.Activity.LoginActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private NewChatroomAdapter msgAdapter;
    //WebSocket 创建
    private WebSocketClient mSocketClient;
    private Button btn_chat_message_send;
    private Button back;
    private EditText et_chat_message;

    //实体消息集合
    private List<PersonsChat> personsChats =new ArrayList<PersonsChat>(  );

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate( R.layout.newchatroom_fragment, null );
        initView();
        init();


        /**
         * 发送按钮的点击事件
         */
        btn_chat_message_send.setOnClickListener( new View.OnClickListener() {

            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (TextUtils.isEmpty( et_chat_message.getText().toString() )) {
                    Toast.makeText( getActivity(), "发送内容不能为空", 0 ).show();
                    return;
                }
                if (mSocketClient != null) {
                    mSocketClient.send(et_chat_message.getText().toString().trim());
                }
                PersonsChat personChat = new PersonsChat();
                //代表自己发送
                personChat.setMeSend( true );
                personChat.setImgUrl(UserConstant.user_head_picture);
                //得到发送内容
                personChat.setChatMessage( et_chat_message.getText().toString() );
                //加入集合
                personsChats.add( personChat );
                //清空输入框
                et_chat_message.setText( "" );
                //要求适配器重新刷新
                msgAdapter.notifyItemInserted(personsChats.size()-1);
                //要求recyclerView布局将消息刷新
                recyclerView.scrollToPosition(personsChats.size()-1);
            }
        } );
        backToRoom();
        return view;
    }

    private void initView() {
        recyclerView = (RecyclerView) view.findViewById( R.id.chatroomRecyclerView );
        btn_chat_message_send = (Button) view.findViewById( R.id.send );
        back = (Button) view.findViewById(R.id.back);
        et_chat_message = (EditText) view.findViewById(R.id.enter);
        // 布局排列方式
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        msgAdapter = new NewChatroomAdapter(personsChats, getActivity());
        recyclerView.setAdapter(msgAdapter);

    }


    private void init() {
        try {
            if(mSocketClient != null) {
                return;
            }
            mSocketClient = new WebSocketClient(new URI(UrlConfig.chatRoomUrl+"/"+UserConstant.roomID+"/"+UserConstant.tokenCode
            ), new Draft_6455(  )) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d("picher_log","打开通道"+handshakedata.getHttpStatus());
                }

                @Override
                public void onMessage(String message) {
                    if(message.equals("USER_LOCK")) {
                        Toast.makeText(getActivity(),"你的账号已被锁定",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        onDestroy();
                    }
                    Log.d( "picher_log", "收到消息"+message );
                    if (message.contains("!@#msg!@#history")) {
                        String[] strs = message.split("!@#msg!@#history");
                        for (String str: strs) {
                            if(str.equals("")) {
                                continue;
                            }
                            String[] mess = str.split("!@#msg!@#");
                            String date = mess[0];
                            String id = mess[1];
                            String nickName = mess[2];
                            String img = mess[3];
                            String msg = mess[4];
                            PersonsChat personsChat=new PersonsChat(  );
                            if( id.equals(UserConstant.uesrID+"")) {
                                personsChat.setMeSend(true);
                            } else {
                                personsChat.setMeSend(false);
                            }
                            personsChat.setName(nickName);
                            personsChat.setId(id);
                            personsChat.setImgUrl(img);
                            personsChat.setTime(date);
                            personsChat.setChatMessage( msg );
                            personsChats.add( personsChat );
                        }
                    } else {
                        String[] mess = message.split("!@#msg!@#");
                        String date = mess[0];
                        String id = mess[1];
                        String nickName = mess[2];
                        String img = mess[3];
                        String msg = mess[4];
                    //将收到的信息加入到消息实体列表中
                        PersonsChat personsChat=new PersonsChat(  );
                        if( id.equals(UserConstant.uesrID+"")) {
                            personsChat.setMeSend(true);
                        } else {
                            personsChat.setMeSend(false);
                        }
                        personsChat.setName(nickName);
                        personsChat.setTime(date);
                        personsChat.setId(id);
                        personsChat.setImgUrl(img);
                        personsChat.setChatMessage( msg );
                        personsChats.add( personsChat );
                     }
                    //要求适配器重新刷新
                    msgAdapter.notifyItemInserted(personsChats.size()-1);
                    //要求recyclerView布局将消息刷新
                    recyclerView.scrollToPosition(personsChats.size()-1);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d( "picher_log", "通信关闭" + reason );
                }

                @Override
                public void onError(Exception ex) {
                    Log.d( "picher_log", "通信失败" );
                }
            };
            mSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocketClient != null) {
            mSocketClient.close();
        }
    }

    private void backToRoom() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterRoom();
                FragmentManager fm=getActivity().getFragmentManager();
                Fragment fragment=new RoomListFragment();
                fm.beginTransaction().replace( R.id.main_content,fragment ).commit();
                onDestroy();
            }
        });
    }

    private void enterRoom() {
        UserConstant.roomID=-1+"";
        RequestManager requestManager =RequestManager.getInstance(getActivity());
        requestManager.requestPutWithParam(UrlConfig.enterRoomUrl, new Param().append("id", "-1").end(),
                true, new RequestManager.ReqCallBack<ResponseModel>() {
                    @Override
                    public void onReqSuccess(ResponseModel result) {
                        if(CheckStatuss.CheckStatus(result, getActivity()) != 1) {
                            Toast.makeText(getActivity(),"加入失败",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        Toast.makeText(getActivity(),"加入失败",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
