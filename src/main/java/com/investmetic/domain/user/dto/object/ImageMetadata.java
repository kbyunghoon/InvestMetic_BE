package com.investmetic.domain.user.dto.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class ImageMetadata{
    private String imageName;
    private String extension;
    private String size;

}
