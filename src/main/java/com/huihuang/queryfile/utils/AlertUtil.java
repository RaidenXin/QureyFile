package com.huihuang.queryfile.utils;

import javax.swing.*;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 11:48 2022/6/5
 * @Modified By:
 */
public final class AlertUtil {

    public static void info(String messge){
        JOptionPane.showMessageDialog(null, messge, "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(String errorMessage){
        String error = errorMessage;
        JOptionPane.showMessageDialog(null, error, "异常", JOptionPane. ERROR_MESSAGE);
    }

    public static void error(String errorMessage, Throwable e){
        String error = errorMessage + (StringUtils.isBlank(e.getMessage()) ? StringUtils.EMPTY : e.getMessage());
        JOptionPane.showMessageDialog(null, error, "异常", JOptionPane. ERROR_MESSAGE);
    }

    public static void error(Throwable e){
        String error = e.getMessage();
        JOptionPane.showMessageDialog(null, error, "异常", JOptionPane. ERROR_MESSAGE);
    }

    public static void warn(String warnMessage){
        JOptionPane.showMessageDialog(null, warnMessage, "警告", JOptionPane.INFORMATION_MESSAGE);
    }
}
