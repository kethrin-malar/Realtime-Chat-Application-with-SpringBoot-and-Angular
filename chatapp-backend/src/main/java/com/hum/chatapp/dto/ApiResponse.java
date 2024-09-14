package com.hum.chatapp.dto;


public class ApiResponse {
    private Integer statusCode;
    private String status;
    private String reason;
    private Object data;

    public ApiResponse() {}

    public ApiResponse(Integer statusCode, String status, String reason, Object data) {
        this.statusCode = statusCode;
        this.status = status;
        this.reason = reason;
        this.data = data;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
