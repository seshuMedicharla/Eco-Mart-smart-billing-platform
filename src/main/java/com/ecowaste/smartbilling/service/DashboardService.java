package com.ecowaste.smartbilling.service;

import com.ecowaste.smartbilling.dto.DashboardResponse;
import com.ecowaste.smartbilling.model.WasteCategory;
import com.ecowaste.smartbilling.repository.BillItemRepository;
import com.ecowaste.smartbilling.repository.BillRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DashboardService {

    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;

    public DashboardService(BillRepository billRepository, BillItemRepository billItemRepository) {
        this.billRepository = billRepository;
        this.billItemRepository = billItemRepository;
    }

    public DashboardResponse getDashboardStatistics(Long storeId) {
        // Dashboard values are intentionally simple aggregated metrics for demo presentation.
        BigDecimal totalSales = billRepository.getTotalSales(storeId);
        Long recyclableItemsSold = billItemRepository.getTotalQuantityByCategory(storeId, WasteCategory.RECYCLABLE);
        Long reusableItemsSold = billItemRepository.getTotalQuantityByCategory(storeId, WasteCategory.REUSABLE);
        Long ecoDisposalItemsSold = billItemRepository.getTotalQuantityByCategory(storeId, WasteCategory.ECO_DISPOSAL);
        Long totalBillsGenerated = billRepository.countByStoreId(storeId);

        return new DashboardResponse(
                totalSales,
                recyclableItemsSold,
                reusableItemsSold,
                ecoDisposalItemsSold,
                totalBillsGenerated
        );
    }
}
