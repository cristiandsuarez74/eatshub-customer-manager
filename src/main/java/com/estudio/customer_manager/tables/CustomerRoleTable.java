package com.estudio.customer_manager.tables;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("customer_role")
public class CustomerRoleTable {
    @Column(value = "customer_id")
    private Long customerId;
    @Column(value = "role_name")
    private Long roleName;
}
