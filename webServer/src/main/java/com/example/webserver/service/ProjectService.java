package com.example.webserver.service;

import com.example.webserver.dto.ProjectDTO;
import com.example.webserver.exception.ResourceNotFoundException;
import com.example.webserver.mapper.CustomerMapper;
import com.example.webserver.model.Project;
import com.example.webserver.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    CustomerMapper mapper;


    public Project putMet(Long id, Project req) throws ResourceNotFoundException {
        Project message = findById(id);
        message.setDescription(req.getDescription());
        message.setProjectName(req.getProjectName());
        projectRepository.save(message);
        return message;
    }

    public Project updateProject(Project d, Long id) throws ResourceNotFoundException {
        ProjectDTO dto = new ProjectDTO(d);
        dto.setId(id);
        Project message = findById(id);
        mapper.updateProjectFromDto(dto, message);
        projectRepository.save(message);
        return message;
    }


    public void delete(Long id) throws ResourceNotFoundException {
        Project project = findById(id);
        projectRepository.delete(project);
    }
    public Project save(Project project){
        return projectRepository.save(project);
    }
    public Project findById(Long id) throws ResourceNotFoundException {
        return projectRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Project not found for id:" + id.toString() + ""));
    }
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

}
