package com.investmetic.global.util.s3;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FilePath {
    USER_PROFILE("user-profile/", 1024 * 1024 * 2, Arrays.asList("jpg", "jpeg", "png")),
    STRATEGY_EXCEL("strategy/excel/", 1024 * 1024 * 500, Arrays.asList("xls", "xlsx")),
    STRATEGY_PROPOSAL("strategy/proposal/", 1024 * 1024 * 500, Arrays.asList("xls", "xlsx")),
    STRATEGY_IMAGE("strategy/image/", 1024 * 1024 * 2, Arrays.asList("jpg", "jpeg", "png")),
    NOTICE("notice/", 1024 * 1024 * 5, Arrays.asList("jpg", "jpeg", "png", "doc", "docx", "pptx", "ppt"));

    private final String path;
    private final int maxSize;
    private final List<String> allowedExtensions;

    public boolean isValidExtension(String fileName) {
        String fileExtension = getFileExtension(fileName);
        return allowedExtensions.contains(fileExtension.toLowerCase());
    }

    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        return index > 0 ? fileName.substring(index + 1) : "";
    }
}
