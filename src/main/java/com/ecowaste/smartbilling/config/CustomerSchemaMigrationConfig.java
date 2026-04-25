package com.ecowaste.smartbilling.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Configuration
public class CustomerSchemaMigrationConfig {

    @Bean
    public CommandLineRunner relaxCustomerEmailConstraint(JdbcTemplate jdbcTemplate) {
        return args -> {
            List<String> uniqueCustomerIndexes = jdbcTemplate.query(
                    """
                    SELECT DISTINCT INDEX_NAME
                    FROM INFORMATION_SCHEMA.STATISTICS
                    WHERE TABLE_SCHEMA = DATABASE()
                      AND TABLE_NAME = 'customers'
                      AND COLUMN_NAME IN ('email', 'phone_number')
                      AND NON_UNIQUE = 0
                      AND INDEX_NAME <> 'PRIMARY'
                    """,
                    (rs, rowNum) -> rs.getString("INDEX_NAME")
            );

            for (String indexName : uniqueCustomerIndexes) {
                jdbcTemplate.execute("ALTER TABLE customers DROP INDEX `" + indexName + "`");
            }
        };
    }
}
