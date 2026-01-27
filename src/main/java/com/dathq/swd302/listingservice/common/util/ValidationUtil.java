package com.dathq.swd302.listingservice.common.util;

import com.dathq.swd302.listingservice.exception.FileSizeExceededException;
import com.dathq.swd302.listingservice.exception.InvalidFileTypeException;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import static com.dathq.swd302.listingservice.common.constant.AppConstants.*;


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

    public static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileTypeException("File cannot be empty");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new FileSizeExceededException("Image size exceeds maximum allowed size of 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidFileTypeException("Invalid image type. Allowed types: JPEG, JPG, PNG, WEBP, HEIC");
        }
    }

    public static void validate360Image(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileTypeException("File cannot be empty");
        }

        if (file.getSize() > MAX_360_IMAGE_SIZE) {
            throw new FileSizeExceededException("360° image size exceeds maximum allowed size of 20MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_360_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidFileTypeException("Invalid 360° image type. Allowed types: JPEG, JPG, PNG");
        }
    }
}
