package com.example.service.impl.files;

import com.example.exceptions.InvalidFileTypeException;
import com.example.service.FileUploadValidator;
import org.springframework.web.multipart.MultipartFile;

public abstract class AbstractFileUploadValidator implements FileUploadValidator {
    public FileUploadValidator nextValidator;


    public FileUploadValidator setNextValidator(FileUploadValidator nextValidator) {
        this.nextValidator = nextValidator;
        return this.nextValidator; // return type is helpful for method chaining
    }

    public abstract boolean validateNext(MultipartFile multipartFile);
    @Override
    public boolean validate(MultipartFile multipartFile) throws InvalidFileTypeException {
        return validateNext(multipartFile);
    }
}
