package com.huihuang.queryfile.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

public class FileUtils {

    private static final String EXCEL2007 = ".xlsx";
    private static final String EXCEL2003 = ".xls";
    private static final String HTML = ".html";
    private static final String TXT = ".txt";
    private static final String XML = ".xml";
    private static final String WORD2003 = "doc";
    private static final String WORD2007 = "docx";


    private static final String GBK = "gbk";
    private static final String UTF8 = "utf-8";
    /**
     * *解析文件返回文件内容字符串
     * @param file
     * @param endFileName
     * @return
     */
    @SuppressWarnings("resource")
    public static String fileParse(File file, String endFileName) {
        if (file.isFile()){
            if(EXCEL2003.equals(endFileName) || EXCEL2007.equals(endFileName)){
                return parseExcel(file,endFileName);
            }else if (HTML.equals(endFileName) || TXT.equals(endFileName) || XML.equals(endFileName)){
                return parseHtmlAndTxt(file,endFileName);
            }else if (WORD2003.equals(endFileName) || WORD2007.equals(endFileName)){
                return parseWord(file,endFileName);
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 解析页面或者文本文件
     * @param file
     * @param endFileName
     * @return
     */
    private static String parseHtmlAndTxt(File file, String endFileName){
        String fileName = file.getName();
        ByteBuffer buffer = ByteBuffer.allocate(QueryFileProcessor.SIZE);
        StringBuffer stringBuffer = new StringBuffer(QueryFileProcessor.ENPTY_STR);
        if (file.isFile() && fileName.endsWith(endFileName)) {
            try (FileChannel fileChannel = new FileInputStream(file).getChannel()) {
                Charset encoded = Charset.forName(GBK);
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

    /**
     * 解析Excel
     * @param file
     * @param endFileName
     * @return
     */
    private static String parseExcel(File file, String endFileName){
        StringBuilder builder = new StringBuilder(StringUtils.EMPTY);
        if (file.isFile() && file.getName().endsWith(endFileName)) {
            try (InputStream is = new FileInputStream(file)) {
                XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
                for (int numSheet = 0,n = xssfWorkbook.getNumberOfSheets(); numSheet < n; numSheet++) {
                    XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
                    if (null == xssfSheet){
                        continue;
                    }
                    for (int j = 0, x = xssfSheet.getLastRowNum() + 1; j < x;j++){
                        XSSFRow row = xssfSheet.getRow(j);
                        builder.append(getValue(row));
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return builder.toString();
    }

    /**
     * 获取一行的所有字符
     * @param row
     * @return
     */
    private static String getValue(XSSFRow row) {
        StringBuilder builder = new StringBuilder(StringUtils.EMPTY);
        if (null != row){
            for (int i = 0,n = row.getLastCellNum(); i < n; i++){
                XSSFCell xssfCell = row.getCell(i);
                xssfCell.setCellType(XSSFCell.CELL_TYPE_STRING);
                builder.append(String.valueOf(xssfCell.getStringCellValue()));
                builder.append(";");
            }
        }
        return builder.toString();
    }

    /**
     * 解析Word文档
     * @param file
     * @param endFileName
     * @return
     */
    private static String parseWord(File file, String endFileName){
        StringBuilder builder = new StringBuilder(StringUtils.EMPTY);
        if (file.isFile()) {
            try(FileInputStream fileInputStream = new FileInputStream(file)){
                String fileName = file.getName();
                if (fileName.endsWith(WORD2003)){
                    WordExtractor ex = new WordExtractor(fileInputStream);
                    builder.append(ex.getText());
                }else if (fileName.endsWith(WORD2007)){
                    OPCPackage opcPackage = POIXMLDocument.openPackage(file.getPath());
                    POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
                    builder.append(extractor.getText());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return builder.toString();
    }
}
