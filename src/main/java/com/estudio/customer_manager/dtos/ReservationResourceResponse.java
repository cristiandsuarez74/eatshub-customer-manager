package com.estudio.customer_manager.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ReservationResourceResponse {
    @JsonProperty("Resource")
    private String resource;
}
