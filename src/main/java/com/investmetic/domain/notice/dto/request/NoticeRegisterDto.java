package com.investmetic.domain.notice.dto.request;

import com.investmetic.domain.notice.model.entity.Notice;
import com.investmetic.domain.user.model.entity.User;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NoticeRegisterDto {
    private String title;
    private String content;
    private List<Integer> sizes;
    private List<String> filePaths;
    public Notice toEntity(User user){
        return Notice.builder()
                .title(title)
                .content(content)
                .user(user)
                .build();
    }
}
