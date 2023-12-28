package com.example.service.impl.files;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileEmptyValidator extends AbstractFileUploadValidator {

    @Override
    public boolean validateNext(MultipartFile multipartFile) {
        return false;
    }

    @Override
    public boolean validate(MultipartFile multipartFile) {
        return false;
    }
}
