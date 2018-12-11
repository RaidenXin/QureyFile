package com.huihuang.queryfile.Utils;

public class StringUtils {

    public static final String EMPTY = "";

    public static boolean isBlank(String value){
        if (null == value || EMPTY.equals(value)){
            return true;
        }
        return false;
    }

    public static boolean isNonBlank(String value){
        return !isBlank(value);
    }
}
