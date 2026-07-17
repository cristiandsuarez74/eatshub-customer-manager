package com.estudio.customer_manager.services;

import com.estudio.customer_manager.enums.UpdateRoleOperation;
import com.estudio.customer_manager.repositories.CustomerRepository;
import com.estudio.customer_manager.repositories.RoleRepository;
import com.estudio.customer_manager.tables.CustomerTable;
import com.estudio.customer_manager.tables.RoleTable;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final DatabaseClient databaseClient;
    @Override
    public Mono<CustomerTable> createCustomer(CustomerTable customerTable, Set<String> roleNames) {
        log.info("Creating customer with mail: {}",customerTable.getEmail());
        return this.customerRepository.save(customerTable)
                .flatMap(savedCustomer->{
                    return Flux.fromIterable(roleNames)
                            .flatMap(roleName->
                                    this.databaseClient.sql(INSERT_BY_ROLE_QUERY)
                                            .bind("customerId",savedCustomer.getId())
                                            .bind("roleName",roleName)
                                            .fetch()
                                            .rowsUpdated())
                            .then(Mono.just(savedCustomer));

                })
                .doOnSuccess(createdCustomer-> log.info("Customer Created:{}",createdCustomer.getId()))
                .doOnError(error->log.error("Error creating customer",error));
    }

    @Override
    public Mono<Map<String, List<RoleTable>>> readRolesByEmail(String email) {
        log.info("reading role with email: {}",email);
        return customerRepository.findByEmail(email)
                .flatMap(customer ->
                    this.databaseClient
                            .sql(FIND_BY_ROLE_QUERY)
                            .bind("customerId",customer.getId())
                            .map((row, rowMetadata) -> {
                                RoleTable roleTable = new RoleTable();
                                roleTable.setName(row.get("name",String.class));
                                roleTable.setDescription(row.get("description",String.class));
                                return roleTable;
                            })
                            .all()
                            .collectList()
                            .map(roles-> Map.of(email,roles))

                )
                .switchIfEmpty(Mono.just(Map.of(email,List.of())));


    }

    @Override
    public Mono<Void> delateCustomer(Long id) {
        log.info("deleting customer with id:{}",id);
        return customerRepository.deleteById(id);

    }

    @Override
    public Mono<CustomerTable> updateRoleInCustomer(Long id, Set<String> roleNames,
                                                    UpdateRoleOperation operation) {
        log.info("updating customer with id:{}",id);
        return this.customerRepository.findById(id)
                .flatMap(customerDB->{
                    if (operation==UpdateRoleOperation.ADD){
                        return Flux.fromIterable(roleNames)
                                .flatMap(rolesName->
                                    this.databaseClient.sql(INSERT_BY_ROLE_QUERY)
                                            .bind("customerId",customerDB.getId())
                                            .bind("roleName",rolesName)
                                            .fetch()
                                            .rowsUpdated()
                                            .onErrorResume(error->{
                                                log.error("Error updating customer",error);
                                                return Mono.just(0L);
                                            })
                                            )
                                .then(Mono.just(customerDB));
                    }else {
                        return Flux.fromIterable(roleNames)
                                .flatMap(roleName->
                                        this.databaseClient.sql(DELETE_FROM_CUSTOMER_ROLE_QUERY)
                                                .bind("customerId",customerDB.getId())
                                                .bind("roleName",roleName)
                                                .fetch()
                                                .rowsUpdated()
                                )
                                .then(Mono.just(customerDB));
                    }
                });


    }
    private static final  String INSERT_BY_ROLE_QUERY= """
            INSERT INTO customer_role(customer_id, role_name)
            VALUES(:customerId, :roleName)
            
            """;
    private static final String DELETE_FROM_CUSTOMER_ROLE_QUERY= """
            DELETE FROM customer_role WHERE
            customer_id=:customerId AND
            role_name=:roleName
            """;
    private static final  String FIND_BY_ROLE_QUERY= """
            
            SELECT r.name, r.description
            FROM role r
            INNER JOIN customer_role cr ON cr.role_name=r.name
            WHERE cr.customer_id =:customerId
            """;
}
