package com.ecowaste.smartbilling.repository;

import com.ecowaste.smartbilling.model.StoreProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreProfileRepository extends JpaRepository<StoreProfile, Long> {
    Optional<StoreProfile> findByLoginUsername(String loginUsername);
    boolean existsByLoginUsername(String loginUsername);
    long countByActiveTrue();
}
