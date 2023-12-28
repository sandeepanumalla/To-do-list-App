package com.example.service.impl;

import com.example.model.Task;
import com.example.model.User;
import com.example.repository.TaskRepository;
import com.example.service.FileUploadService;
import com.example.service.factory.NotificationType;
import com.example.service.impl.files.FileEmptyValidator;
import com.example.service.impl.files.FileTypeValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.Optional;

@Service
public class AttachmentFileUploadService implements FileUploadService {

    @Value("${upload.directory}") // External directory where files will be saved
    private String UPLOAD_DIR;

    private final FileTypeValidator fileTypeValidator;

    private final FileEmptyValidator fileEmptyValidator;

    private final NotificationService notificationService;

    private final TaskRepository taskRepository;

    public AttachmentFileUploadService(FileTypeValidator fileTypeValidator, FileEmptyValidator fileEmptyValidator, NotificationService notificationService, TaskRepository taskRepository) {
        this.fileTypeValidator = fileTypeValidator;
        this.fileEmptyValidator = fileEmptyValidator;
        this.notificationService = notificationService;
        this.taskRepository = taskRepository;
    }

    @Override
    public void upload(MultipartFile multipartFile) {
        fileEmptyValidator.setNextValidator(fileTypeValidator);
        fileEmptyValidator.validate(multipartFile);
        save(multipartFile);
        for(User user: notifySharedUsers(1)) {
            notificationService.sendNotification(user.getUsername(), "an attachment has been uploaded", NotificationType.FILE_UPLOAD);
        }
    }


    public List<User> notifySharedUsers(long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("task not found: " + taskId));
            return task.getSharedWithUsers();
    }



    public void save(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        File file = new File(UPLOAD_DIR + fileName);
        try {
            multipartFile.transferTo(file);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }




}
