package com.huihuang.queryfile.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Objects;

import com.huihuang.queryfile.handler.QueryFileProcessor;
import com.huihuang.queryfile.logs.Logger;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

public class FileUtils {

    private static final String EXCEL2007 = ".xlsx";
    private static final String EXCEL2003 = ".xls";
    private static final String HTML = ".html";
    private static final String TXT = ".txt";
    private static final String XML = ".xml";
    private static final String WORD2003 = ".doc";
    private static final String WORD2007 = ".docx";

    private static final Logger logger = Logger.newInstance();


    /**
     * 工具类最好不要有构造方法
     */
    private FileUtils() {
    }

    /**
     * *解析文件返回文件内容字符串
     *
     * @param file
     * @param endFileName
     * @return
     */
    @SuppressWarnings("resource")
    public static String fileParse(File file, String endFileName) {
        if (file.isFile()) {
            if (EXCEL2003.equals(endFileName) || EXCEL2007.equals(endFileName)) {
                return parseExcel(file, endFileName);
            } else if (HTML.equals(endFileName) || TXT.equals(endFileName) || XML.equals(endFileName)) {
                return parseHtmlAndTxt(file, endFileName);
            } else if (WORD2003.equals(endFileName) || WORD2007.equals(endFileName)) {
                return parseWord(file);
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 解析页面或者文本文件
     *
     * @param file
     * @param endFileName
     * @return
     */
    private static String parseHtmlAndTxt(File file, String endFileName) {
        String fileName = file.getName();
        ByteBuffer buffer = ByteBuffer.allocate(QueryFileProcessor.SIZE);
        StringBuffer stringBuffer = new StringBuffer(QueryFileProcessor.ENPTY_STR);
        if (file.isFile() && fileName.endsWith(endFileName)) {
            try (FileChannel fileChannel = new FileInputStream(file).getChannel()) {
                Charset encoded = Charset.forName(EncodeUtil.getEncode(file, true));
                while (fileChannel.read(buffer) != -1) {
                    buffer.flip();
                    stringBuffer.append(encoded.decode(buffer));
                    buffer.clear();
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return stringBuffer.toString();
    }

    /**
     * 解析Excel
     *
     * @param file
     * @param endFileName
     * @return
     */
    private static String parseExcel(File file, String endFileName) {
        StringBuilder builder = new StringBuilder(StringUtils.EMPTY);
        if (file.isFile() && file.getName().endsWith(endFileName)) {
            try (InputStream is = new FileInputStream(file)) {
                Workbook workbook;
                if (endFileName.equals(EXCEL2003)) {
                    workbook = new HSSFWorkbook(is);
                } else {
                    workbook = new XSSFWorkbook(is);
                }
                for (int numSheet = 0, n = workbook.getNumberOfSheets(); numSheet < n; numSheet++) {
                    Sheet sheet = workbook.getSheetAt(numSheet);
                    if (Objects.isNull(sheet)) {
                        continue;
                    }
                    for (int j = 0, x = sheet.getLastRowNum() + 1; j < x; j++) {
                        Row row = sheet.getRow(j);
                        builder.append(getValue(row));
                    }
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return builder.toString();
    }

    /**
     * 获取一行的所有字符
     *
     * @param row
     * @return
     */
    private static String getValue(Row row) {
        StringBuilder builder = new StringBuilder(StringUtils.EMPTY);
        if (null != row) {
            for (int i = 0, n = row.getLastCellNum(); i < n; i++) {
                Cell cell = row.getCell(i);
                cell.setCellType(XSSFCell.CELL_TYPE_STRING);
                builder.append(cell.getStringCellValue());
                builder.append(";");
            }
        }
        return builder.toString();
    }

    /**
     * 解析Word文档
     *
     * @param file
     * @return
     */
    private static String parseWord(File file) {
        StringBuilder builder = new StringBuilder(StringUtils.EMPTY);
        if (file.isFile()) {
            String fileName = file.getName();
            if (fileName.endsWith(WORD2003)) {
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    WordExtractor ex = new WordExtractor(fileInputStream);
                    builder.append(ex.getText());
                } catch (Exception e) {
                    logger.error(e);
                }
            } else if (fileName.endsWith(WORD2007)) {
                try (OPCPackage opcPackage = POIXMLDocument.openPackage(file.getPath())) {
                    POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
                    builder.append(extractor.getText());
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        }
        return builder.toString();
    }
}
