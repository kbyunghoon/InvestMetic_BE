package com.investmetic.domain.user.dto.object;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ImageMetadata {

    public ImageMetadata(String imageName, String extension, int size) {
        this.imageName = imageName;
        this.extension = extension;
        this.size = size;
    }

    @NotBlank(message = "유저 이미지 이름 null")
    private String imageName;

    private String extension;

    @Min(value=1, message = "유저 이미지 0Byte")
    @Max(value=1024*1024*2, message = "유저 이미지 2MB 초과.")
    private int size;

}
