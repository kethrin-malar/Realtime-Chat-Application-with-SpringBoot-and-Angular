package com.hum.chatapp.service.impl;

import com.hum.chatapp.dto.ApiResponse;
import com.hum.chatapp.entity.Conversation;
import com.hum.chatapp.entity.User;
import com.hum.chatapp.repository.ConversationRepository;
import com.hum.chatapp.repository.UserRepository;
import com.hum.chatapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ConversationRepository conversationRepository) {
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
    }

    @Override
    public ResponseEntity<ApiResponse> saveUser(User user) {
        try {
            user = userRepository.save(user);
            ApiResponse response = new ApiResponse(200, "Success", "OK", user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            ApiResponse response = new ApiResponse(200, "Failed", "Email already registered", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            ApiResponse response = new ApiResponse(200, "Success", "OK", user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse response = new ApiResponse(200, "Failed", "User not found", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> findAllUsers() {
        List<User> list = userRepository.findAll();
        ApiResponse response = new ApiResponse(200, "Success", "OK", list);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> findAllUsersExceptThisUserId(int userId) {
        List<User> list = userRepository.findAllUsersExceptThisUserId(userId);
        ApiResponse response = new ApiResponse(200, "Success", "OK", list);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> findConversationIdByUser1IdAndUser2Id(int user1Id, int user2Id) {
        Optional<User> user1 = userRepository.findById(user1Id);
        Optional<User> user2 = userRepository.findById(user2Id);
        
        if (user1.isEmpty() || user2.isEmpty()) {
            ApiResponse response = new ApiResponse(200, "Failed", "User not found", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Optional<Conversation> existingConversation = conversationRepository.findConversationByUsers(user1.get(), user2.get());
        int conversationId = existingConversation.map(Conversation::getConversationId) // This is correct
                .orElseGet(() -> {
                    Conversation newConversation = new Conversation();
                    newConversation.setUser1(user1.get());
                    newConversation.setUser2(user2.get());
                    return conversationRepository.save(newConversation).getConversationId(); // This is correct
                });

        ApiResponse response = new ApiResponse(200, "Success", "OK", conversationId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
