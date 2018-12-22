package com.huihuang.queryfile.information;

import com.huihuang.queryfile.controller.Controller;

import java.io.File;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;

/**
 * 一个用于传递数据的model
 */
public class Information {

    private File[] files;
    private CountDownLatch countDownLatch;
    private String endFileName;
    private String content;
    private int startIndex;
    private int endIndex;
    private Controller controller;

    public Information(Controller controller, File[] files, CountDownLatch countDownLatch, int start, int end, String endFileName, String content){
        this.endFileName = endFileName;
        this.content = content;
        this.files = files;
        this.startIndex = start;
        this.endIndex = end;
        this.countDownLatch = countDownLatch;
        this.controller = controller;
    }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public String getEndFileName() {
        return endFileName;
    }

    public void setEndFileName(String endFileName) {
        this.endFileName = endFileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
