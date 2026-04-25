package com.ecowaste.smartbilling.repository;

import com.ecowaste.smartbilling.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByPhoneNumberAndStoreId(String phoneNumber, Long storeId);
    Optional<Customer> findByEmailAndStoreId(String email, Long storeId);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    java.util.List<Customer> findAllByStoreIsNull();
    long countByStoreId(Long storeId);
}
