package com.investmetic.global.util.stibee.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class StibeeSubscribeResponse<T> {
    @JsonProperty("Ok")
    private boolean ok;

    @JsonProperty("Error")
    private StibeeError error;

    @Data
    public static class StibeeError {
        private String code;
        private int httpStatusCode;
        private String message;
    }

    @JsonProperty("Value")
    private T value;


}
