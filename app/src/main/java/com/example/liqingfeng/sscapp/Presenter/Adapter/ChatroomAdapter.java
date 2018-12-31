package com.example.liqingfeng.sscapp.Presenter.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.liqingfeng.sscapp.Model.Entity.PersonsChat;
import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Presenter.Util.ImageUtil.ImageLoaderUtil;
import com.example.liqingfeng.sscapp.R;
import com.example.liqingfeng.sscapp.View.CustomView.CircleImageView;

import java.util.List;

public class ChatroomAdapter extends BaseAdapter {
    private Context context;
    private List<PersonsChat> lists;
    private HolderAll holderAll;

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
        if (entity.getMeSend()) { //接受到的消息
            return IMsgViewType.IMVT_COM_MSG;
        } else  { //自己发送的消息
            return IMsgViewType.IMVT_TO_MSG;
        }
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageLoaderUtil imageLoaderUtil = ImageLoaderUtil.getInstance(context);
        if(view == null) {
            holderAll = new HolderAll();
            holderAll.holderViewFriend = new HolderViewFriend();
            holderAll.holderViewMy = new HolderViewMy();
            view= LayoutInflater.from( context ).inflate( R.layout.fragment_chatroom_leftitem,null );
            holderAll.holderViewFriend.tv_chart_me_message=(TextView)view.findViewById( R.id.tv_friend_message );
            holderAll.holderViewFriend.tv_friendName = (TextView) view.findViewById(R.id.tv_friendName);
            holderAll.holderViewFriend.tv_friendMsgSendTime = (TextView) view.findViewById(R.id.tv_friendMsgSendTime);
            holderAll.holderViewFriend.iv_friendAvatar = (CircleImageView) view.findViewById(R.id.iv_friendAvatar);
            view= LayoutInflater.from( context ).inflate( R.layout.fragment_chatroom_rightitem,null );
            holderAll.holderViewMy.tv_my_message= (TextView) view.findViewById( R.id.tv_my_message );
            holderAll.holderViewMy.iv_myAvatar = (CircleImageView) view.findViewById(R.id.iv_myAvatar);
            view.setTag( holderAll );
        } else {
            holderAll = (HolderAll) view.getTag();
        }
        // 开始解析
        if(lists.get(i).getMeSend()) {
            // 是自己发送的
            view= LayoutInflater.from( context ).inflate( R.layout.fragment_chatroom_rightitem,null );
            imageLoaderUtil.displayImage(holderAll.holderViewMy.iv_myAvatar, UrlConfig.imageBaseUrl+lists.get(i).getImgUrl());
            holderAll.holderViewMy.tv_my_message.setText(lists.get(i).getChatMessage());
        } else {
            view= LayoutInflater.from( context ).inflate( R.layout.fragment_chatroom_leftitem,null );
            //imageLoaderUtil.displayImage(holderAll.holderViewFriend.iv_friendAvatar, UrlConfig.imageBaseUrl+lists.get(i).getImgUrl());
            holderAll.holderViewFriend.tv_friendName.setText(lists.get(i).getName());
            holderAll.holderViewFriend.tv_chart_me_message.setText(lists.get(i).getChatMessage());
            holderAll.holderViewFriend.tv_friendMsgSendTime.setText(lists.get(i).getTime());
        }
        return view;
    }

    class HolderViewFriend {
        TextView tv_chart_me_message;
        TextView tv_friendName;
        TextView tv_friendMsgSendTime;
        CircleImageView iv_friendAvatar;

    }
    class HolderViewMy {
        TextView tv_my_message;
        CircleImageView iv_myAvatar;
    }

    class HolderAll{
        HolderViewFriend holderViewFriend;
        HolderViewMy holderViewMy;
    }
    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
