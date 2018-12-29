package com.huihuang.queryfile.logs;

import java.util.EmptyStackException;
import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LogsStack {

    private static final Stack<String> stack = new Stack<>();
    private static final Stack<String> error_stack = new Stack<>();
    private static final Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();
    private static final LogsStack instance = new LogsStack();

    private LogsStack(){
    }

    public static final LogsStack newInstance(){
        return instance;
    }

    public void push(String log){
        push(log, stack);
    }

    private void push(String log,Stack stack){
        lock.lock();
        try{
            log = log + "\r\n";
            stack.push(log);
            condition.signal();
        }catch (Exception e){
            stack.push(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }finally {
            lock.unlock();
        }
    }

    public String pop(){
        String result = null;
        try{
            result = stack.pop();
        }catch (Exception e){
            stack.push(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    public void errorPush(String log){
        push(log, error_stack);
    }

    public String errorPop(){
        String result = null;
        try{
            result = error_stack.pop();
        }catch (Exception e){
            error_stack.push(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    public boolean logsIsEmpty(){
        isAwait();
        return stack.isEmpty();
    }

    public boolean errorLogsIsEmpty(){
        isAwait();
        return error_stack.isEmpty();
    }

    private void isAwait(){
        lock.lock();
        try{
            if (stack.isEmpty() && error_stack.isEmpty()){
                condition.await();
            }
        }catch (Exception e){
            error_stack.push(e.getMessage());
        }finally {
            lock.unlock();
        }
    }
}
