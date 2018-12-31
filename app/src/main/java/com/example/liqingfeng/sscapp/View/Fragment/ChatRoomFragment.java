package com.example.liqingfeng.sscapp.View.Fragment;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.liqingfeng.sscapp.Model.Entity.PersonsChat;
import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Model.UserConstant;
import com.example.liqingfeng.sscapp.Presenter.Adapter.ChatroomAdapter;
import com.example.liqingfeng.sscapp.R;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomFragment extends Fragment {
    private View view;
    private ChatroomAdapter chatAdapter;
    private ListView lv_chat_dialog;
    //WebSocket 创建
    private WebSocketClient mSocketClient;
    private Button btn_chat_message_send;
    private Button back;

    //实体消息集合
    private List<PersonsChat> personsChats =new ArrayList<PersonsChat>(  );
    private Handler handler = new Handler(  ) {
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case 1:
                    lv_chat_dialog.setSelection( personsChats.size() );
                    break;
                default:
                    break;
            }
        }
    };
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate( R.layout.chatroom_fragment, null );
        init();
        lv_chat_dialog = (ListView) view.findViewById( R.id.lv_chat_dialog );
        lv_chat_dialog.setFastScrollAlwaysVisible(true);
        btn_chat_message_send = (Button) view.findViewById( R.id.btn_chat_message_send );
        back = (Button) view.findViewById(R.id.btn_chat_message_back);
        final EditText et_chat_message = (EditText) view.findViewById( R.id.et_chat_message );

        /**
         *setAdapter
         */
        chatAdapter = new ChatroomAdapter( getActivity(), personsChats );
        lv_chat_dialog.setAdapter( chatAdapter );
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
                //刷新ListView
                refreshListview();
            }
        } );
        init();
        backToRoom();
        return view;
    }

    /**
     * 刷新listview
     */
    public void refreshListview() {
        handler.sendEmptyMessage( 1 );
        chatAdapter.notifyDataSetChanged();
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
                            personsChat.setMeSend( false );
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
                        personsChat.setMeSend( false );
                        personsChat.setName(nickName);
                        personsChat.setTime(date);
                        personsChat.setId(id);
                        personsChat.setImgUrl(img);
                        personsChat.setChatMessage( msg );
                        personsChats.add( personsChat );
                     }
                    refreshListview();
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
                onDestroy();
                FragmentManager fm=getActivity().getFragmentManager();
                Fragment fragment=new RoomListFragment();
                fm.beginTransaction().replace( R.id.main_content,fragment ).commit();
            }
        });
    }
}
