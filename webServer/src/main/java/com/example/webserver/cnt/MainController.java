package com.example.webserver.cnt;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.forchat.MessageModel;
import com.example.webserver.model.*;
import com.example.webserver.repository.ChatRepository;
import com.example.webserver.repository.TopicRepository;
import com.example.webserver.repository.UserRepository;
import com.example.webserver.service.*;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.*;

@Controller
public class MainController {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageService messageService;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    TopicMessageService topicMessageService;
    @Autowired
    ChatService chatService;

    @GetMapping(value = {"/", "/main"})
    public String index(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        return "/main";
    }

    @GetMapping("/profile")
    public String registration(HttpServletRequest request,Model model) throws ResourceNotFoundException {
        User user = userRepository.findUserByUsername(request.getRemoteUser());

        model.addAttribute("user",user);
        return "/profile";
    }

    @RequestMapping("/chats")
    public String chat(HttpServletRequest request, Model model) {
        User user = userRepository.findUserByUsername(request.getRemoteUser());

        model.addAttribute("username", user.getUserFN());

        return "chat";
    }



    @GetMapping("/personal")
    public String personal(HttpServletRequest request, Model model) {
        ArrayList<User> users = userRepository.findAll();
        User user = userRepository.findUserByUsername(request.getRemoteUser());
        users.remove(user);


        model.addAttribute("users",users);

        return "/personal";
    }
    @GetMapping("/profile_personal/{id}")
    public String projectEdit(@PathVariable(value = "id") Long id,Model model){
        User user = userRepository.findById(id).orElse(new User());

        model.addAttribute("user",user);
        return "/profile_personal";
    }

    @GetMapping("/chatgroup")
    public String chatgroup(HttpServletRequest request, Model model) {
        User user = userRepository.findUserByUsername(request.getRemoteUser());

        Iterable<Chat> chat =  chatRepository.findAllByUserId1(user);


        model.addAttribute("chat",chat);

        return "/chatgroup";
    }

    @RequestMapping("/chats/add/{id}")
    public String chatAdd( @PathVariable(value = "id") Long id,HttpServletRequest request, Model model) {

        User user = userRepository.findUserByUsername(request.getRemoteUser());
        User recipient = userRepository.findById(id).orElse(new User());

        if(chatRepository.findAllByUserId1AndUserId2(user,recipient).isEmpty()) {
            chatService.save(new Chat(user, recipient));
            chatService.save(new Chat(recipient, user));
        }
        model.addAttribute("userName", user.getUsername());
        model.addAttribute("recipient", recipient.getUsername());
        model.addAttribute("recipientId", id);
        model.addAttribute("userId", user.getId());

        return "chat_user";
    }

    @RequestMapping("/chats/{id}")
    public String chatToUser( @PathVariable(value = "id") Long id,HttpServletRequest request, Model model) {
        User user = userRepository.findUserByUsername(request.getRemoteUser());
        User recipient = userRepository.findById(id).orElse(new User());

        model.addAttribute("userName", user.getUsername());
        model.addAttribute("recipientName", user.getUserFN());
        model.addAttribute("recipient", recipient.getUsername());
        model.addAttribute("recipientId", id);
        model.addAttribute("userId", user.getId());

        return "chat_user";
    }



    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat/{to}")
    public void sendMessage(@DestinationVariable String to, Message message) {
        String[] users = to.split(" ");
        String recipientStr =  users[0];
        String userStr = users[1];
        User user = userRepository.findUserByUsername(userStr);
        User recipient = userRepository.findUserByUsername(recipientStr);
        System.out.println(new Date());
        Message msg = new Message(user,recipient,message.getText(), message.getDoc(), new Date());
        messageService.save(msg);

        System.out.println("handling send message: " + message + " to: " + recipientStr);
        simpMessagingTemplate.convertAndSend("/topic/messages/" + recipientStr, message);
    }

    @MessageMapping("/chat_topic/{to}")
    public void sendMessageTopic(@DestinationVariable String to, TopicMessage message) {

        User user = userRepository.findUserByUsername(message.getUser());
        Topic topic = topicRepository.findById(Long.valueOf(to)).orElse(new Topic());
        System.out.println(new Date());
        TopicMessage msg = new TopicMessage(user,topic,message.getText(), message.getDoc(), new Date());
        topicMessageService.save(msg);

        System.out.println("handling send message: " + message + " to: " + to);
        simpMessagingTemplate.convertAndSend("/topic/messages_topic/" + to, message);
    }


    private static final String DIR_TO_UPLOAD = "D:\\fileServer\\";
    @PostMapping("/upload")
    @ResponseBody
    public void upload(@RequestPart("file") MultipartFile file) {
        System.out.println("Uploaded File: ");
        System.out.println("Name : " + file.getName());
        System.out.println("Type : " + file.getContentType());
        System.out.println("Name : " + file.getOriginalFilename());
        System.out.println("Size : " + file.getSize());

        try {
        byte[] bytes = file.getBytes();
        Path path = Paths.get(DIR_TO_UPLOAD + file.getOriginalFilename());
        Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


/*    @MessageMapping("/hello")
    public void send(SimpMessageHeaderAccessor sha, @Payload String username) {
        String message = "Hello from " + sha.getUser().getName();

        simpMessagingTemplate.convertAndSendToUser(username, "/queue/messages", message);
    }*/

/*
        @Autowired private SimpMessagingTemplate messagingTemplate;


        @MessageMapping("/chat")
        public void processMessage(@Payload Message message) {

            System.out.println("Отправка...");
            messagingTemplate.convertAndSendToUser(
                    message.getRecipientId().getUsername(),"/queue/messages", message);

        }*/

/*

    @MessageMapping("/message")
    @SendToUser("/queue/reply")
    public String processMessageFromClient(@Payload String message, Principal principal) throws Exception {
        String name = new Gson().fromJson(message, Map.class).get("name").toString();
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/reply", name);
        return name;
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }
*/

/*    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/secured/room")
    public void sendSpecific(@Payload Message msg, Principal user) throws Exception {
        System.out.println("Отправка...");
        simpMessagingTemplate.convertAndSendToUser(
                msg.getRecipientId().getUsername(), "/secured/user/queue/specific-user", msg);
    }*/
}














