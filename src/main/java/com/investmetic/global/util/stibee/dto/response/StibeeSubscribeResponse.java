package com.investmetic.global.util.stibee.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class StibeeSubscribeResponse<T> {
    @JsonProperty("Ok")
    private boolean ok;

    @JsonProperty("Error")
    private StibeeError error;

    @JsonProperty("Value")
    private T value;

    @Data
    public static class StibeeError {
        private String code;
        private int httpStatusCode;
        private String message;
    }


}
