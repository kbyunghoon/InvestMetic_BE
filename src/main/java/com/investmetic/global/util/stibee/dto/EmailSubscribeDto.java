package com.investmetic.global.util.stibee.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.investmetic.domain.user.dto.request.UserSignUpDto;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * 스티비 회원 등록시에 사용될 클래스
 */
@Getter
public class EmailSubscribeDto {

    /*
     * eventOccurredBy가 SUBSCRIBER일 때: 구독자의 구독 상태에 따라 다르게 처리됩니다.
     *
     * 1. 구독 중 상태일 때: 구독자 정보를 업데이트하지 않습니다. 기존 정보가 그대로 유지됩니다.
     * 2. 수신거부 또는 자동 삭제 상태일 때: 구독자의 구독 상태를 '구독 중으로 변경하고 이름, 전화번호 등의 구독자 정보를 업데이트합니다.
     * */
    private final String eventOccurredBy = "SUBSCRIBER";

    // "Y"로 하면 이메일로 구독을 하시겠습니까? 라는 메일이 옴. -> 구독자가 ok를 해야 추가됨.
    private final String confirmEmailYN = "N";

    /*
     * 세그먼트: 설정한 조건에 맞춰 구독자가 자동으로 분류됩니다.
     *           구독자 정보나 구독자 행동에 따라 구독자를 분류하는 조건을 설정하고,
     *            구독자가 특정 조건을 만족할 때마다 자동으로 설정한 세그먼트로 분류됩니다.
     * 그룹: 관리자가 필요에 따라 구독자를 직접 수동으로 분류합니다.
     * */
    private final List<String> groupIds; // 이거 없앨까요.

    private final List<EmailAndName> subscribers;

    @JsonProperty("$ad_agreed")
    private final String ad_agreed;



    private EmailSubscribeDto(String ad_agreed, List<String> groupIds, List<EmailAndName> subscribers) {
        this.ad_agreed = ad_agreed;
        this.groupIds = groupIds;
        this.subscribers = subscribers;
    }

    public static EmailSubscribeDto fromUserSignupDto(UserSignUpDto userSignUpDto) {

        //이메일 수신 동의
        String ad_agreed = userSignUpDto.getInfoAgreement() ? "Y" : "N";

        List<EmailAndName> subscribers = new ArrayList<>(
                List.of(EmailAndName.create(userSignUpDto.getEmail(), userSignUpDto.getUsername())));

        return new EmailSubscribeDto(ad_agreed, null, subscribers);
    }


}
