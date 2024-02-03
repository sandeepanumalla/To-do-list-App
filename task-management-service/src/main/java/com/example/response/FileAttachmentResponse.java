package com.example.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;

@Getter
@Setter
@Builder
public class FileAttachmentResponse {

    private String fileName;

    private byte[] fileBytes;

    private ByteArrayResource byteArrayResource;

    private MediaType mediaType;
}
