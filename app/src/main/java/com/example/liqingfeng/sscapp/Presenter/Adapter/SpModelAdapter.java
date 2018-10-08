package com.example.liqingfeng.sscapp.Presenter.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.liqingfeng.sscapp.Model.UrlConfig;
import com.example.liqingfeng.sscapp.Model.UserConstant;
import com.example.liqingfeng.sscapp.Presenter.Util.ImageUtil.ImageLoaderUtil;

import com.example.liqingfeng.sscapp.Presenter.Util.ImageUtil.ImageUtil;
import com.example.liqingfeng.sscapp.R;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * 运动房间 sportsListview的Adapter
 */
public class SpModelAdapter extends BaseAdapter implements View.OnClickListener {
    public static int postion = -1;
    private Context mcontext;
    //初化始数据
    private List<Map<String, String>> mlist;
    //接口
    private InnerItemOnclickListener mListener;

    public SpModelAdapter(Context context) {
        mcontext = context;
        mlist = UserConstant.list_sport;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int i) {
        return mlist.get( i );
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from( mcontext ).inflate( R.layout.fragment_spmodel, null );
            viewHolder.frameLayout = (FrameLayout) view.findViewById( R.id.sports_flt );
            viewHolder.stablish_vt = (Button) view.findViewById( R.id.stablish_bt );
            viewHolder.join_bt = (Button) view.findViewById( R.id.join_bt );
            view.setTag( viewHolder );
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        //给List的item控件操作
        try {
            //将本机的图片转换成drawable对象,然后设置为背景
            Bitmap bitmap = ImageUtil.getImage( mlist.get( i ).get( "spImg" ) );
            viewHolder.frameLayout.setBackground( new BitmapDrawable( mcontext.getResources(), bitmap ) );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        viewHolder.stablish_vt.setOnClickListener( this );
        viewHolder.join_bt.setOnClickListener( this );
        viewHolder.join_bt.setTag( i );
        viewHolder.stablish_vt.setTag( i );
        return view;
    }

    public interface InnerItemOnclickListener {
        void itemClick(View v);
    }

    public void setOnInnerItemOnClickListener(InnerItemOnclickListener listener){
        this.mListener=listener;
    }

    @Override
    public void onClick(View v) {
        mListener.itemClick(v);
    }



    class ViewHolder {
        FrameLayout frameLayout;
        Button stablish_vt;
        Button join_bt;
    }
}
