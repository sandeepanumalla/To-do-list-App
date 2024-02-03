package com.example.service.impl.files;

import com.example.model.Attachment;
import com.example.model.Task;
import com.example.model.User;
import com.example.repository.AttachmentRepository;
import com.example.repository.TaskRepository;
import com.example.request.AttachmentUploadRequest;
import com.example.service.FileUploadService;
import com.example.service.factory.NotificationType;
import com.example.service.impl.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

@Service
public class AttachmentFileUploadService implements FileUploadService {
//
//    @Value("${upload.directory}") // External directory where files will be saved
//    private String UPLOAD_DIR;

    private final FileTypeValidator fileTypeValidator;

    private final FileEmptyValidator fileEmptyValidator;

    private final NotificationService notificationService;

    private final TaskRepository taskRepository;
    private final AttachmentRepository attachmentRepository;


    public AttachmentFileUploadService(FileTypeValidator fileTypeValidator, FileEmptyValidator fileEmptyValidator, NotificationService notificationService, TaskRepository taskRepository,
                                       AttachmentRepository attachmentRepository) {
        this.fileTypeValidator = fileTypeValidator;
        this.fileEmptyValidator = fileEmptyValidator;
        this.notificationService = notificationService;
        this.taskRepository = taskRepository;
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public void upload(AttachmentUploadRequest attachmentUploadRequest) {
        MultipartFile multipartFile = attachmentUploadRequest.getMultipartFile();
        long taskId = attachmentUploadRequest.getTaskId();
        fileEmptyValidator.setNextValidator(fileTypeValidator);
        fileEmptyValidator.validate(multipartFile);
        save(multipartFile, attachmentUploadRequest.getOwner());
        for(User user: notifySharedUsers(taskId)) {
            notificationService.sendNotification(user.getUsername(), "An attachment has been uploaded", NotificationType.FILE_UPLOAD);
        }
    }


    public List<User> notifySharedUsers(long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("task not found: " + taskId));
            return task.getSharedWithUsers();
    }



    public void save(MultipartFile multipartFile, User user) {
        String fileName = multipartFile.getOriginalFilename();

        try {
            Task task = taskRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("task not found:"));
            byte[] bytes = multipartFile.getBytes();
            Attachment attachment = Attachment.builder()
                    .data(bytes)
                    .fileName(fileName)
                    .uploadedBy(user)
                    .mediaType(multipartFile.getContentType())
                    .task(task)
                    .build();

            attachmentRepository.save(attachment);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }


    public byte[] compressBytes(byte[] data) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(bos)) {

            // Configure ZIP compression level (0-9, 9 being highest compression)
            zipOut.setLevel(Deflater.BEST_COMPRESSION);

            // Create a ZIP entry with a specific name (e.g., "compressed_data.txt")
            ZipEntry zipEntry = new ZipEntry("compressed_data.txt");
            zipOut.putNextEntry(zipEntry);

            // Write the compressed data to the ZIP entry
            zipOut.write(data);

            // Close the ZIP entry and the streams
            zipOut.closeEntry();
            zipOut.finish();

            // Get the compressed data as a byte array
            return bos.toByteArray();
        } catch (IOException ignored) {
            throw new RuntimeException(ignored);
        }
    }

}
