package com.example.Controller;

import com.example.model.User;
import com.example.repository.TaskRepository;
import com.example.request.AttachmentUploadRequest;
import com.example.service.FileDownloadService;
import com.example.service.FileUploadService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping
public class AttachmentController {

    public final TaskRepository taskRepository;

    public final FileUploadService uploadService;

    public final FileDownloadService fileDownloadService;

    public final UserService userService;

    public AttachmentController(TaskRepository taskRepository, @Qualifier("attachmentFileUploadService") FileUploadService uploadService, FileDownloadService fileDownloadService, UserService userService) {
        this.taskRepository = taskRepository;
        this.uploadService = uploadService;
        this.fileDownloadService = fileDownloadService;
        this.userService = userService;
    }

    @PostMapping("/tasks/{taskId}/attachments/upload")
    public ResponseEntity<String> upload(@CookieValue("jwt") String token, @PathVariable int taskId, MultipartFile multipartFile) {
        User user = userService.getUserIdByToken(token);
        AttachmentUploadRequest attachmentUploadRequest = AttachmentUploadRequest.builder()
                .multipartFile(multipartFile)
                .owner(user)
                .taskId(taskId)
                .build();
        uploadService.upload(attachmentUploadRequest);
         return ResponseEntity.status(HttpStatus.OK).body("file successfully uploaded");
    }

    @GetMapping("/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> download(@PathVariable("attachmentId") String attachmentId) {
        // fetch the file from downloadService
        // TODO fetch the FileAttachmentResponse from downloadService
        //
        fileDownloadService.download(attachmentId);

        return ResponseEntity.status(HttpStatus.OK).contentType(null).body(null);
    }
}
