package com.example.webserver.cnt;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.Project;
import com.example.webserver.model.Topic;
import com.example.webserver.model.TopicMessage;
import com.example.webserver.model.User;
import com.example.webserver.repository.ProjectRepository;
import com.example.webserver.repository.TopicMessageRepository;
import com.example.webserver.repository.TopicRepository;
import com.example.webserver.repository.UserRepository;
import com.example.webserver.service.TopicMessageService;
import com.example.webserver.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@Controller
public class TopicControllerWeb {

    @Autowired
    UserRepository userRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    TopicService topicService;

    @Autowired
    TopicMessageService topicMessageService;
    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    TopicMessageRepository topicMessageRepository;

    @GetMapping("/topic/{id}")
    public String topic(HttpServletRequest request, @PathVariable(value = "id") Long id, Model model){
        User user = userRepository.findUserByUsername(request.getRemoteUser());
        model.addAttribute("role",user.printRole());
        model.addAttribute("user",request.getRemoteUser());
        model.addAttribute("userFN",user.getUserLN());
        Topic topic =  topicRepository.findById(id).orElse(new Topic());
        model.addAttribute("topic",topic);
        return "/topic";
    }

    @GetMapping("/topic/add/{id}")
    public String addTopic(@PathVariable(value = "id") Long id,Model model){
        model.addAttribute("projectId",id);
        return "/add_topic";
    }

    @PostMapping("/topic/add")
    public String addTopic(@RequestParam String nameTopic, @RequestParam String category, @RequestParam String projectId , Model model){

        System.out.println("туть");
        Project project =   projectRepository.findById(Long.valueOf(projectId)).orElse(new Project());
        System.out.println(project);
        topicRepository.save(new Topic(project,nameTopic,category));

        return "redirect:/project/"+projectId;
    }

    @GetMapping("/topic/edit/{id}")
    public String projectEdit(@PathVariable(value = "id") Long id,Model model){

        Topic topic =  topicRepository.findById(id).orElse(new Topic());

        model.addAttribute("topic",topic);
        return "/edit_topic";
    }

    @PostMapping("/topic/edit/{id}")
    public String projectEdit(@PathVariable(value = "id") Long id,@RequestParam String nameTopic, @RequestParam String category, Model model) throws ResourceNotFoundException {

        Topic topic =  topicRepository.findById(id).orElse(new Topic());
        topic.setNameTopic(nameTopic);
        topic.setCategory(category);
        topicService.updateTopic(topic,id);

        model.addAttribute("topic",topic);
        return "redirect:/topic/"+id;
    }

    @GetMapping("/topic/delete/{id}")
    public String projectDelete(@PathVariable(value = "id") Long id,Model model) throws ResourceNotFoundException {

        Topic topic = topicRepository.findById(id).orElse(new Topic());

        ArrayList<TopicMessage> topicMessage = topicMessageRepository
                .findAllByTopicId(topicRepository.findById(id).orElse(new Topic()));

        topicMessage.forEach(c -> {
            try {
                topicMessageService.delete(c.getId());
            } catch (ResourceNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        topicService.delete(id);
        return "redirect:/project/" + topic.getProjectId().getId();
    }


}














