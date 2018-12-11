package com.huihuang.queryfile.controller;

import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Controller {

    private Stack<String> taskStack;
    private Lock lock;
    private Condition condition;

    public Controller(){
        taskStack = new Stack<>();
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public void add(String path){
        taskStack.push(path);
        condition.signal();
    }

    public void start(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try{
                    while (true){
                        String path = taskStack.pop();
                        if (null == path){
                            condition.await();
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
    }
}
