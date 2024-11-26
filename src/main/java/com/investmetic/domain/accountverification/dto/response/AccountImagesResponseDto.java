package com.investmetic.domain.accountverification.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.investmetic.domain.accountverification.model.entity.AccountVerification;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountImagesResponseDto {
    @JsonInclude(Include.NON_NULL)
    private Long id;
    private String title;
    private String imageUrl;

    @Builder
    public AccountImagesResponseDto(Long id, String title, String imageUrl) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public static AccountImagesResponseDto from(AccountVerification accountVerification) {
        return AccountImagesResponseDto.builder()
                .id(accountVerification.getAccountVerificationId())
                .title(accountVerification.getTitle())
                .imageUrl(accountVerification.getAccountVerificationUrl())
                .build();
    }

    public static AccountImagesResponseDto createAccountImages(AccountVerification accountVerification) {
        return AccountImagesResponseDto.builder()
                .id(null)
                .title(accountVerification.getTitle())
                .imageUrl(accountVerification.getAccountVerificationUrl())
                .build();
    }
}
