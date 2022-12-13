
package com.example.webserver.forchat;

import com.example.webserver.model.*;

import com.example.webserver.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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


        Set<Message> users = messageRepository.findAllByUserIdInAndRecipientIdInOrderByIdAsc(Arrays.asList(user, recipient),Arrays.asList(user, recipient));

        return users;
    }


    @GetMapping("/chats/save_file/{name}")
    public void  chatToUser( @PathVariable(value = "name") String fileName, HttpServletResponse response) {

        //Авторизованные пользователи смогут скачать файл
        Path file = Paths.get("D:\\fileServer\\"+ fileName);
        if (Files.exists(file)){
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            response.setContentType("application/vnd.ms-excel");

            try {
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            } catch (IOException e) {
                throw new RuntimeException("IOError writing file to output stream");
            }
        }
        System.out.println("Скачиваем файлик "+ fileName);
    }

}

