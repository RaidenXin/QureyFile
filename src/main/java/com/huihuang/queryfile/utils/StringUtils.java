package com.huihuang.queryfile.utils;

import com.huihuang.queryfile.exception.EmptyException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static final String EMPTY = "";

    private static final Pattern PATTERN = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");

    /**
     * 工具类最好不要有构造方法
     */
    private StringUtils() {
    }

    public static boolean isBlank(String value) {
        if (null == value || EMPTY.equals(value)) {
            return true;
        }
        return false;
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }


    /**
     * 字符串是否包含中文
     *
     * @param str 待校验字符串
     * @return true 包含中文字符  false 不包含中文字符
     * @throws EmptyException
     */
    public static boolean isContainChinese(String str) throws EmptyException {
        if (isBlank(str)) {
            throw new EmptyException("sms context is empty!");
        }
        Matcher m = PATTERN.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
}
