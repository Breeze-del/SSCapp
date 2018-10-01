package com.example.liqingfeng.sscapp.Presenter.Util.ConvertUtil;

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

}
