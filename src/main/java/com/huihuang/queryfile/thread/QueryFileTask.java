package com.huihuang.queryfile.thread;

import com.huihuang.queryfile.Utils.FileUtils;
import com.huihuang.queryfile.controller.Controller;
import com.huihuang.queryfile.information.Information;
import com.huihuang.queryfile.information.TaskInformation;

import java.io.File;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;

/**
 * 查询任务工作线程类
 */
public class QueryFileTask implements Runnable {

    private String endFileName;
    private String content;
    private List<String> fileNames;
    private File[] files;
    private int startIndex;
    private int endIndex;
    private CountDownLatch countDownLatch;
    private Controller controller;

    public QueryFileTask(List<String> fileNames, Information information){
        this.endFileName = information.getEndFileName();
        this.content = information.getContent();
        this.fileNames = fileNames;
        this.files = information.getFiles();
        this.startIndex = information.getStartIndex();
        this.endIndex = information.getEndIndex();
        this.countDownLatch = information.getCountDownLatch();
        this.controller = information.getController();
    }

    @Override
    public void run() {
        for (int i = startIndex; i < endIndex; i++){
            File file = files[i];
            if (FileUtils.fileParse(file, endFileName).contains(content)) {
                String fileName = file.getName();
                synchronized (fileNames){
                    if (!fileNames.contains(fileName)){
                        fileNames.add(fileName);
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

}
