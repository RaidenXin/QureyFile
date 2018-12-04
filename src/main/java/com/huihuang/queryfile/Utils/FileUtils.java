package com.huihuang.queryfile.Utils;

import com.huihuang.queryfile.QueryFileProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class FileUtils {
    /**
     * *解析文件返回文件内容字符串
     * @param file
     * @param endFileName
     * @return
     */
    @SuppressWarnings("resource")
    public static  String fileParse(File file, String endFileName) {
        String fileName = file.getName();
        ByteBuffer buffer = ByteBuffer.allocate(QueryFileProcessor.SIZE);
        StringBuffer stringBuffer = new StringBuffer(QueryFileProcessor.ENPTY_STR);
        if (file.isFile() && fileName.endsWith(endFileName)) {
            try (FileChannel fileChannel = new FileInputStream(file).getChannel()) {
                Charset encoded = Charset.defaultCharset();
                while (fileChannel.read(buffer) != -1) {
                    buffer.flip();
                    stringBuffer.append(encoded.decode(buffer));
                    buffer.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stringBuffer.toString();
    }
}
