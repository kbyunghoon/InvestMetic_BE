package com.investmetic.domain.user.dto.object;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageMetadata {
    private String imageName;
    private String extension;
    private String size;
}
