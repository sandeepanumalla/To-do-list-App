package com.example.service;

import com.example.request.AttachmentUploadRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    void upload(AttachmentUploadRequest attachmentUploadRequest);
}
