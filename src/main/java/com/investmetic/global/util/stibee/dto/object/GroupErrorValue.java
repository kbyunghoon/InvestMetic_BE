package com.investmetic.global.util.stibee.dto.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GroupErrorValue {
    @JsonProperty("Code")
    private String code;

    @JsonProperty("Message")
    private String message;
}
