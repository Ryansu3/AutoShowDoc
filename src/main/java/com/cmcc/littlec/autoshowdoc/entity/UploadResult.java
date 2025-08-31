package com.cmcc.littlec.autoshowdoc.entity;

/**
 * 文档上传结果
 * @program: AutoShowDoc
 * @ClassName: UploadResult
 * @author: suran
 * @create: 2025-08-31 13:24
 */
public class UploadResult {
    private String methodName;
    private boolean showDocSuccess;
    private boolean runApiSuccess;
    private String showDocMessage;
    private String runApiMessage;

    public UploadResult(String methodName) {
        this.methodName = methodName;
    }

    public String getRunApiMessage() {
        return runApiMessage;
    }

    public void setRunApiMessage(String runApiMessage) {
        this.runApiMessage = runApiMessage;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public boolean isShowDocSuccess() {
        return showDocSuccess;
    }

    public void setShowDocSuccess(boolean showDocSuccess) {
        this.showDocSuccess = showDocSuccess;
    }

    public boolean isRunApiSuccess() {
        return runApiSuccess;
    }

    public void setRunApiSuccess(boolean runApiSuccess) {
        this.runApiSuccess = runApiSuccess;
    }

    public String getShowDocMessage() {
        return showDocMessage;
    }

    public void setShowDocMessage(String showDocMessage) {
        this.showDocMessage = showDocMessage;
    }
}