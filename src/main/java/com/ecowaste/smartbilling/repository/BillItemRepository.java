package com.ecowaste.smartbilling.repository;

import com.ecowaste.smartbilling.model.BillItem;
import com.ecowaste.smartbilling.model.WasteCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BillItemRepository extends JpaRepository<BillItem, Long> {

    @Query("""
            select coalesce(sum(bi.quantity), 0)
            from BillItem bi
            where bi.wasteCategory = :category
              and bi.bill.store.id = :storeId
            """)
    Long getTotalQuantityByCategory(@Param("storeId") Long storeId, @Param("category") WasteCategory category);
}
