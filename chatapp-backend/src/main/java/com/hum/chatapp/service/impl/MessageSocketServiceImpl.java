package com.hum.chatapp.service.impl;

import com.hum.chatapp.dto.MessageRequest;
import com.hum.chatapp.dto.MessageResponse;
import com.hum.chatapp.dto.WebSocketResponse;
import com.hum.chatapp.dto.ConversationResponse;
import com.hum.chatapp.dto.impl.ConversationResponseImpl;
import com.hum.chatapp.entity.Conversation;
import com.hum.chatapp.entity.Message;
import com.hum.chatapp.entity.User;
import com.hum.chatapp.exception.UserNotFoundException;
import com.hum.chatapp.repository.ConversationRepository;
import com.hum.chatapp.repository.MessageRepository;
import com.hum.chatapp.repository.UserRepository;
import com.hum.chatapp.service.MessageSocketService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageSocketServiceImpl implements MessageSocketService {
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public MessageSocketServiceImpl(SimpMessagingTemplate messagingTemplate, 
                                    UserRepository userRepository, 
                                    ConversationRepository conversationRepository, 
                                    MessageRepository messageRepository) {
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public void sendUserConversationByUserId(int userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        List<Conversation> conversations = conversationRepository.findByUser1OrUser2(user, user);
        List<ConversationResponse> conversationResponses = conversations.stream()
            .map(conversation -> new ConversationResponseImpl(
                conversation.getConversationId(),
                getOtherUserId(user, conversation),
                getOtherUserName(user, conversation),
                getLastMessage(conversation),
                new Timestamp(System.currentTimeMillis())
            ))
            .collect(Collectors.toList());

        messagingTemplate.convertAndSend(
            "/topic/user/" + userId,
            new WebSocketResponse.Builder()
                .status("ALL")
                .data(conversationResponses)
                .build()
        );
    }

    private Integer getOtherUserId(User currentUser, Conversation conversation) {
        return conversation.getUser1().equals(currentUser) ? 
               conversation.getUser2().getUserId() : 
               conversation.getUser1().getUserId();
    }

    private String getOtherUserName(User currentUser, Conversation conversation) {
        return conversation.getUser1().equals(currentUser) ? 
               conversation.getUser2().getFirstName() : 
               conversation.getUser1().getFirstName();
    }

    private String getLastMessage(Conversation conversation) {
        // Placeholder: you may want to fetch the last message from the conversation
        return "Last message"; 
    }

    @Override
    public void sendMessagesByConversationId(int conversationId) {
        Conversation conversation = new Conversation();
        conversation.setConversationId(conversationId);
        List<Message> messages = messageRepository.findAllByConversation(conversation);
        
        List<MessageResponse> messageResponses = messages.stream()
            .map(message -> new MessageResponse(
                message.getMessageId(),
                message.getMessage(),
                Timestamp.from(message.getTimestamp().atZone(ZoneId.systemDefault()).toInstant()),
                message.getSender().getUserId(),
                message.getReceiver().getUserId()
            ))
            .collect(Collectors.toList());

        messagingTemplate.convertAndSend("/topic/conv/" + conversationId, 
            new WebSocketResponse.Builder()
                .status("ALL")
                .data(messageResponses)
                .build()
        );
    }

    @Override
    public void saveMessage(MessageRequest msg) {
        User sender = userRepository.findById(msg.getSenderId())
            .orElseThrow(() -> new UserNotFoundException("Sender not found"));
        User receiver = userRepository.findById(msg.getReceiverId())
            .orElseThrow(() -> new UserNotFoundException("Receiver not found"));
        
        Conversation conversation = conversationRepository.findConversationByUsers(sender, receiver)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        Message newMessage = new Message();
        newMessage.setMessage(msg.getMessage());
        newMessage.setTimestamp(msg.getTimestamp());
        newMessage.setConversation(conversation);
        newMessage.setSender(sender);
        newMessage.setReceiver(receiver);
        
        Message savedMessage = messageRepository.save(newMessage);
        
        MessageResponse messageResponse = new MessageResponse(
            savedMessage.getMessageId(),
            savedMessage.getMessage(),
            Timestamp.from(savedMessage.getTimestamp().atZone(ZoneId.systemDefault()).toInstant()),
            savedMessage.getSender().getUserId(),
            savedMessage.getReceiver().getUserId()
        );

        messagingTemplate.convertAndSend("/topic/conv/" + msg.getConversationId(), 
            new WebSocketResponse.Builder()
                .status("ADDED")
                .data(messageResponse)
                .build()
        );

        sendUserConversationByUserId(msg.getSenderId());
        sendUserConversationByUserId(msg.getReceiverId());
    }

    @Transactional
    @Override
    public void deleteConversationByConversationId(int conversationId) {
        Conversation conversation = new Conversation();
        conversation.setConversationId(conversationId);
        messageRepository.deleteAllByConversation(conversation);
        conversationRepository.deleteById(conversationId);
    }

    @Override
    public void deleteMessageByMessageId(int conversationId, int messageId) {
        messageRepository.deleteById(messageId);
        sendMessagesByConversationId(conversationId);
    }
}
