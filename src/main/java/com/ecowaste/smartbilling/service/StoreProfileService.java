package com.ecowaste.smartbilling.service;

import com.ecowaste.smartbilling.dto.StoreRegistrationRequest;
import com.ecowaste.smartbilling.dto.StoreRegistrationResponse;
import com.ecowaste.smartbilling.dto.AdminLoginRequest;
import com.ecowaste.smartbilling.dto.StoreLoginResponse;
import com.ecowaste.smartbilling.dto.StorePasswordResetRequest;
import com.ecowaste.smartbilling.model.Bill;
import com.ecowaste.smartbilling.model.Customer;
import com.ecowaste.smartbilling.model.Product;
import com.ecowaste.smartbilling.model.StoreProfile;
import com.ecowaste.smartbilling.repository.BillRepository;
import com.ecowaste.smartbilling.repository.CustomerRepository;
import com.ecowaste.smartbilling.repository.ProductRepository;
import com.ecowaste.smartbilling.repository.StoreProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreProfileService {

    private final StoreProfileRepository storeProfileRepository;
    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final BillRepository billRepository;

    public StoreProfileService(StoreProfileRepository storeProfileRepository,
                               AdminService adminService,
                               PasswordEncoder passwordEncoder,
                               ProductRepository productRepository,
                               CustomerRepository customerRepository,
                               BillRepository billRepository) {
        this.storeProfileRepository = storeProfileRepository;
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.billRepository = billRepository;
    }

    public StoreRegistrationResponse getRegistrationStatus() {
        long totalStores = storeProfileRepository.count();
        return storeProfileRepository.findAll().stream()
                .findFirst()
                .map(store -> new StoreRegistrationResponse(
                        true,
                        totalStores + " store account(s) onboarded",
                        totalStores > 0,
                        store.getId(),
                        store.getStoreName(),
                        store.getLoginUsername(),
                        null,
                        totalStores
                ))
                .orElse(new StoreRegistrationResponse(
                        true,
                        "No store accounts onboarded yet",
                        false,
                        null,
                        null,
                        null,
                        null,
                        0L
                ));
    }

    @Transactional
    public StoreRegistrationResponse registerStore(StoreRegistrationRequest request) {
        if (!adminService.validatePlatformAdmin(request.getPlatformUsername().trim(), request.getPlatformPassword().trim())) {
            throw new IllegalArgumentException("Platform admin credentials are invalid");
        }

        String operatorUsername = request.getOperatorUsername().trim().toLowerCase();
        if (storeProfileRepository.existsByLoginUsername(operatorUsername)) {
            throw new IllegalArgumentException("Store login username is already in use");
        }

        StoreProfile storeProfile = StoreProfile.builder()
                .storeName(request.getStoreName().trim())
                .ownerName(request.getOwnerName().trim())
                .phoneNumber(request.getPhoneNumber().trim())
                .email(request.getEmail().trim().toLowerCase())
                .address(request.getAddress().trim())
                .loginUsername(operatorUsername)
                .loginPassword(passwordEncoder.encode(request.getOperatorPassword().trim()))
                .active(true)
                .build();

        StoreProfile savedStore = storeProfileRepository.save(storeProfile);
        attachLegacyDataToStore(savedStore);
        return new StoreRegistrationResponse(
                true,
                "Store onboarded successfully. Share the credentials with the supermarket team.",
                true,
                savedStore.getId(),
                savedStore.getStoreName(),
                savedStore.getLoginUsername(),
                request.getOperatorPassword().trim(),
                storeProfileRepository.count()
        );
    }

    public StoreLoginResponse loginStore(AdminLoginRequest request) {
        String username = request.getUsername().trim().toLowerCase();
        return storeProfileRepository.findByLoginUsername(username)
                .filter(store -> Boolean.TRUE.equals(store.getActive()))
                .filter(store -> passwordEncoder.matches(request.getPassword(), store.getLoginPassword()))
                .map(store -> new StoreLoginResponse(
                        true,
                        "Store login successful",
                        store.getId(),
                        store.getStoreName(),
                        store.getLoginUsername()
                ))
                .orElse(new StoreLoginResponse(false, "Invalid store username or password", null, null, null));
    }

    @Transactional
    public StoreLoginResponse resetStorePassword(StorePasswordResetRequest request) {
        if (!adminService.validatePlatformAdmin(request.getPlatformUsername().trim(), request.getPlatformPassword().trim())) {
            throw new IllegalArgumentException("Platform admin credentials are invalid");
        }

        String username = request.getOperatorUsername().trim().toLowerCase();
        StoreProfile store = storeProfileRepository.findByLoginUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Store login username not found"));

        store.setLoginPassword(passwordEncoder.encode(request.getNewPassword().trim()));
        storeProfileRepository.save(store);

        return new StoreLoginResponse(
                true,
                "Store password reset successfully",
                store.getId(),
                store.getStoreName(),
                store.getLoginUsername()
        );
    }

    public StoreProfile getStoreOrThrow(Long storeId) {
        if (storeId == null) {
            throw new IllegalArgumentException("Store access is required for this operation");
        }

        return storeProfileRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found with ID: " + storeId));
    }

    private void attachLegacyDataToStore(StoreProfile store) {
        for (Product product : productRepository.findAllByStoreIsNull()) {
            product.setStore(store);
        }
        for (Customer customer : customerRepository.findAllByStoreIsNull()) {
            customer.setStore(store);
        }
        for (Bill bill : billRepository.findAllByStoreIsNull()) {
            bill.setStore(store);
        }
    }
}
