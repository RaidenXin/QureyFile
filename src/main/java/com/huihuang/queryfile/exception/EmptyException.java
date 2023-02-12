package com.huihuang.queryfile.exception;

/**
 * @author: JiangJi
 * @Descriotion:
 * @Date:Created in 2023/2/12 17:36
 */
public class EmptyException extends RuntimeException{

    public EmptyException(String errorMessage) {
        super(errorMessage);
    }
}
