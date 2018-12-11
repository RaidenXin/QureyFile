package com.huihuang.queryfile.information;

public class TaskInformation {

    private String path;
    private String endFileName;
    private String content;

    public TaskInformation(String path,String endFileName,String content){
        this.path = path;
        this.endFileName = endFileName;
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
}
