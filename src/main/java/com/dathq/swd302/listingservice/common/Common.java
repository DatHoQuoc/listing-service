package com.dathq.swd302.listingservice.common;

import java.util.UUID;

public class Common {

    public static String generateDocumentFileName(UUID listingId, String originalFilename) {
        String extension = "";
        if(originalFilename != null && originalFilename.contains(".")){
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return "listings/" + listingId + "/documents/" + UUID.randomUUID() + extension;
    }

    public static String generateImageFileName(UUID listingId, String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return "listings/" + listingId + "/images/" + UUID.randomUUID() + extension;
    }

    public static String generate360FileName(UUID listingId, String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return "listings/" + listingId + "/360-images/" + UUID.randomUUID() + extension;
    }
}
