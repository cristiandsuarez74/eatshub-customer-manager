package com.estudio.customer_manager.clients;

import com.estudio.customer_manager.dtos.ReservationRequest;
import com.estudio.customer_manager.dtos.ReservationResourceResponse;
import com.estudio.customer_manager.dtos.ReservationResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@Slf4j

public class ReservationCrudClientImpl implements ReservationCrudClient {

    private final WebClient webClient;
    private static final String RESOURCE= "catalog/reservation";
    private static final String ERROR_MSG_4XX="error  while creating reservation";
    private static final String ERROR_MSG_5XX="error  while calling reservation service";
    private static final Mono<Throwable> MONO_400_ERROR = Mono.error(new IllegalArgumentException(ERROR_MSG_4XX));
    private static final Mono<Throwable> MONO_500_ERROR = Mono.error(new IllegalArgumentException(ERROR_MSG_5XX));

    public ReservationCrudClientImpl(WebClient.Builder builder){
        this.webClient= builder.build();
    }
    @Override
    public Mono<String> create(ReservationRequest reservationRequest) {
        log.info("Creating reservation with id: {}",reservationRequest.getRestaurantId());
        return this.webClient
                .post()
                .uri(RESOURCE)
                .bodyValue(reservationRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,r-> MONO_400_ERROR)
                .onStatus(HttpStatusCode::is5xxServerError,r->MONO_500_ERROR)
                .bodyToMono(ReservationResourceResponse.class)
                .map(ReservationResourceResponse::getResource)
                .doOnSuccess(res-> log.info("Reservation created:{}",res));

    }

    @Override
    public Mono<ReservationResponse> read(String uuid) {
        log.info("Reading reservation With id: {}",uuid);
        return this.webClient
                .get()
                .uri(RESOURCE+"/{reservationId}",uuid)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,r->MONO_400_ERROR)
                .bodyToMono(ReservationResponse.class)
                .doOnSuccess(res->log.info("Reservation Read: {}",res))
                .doOnError(er->log.info("Error reading reservation with id: {}",uuid));
    }

    @Override
    public Mono<ReservationResponse> update(String uuid, ReservationRequest reservationRequest) {
        log.info("update reservation With id: {}",uuid);
        return webClient
                .put()
                .uri(RESOURCE+"/{reservationId}",uuid)
                .bodyValue(reservationRequest)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,r->MONO_400_ERROR)
                .onStatus(HttpStatusCode::is5xxServerError,r->MONO_400_ERROR)
                .bodyToMono(ReservationResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(3))
                        .filter(throwable -> throwable instanceof WebClientResponseException.ServiceUnavailable))
                .doOnSuccess(r->log.info("Reservation Update  : {}",r))
                .doOnError(e->log.error("Error updating with id:{}",uuid,e));

    }

    @Override
    public Mono<Void> delate(String uuid) {
        log.info("Deleting reservation With id: {}",uuid);
        return this.webClient
                .delete()
                .uri(RESOURCE+"/{reservationId}",uuid)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,r->MONO_400_ERROR)
                .bodyToMono(Void.class)
                .doOnSuccess(res->log.info("Reservation deleted: {}",res));

    }
}
