package com.ecomm.productservice.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final List<FieldErrorDetail> fieldErrors;

    @Getter
    @Builder
    public static class FieldErrorDetail {
        private final String field;
        private final String message;
        private final Object rejectedValue;
    }

}