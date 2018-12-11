package com.huihuang.queryfile.thread;

import com.huihuang.queryfile.Utils.FileUtils;
import com.huihuang.queryfile.information.Information;

import java.io.File;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;

public class QueryFileTask implements Runnable {

    private String endFileName;
    private String content;
    private List<String> fileNames;
    private File[] files;
    private int startIndex;
    private int endIndex;
    private CountDownLatch countDownLatch;
    private Stack<String> NEW_PATH_STACK;

    public QueryFileTask(List<String> fileNames, Information information){
        this.endFileName = information.getEndFileName();
        this.content = information.getContent();
        this.fileNames = fileNames;
        this.files = information.getFiles();
        this.startIndex = information.getStartIndex();
        this.endIndex = information.getEndIndex();
        this.countDownLatch = information.getCountDownLatch();
        this.NEW_PATH_STACK = information.getNewPathStack();
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
                String newPath = file.getPath();
                NEW_PATH_STACK.push(newPath);
            }
        }
        if (null != countDownLatch){
            countDownLatch.countDown();
        }
    }

}
