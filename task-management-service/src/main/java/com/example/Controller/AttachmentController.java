package com.example.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {

    @PostMapping("/upload")
    public String upload(MultipartFile multipartFile) {
         String filename = multipartFile.getOriginalFilename();

         return filename;
    }

}
