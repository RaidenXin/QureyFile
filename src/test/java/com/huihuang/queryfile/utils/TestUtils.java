package com.huihuang.queryfile.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;

/**
 * 测试工具类
 * @author: JiangJi
 * @Descriotion:
 * @Date:Created in 2023/2/12 18:23
 */
public class TestUtils {


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
                Workbook workbook = new XSSFWorkbook(is);
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
                e.printStackTrace();
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
}
