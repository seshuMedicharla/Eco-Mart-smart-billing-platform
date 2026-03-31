package com.ecowaste.smartbilling.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private StoreProfile store;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "otp_code", length = 10)
    private String otpCode;

    @Column(name = "otp_verified", nullable = false)
    private Boolean otpVerified;

    @Column(name = "next_discount_eligible", nullable = false)
    private Boolean nextDiscountEligible;

    @Column(name = "next_discount_percent", precision = 5, scale = 2)
    private BigDecimal nextDiscountPercent;

    @OneToMany(mappedBy = "customer")
    @Builder.Default
    private List<Bill> bills = new ArrayList<>();
}
