package com.investmetic.domain.user.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;

import com.investmetic.domain.user.dto.object.TraderListSort;
import com.investmetic.domain.user.dto.response.TraderProfileDto;
import com.investmetic.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("트레이더 조회 목록 기능")
@ExtendWith(MockitoExtension.class)
class TraderListTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;


    @Test
    @DisplayName("트레이더 목록 조회 정상")
    void traderListTest1() {
        // given
        Pageable pageable = PageRequest.of(0, 12);
        TraderListSort sort = TraderListSort.SUBSCRIBE_TOTAL;
        String keyword = null;

        List<TraderProfileDto> list = new ArrayList<>();
        TraderProfileDto traderProfileDto = new TraderProfileDto(1L, "userName", "nickname", "imageUrl", 4L, 100);
        list.add(traderProfileDto);

        // size가 1인 목록 반환.
        when(userRepository.getTraderListPage(sort, keyword, pageable)).thenReturn(new PageImpl<>(list));

        // when, then 아무런 에러도 없어야함.
        assertThatCode(() -> userRepository.getTraderListPage(sort, keyword, pageable)).doesNotThrowAnyException();
    }

}
