package com.example.liqingfeng.sscapp.Presenter.Util.ConvertUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 数据类型转换
 */
public class DataConvertUtil {
    public static final int toInt(Object value) {
        if (value instanceof String) {
            return ((Double)Double.parseDouble((String) value)).intValue();
        } else if (value instanceof Double) {
            return ((Double) value).intValue();
        } else if (value instanceof Long) {
            return ((Long) value).intValue();
        } else {
            return -1;
        }
    }

    public static final double toDouble(Object value) {
        if (value instanceof String) {
            return new Double((String) value);
        } else if (value instanceof Integer) {
            return new Double(value + "");
        } else if (value instanceof Long) {
            return new Double(value + "");
        } else {
            return -1;
        }
    }
    //double转为long
    public static long doubleToLong(double data) {
        return new Double(data).longValue();
    }

    //long转为string
    public static String longToString(long data) {
        return new Long( data ).toString();
    }

    //int转换string
    public static String intToString(int data) {
        return String.valueOf( data );
    }
    //double转换int然后string
    public static String doubleToString(double data) {
        Double a= new Double(data);
        if( a == null) {
            return "null";
        }
        return intToString( a.intValue() );
    }
    //返回时间
    public static String Time(double time) {
        Date dtime = new Date(DataConvertUtil.doubleToLong( time ));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        return sdf.format(dtime);
    }

}
