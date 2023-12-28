package com.example.Controller;

import com.example.repository.TaskRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {

    public final TaskRepository taskRepository;

    public AttachmentController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostMapping("/{taskId}/upload")
    public String upload(@PathVariable int taskId, MultipartFile multipartFile) {
         String filename = multipartFile.getOriginalFilename();

         return filename;
    }

}
