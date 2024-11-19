package com.investmetic.global.util.stibee;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.investmetic.domain.user.dto.request.UserSignUpDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.stibee.client.AutoApiStibeeClient;
import com.investmetic.global.util.stibee.client.StibeeClient;
import com.investmetic.global.util.stibee.dto.EmailSubscribeDto;
import com.investmetic.global.util.stibee.dto.object.DeleteValue;
import com.investmetic.global.util.stibee.dto.object.SignUpValue;
import com.investmetic.global.util.stibee.dto.request.EmailAndCode;
import com.investmetic.global.util.stibee.dto.response.StibeeSubscribeResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class StibeeEmailService {

    private final ObjectMapper objectMapper;
    @Value("${stibee.email.key}")
    private String stibeeEmailKey;
    @Value("${stibee.email.address-book.trader}")
    private int TraderAddressBook;
    @Value("${stibee.email.address-book.investor}")
    private int investorAddressBook;

    private final StibeeClient stibeeClient;

    private final AutoApiStibeeClient autoApiStibeeClient;

    /**
     * 주소록에 추가
     */
    public StibeeSubscribeResponse<SignUpValue> addSubscriber(UserSignUpDto user) {

        int listid = Role.isAdmin(user.getRole()) ? TraderAddressBook : investorAddressBook;

        RestClient restClient = RestClient.builder().defaultHeader("AccessToken", stibeeEmailKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();

        EmailSubscribeDto emailSubscribeDto = EmailSubscribeDto.fromUserSignupDto(user);

        ParameterizedTypeReference<StibeeSubscribeResponse<SignUpValue>> type = new ParameterizedTypeReference<>() {
        };

        return restClient.post().uri("https://api.stibee.com/v1/lists/{listid}/subscribers", listid)
                .body(emailSubscribeDto).retrieve().body(type);

    }

    /**
     * 주소록에서 삭제
     */
    public StibeeSubscribeResponse<DeleteValue> deleteSubscriber(Role role, String email) {

        int listid = Role.isAdmin(role) ? TraderAddressBook : investorAddressBook;
        List<String> list = new ArrayList<>(List.of(email));

        RestClient restClient = RestClient.builder().defaultHeader("AccessToken", stibeeEmailKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
        ParameterizedTypeReference<StibeeSubscribeResponse<DeleteValue>> type = new ParameterizedTypeReference<>() {
        };

        return restClient.method(HttpMethod.DELETE).uri("https://api.stibee.com/v1/lists/{listid}/subscribers", listid)
                .body(list).retrieve().body(type);
    }

    /**
     * 인증코드 발송
     */
    public void sendAuthenticationCode(String email, String code) {

        EmailAndCode emailAndCode = new EmailAndCode(email, code);

        RestClient restClient = RestClient.builder().defaultHeader("AccessToken", stibeeEmailKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();


        autoApiStibeeClient.sendAuthenticationCode(emailAndCode);

        // 성공시 그냥 ok 만 옴
        restClient.post()
                .uri("https://stibee.com/api/v1.0/auto/NmEwMmU2ZTItNzU2Ni00MzNhLWJkODktNzAzMjljOTQ2Mjhl")
                .body(emailAndCode).exchange((req, res) -> {
                    if (res.getStatusCode().is5xxServerError()) {

                        //스티비 서버 error
                        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
                    } else if (res.getStatusCode().is4xxClientError()) {

                        // response.getBody() -> inputStream
                        // 실패시 "구독자 상태를 파악할 수 없습니다.", HttpClientErrorException&BadRequest -> 스티비 안에 해당 이메일이 없음.
                        throw new BusinessException(ErrorCode.USERS_NOT_FOUND);
                    }

                    // 성공
                    return "";
                });

    }


}
