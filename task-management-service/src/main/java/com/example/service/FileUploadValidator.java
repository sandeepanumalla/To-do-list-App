package com.example.service;

import com.example.exceptions.InvalidFileTypeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileUploadValidator {
    boolean validate(MultipartFile multipartFile) throws InvalidFileTypeException;

}
