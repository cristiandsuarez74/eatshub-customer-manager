package com.estudio.customer_manager.controllers;

import com.estudio.customer_manager.enums.UpdateRoleOperation;
import com.estudio.customer_manager.services.CustomerService;
import com.estudio.customer_manager.tables.CustomerTable;
import com.estudio.customer_manager.tables.RoleTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "manager")
public class ManagerController {
    private final CustomerService customerService;
    @PostMapping
    public Mono<ResponseEntity<CustomerTable>> createCustomer(@RequestBody CustomerTable customerTable, @RequestParam Set<String> roles) {
        log.info("POST customer/manager");

        return this.customerService.createCustomer(customerTable, roles)
                .map(createdCustomer -> ResponseEntity
                        .created(URI.create("customers/manager/"+ createdCustomer.getId()))
                        .body(createdCustomer))
                .onErrorResume(IllegalArgumentException.class, error -> {
                    log.error("POST customers/manager/ failed", error);
                    return Mono.just(ResponseEntity.badRequest().build());

                })
                .onErrorResume(RuntimeException.class, error -> {
                    log.error("POST customers/manager/ failed", error);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
    @GetMapping("/roles/{email}")
    public Mono<ResponseEntity<Map<String, List<RoleTable>>>> getRolesByEmail(@PathVariable String email){
        log.info("GET customer/manager/roles:{}",email);
        return this.customerService.readRolesByEmail(email)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, error -> {
                    log.error("GET customers/manager/roles/{} failed", email, error);
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(RuntimeException.class, error -> {
                    log.error("GET customers/manager/roles/{} failed", email, error);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });

    }
    @PutMapping("/{id}/roles")
    public Mono<ResponseEntity<CustomerTable>> updateRoles(@PathVariable Long id, @RequestParam Set<String> roles, @RequestParam UpdateRoleOperation operation){
        log.info("PUT customer/manager/{}/roles",id);
        return this.customerService.updateRoleInCustomer(id,roles,operation)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class, error -> {
                    log.error("PUT customers/manager/{}/roles failed", id, error);
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(RuntimeException.class, error -> {
                    log.error("PUT customers/manager/{}/roles failed", id, error);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> deletedCustomer(@PathVariable Long id){
        log.info("DELETE customer/manager{}",id);
        return this.customerService.delateCustomer(id)
                .then(Mono.just(ResponseEntity.noContent().build()))
                .onErrorResume(IllegalArgumentException.class, error->{
                    log.error("DELETE customers/manager/{} failed", id, error);
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(RuntimeException.class, error -> {
                    log.error("DELETE customers/manager/{} failed", id, error);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

}
