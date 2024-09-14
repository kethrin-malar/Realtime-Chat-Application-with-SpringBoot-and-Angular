package com.hum.chatapp.dto;



import java.time.LocalDateTime;


public class MessageRequest {
    Integer conversationId;
    Integer senderId;
    Integer receiverId;
    String message;
    LocalDateTime timestamp;
	public Integer getConversationId() {
		return conversationId;
	}
	public void setConversationId(Integer conversationId) {
		this.conversationId = conversationId;
	}
	public Integer getSenderId() {
		return senderId;
	}
	public void setSenderId(Integer senderId) {
		this.senderId = senderId;
	}
	public Integer getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(Integer receiverId) {
		this.receiverId = receiverId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
    
    
}
