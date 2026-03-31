package com.ecowaste.smartbilling.service;

import com.ecowaste.smartbilling.dto.AdminLoginRequest;
import com.ecowaste.smartbilling.dto.AdminLoginResponse;
import com.ecowaste.smartbilling.model.Admin;
import com.ecowaste.smartbilling.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final String defaultAdminUsername;
    private final String defaultAdminPassword;

    public AdminService(AdminRepository adminRepository,
                        PasswordEncoder passwordEncoder,
                        @Value("${app.admin.username:admin}") String defaultAdminUsername,
                        @Value("${app.admin.password:admin123}") String defaultAdminPassword) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultAdminUsername = defaultAdminUsername;
        this.defaultAdminPassword = defaultAdminPassword;
    }

    public AdminLoginResponse login(AdminLoginRequest request) {
        return adminRepository.findByUsername(request.getUsername())
                .filter(admin -> passwordMatches(request.getPassword(), admin))
                .map(admin -> new AdminLoginResponse(true, "Login successful", admin.getUsername()))
                .orElse(new AdminLoginResponse(false, "Invalid username or password", null));
    }

    public boolean validatePlatformAdmin(String username, String password) {
        return adminRepository.findByUsername(username)
                .map(admin -> passwordMatches(password, admin))
                .orElse(false);
    }

    @Bean
    CommandLineRunner seedAdmin() {
        return args -> {
            if (adminRepository.findByUsername(defaultAdminUsername).isEmpty()) {
                Admin admin = Admin.builder()
                        .username(defaultAdminUsername)
                        .password(passwordEncoder.encode(defaultAdminPassword))
                        .build();
                adminRepository.save(admin);
                return;
            }

            adminRepository.findByUsername(defaultAdminUsername).ifPresent(existingAdmin -> {
                if (!existingAdmin.getPassword().startsWith("$2")) {
                    existingAdmin.setPassword(passwordEncoder.encode(existingAdmin.getPassword()));
                    adminRepository.save(existingAdmin);
                }
            });
        };
    }

    private boolean passwordMatches(String rawPassword, Admin admin) {
        String storedPassword = admin.getPassword();
        if (storedPassword == null || storedPassword.isBlank()) {
            return false;
        }

        if (storedPassword.startsWith("$2")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }

        boolean matches = storedPassword.equals(rawPassword);
        if (matches) {
            admin.setPassword(passwordEncoder.encode(rawPassword));
            adminRepository.save(admin);
        }
        return matches;
    }
}
