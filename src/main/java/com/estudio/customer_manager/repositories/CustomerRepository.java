package com.estudio.customer_manager.repositories;

import com.estudio.customer_manager.tables.CustomerTable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends R2dbcRepository<CustomerTable, Long> {
    @Query("SELECT * fROM customer Where email =:email")
    Mono<CustomerTable> findByEmail(String email);
    @Query("SELECT COUNT(*) >0 fROM customer Where email =:email")
    Mono<Boolean> existByEmail(String email);
}
