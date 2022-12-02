package com.example.webserver.cnt;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.model.Project;
import com.example.webserver.model.ProjectStaff;
import com.example.webserver.model.Topic;
import com.example.webserver.model.User;
import com.example.webserver.repository.ProjectRepository;
import com.example.webserver.repository.ProjectStaffRepository;
import com.example.webserver.repository.TopicRepository;
import com.example.webserver.repository.UserRepository;
import com.example.webserver.service.ProjectService;
import com.example.webserver.service.ProjectStaffService;
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
import java.util.List;
import java.util.Objects;

@Controller
public class ProjectControllerWeb {

    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    TopicRepository topicRepository;

    @Autowired
    TopicService topicService;
    @Autowired
    ProjectStaffRepository projectStaffRepository;
    @Autowired
    ProjectStaffService projectStaffService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    ProjectService projectService;


    @GetMapping("/projects")
    public String projects(HttpServletRequest request, Model model){


        User user = userRepository.findUserByUsername(request.getRemoteUser());
        Iterable<ProjectStaff> projectStaffs = projectStaffRepository.findAllByUserId(user);

        System.out.println(user.printRole());
        List<Project> project = new ArrayList<>();
        projectStaffs.forEach(customer -> {
            if (Objects.equals(customer.getUserId().getId(), user.getId())) {
                project.add(customer.getProjectId());
            }
        });
        model.addAttribute("role",user.printRole());
        model.addAttribute("projects",project);


        return "/projects";
    }



    @GetMapping("/project/{id}")
    public String project(HttpServletRequest request,@PathVariable(value = "id") Long id,Model model){

        Project project =  projectRepository.findById(id).orElse(new Project());

        Iterable<Topic> topics = topicRepository.findAllByProjectId(project);

        User user = userRepository.findUserByUsername(request.getRemoteUser());

        ArrayList<ProjectStaff> projectStaffs = projectStaffRepository.findAllByProjectId(project);
        ArrayList<String> users = new ArrayList<>();
        for (ProjectStaff s: projectStaffs) {
            users.add(""+s.getUserId().getUserLN() +" "+s.getUserId().getUserFN() + " " +s.getUserId().getUsername());
        }

        model.addAttribute("role",user.printRole());
        model.addAttribute("topic",topics);
        model.addAttribute("project",project);
        model.addAttribute("users",users);
        return "/project";
    }

    @GetMapping("/project/add")
    public String addproject(HttpServletRequest request,Model model){
        ArrayList<User> users = userRepository.findAll();
        User user = userRepository.findUserByUsername(request.getRemoteUser());
        users.remove(user);

        model.addAttribute("users",users);
        return "/add_project";
    }

    @PostMapping("/project/add")
    public String addProject(HttpServletRequest request,@RequestParam String usr,@RequestParam String projectName, @RequestParam String description, Model model){

        Project project = new Project(projectName,description);
        projectRepository.save(project);
        User user = userRepository.findUserByUsername(request.getRemoteUser());
        projectStaffService.save(new ProjectStaff(user,project));

        if(!usr.equals("")) {
            String[] users = usr.split(", ");
            for (String s : users) {
                user = userRepository.findUserByUsername(s);
                projectStaffService.save(new ProjectStaff(user, project));
            }
        }

        return "redirect:/projects";
    }

    @GetMapping("/project/edit/{id}")
    public String projectEdit(@PathVariable(value = "id") Long id,Model model){

        Project project =  projectRepository.findById(id).orElse(new Project());

        model.addAttribute("project",project);
        return "/edit_project";
    }

    @PostMapping("/project/edit/{id}")
    public String projectEdit(@PathVariable(value = "id") Long id,@RequestParam String projectName, @RequestParam String description, Model model) throws ResourceNotFoundException {

        Project project =  projectRepository.findById(id).orElse(new Project());
        project.setProjectName(projectName);
        project.setDescription(description);
        projectService.updateProject(project,id);

        model.addAttribute("project",project);
        return "redirect:/project/"+id;
    }


    @GetMapping("/project/delete/{id}")
    public String projectDelete(@PathVariable(value = "id") Long id,Model model) throws ResourceNotFoundException {

        Project project =  projectRepository.findById(id).orElse(new Project());
        Iterable<Topic> topics =  topicRepository.findAllByProjectId(project);
        Iterable<ProjectStaff> projectStaffs =  projectStaffRepository.findAllByProjectId(project);
        topics.forEach(c -> {
            try {
                topicService.delete(c.getId());
            } catch (ResourceNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        projectStaffs.forEach(c -> {
            try {
                projectStaffService.delete(c.getId());
            } catch (ResourceNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        projectService.delete(id);

        return "redirect:/projects";
    }


}














