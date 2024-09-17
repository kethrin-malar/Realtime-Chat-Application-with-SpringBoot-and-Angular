package com.chat.demo.dto;

import java.sql.Timestamp;

public interface ConversationResponse {

    Integer getConversationId();

    Integer getOtherUserId();

    String getOtherUserName();

    String getLastMessage();

    Timestamp getLastMessageTimestamp();
}