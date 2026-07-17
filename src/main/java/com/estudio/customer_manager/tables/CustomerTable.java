package com.estudio.customer_manager.tables;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("customer")
public class CustomerTable {
    @Id
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private Integer age;
    private LocalDate birthdate;
    @Column(value = "user_password")
    private String password;
    private Boolean enabled;
    @Column(value = "account_non_expired")
    private Boolean accountNonExpired;
    @Column(value = "credentials_non_expired")
    private Boolean credentialsNonExpired;
    @Column(value = "alternative_id")
    private UUID alternativeId;




}
