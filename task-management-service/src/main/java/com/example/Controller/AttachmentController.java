package com.example.Controller;

import com.example.config.RestEndpoints;
import com.example.model.User;
import com.example.repository.TaskRepository;
import com.example.request.AttachmentUploadRequest;
import com.example.response.FileAttachmentResponse;
import com.example.service.FileDownloadService;
import com.example.service.FileUploadService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
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

    @PostMapping(value = "/tasks/{taskId}/attachments/upload", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(@CookieValue("jwt") String token,
                                         @PathVariable("taskId") int taskId,
                                         @RequestParam("file") MultipartFile multipartFile) {
        try {
            User user = userService.getUserIdByToken(token);
            AttachmentUploadRequest attachmentUploadRequest = AttachmentUploadRequest.builder()
                    .multipartFile(multipartFile)
                    .owner(user)
                    .taskId(taskId)
                    .build();
            uploadService.upload(attachmentUploadRequest);
            return ResponseEntity.status(HttpStatus.OK).body("File successfully uploaded");
        } catch (Exception e) {
            // Handle the exception and return an appropriate error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An internal server error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> download(@PathVariable("attachmentId") String attachmentId) {
        // fetch the file from downloadService
        // TODO fetch the FileAttachmentResponse from downloadService
        //
        FileAttachmentResponse fileAttachmentResponse= fileDownloadService.download(attachmentId);

        return ResponseEntity.ok()
                .contentType(fileAttachmentResponse.getMediaType())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""  + "\"")
                .body(fileAttachmentResponse.getByteArrayResource());
    }

    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<?> deleteAttachment(@PathVariable("attachmentId") String attachmentId) {
        // delete the file from downloadService
        // TODO delete the FileAttachmentResponse from downloadService
        //
//        fileDownloadService.delete(attachmentId);
        return ResponseEntity.ok().body("attachment has been deleted successfully");
    }
}
