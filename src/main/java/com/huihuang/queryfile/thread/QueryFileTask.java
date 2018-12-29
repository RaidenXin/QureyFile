package com.huihuang.queryfile.thread;

import com.huihuang.queryfile.Utils.FileUtils;
import com.huihuang.queryfile.controller.Controller;
import com.huihuang.queryfile.information.Information;
import com.huihuang.queryfile.information.TaskInformation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;

/**
 * 查询任务工作线程类
 */
public class QueryFileTask implements Runnable {

    private String endFileName;
    private String content;
    private List<File> fileList;
    private File[] files;
    private int startIndex;
    private int endIndex;
    private CountDownLatch countDownLatch;
    private Controller controller;

    public QueryFileTask(List<File> fileList, Information information){
        this.endFileName = information.getEndFileName();
        this.content = information.getContent();
        this.files = information.getFiles();
        this.startIndex = information.getStartIndex();
        this.endIndex = information.getEndIndex();
        this.countDownLatch = information.getCountDownLatch();
        this.controller = information.getController();
        this.fileList = fileList;
    }

    @Override
    public void run() {
        for (int i = startIndex; i < endIndex; i++){
            File file = files[i];
            if (FileUtils.fileParse(file, endFileName).contains(content)) {
                String fileName = file.getName();
                synchronized (fileList){
                    if (!contains(fileList, file)){
                        fileList.add(file);
                    }
                }
            }else if (!file.isFile()){
                //如果不是文件，则加入任务队列中
                String newPath = file.getPath();
                controller.push(newPath, endFileName, content);
            }
        }
        if (null != countDownLatch){
            countDownLatch.countDown();
        }
    }

    private boolean contains(List<File> fileList, File file){
        boolean isContains = false;
        for (File f : fileList) {
            isContains = f.getName().endsWith(file.getName());
            if (isContains){
                break;
            }
        }
        return isContains;
    }

}
