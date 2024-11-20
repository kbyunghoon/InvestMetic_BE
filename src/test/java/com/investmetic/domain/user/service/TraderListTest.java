package com.investmetic.domain.user.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.investmetic.domain.user.dto.response.TraderProfileDto;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
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
    @DisplayName("")
    void traderListTest1() {
        // given
        Pageable pageable = PageRequest.of(0, 12);
        String orderBy = null;
        String keyword = null;

        List<TraderProfileDto> list = new ArrayList<>();
        TraderProfileDto traderProfileDto = new TraderProfileDto(1, "userName", "nickname", "imageUrl", 4L, 100L);
        list.add(traderProfileDto);

        // size가 1인 목록 반환.
        when(userRepository.getTraderListPage(orderBy, keyword, pageable)).thenReturn(new PageImpl<>(list));

        // when, then 아무런 에러도 없어야함.
        assertThatCode(() -> userRepository.getTraderListPage(orderBy, keyword, pageable)).doesNotThrowAnyException();


    }

    @Test
    @DisplayName("트레이더 조회 목록 size가 0일때.")
    void traderListTest2() {

        // given
        Pageable pageable = PageRequest.of(0, 12);
        String orderBy = null;
        String keyword = null;

        List<TraderProfileDto> list = new ArrayList<>();

        // size가 0인 목록 반환
        when(userRepository.getTraderListPage(orderBy, keyword, pageable)).thenReturn(new PageImpl<>(list));

        // when, then - throw BusinessException
        assertThatThrownBy(() -> userService.getTraderList(orderBy, keyword, pageable)).isInstanceOf(
                BusinessException.class).hasMessage(ErrorCode.TRADER_LIST_RETRIEVAL_FAILED.getMessage());

    }

}
