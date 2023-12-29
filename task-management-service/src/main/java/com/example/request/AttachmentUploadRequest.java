package com.example.request;

import com.example.model.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
@Setter
public class AttachmentUploadRequest {
    MultipartFile multipartFile;
    private User owner;
    private long taskId;
}
