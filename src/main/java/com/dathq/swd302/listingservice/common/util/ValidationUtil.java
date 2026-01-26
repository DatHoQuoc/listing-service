package com.dathq.swd302.listingservice.common.util;

import com.dathq.swd302.listingservice.exception.FileSizeExceededException;
import com.dathq.swd302.listingservice.exception.InvalidFileTypeException;
import org.springframework.web.multipart.MultipartFile;

import static com.dathq.swd302.listingservice.common.constant.AppConstants.ALLOWED_MIME_TYPES;
import static com.dathq.swd302.listingservice.common.constant.AppConstants.MAX_FILE_SIZE;

public  class ValidationUtil {

    public static void validateDocumentFile(MultipartFile file){
        if (file == null || file.isEmpty()) {
            throw new InvalidFileTypeException("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException("File size exceeds maximum allowed size of 25MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new InvalidFileTypeException("Invalid file type. Allowed types: PDF, DOC, DOCX, JPG, PNG");
        }
    }
}
