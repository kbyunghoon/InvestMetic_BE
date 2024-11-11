package com.investmetic.domain.user.dto.object;

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

    private String imageName;
    private String extension;
    private int size;
}
