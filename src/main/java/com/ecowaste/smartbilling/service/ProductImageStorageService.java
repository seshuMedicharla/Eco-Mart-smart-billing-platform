package com.ecowaste.smartbilling.service;

import com.ecowaste.smartbilling.dto.ProductImageUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");
    private static final Map<String, String> MIME_TO_EXTENSION = Map.of(
            "image/jpeg", "jpg",
            "image/jpg", "jpg",
            "image/png", "png",
            "image/x-png", "png",
            "image/webp", "webp",
            "image/gif", "gif"
    );

    @Value("${app.product-images.dir:uploads/product-images}")
    private String productImagesDir;

    public ProductImageUploadResponse uploadProductImage(Long storeId, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Please choose an image to upload.");
        }

        String originalFileName = imageFile.getOriginalFilename();
        String extension = resolveExtension(imageFile);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Only JPG, JPEG, PNG, WEBP, and GIF images are supported.");
        }

        String safeBaseName = sanitizeBaseName(originalFileName);
        String storedFileName = safeBaseName + "-" + UUID.randomUUID() + "." + extension;

        try {
            Path storeDirectory = Paths.get(productImagesDir, "store-" + storeId).toAbsolutePath().normalize();
            Files.createDirectories(storeDirectory);

            Path targetPath = storeDirectory.resolve(storedFileName);
            Files.copy(imageFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String publicPath = "/product-images/store-" + storeId + "/" + storedFileName;
            return new ProductImageUploadResponse(true, "Product image uploaded successfully.", publicPath, storedFileName);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save the selected product image.", exception);
        }
    }

    private String resolveExtension(MultipartFile imageFile) {
        String fileName = imageFile.getOriginalFilename();

        if (fileName != null && fileName.contains(".")) {
            String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
            if (ALLOWED_EXTENSIONS.contains(extension)) {
                return extension;
            }
        }

        String contentType = String.valueOf(imageFile.getContentType()).toLowerCase(Locale.ROOT);
        String mappedExtension = MIME_TO_EXTENSION.get(contentType);
        if (mappedExtension != null) {
            return mappedExtension;
        }

        throw new IllegalArgumentException("Only JPG, JPEG, PNG, WEBP, and GIF images are supported.");
    }

    private String sanitizeBaseName(String fileName) {
        String baseName = fileName == null ? "product-image" : fileName.replaceFirst("\\.[^.]+$", "");
        String sanitized = baseName.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        return sanitized.isBlank() ? "product-image" : sanitized;
    }
}
