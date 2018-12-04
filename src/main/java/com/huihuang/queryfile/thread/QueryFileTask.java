package com.huihuang.queryfile.thread;

import com.huihuang.queryfile.QueryFileProcessor;
import com.huihuang.queryfile.Utils.FileUtils;

import java.io.File;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class QueryFileTask implements Runnable {

    private String endFileName;
    private String content;
    private List<String> fileNames;
    private File[] files;
    private int start;
    private int end;
    private CountDownLatch countDownLatch;

    public QueryFileTask(List<String> fileNames, File[] files, CountDownLatch countDownLatch,int start, int end, String endFileName, String content){
        this.endFileName = endFileName;
        this.content = content;
        this.fileNames = fileNames;
        this.files = files;
        this.start = start;
        this.end = end;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        for (int i = start;i < end; i++){
            File file = files[i];
            if (FileUtils.fileParse(file, endFileName).contains(content)) {
                synchronized (fileNames){
                    fileNames.add(file.getName());
                }
            }
        }
        if (null != countDownLatch){
            countDownLatch.countDown();
        }
    }

}
