package com.estudio.customer_manager.clients;

import com.estudio.customer_manager.dtos.ReservationRequest;
import com.estudio.customer_manager.dtos.ReservationResponse;
import reactor.core.publisher.Mono;

public interface ReservationCrudClient {
        Mono<String> create(ReservationRequest reservationRequest);
        Mono<ReservationResponse> read(String uuid);
        Mono<ReservationResponse> update(String uuid,ReservationRequest reservationRequest);
        Mono<Void> delate(String uuid);
}
