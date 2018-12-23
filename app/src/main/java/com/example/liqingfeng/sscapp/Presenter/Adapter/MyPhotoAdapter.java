package com.example.liqingfeng.sscapp.Presenter.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.liqingfeng.sscapp.R;
import com.example.liqingfeng.sscapp.Model.Entity.Image;

import java.util.List;

public class MyPhotoAdapter extends BaseAdapter {
    private Context mContext;
    private List<Image> mData;

    public MyPhotoAdapter(Context mContext, List<Image> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.activity_cell_layout,null);
            holder.mPic = view.findViewById(R.id.item_image);
            holder.mShade = view.findViewById(R.id.item_shade);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        if(position < 1){
            //第一个Item 特殊待遇下
            holder.mShade.setVisibility(View.GONE);
            Glide.with(mContext).load(R.mipmap.ic_photo_add)
                    .into(holder.mPic);
        }else {
            Glide.with(mContext)
                    .load(mData.get(position).getPath())
                    .into(holder.mPic);
        }

        return view;
    }
    static class ViewHolder{
        private ImageView mPic;
        private View mShade;
    }
}
