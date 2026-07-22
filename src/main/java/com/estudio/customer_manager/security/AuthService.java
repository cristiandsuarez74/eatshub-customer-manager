package com.estudio.customer_manager.security;

import com.estudio.customer_manager.helpers.JwtHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ReactiveUserDetailsService userDetailsService;
    private final JwtHelper jwtHelper;
    private final PasswordEncoder passwordEncoder;

    public Mono<String> authenticate(String email, String password){
        return this.userDetailsService.findByUsername(email)
                .filter(userDetails -> this.passwordEncoder.matches(password,userDetails.getPassword()))
                .map(userDetails -> {
                    List<String> roles=userDetails.getAuthorities().stream()
                            .map(auth-> auth.getAuthority().replace("ROLE_",""))
                            .toList();
                    return this.jwtHelper.generateJwt(email,roles);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("invalided credentials")));
    }
}
