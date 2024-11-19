package com.investmetic.global.util.stibee.dto.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class SignUpValue {
    private List<Detail> failDuplicatedEmail = new ArrayList<>();
    private List<Detail> failDuplicatedPhone = new ArrayList<>();
    private List<Detail> failExistEmail = new ArrayList<>();
    private List<Detail> failExistPhone = new ArrayList<>();
    private List<Detail> failNoEmail = new ArrayList<>();
    private List<Detail> failUnknown = new ArrayList<>();
    private List<Detail> failValidation = new ArrayList<>();
    private List<Detail> failValidationDateTime = new ArrayList<>();
    private List<Detail> failWrongEmail = new ArrayList<>();
    private List<Detail> failWrongPhone = new ArrayList<>();
    private List<Detail> success = new ArrayList<>();
    private List<Detail> update = new ArrayList<>();

    @Data
    public static class Detail {
        @JsonProperty("$createdTime")
        private String createdTime;

        @JsonProperty("$modifiedTime")
        private String modifiedTime;

        @JsonProperty("$status")
        private String status;

        @JsonProperty("$type")
        private String type;

        private String email;
        private String name;

        @JsonProperty("stb_ad_agreement")
        private boolean stbAdAgreement;
    }
}
