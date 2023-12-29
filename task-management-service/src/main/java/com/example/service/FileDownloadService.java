package com.example.service;

import com.example.response.FileAttachmentResponse;

public interface FileDownloadService {
    FileAttachmentResponse download(Object object);
}
