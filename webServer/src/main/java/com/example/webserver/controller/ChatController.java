package com.example.webserver.controller;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.forchat.MessageModel;
import com.example.webserver.model.Chat;
import com.example.webserver.respons.DeleteResponse;
import com.example.webserver.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class ChatController {

    @Autowired
    ChatService chatService;


    @GetMapping("/chat")
    public List<Chat> getAllChat() {
        return chatService.findAll();
    }

    @GetMapping("/chat/{id}")
    public ResponseEntity<Chat> getChatById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(chatService.findById(id));
    }

    @PostMapping("/chat")
    public Chat createChat(@RequestBody Chat chat){
        return  chatService.save(chat);
    }

    @DeleteMapping("/chat/{id}")
    public ResponseEntity<DeleteResponse> deleteChat(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        chatService.delete(id);
        return ResponseEntity.ok(new DeleteResponse("Chat with id:"+id+" deleted"));
    }
    @PatchMapping(value = "/chat/{id}")
    public ResponseEntity<Chat> patchChat(@PathVariable Long id, @RequestBody Chat chat) throws ResourceNotFoundException {
        return ResponseEntity.ok(chatService.updateQuestion(chat,id));
    }

    @PutMapping("/chat/{id}")
    public ResponseEntity<Chat> putChat(@PathVariable Long id,@RequestBody Chat req) throws ResourceNotFoundException {
        return ResponseEntity.ok(chatService.putMet(id,req));
    }

}














