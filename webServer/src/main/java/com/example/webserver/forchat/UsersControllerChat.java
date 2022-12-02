
package com.example.webserver.forchat;

import com.example.webserver.model.*;

import com.example.webserver.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@CrossOrigin
public class UsersControllerChat {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ChatRepository chatRepository;

    @Autowired
    MessageRepository messageRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    TopicMessageRepository topicMessageRepository;

    @GetMapping("/topicMessageData/{topic}")
    public  ArrayList<TopicMessage> register(@PathVariable String topic) {
        System.out.println("handling register user request: " + topic);

        ArrayList<TopicMessage> topicMessage = topicMessageRepository
                .findAllByTopicId(topicRepository.findById(Long.valueOf(topic)).orElse(new Topic()));

        return topicMessage;
    }

    @GetMapping("/fetchAllUsers/{userName}")
    public Set<Message> fetchAllNoMe(@PathVariable String userName) {

        String[] usersStr = userName.split(" ");
        User recipient = userRepository.findUserByUsername(usersStr[1]);
        User user = userRepository.findUserByUsername(usersStr[0]);


        Set<Message> users = messageRepository.findAllByUserIdInAndRecipientIdIn(Arrays.asList(user, recipient),Arrays.asList(user, recipient));

     /*   Set<Message> messages = new HashSet<>();
        chat.forEach(messages::add);*/

        return users;
    }
}

