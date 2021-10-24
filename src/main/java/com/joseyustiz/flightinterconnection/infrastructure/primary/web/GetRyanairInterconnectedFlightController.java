package com.joseyustiz.flightinterconnection.infrastructure.primary.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class GetRyanairInterconnectedFlightController {
    @GetMapping(value = "/v1/flights/interconnections", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> hello(){
        return Mono.just("{\"text\": \"Hello\"}");
    }
}
