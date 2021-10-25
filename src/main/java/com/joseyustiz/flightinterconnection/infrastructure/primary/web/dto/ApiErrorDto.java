package com.joseyustiz.flightinterconnection.infrastructure.primary.web.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorDto {
    @Singular
    List<Error> errors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Error{
        private String property;
        private String message;


    }
}
