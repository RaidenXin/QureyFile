package com.huhuang.queryfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/**
 * *主要工作函数
 * @author Raiden
 *
 */
public class QueryFileProcessor {

	private static final int SIZE = 1024;
	/**
	 * *查询文件的方法,如果是文件则直接访问其内容,如果不是则遍历其子目录
	 * @param path
	 * @param endFileName
	 * @param content
	 * @return
	 */
	public List<String> queryFile(String path,String endFileName,String content) {
		path = getLocalPath(path);
		List<String> result = new ArrayList<>();
		File file = new File(path);
		if (!file.isFile()) {
			File[] files = file.listFiles();
			for (File file2 : files) {
				if (fileParse(file2, endFileName).contains(content)) {
					result.add(file2.getName());
				}
			}
		}else {
			if (fileParse(file, endFileName).contains(content)) {
				result.add(file.getName());
			}
		}
		return result;
	}
	/**
	 * *填写的文件地址为空,则默认使用当前运行文件所在地址
	 * @param path
	 * @return
	 */
	private String getLocalPath(String path) {
		if (null == path || "".equals(path)) {
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
	
	/**
	 * *解析文件返回文件内容字符串
	 * @param file
	 * @param endFileName
	 * @return
	 */
	@SuppressWarnings("resource")
	private String fileParse(File file,String endFileName) {
		String fileName = file.getName();
		ByteBuffer buffer = ByteBuffer.allocate(SIZE);
		StringBuffer stringBuffer = new StringBuffer("");
		if (file.isFile() && fileName.endsWith(endFileName)) {
			FileChannel fileChannel = null;
			try {
				Charset encoded = Charset.defaultCharset();
				fileChannel = new FileInputStream(file).getChannel();
				while (fileChannel.read(buffer) != -1) {
					buffer.flip();
					stringBuffer.append(encoded.decode(buffer));
					buffer.clear();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				try {
					fileChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return stringBuffer.toString();
	}
}
