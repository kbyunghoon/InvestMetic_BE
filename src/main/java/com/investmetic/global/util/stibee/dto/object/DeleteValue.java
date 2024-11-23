package com.investmetic.global.util.stibee.dto.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteValue {
    @JsonProperty("fail")
    private List<String> failedUsers;
    @JsonProperty("success")
    private List<String> successUsers;

    public DeleteValue(List<String> failedUsers, List<String> successUsers) {
        this.failedUsers = failedUsers;
        this.successUsers = successUsers;
    }
}
