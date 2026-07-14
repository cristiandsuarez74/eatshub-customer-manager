package com.estudio.customer_manager.controllers;

import com.estudio.customer_manager.clients.ReservationCrudClient;
import com.estudio.customer_manager.dtos.ReservationRequest;
import com.estudio.customer_manager.dtos.ReservationResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "reservation")
public class ReservationController {

    private final ReservationCrudClient reservationCrudClient;
    @PostMapping
    public Mono<ResponseEntity<Object>> postReservation(@Valid @RequestBody ReservationRequest reservationRequest){
        log.info("POST customer/reservation");
        return this.reservationCrudClient.create(reservationRequest)
                .map(resource->ResponseEntity.created(URI.create(resource)).build())
                .onErrorResume(IllegalArgumentException.class,err->{
                    log.error("POST customer/reservation failed",err);
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(RuntimeException.class, err->{
                    log.error("POST customer/reservation failed",err);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });


    }
    @GetMapping(path = "{id}")
    public Mono<ResponseEntity<ReservationResponse>> getReservation(@Valid @PathVariable String id){
        log.info("GET customer/reservation/{}",id);
        return reservationCrudClient.read(id)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class,err->{
                    log.error("GET customer/reservation failed",err);
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(RuntimeException.class, err->{
                    log.error("GET customer/reservation failed",err);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });

    }
    @PutMapping(path = "{id}")
    public Mono<ResponseEntity<ReservationResponse>> putReservation(
            @Valid @PathVariable String id, @RequestBody ReservationRequest reservationRequest){

        log.info("PUT customer/reservation/{}",id);

        return this.reservationCrudClient.update(id,reservationRequest)
                .map(ResponseEntity::ok)
                .onErrorResume(IllegalArgumentException.class,err->{
                    log.error("GET customer/reservation failed",err);
                    return Mono.just(ResponseEntity.badRequest().build());
                })
                .onErrorResume(RuntimeException.class, err->{
                    log.error("GET customer/reservation failed",err);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
    @DeleteMapping(path = "{id}")
    public Mono<ResponseEntity<Object>> deleteReservation(@PathVariable String id){
        log.info("DELETE customer/reservation/{}",id);
        return this.reservationCrudClient.delate(id)
                .then(Mono.just(ResponseEntity.noContent().build()))
                .onErrorResume(IllegalArgumentException.class,err->{
            log.error("DELETED customer/reservation failed",err);
            return Mono.just(ResponseEntity.badRequest().build());
        })
                .onErrorResume(RuntimeException.class, err->{
                    log.error("DELETE customer/reservation failed",err);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }
}
