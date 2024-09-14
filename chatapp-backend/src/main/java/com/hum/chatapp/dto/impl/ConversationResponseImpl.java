package com.hum.chatapp.dto.impl;

import java.sql.Timestamp;

import com.hum.chatapp.dto.ConversationResponse;

public class ConversationResponseImpl implements ConversationResponse {
    private Integer conversationId;
    private Integer otherUserId;
    private String otherUserName;
    private String lastMessage;
    private Timestamp lastMessageTimestamp;

    public ConversationResponseImpl(Integer conversationId, Integer otherUserId, String otherUserName, String lastMessage, Timestamp lastMessageTimestamp) {
        this.conversationId = conversationId;
        this.otherUserId = otherUserId;
        this.otherUserName = otherUserName;
        this.lastMessage = lastMessage;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    @Override
    public Integer getConversationId() {
        return conversationId;
    }

    @Override
    public Integer getOtherUserId() {
        return otherUserId;
    }

    @Override
    public String getOtherUserName() {
        return otherUserName;
    }

    @Override
    public String getLastMessage() {
        return lastMessage;
    }

    @Override
    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }
}
