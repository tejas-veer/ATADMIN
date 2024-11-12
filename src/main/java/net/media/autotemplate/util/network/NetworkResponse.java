package net.media.autotemplate.util.network;

import net.media.autotemplate.bean.ApiResponse;
import org.apache.http.HttpStatus;

/*
    Created by shubham-ar
    on 18/10/17 3:32 PM   
*/
public class    NetworkResponse {
    private final int status;
    private final String body;

    public NetworkResponse(int status, String body) {
        this.status = status;
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }

    public ApiResponse getApiResponse() {
        return new ApiResponse(this.status, this.body);
    }

    public boolean isOK() {
        return this.status == HttpStatus.SC_OK;
    }

    public boolean notOK() {
        return !isOK();
    }
}
