package com.company.fooddash.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryImageService implements ImageService {
    private static final List<String> ALLOWED = List.of("image/png", "image/jpeg", "image/webp");
    private final Cloudinary cloudinary;
    private final String cloudName;

    public CloudinaryImageService(Cloudinary cloudinary, @Value("${cloudinary.cloud-name}") String cloudName) {
        this.cloudinary = cloudinary;
        this.cloudName = cloudName;
    }

    @Override
    public String upload(MultipartFile file) {
        if (cloudName == null || cloudName.isBlank())
            throw new IllegalStateException(
                    "Image upload is not configured. Set the CLOUDINARY_* environment variables.");
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("Please choose an image file");
        if (file.getContentType() == null || !ALLOWED.contains(file.getContentType()))
            throw new IllegalArgumentException("Only PNG, JPEG or WebP images are allowed");
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "fooddash/products", "resource_type", "image"));
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new IllegalStateException("Image upload failed. Please try again.");
        }
    }
}
