package com.huihuang.queryfile.logs;

/**
 * 日志类。用的最简单的单例
 */
public final class Logger {

    private static final LogsStack stack = LogsStack.newInstance();
    private static final Logger logger = new Logger();

    private Logger(){
    }

    public final static Logger newInstance(){
        return logger;
    }

    public void info(String log){
        stack.push(log);
    }

    public void error(String errorStr){
        stack.errorPush(errorStr);
    }

    public void error(Exception e){
        StringBuilder builder = new StringBuilder();
        builder.append(e.getClass().getName());
        builder.append("\t:");
        builder.append(e.getMessage());
        builder.append("\r\n");
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            builder.append(stackTraceElement.getClassName());
            builder.append("\t");
            builder.append(stackTraceElement.getFileName());
            builder.append("\t");
            builder.append(stackTraceElement.getMethodName());
            builder.append("\t");
            builder.append("Line in line");
            builder.append(stackTraceElement.getLineNumber());
            builder.append("\r\n");
        }
        stack.errorPush(builder.toString());
    }
}
