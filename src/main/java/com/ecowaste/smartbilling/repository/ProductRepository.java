package com.ecowaste.smartbilling.repository;

import com.ecowaste.smartbilling.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByStoreIdOrderByNameAsc(Long storeId);
    Optional<Product> findByIdAndStoreId(Long id, Long storeId);
    List<Product> findAllByStoreIsNull();
    long countByStoreId(Long storeId);
}
