package com.ecowaste.smartbilling.repository;

import com.ecowaste.smartbilling.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    @Query("select coalesce(sum(b.totalAmount), 0) from Bill b where b.store.id = :storeId")
    BigDecimal getTotalSales(@Param("storeId") Long storeId);

    @Query("select coalesce(sum(b.totalAmount), 0) from Bill b")
    BigDecimal getTotalPlatformSales();

    Optional<Bill> findByInvoiceNumberAndStoreId(String invoiceNumber, Long storeId);

    List<Bill> findTop10ByStoreIdOrderByCreatedDateDesc(Long storeId);

    long countByStoreId(Long storeId);

    List<Bill> findAllByStoreIsNull();
}
