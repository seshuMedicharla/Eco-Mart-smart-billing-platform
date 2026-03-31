package com.ecowaste.smartbilling.service;

import com.ecowaste.smartbilling.dto.AdminLoginRequest;
import com.ecowaste.smartbilling.dto.PlatformOverviewResponse;
import com.ecowaste.smartbilling.dto.PlatformStoreSummaryResponse;
import com.ecowaste.smartbilling.model.StoreProfile;
import com.ecowaste.smartbilling.repository.BillRepository;
import com.ecowaste.smartbilling.repository.CustomerRepository;
import com.ecowaste.smartbilling.repository.ProductRepository;
import com.ecowaste.smartbilling.repository.StoreProfileRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class PlatformAdminService {

    private final AdminService adminService;
    private final StoreProfileRepository storeProfileRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final BillRepository billRepository;

    public PlatformAdminService(AdminService adminService,
                                StoreProfileRepository storeProfileRepository,
                                ProductRepository productRepository,
                                CustomerRepository customerRepository,
                                BillRepository billRepository) {
        this.adminService = adminService;
        this.storeProfileRepository = storeProfileRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.billRepository = billRepository;
    }

    public PlatformOverviewResponse getOverview(AdminLoginRequest request) {
        if (!adminService.validatePlatformAdmin(request.getUsername().trim(), request.getPassword().trim())) {
            throw new IllegalArgumentException("Platform admin credentials are invalid");
        }

        List<PlatformStoreSummaryResponse> stores = storeProfileRepository.findAll().stream()
                .sorted(Comparator.comparing(StoreProfile::getStoreName, String.CASE_INSENSITIVE_ORDER))
                .map(this::mapStoreSummary)
                .toList();

        return new PlatformOverviewResponse(
                true,
                "Platform overview loaded successfully",
                (long) stores.size(),
                storeProfileRepository.countByActiveTrue(),
                billRepository.count(),
                defaultAmount(billRepository.getTotalPlatformSales()),
                stores
        );
    }

    private PlatformStoreSummaryResponse mapStoreSummary(StoreProfile store) {
        Long storeId = store.getId();
        return new PlatformStoreSummaryResponse(
                storeId,
                store.getStoreName(),
                store.getOwnerName(),
                store.getLoginUsername(),
                store.getActive(),
                productRepository.countByStoreId(storeId),
                customerRepository.countByStoreId(storeId),
                billRepository.countByStoreId(storeId),
                defaultAmount(billRepository.getTotalSales(storeId))
        );
    }

    private BigDecimal defaultAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
