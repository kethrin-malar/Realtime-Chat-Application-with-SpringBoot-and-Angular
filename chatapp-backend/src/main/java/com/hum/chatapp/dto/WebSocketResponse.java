package com.hum.chatapp.dto;

public class WebSocketResponse {
    private String status;
    private Object data; // Can be List<ConversationResponse> or any other type

    // Private constructor to prevent direct instantiation
    private WebSocketResponse(Builder builder) {
        this.status = builder.status;
        this.data = builder.data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    // Static inner Builder class
    public static class Builder {
        private String status;
        private Object data;

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public WebSocketResponse build() {
            return new WebSocketResponse(this);
        }
    }
}
