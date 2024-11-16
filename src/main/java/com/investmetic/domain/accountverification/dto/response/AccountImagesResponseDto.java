package com.investmetic.domain.accountverification.dto.response;

import com.investmetic.domain.accountverification.model.entity.AccountVerification;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountImagesResponseDto {
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
}
