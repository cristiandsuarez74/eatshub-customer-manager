package com.estudio.customer_manager.configs;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j

public class WebClientConfiguration {

    @Value("http://localhost:8080/")
    private String baseUrl;
    @Bean
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder()
                .baseUrl(baseUrl)
                .codecs(configure-> configure
                        .defaultCodecs()
                        .maxInMemorySize(16*1024*1024))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT,MediaType.APPLICATION_JSON_VALUE)
                .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                    log.info("Client request : {},{}",clientRequest.method(),clientRequest.url());
                    return Mono.just(clientRequest);
                }))
                .filter(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                    log.info("client Response: {},{}",clientResponse.statusCode(),clientResponse.headers());
                    return Mono.just(clientResponse);
                }));

    }
}
