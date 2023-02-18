package com.huihuang.queryfile.controller;

import com.huihuang.queryfile.common.Constant;
import com.huihuang.queryfile.common.FileType;
import com.huihuang.queryfile.utils.AlertUtil;
import com.huihuang.queryfile.utils.ListUtil;
import com.huihuang.queryfile.utils.StringUtils;
import com.huihuang.queryfile.handler.QueryFileProcessor;
import com.huihuang.queryfile.information.TaskInformation;
import com.huihuang.queryfile.logs.Logger;

import javax.swing.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

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
    private static final Map<String,List<String>> QUERIED_COLLECTION_OF_FILES = new ConcurrentHashMap<>();
    private static final AtomicBoolean SEARCH_BEACON = new AtomicBoolean(false);

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
        if (SEARCH_BEACON.compareAndSet(false, true)) {
            taskStack.add(new TaskInformation(path, fileType.getSuffix(), content));
            lock.lock();
            try {
                condition.signal();
            }finally {
                lock.unlock();
            }
            return;
        }
        AlertUtil.error("正在搜索中，请勿重复操作！");
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
                        // 如果过处理完了任务 说明此次搜索完成
                        SEARCH_BEACON.compareAndSet(true, false);
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
            final List<String> newFiles = new ArrayList<>();
            final List<String> oldFiles = QUERIED_COLLECTION_OF_FILES.putIfAbsent(content, newFiles);
            boolean oldFilesIsNull = Objects.isNull(oldFiles);
            fileList.stream().forEach(x -> {
                fileNames.add(x.getPath());
                if (oldFilesIsNull) {
                    newFiles.add(x.getPath());
                } else {
                    oldFiles.add(x.getPath());
                }
            });
        }
        return fileNames;
    }

    /**
     * 收集文件
     * @param path
     * @param content
     */
    public void obtainFiles(String path, String content){
        if (SEARCH_BEACON.get()) {
            AlertUtil.error("正在搜索中，请勿操作搜集！");
            return;
        }
        List<String> fileList = QUERIED_COLLECTION_OF_FILES.remove(content);
        File file = new File(path);
        if (ListUtil.isNotEmpty(fileList) && !file.isFile()) {
            String savePath = path + SEPARATIVE_SIGN + content + SEPARATIVE_SIGN;
            try {
                for (String f : fileList) {
                    fileRename(savePath, new File(f));
                }
                AlertUtil.info("文件收集成功！已经放置到目录：" + savePath);
                text.setText(StringUtils.EMPTY);
            } catch (Exception e) {
                logger.error("文件收集失败！", e);
                AlertUtil.error("文件收集失败！", e);
            }
        } else {
            if (StringUtils.isBlank(path)) {
                AlertUtil.error("未找到可以收集的文件,路径不正确！", new Exception());
                return;
            }
            if (StringUtils.isBlank(content)) {
                AlertUtil.error("未找到可以收集的文件,查找内容不正确！", new Exception());
                return;
            }
            AlertUtil.error("未找到可以收集的文件,请先搜索文件！", new Exception());
        }
    }

    /**
     * 文件重命名
     * @param savePath
     * @param file
     */
    private void fileRename(String savePath, File file){
        File newFile = new File(savePath);
        if (!newFile.exists()){
            newFile.mkdir();
        }
        if (file.exists()){
            String newFilePath = getNewFilePath(savePath, file.getName());
            if(file.renameTo(new File(newFilePath))) {
                logger.info("重命名成功！");
            }else {
                AlertUtil.error("文件收集失败！Path:" + newFilePath, new Exception());
                logger.error("文件收集失败！");
            }
        }else {
            logger.error("文件不存在！文件Path:" + file.getPath());
        }
    }

    /**
     * 获取新文件的 path
     * @param savePath
     * @param fileName
     * @return
     */
    private String getNewFilePath(String savePath, String fileName) {
        final StringBuilder newFilePath = new StringBuilder(savePath);
        newFilePath.append(fileName);
        File renameFile = new File(newFilePath.toString());
        // 如果文件已经存在说明存在同名称的文件 就在文件后面新增序列号
        int serial = 1;
        while (renameFile.exists()){
            //如果存在清空名字 从新生成 在名字前面 加上序列号-
            newFilePath.setLength(0);
            newFilePath.append(savePath);
            newFilePath.append(serial++);
            newFilePath.append("-");
            newFilePath.append(fileName);
            renameFile = new File(newFilePath.toString());
        }
        return newFilePath.toString();
    }
}
