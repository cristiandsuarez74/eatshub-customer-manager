package com.estudio.customer_manager.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "rating")
public class RatingController {
    @PostMapping
    public Mono<ResponseEntity<Object>>postRating(){
        return Mono.just(ResponseEntity.ok().build());
    }
}
