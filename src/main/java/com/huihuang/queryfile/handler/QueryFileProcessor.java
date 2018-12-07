package com.huihuang.queryfile.handler;

import com.huihuang.queryfile.Utils.FileUtils;
import com.huihuang.queryfile.thread.QueryFileTask;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * *主要工作函数
 * @author Raiden
 *
 */
public class QueryFileProcessor {

	public static final int SIZE = 1024;
	public static final String ENPTY_STR = "";
	public static final String Non_existent = "搜索的文件中不存在该元素！";

	private static final int MAX_NUMBER = 1000;
	private static final ExecutorService executors =  Executors.newScheduledThreadPool(20);

	/**
	 * *查询文件的方法,如果是文件则直接访问其内容,如果不是则遍历其子目录
	 * @param path
	 * @param endFileName
	 * @param content
	 * @return
	 */
	public List<String> queryFile(String path, String endFileName, String content) {
		List<String> result = new ArrayList<>();
		File file = new File(getLocalPath(path));
		CountDownLatch countDownLatch = null;
		if (!file.isFile()) {
			File[] files = file.listFiles();
			int length = files.length;
			if (length > MAX_NUMBER){
				int n = length / MAX_NUMBER + 1;
				countDownLatch = new CountDownLatch(n);
				for (int i = 0;i < n; i++){
					int start = i * MAX_NUMBER;
					int end = i == n -1? length : (i + 1) * MAX_NUMBER;
					Runnable task = new QueryFileTask(result, files, countDownLatch, start, end, endFileName, content);
					executors.submit(task);
				}
				try{
					countDownLatch.await();
				}catch (Exception e){
					e.printStackTrace();
				}
			}else{
				Runnable task = new QueryFileTask(result, files, countDownLatch, 0, length, endFileName, content);
				task.run();
			}
		}else {
			if (FileUtils.fileParse(file, endFileName).contains(content)) {
				result.add(file.getName());
			}
		}
		if (result.isEmpty()){
			result.add(Non_existent);
		}
		return result;
	}
	/**
	 * *填写的文件地址为空,则默认使用当前运行文件所在地址
	 * @param path
	 * @return
	 */
	private String getLocalPath(String path) {
		if (null == path || ENPTY_STR.equals(path)) {
			String jarWholePath = QueryFileProcessor.class.getProtectionDomain().getCodeSource().getLocation().getFile();
			try {
				jarWholePath = java.net.URLDecoder.decode(jarWholePath, Charset.defaultCharset().name());
			} catch (UnsupportedEncodingException e) {
				System.out.println(e.toString());
			}
			path = new File(jarWholePath).getParentFile().getAbsolutePath();
		}
		return path;
	}
}
