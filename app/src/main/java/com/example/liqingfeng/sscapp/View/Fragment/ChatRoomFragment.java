package com.example.liqingfeng.sscapp.View.Fragment;


import android.annotation.SuppressLint;
import android.app.Fragment;
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

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.example.liqingfeng.sscapp.Model.Entity.PersonsChat;
import com.example.liqingfeng.sscapp.Presenter.Adapter.ChatroomAdapter;
import com.example.liqingfeng.sscapp.R;

public class ChatRoomFragment extends Fragment {
    private View view;
    private ChatroomAdapter chatAdapter;
    private ListView lv_chat_dialog;
    //WebSocket 创建
    private WebSocketClient mSocketClient;

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
        for (int i = 0; i <= 3; i++) {
            PersonsChat personsChat = new PersonsChat();
            personsChat.setMeSend( false );
            personsChat.setChatMessage( "aaaaaaaaaa" );
            personsChats.add( personsChat );
        }

        lv_chat_dialog = (ListView) view.findViewById( R.id.lv_chat_dialog );
        Button btn_chat_message_send = (Button) view.findViewById( R.id.btn_chat_message_send );
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
        return view;
    }

    /**
     * 刷新listview
     */
    public void refreshListview() {
        chatAdapter.notifyDataSetChanged();
        handler.sendEmptyMessage( 1 );
    }
    private void init() {
        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    mSocketClient = new WebSocketClient(new URI( "ws://192.168.0.100/api/community/1-12" ), new Draft_6455(  )) {
                        @Override
                        public void onOpen(ServerHandshake handshakedata) {
                            Log.d("picher_log","打开通道"+handshakedata.getHttpStatus());
                        }

                        @Override
                        public void onMessage(String message) {
                            Log.d( "picher_log", "收到消息"+message );
                            //将收到的信息加入到消息实体列表中
                            PersonsChat personsChat=new PersonsChat(  );
                            personsChat.setMeSend( false );
                            personsChat.setChatMessage( message );
                            personsChats.add( personsChat );
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
        } ).start();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSocketClient != null) {
            mSocketClient.close();
        }
    }
}
