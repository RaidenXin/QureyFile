package com.huihuang.queryfile.utils;

import javafx.scene.control.Alert;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 11:48 2022/6/5
 * @Modified By:
 */
public final class AlertUtil {

    public static void info(String messge){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, messge);
        alert.showAndWait();
    }

    public static void error(String errorMessage, Throwable e){
        String error = errorMessage + e.getMessage();
        Alert alert = new Alert(Alert.AlertType.ERROR, error);
        alert.showAndWait();
    }

    public static void error(Throwable e){
        String error = e.getMessage();
        Alert alert = new Alert(Alert.AlertType.ERROR, error);
        alert.showAndWait();
    }

    public static void warn(String warnMessage){
        Alert alert = new Alert(Alert.AlertType.WARNING,  warnMessage);
        alert.showAndWait();
    }
}
