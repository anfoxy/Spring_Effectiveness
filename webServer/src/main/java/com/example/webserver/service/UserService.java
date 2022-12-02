package com.example.webserver.service;

import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.dto.UserDTO;
import com.example.webserver.mapper.CustomerMapper;
import com.example.webserver.model.User;
import com.example.webserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomerMapper mapper;

    public User putMet(Long id, User s) throws ResourceNotFoundException {
        User user = findById(id);
        user.setUsername(s.getUsername());
        user.setPassword(s.getPassword());
        user.setCode(s.getCode());
        user.setMatchingPassword(s.getMatchingPassword());
        user.setUserLN(s.getUserLN());
        user.setUserFN(s.getUserFN());
        user.setRoles(s.getRoles());
        userRepository.save(user);
        return user;
    }



    public User updateUser(User s, Long id) throws ResourceNotFoundException {
        UserDTO dto = new UserDTO(s);
        dto.setId(id);
        User user = findById(id);
        mapper.updateUserFromDto(dto, user);
        userRepository.save(user);
        return user;
    }

    public Boolean login(String username, String password) {
        return !userRepository.searchUserByLoginAndPassword(username, password).isEmpty();
    }
    public Boolean register(User user) {
        if(userRepository.findUserForRegister(user.getUsername(),user.getUserFN(), user.getCode(), user.getUserLN()).isEmpty()){
            userRepository.save(user);
            return true;
        } else return false;
    }


    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public void delete(Long id) throws ResourceNotFoundException {
        User user = findById(id);
        userRepository.delete(user);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findById(Long id) throws ResourceNotFoundException {
        return userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User not found for id:" + id + ""));
    }
    public List<User> findAll() {
        return userRepository.findAll();
    }

}
