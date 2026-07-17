package com.estudio.customer_manager.repositories;

import com.estudio.customer_manager.tables.CustomerRoleTable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface CustomerRoleRepository extends R2dbcRepository<CustomerRoleTable,Void> {
}
