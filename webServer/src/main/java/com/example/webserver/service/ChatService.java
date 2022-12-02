package com.example.webserver.service;

import com.example.webserver.dto.ChatDTO;
import com.example.webserver.dto.MessageDTO;
import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.mapper.CustomerMapper;
import com.example.webserver.model.Chat;
import com.example.webserver.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    @Autowired
    ChatRepository chatRepository;

    @Autowired
    CustomerMapper mapper;


    public Chat putMet(Long id, Chat req) throws ResourceNotFoundException {
        Chat message = findById(id);
        message.setUserId2(req.getUserId2());
        message.setUserId1(req.getUserId1());
        chatRepository.save(message);
        return message;
    }

    public Chat updateQuestion(Chat d, Long id) throws ResourceNotFoundException {
        ChatDTO dto = new ChatDTO(d);
        dto.setId(id);
        Chat chat = findById(id);
        mapper.updateChatFromDto(dto, chat);
        chatRepository.save(chat);
        return chat;
    }

    public void delete(Long id) throws ResourceNotFoundException {
        Chat chat = findById(id);
        chatRepository.delete(chat);
    }
    public Chat save(Chat chat){
        return chatRepository.save(chat);
    }
    public Chat findById(Long id) throws ResourceNotFoundException {
        return chatRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Chat not found for id:" + id.toString() + ""));
    }
    public List<Chat> findAll() {
        return chatRepository.findAll();
    }

}
