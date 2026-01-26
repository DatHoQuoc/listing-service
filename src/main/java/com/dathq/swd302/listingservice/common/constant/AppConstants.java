package com.dathq.swd302.listingservice.common.constant;

import java.util.Arrays;
import java.util.List;

public class AppConstants {

    public static final long MAX_FILE_SIZE = 25 * 1024 * 1024;
    public static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "image/jpeg",
            "image/png"
    );
}
