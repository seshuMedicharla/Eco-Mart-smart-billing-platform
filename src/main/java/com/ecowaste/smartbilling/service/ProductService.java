package com.ecowaste.smartbilling.service;

import com.ecowaste.smartbilling.dto.ProductResponse;
import com.ecowaste.smartbilling.dto.InventoryUpdateRequest;
import com.ecowaste.smartbilling.dto.ProductCreateRequest;
import com.ecowaste.smartbilling.dto.ProductUpdateRequest;
import com.ecowaste.smartbilling.model.Product;
import com.ecowaste.smartbilling.model.StoreProfile;
import com.ecowaste.smartbilling.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreProfileService storeProfileService;

    public ProductService(ProductRepository productRepository, StoreProfileService storeProfileService) {
        this.productRepository = productRepository;
        this.storeProfileService = storeProfileService;
    }

    public List<ProductResponse> getAllProducts(Long storeId) {
        return productRepository.findAllByStoreIdOrderByNameAsc(storeId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProductResponse updateInventory(Long storeId, InventoryUpdateRequest request) {
        Product product = productRepository.findByIdAndStoreId(request.getProductId(), storeId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + request.getProductId()));

        product.setStockQuantity(request.getStockQuantity());
        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    public ProductResponse createProduct(Long storeId, ProductCreateRequest request) {
        StoreProfile store = storeProfileService.getStoreOrThrow(storeId);
        Product product = Product.builder()
                .store(store)
                .name(request.getName().trim())
                .imageUrl(request.getImage().trim())
                .legacyImage(request.getImage().trim())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .wasteCategory(request.getWasteCategory())
                .build();
        return mapToResponse(productRepository.save(product));
    }

    public ProductResponse updateProductImage(Long storeId, Long productId, String imagePath) {
        Product product = productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        product.setImageUrl(imagePath.trim());
        product.setLegacyImage(imagePath.trim());
        return mapToResponse(productRepository.save(product));
    }

    public ProductResponse updateProduct(Long storeId, Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        product.setName(request.getName().trim());
        product.setPrice(request.getPrice());
        product.setWasteCategory(request.getWasteCategory());
        product.setStockQuantity(request.getStockQuantity());

        if (request.getImage() != null && !request.getImage().trim().isEmpty()) {
            product.setImageUrl(request.getImage().trim());
            product.setLegacyImage(request.getImage().trim());
        }

        return mapToResponse(productRepository.save(product));
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                resolveImagePath(product),
                product.getPrice(),
                product.getStockQuantity(),
                product.getWasteCategory()
        );
    }

    private String resolveImagePath(Product product) {
        String imagePath = product.getImageUrl();
        if (imagePath == null || imagePath.isBlank()) {
            imagePath = product.getLegacyImage();
        }

        if (imagePath == null || imagePath.isBlank()) {
            return buildPlaceholderImage(product.getName());
        }

        if (imagePath.startsWith("http://") || imagePath.startsWith("https://") || imagePath.startsWith("/")) {
            return imagePath;
        }

        if (imagePath.startsWith("product-images/") || imagePath.startsWith("images/")) {
            return "/" + imagePath;
        }

        // Legacy demo rows often store only a filename. Use a friendly placeholder for a cleaner UI.
        return buildPlaceholderImage(product.getName());
    }

    private String buildPlaceholderImage(String productName) {
        return "https://placehold.co/600x400/e4f2e6/204f34?text="
                + URLEncoder.encode(productName, StandardCharsets.UTF_8);
    }
}
