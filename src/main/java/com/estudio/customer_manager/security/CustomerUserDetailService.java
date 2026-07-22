package com.estudio.customer_manager.security;

import com.estudio.customer_manager.repositories.CustomerRepository;
import com.estudio.customer_manager.services.CustomerService;
import com.estudio.customer_manager.tables.RoleTable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailService implements ReactiveUserDetailsService {
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return customerRepository.findByEmail(email)
                .flatMap(customerDB->
                        this.customerService.readRolesByEmail(email)
                                .map(roleMap->{
                                    List<RoleTable> roles=roleMap.values().stream()
                                            .flatMap(List::stream)
                                            .toList();
                                    List<SimpleGrantedAuthority> authorities=roles.stream()
                                            .map(role-> new SimpleGrantedAuthority("ROLE_"+role.getName()))
                                            .toList();
                                    return User.builder()
                                            .username(customerDB.getEmail())
                                            .password(customerDB.getPassword())
                                            .authorities(authorities)
                                            .disabled(!customerDB.getEnabled())
                                            .accountExpired(!customerDB.getAccountNonExpired())
                                            .credentialsExpired(!customerDB.getCredentialsNonExpired())
                                            .accountLocked(false)
                                            .build();

                                }));
    }
}
