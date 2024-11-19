package com.investmetic.global.util.stibee;


import com.investmetic.domain.user.dto.request.UserSignUpDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.global.util.stibee.client.AutoApiStibeeClient;
import com.investmetic.global.util.stibee.client.StibeeClient;
import com.investmetic.global.util.stibee.dto.EmailAndCode;
import com.investmetic.global.util.stibee.dto.object.DeleteValue;
import com.investmetic.global.util.stibee.dto.object.SignUpValue;
import com.investmetic.global.util.stibee.dto.request.EmailSubscribe;
import com.investmetic.global.util.stibee.dto.response.StibeeSubscribeResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StibeeEmailService {

    // HttpInterface로 사용.
    private final StibeeClient stibeeClient;

    private final AutoApiStibeeClient autoApiStibeeClient;

    @Value("${stibee.email.address-book.trader}")
    private int TraderAddressBook;

    @Value("${stibee.email.address-book.investor}")
    private int investorAddressBook;

    /**
     * 주소록에 추가 - 회원 가입시
     */
    public StibeeSubscribeResponse<SignUpValue> addSubscriber(UserSignUpDto user) {

        int listId = Role.isAdmin(user.getRole()) ? TraderAddressBook : investorAddressBook;

        EmailSubscribe emailSubscribe = EmailSubscribe.fromUserSignupDto(user);

        return stibeeClient.subscribe(listId, emailSubscribe);
    }

    /**
     * 주소록에서 삭제 - 회원 탈퇴시
     */
    public StibeeSubscribeResponse<DeleteValue> deleteSubscriber(Role role, String email) {

        int listid = Role.isAdmin(role) ? TraderAddressBook : investorAddressBook;
        List<String> list = new ArrayList<>(List.of(email));

        // restClient.delete()로는 body() 사용 못함. method(HttpMethod.DELETE)로 해야 가능함.

        return stibeeClient.deleteSubscriber(listid, list);
    }

    /**
     * 인증코드 발송 - 비밀번호 재설정 및 이메일 인증시
     */
    public void sendAuthenticationCode(String email, String code) {

        EmailAndCode emailAndCode = new EmailAndCode(email, code);

//         성공시 그냥 ok 만 옴
//         실패시 "구독자 상태를 파악할 수 없습니다.", HttpClientErrorException&BadRequest -> 스티비 안에 해당 이메일이 없음.
//         response.getBody() -> inputStream
        autoApiStibeeClient.sendAuthenticationCode(emailAndCode);
    }
}
