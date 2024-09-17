package com.chat.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chat.demo.entity.Conversation;
import com.chat.demo.entity.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findAllByConversation(Conversation conversation);

    void deleteAllByConversation(Conversation conversation);
}