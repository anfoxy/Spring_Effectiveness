package com.example.webserver.cnt;

import com.example.webserver.model.Role;
import com.example.webserver.model.User;
import com.example.webserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/registration")
    public String registration() {

        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Map<String, Object> model) {
        User userFromDb = userRepo.findUserByUsername(user.getUsername());

        if (userFromDb != null) {
            model.put("message", "User exists!");
            return "registration";
        }
        if(!user.getPassword().equals(user.getMatchingPassword())) {
            model.put("message", "password doesn't match");
            return "registration";
        }
        if(user.getCode().equals("111111111"))user.setRoles(Collections.singleton(Role.ROLE_ADMIN));
        else user.setRoles(Collections.singleton(Role.ROLE_USER));

        userRepo.save(user);

        return "redirect:/login";
    }
}