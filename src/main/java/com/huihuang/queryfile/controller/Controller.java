package com.huihuang.queryfile.controller;

import com.huihuang.queryfile.Utils.StringUtils;
import com.huihuang.queryfile.handler.QueryFileProcessor;
import com.huihuang.queryfile.information.TaskInformation;
import com.huihuang.queryfile.logs.Logger;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Controller {

    private static final String PATH_TEXT = "请填写文件所在路径";
    private static final String END_FILE_NAME_TEXT = "请填写文件后缀";
    private static final String CONTENT_TEXT = "请填写要查询内容";

    private Stack<TaskInformation> taskStack;
    private Lock lock;
    private Condition condition;
    private QueryFileProcessor processor;
    private JTextArea t;
    private Logger logger = Logger.newInstance();

    public Controller(JTextArea t){
        this.taskStack = new Stack<>();
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
        this.processor = new QueryFileProcessor(this);
        this.t = t;
    }

    public void add(String path,String endFileName,String content){
        taskStack.push(new TaskInformation(path, endFileName, content));
        lock.lock();
        try {
            condition.signal();
        }finally {
            lock.unlock();
        }
    }

    public void push(String path,String endFileName,String content){
        taskStack.push(new TaskInformation(path, endFileName, content));
    }

    public void start(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try{
                    while (true){
                        if (taskStack.isEmpty()){
                            condition.await();
                        }
                        TaskInformation information = taskStack.pop();
                        String path = information.getPath();
                        if (null == path){
                            condition.await();
                        }
                        List<String> fileNames = getFileNames(path, information.getEndFileName(), information.getContent());
                        for (String fileName : fileNames) {
                             if (QueryFileProcessor.Non_existent.equals(fileName) && StringUtils.isNonBlank(t.getText())){
                                 fileName = StringUtils.EMPTY;
                            }
                            t.append(fileName + "\n");
                        }
                    }
                }catch (Exception e){
                    logger.error(e);
                }finally {
                    lock.unlock();
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    private List<String> getFileNames(String path,String fileType,String content){
        if (END_FILE_NAME_TEXT.equals(fileType) && CONTENT_TEXT.equals(content)) {
            return Collections.emptyList();
        }
        if (PATH_TEXT.equals(path)) {
            path = null;
        }
        if (fileType.indexOf(".") == -1){
            fileType = "." + fileType;
        }
        return processor.queryFile(path, fileType, content);
    }
}
