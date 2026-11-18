package com.company.fooddash.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    /** Uploads the image and returns its public https URL. */
    String upload(MultipartFile file);
}
