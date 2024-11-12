package net.media.autotemplate.bean;

public class AclStatus {
    private int statusCode;
    private String errMessage;

    public AclStatus(int statusCode, String errMessage) {
        this.statusCode = statusCode;
        this.errMessage = errMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrMessage() {
        return errMessage;
    }

}
