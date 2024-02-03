package com.example.service.impl.files;

import com.example.model.Attachment;
import com.example.repository.AttachmentRepository;
import com.example.response.FileAttachmentResponse;
import com.example.service.FileDownloadService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;


@Service
public class AttachmentFileDownloadService implements FileDownloadService {

    private final AttachmentRepository attachmentRepository;

    public AttachmentFileDownloadService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public FileAttachmentResponse download(Object object) {
        Long taskId = null;
        return fetchFromDatabase(taskId);
    }

    public FileAttachmentResponse fetchFromDatabase(Long taskId) {
        Attachment attachment = attachmentRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Attachment Not Found"));

        byte[] bytes = attachment.getData();
        String fileName = attachment.getFileName();

        return FileAttachmentResponse.builder()
                .fileBytes(bytes)
                .fileName(fileName)
                .mediaType(MediaType.valueOf(attachment.getMediaType()))
                .byteArrayResource(new ByteArrayResource(bytes))
                .build();

    }
}
