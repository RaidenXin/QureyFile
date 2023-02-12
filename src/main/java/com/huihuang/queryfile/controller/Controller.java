package com.huihuang.queryfile.controller;

import com.huihuang.queryfile.common.Constant;
import com.huihuang.queryfile.common.FileType;
import com.huihuang.queryfile.utils.AlertUtil;
import com.huihuang.queryfile.utils.StringUtils;
import com.huihuang.queryfile.handler.QueryFileProcessor;
import com.huihuang.queryfile.information.TaskInformation;
import com.huihuang.queryfile.logs.Logger;

import javax.swing.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 控制器
 */
public class Controller {

    private static final String PATH_TEXT = "请填写文件所在路径";
    private static final String END_FILE_NAME_TEXT = "请填写文件后缀";
    private static final String CONTENT_TEXT = "请填写要查询内容";
    private static final String NON_EXISTENT = "搜索的文件中不存在该元素！";
    private static final String SEPARATIVE_SIGN = "\\";
    private static final String NEW_LINE = "\n";

    private Queue<TaskInformation> taskStack;
    private Lock lock;
    private Condition condition;
    private QueryFileProcessor processor;
    private JTextArea text;
    private Logger logger = Logger.newInstance();
    private static final Map<String,List<File>> QUERIED_COLLECTION_OF_FILES = new HashMap<>();

    public Controller(JTextArea text){
        this.taskStack = new ConcurrentLinkedQueue<>();
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
        this.processor = new QueryFileProcessor(this);
        this.text = text;
    }

    /**
     * 添加任务并且唤醒主线程
     * @param path
     * @param fileType
     * @param content
     */
    public void add(String path, FileType fileType, String content){
        taskStack.add(new TaskInformation(path, fileType.getSuffix(), content));
        lock.lock();
        try {
            condition.signal();
        }finally {
            lock.unlock();
        }
    }

    /**
     * 添加任务
     * @param path
     * @param endFileName
     * @param content
     */
    public void push(String path,String endFileName,String content){
        taskStack.add(new TaskInformation(path, endFileName, content));
    }

    /**
     * 启动一个主线程，查看任务列表，如果有任务就执行，没有就休眠
     */
    public void start(){
        Runnable task = () -> {
            lock.lock();
            try{
                while (true){
                    if (taskStack.isEmpty()){
                        condition.await();
                    }
                    TaskInformation information = taskStack.poll();
                    String path = information.getPath();
                    if (null == path){
                        continue;
                    }
                    List<String> fileNames = getFileNames(path, information.getEndFileName(), information.getContent());
                    for (String fileName : fileNames) {
                        if (NON_EXISTENT.equals(fileName)){
                            if (StringUtils.isBlank(text.getText())){
                                text.append(fileName);
                            }
                        }else {
                            if (NON_EXISTENT.equals(text.getText())){
                                text.setText(fileName + NEW_LINE);
                            }else {
                                text.append(fileName + NEW_LINE);
                            }
                        }
                    }
                }
            }catch (Exception e){
                logger.error(e);
            }finally {
                lock.unlock();
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    /**
     * 取得找到的文件名称
     * @param path
     * @param fileType
     * @param content
     * @return
     */
    private List<String> getFileNames(String path, String fileType, String content){
        if (END_FILE_NAME_TEXT.equals(fileType) && CONTENT_TEXT.equals(content)) {
            return Collections.emptyList();
        }
        if (PATH_TEXT.equals(path)) {
            path = null;
        }
        if (fileType.indexOf(Constant.POINT) == -1){
            fileType = Constant.POINT + fileType;
        }
        List<String> fileNames = new ArrayList<>();
        List<File> fileList = processor.queryFile(path, fileType, content);
        if (fileList.isEmpty()){
            fileNames.add(NON_EXISTENT);
        }else {
            QUERIED_COLLECTION_OF_FILES.put(content, fileList);
            fileList.stream().forEach(x -> fileNames.add(x.getName()));
        }
        return fileNames;
    }

    public void obtainFiles(String path, String content){
        List<File> fileList = QUERIED_COLLECTION_OF_FILES.remove(content);
        File file = new File(path);
        if (Objects.nonNull(fileList) && !file.isFile()){
            String savePath = path + SEPARATIVE_SIGN + content + SEPARATIVE_SIGN;
            for (File f : fileList) {
                obtainFiles(savePath, f);
            }
        }
    }

    private void obtainFiles(String savePath, File file){
        File newFile = new File(savePath);
        if (!newFile.exists()){
            newFile.mkdir();
        }
        if (file.exists()){
            if(file.renameTo(new File(savePath + file.getName()))) {
                AlertUtil.info("文件收集成功！已经放置到目录：" + savePath);
                logger.info("重命名成功！");
            }else {
                AlertUtil.error("文件收集失败！", new Exception());
                logger.error("重命名失败！新文件名已存在");
            }
        }else {
            logger.error("重命名文件不存在！");
        }
    }
}
