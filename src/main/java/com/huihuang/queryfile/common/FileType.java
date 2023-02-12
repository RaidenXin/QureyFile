package com.huihuang.queryfile.common;

/**
 * 文件后缀
 * @author: JiangJi
 * @Descriotion:
 * @Date:Created in 2023/2/12 20:35
 */
public enum FileType {

    EXCEL2007("Excel2007",".xlsx"),
    EXCEL2003("Excel2003",".xls"),
    HTML("Html页面",".html"),
    TXT("txt文本",".txt"),
    XML("xml文本",".xml"),
    WORD2003("Word2003",".doc"),
    WORD2007("Word2007",".docx");

    private String description;
    private String suffix;

    public String getDescription() {
        return description;
    }

    public String getSuffix() {
        return suffix;
    }

    private FileType(String description, String suffix) {
        this.description = description;
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return description;
    }
}
