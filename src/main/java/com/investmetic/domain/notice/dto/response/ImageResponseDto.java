package com.investmetic.domain.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ImageResponseDto {
    String preSignedUrl;
    String imagefilePath;
}
