package com.ecowaste.smartbilling.controller;

import com.ecowaste.smartbilling.dto.ApiResponse;
import com.ecowaste.smartbilling.dto.InventoryUpdateRequest;
import com.ecowaste.smartbilling.dto.ProductCreateRequest;
import com.ecowaste.smartbilling.dto.ProductImageUploadResponse;
import com.ecowaste.smartbilling.dto.ProductImageUpdateRequest;
import com.ecowaste.smartbilling.dto.ProductResponse;
import com.ecowaste.smartbilling.dto.ProductUpdateRequest;
import jakarta.validation.Valid;
import com.ecowaste.smartbilling.service.ProductService;
import com.ecowaste.smartbilling.service.ProductImageStorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductImageStorageService productImageStorageService;

    public ProductController(ProductService productService, ProductImageStorageService productImageStorageService) {
        this.productService = productService;
        this.productImageStorageService = productImageStorageService;
    }

    @GetMapping
    public List<ProductResponse> getAllProducts(@RequestHeader("X-Store-Id") Long storeId) {
        return productService.getAllProducts(storeId);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestHeader("X-Store-Id") Long storeId,
                                                         @Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.ok(productService.createProduct(storeId, request));
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductImageUploadResponse> uploadProductImage(@RequestHeader("X-Store-Id") Long storeId,
                                                                         @RequestParam("image") MultipartFile imageFile) {
        return ResponseEntity.ok(productImageStorageService.uploadProductImage(storeId, imageFile));
    }

    @PutMapping("/{productId}/image")
    public ResponseEntity<ProductResponse> updateProductImage(@RequestHeader("X-Store-Id") Long storeId,
                                                              @PathVariable Long productId,
                                                              @Valid @RequestBody ProductImageUpdateRequest request) {
        return ResponseEntity.ok(productService.updateProductImage(storeId, productId, request.getImage()));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@RequestHeader("X-Store-Id") Long storeId,
                                                         @PathVariable Long productId,
                                                         @Valid @RequestBody ProductUpdateRequest request) {
        return ResponseEntity.ok(productService.updateProduct(storeId, productId, request));
    }

    @PutMapping("/inventory")
    public ResponseEntity<ProductResponse> updateInventory(@RequestHeader("X-Store-Id") Long storeId,
                                                           @Valid @RequestBody InventoryUpdateRequest request) {
        return ResponseEntity.ok(productService.updateInventory(storeId, request));
    }

    @GetMapping("/health")
    public ApiResponse health() {
        return new ApiResponse(true, "Product module ready");
    }
}
