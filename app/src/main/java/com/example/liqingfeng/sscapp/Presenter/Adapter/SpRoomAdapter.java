package com.example.liqingfeng.sscapp.Presenter.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liqingfeng.sscapp.Model.UserConstant;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import com.example.liqingfeng.sscapp.Presenter.Util.ConvertUtil.DataConvertUtil;
import com.example.liqingfeng.sscapp.Presenter.Util.ImageUtil.ImageUtil;
import com.example.liqingfeng.sscapp.R;

public class SpRoomAdapter extends BaseAdapter {
    private List<Map<String,Object>> mlist;
    private Context mcontext;
    public SpRoomAdapter(Context context)
    {
        mcontext=context;
        mlist=UserConstant.list_room;
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
        ViewHolder viewHolder=null;
        if(view==null)
        {
            viewHolder=new ViewHolder();
            view= LayoutInflater.from( mcontext ).inflate( R.layout.fragment_roomlist_item,null );
            viewHolder.room_image=(ImageView)view.findViewById( R.id.room_image );
            viewHolder.room_id=(TextView)view.findViewById( R.id.room_id );
            viewHolder.room_pNum=(TextView)view.findViewById( R.id.room_pNum );
            viewHolder.room_startTime=(TextView)view.findViewById( R.id.room_startTime );
            viewHolder.room_endTime=(TextView)view.findViewById( R.id.room_endTime );
            viewHolder.room_place=(TextView)view.findViewById( R.id.room_place );
            viewHolder.room_join=(ImageView)view.findViewById( R.id.room_join );
            view.setTag( viewHolder );
        }
        else {
            viewHolder=(ViewHolder) view.getTag();
        }
        //控件操作
        try {
            viewHolder.room_id.setText( "房间号--  "+ DataConvertUtil.doubleToString( (double)mlist.get( i ).get( "id" ) ) );
            viewHolder.room_pNum.setText( "人数:  "+DataConvertUtil.doubleToString( (double)mlist.get( i ).get( "roNum" ) )+"/"+
                    DataConvertUtil.doubleToString( (double)mlist.get( i ).get( "roOrinum" ) ) );
            viewHolder.room_startTime.setText( DataConvertUtil.Time( (double)mlist.get( i ).get( "roStartdate" ) )  );
            viewHolder.room_endTime.setText(  DataConvertUtil.Time( (double)mlist.get( i ).get( "roEnddate" ) )  );
            viewHolder.room_place.setText( (String) mlist.get( i ).get( "roLocation" ) );
            Bitmap bitmap = ImageUtil.getImage( UserConstant.room_image_path);
            viewHolder.room_image.setImageBitmap( bitmap );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //创建点击事件
        viewHolder.room_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monItemcomroomListener.oncomroomClick(i);
            }
        });
        return view;
    }


    public interface onItemcomroomListener {
        void oncomroomClick(int i);
    }
    private onItemcomroomListener monItemcomroomListener;
    public void setItemcomroomListener(onItemcomroomListener monItemStablishListener)
    {
        this.monItemcomroomListener=monItemStablishListener;
    }

    class ViewHolder{
        ImageView room_image;
        TextView room_id;
        TextView room_pNum;
        TextView room_startTime;
        TextView room_endTime;
        TextView room_place;
        ImageView room_join;
    }
}
