package com.investmetic.domain.user.dto.object;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageMetadata {

    @NotBlank(message = "유저 이미지 이름 null")
    private String imageName;


    @Min(value = 1, message = "유저 이미지 0Byte")
    @Max(value = 1024 * 1024 * 2, message = "유저 이미지 2MB 초과.")
    private int size;

    public ImageMetadata(String imageName, int size) {
        this.imageName = imageName;
        this.size = size;
    }

}
