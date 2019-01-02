package com.example.liqingfeng.sscapp.Presenter.Adapter;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liqingfeng.sscapp.Model.Entity.PersonsChat;
import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Model.UserConstant;
import com.example.liqingfeng.sscapp.Presenter.Util.ImageUtil.ImageLoaderUtil;
import com.example.liqingfeng.sscapp.View.CustomView.CircleImageView;
import com.example.liqingfeng.sscapp.R;
import com.example.liqingfeng.sscapp.View.Fragment.ChatRoomFragment;
import com.example.liqingfeng.sscapp.View.Fragment.FriendInfoFragment;

import java.util.List;

public class NewChatroomAdapter extends RecyclerView.Adapter<NewChatroomAdapter.ViewHolder> {

    private List<PersonsChat> msgList;
    private ImageLoaderUtil imageLoaderUtil;
    private Activity activity;

    /*
   传入外部list的构造方法
    */
    public NewChatroomAdapter(List<PersonsChat> msgList, Activity mac){
        this.msgList = msgList;
        this.activity= mac;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newchatroom_fragment_item,parent,false);
        imageLoaderUtil = ImageLoaderUtil.getInstance(parent.getContext());
        final ViewHolder holder = new ViewHolder(view);
        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();//得到当前点击的位置
                PersonsChat msg = msgList.get(position);//从点击位置里得到List中的单例
                //从单例中得到时间
                if(!msg.getMeSend()) {
                    UserConstant.friendID = msg.getId();
                    Intent intent = new Intent(parent.getContext(), FriendInfoFragment.class);
                    parent.getContext().startActivity(intent);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PersonsChat msg = msgList.get(position);
        //判断是信息是接收还是发送的，并且分别判断需要隐藏的布局和显示的布局
        if (msg.getMeSend()){
            // 左边隐藏
            holder.left_layout.setVisibility(View.GONE);
            holder.right_layout.setVisibility(View.VISIBLE);
            holder.right_msg.setText(msg.getChatMessage());
            imageLoaderUtil.displayImage(holder.right_image, UrlConfig.imageBaseUrl + UserConstant.user_head_picture);
        } else {
            holder.right_layout.setVisibility(View.GONE);
            holder.left_layout.setVisibility(View.VISIBLE);
            holder.left_msg.setText(msg.getChatMessage());
            holder.left_name.setText(msg.getName());
            holder.left_date.setText(msg.getTime());
            imageLoaderUtil.displayImage(holder.left_image, UrlConfig.imageBaseUrl + msg.getImgUrl());
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    /*
   缓存子布局的内部类
    */
    static  class ViewHolder extends RecyclerView.ViewHolder{
        View myView;
        RelativeLayout left_layout;
        RelativeLayout right_layout;
        TextView left_msg;
        TextView left_date;
        TextView left_name;
        CircleImageView left_image;
        CircleImageView right_image;
        TextView right_msg;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            left_layout = (RelativeLayout) itemView.findViewById(R.id.left_item);
            right_layout = (RelativeLayout) itemView.findViewById(R.id.right_item);

            left_msg = (TextView)itemView.findViewById(R.id.left_message);
            left_date = (TextView) itemView.findViewById(R.id.left_date);
            left_name = (TextView)itemView.findViewById(R.id.left_name);
            left_image = (CircleImageView) itemView.findViewById(R.id.left_image);

            right_image = (CircleImageView) itemView.findViewById(R.id.right_image);
            right_msg = (TextView)itemView.findViewById(R.id.right_message);
        }
    }

}
