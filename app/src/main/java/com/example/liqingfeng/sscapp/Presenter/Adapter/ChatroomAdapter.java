package com.example.liqingfeng.sscapp.Presenter.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.liqingfeng.sscapp.Model.Entity.PersonsChat;
import com.example.liqingfeng.sscapp.R;
import java.util.List;

public class ChatroomAdapter extends BaseAdapter {
    private Context context;
    private List<PersonsChat> lists;

    /**
     * 初始化 adapter
     * @param context
     * @param lists
     */
    public ChatroomAdapter(Context context, List<PersonsChat> lists) {
        super();
        this.context=context;
        this.lists=lists;
    }
    public static interface IMsgViewType{
        int IMVT_COM_MSG = 0;// 收到对方的消息
        int IMVT_TO_MSG = 1;// 自己发送出去的消息
    }
    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int i) {
        return lists.get( i );
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * 判断消息是否是自己发送的
     * @param position
     * @return
     */
    public int getItemViewType(int position) {
        PersonsChat entity = lists.get( position );
        if (entity.isMeSend()) { //接受到的消息
            return IMsgViewType.IMVT_COM_MSG;
        } else  { //自己发送的消息
            return IMsgViewType.IMVT_TO_MSG;
        }
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        HolderView holderView = null;
        PersonsChat entity = lists.get( i );
        boolean isMeSend = entity.isMeSend();
        if(holderView == null) {
            holderView = new HolderView();
            if(isMeSend) {
                view= LayoutInflater.from( context ).inflate( R.layout.fragment_chatroom_rightitem,null );
                holderView.tv_chart_me_message=(TextView)view.findViewById( R.id.tv_my_message );
                holderView.tv_chart_me_message.setText( entity.getChatMessage() );
            } else {
                view= LayoutInflater.from( context ).inflate( R.layout.fragment_chatroom_leftitem,null );
                holderView.tv_chart_me_message=(TextView)view.findViewById( R.id.tv_friend_message );
                holderView.tv_chart_me_message.setText( entity.getChatMessage() );
            }
            view.setTag( holderView );
        } else {
            holderView = (HolderView) view.getTag();
        }
        return view;
    }

    class HolderView {
        TextView tv_chart_me_message;
    }
    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
