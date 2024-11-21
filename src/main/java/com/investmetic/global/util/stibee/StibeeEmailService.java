package com.investmetic.global.util.stibee;


import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.global.util.stibee.client.AutoApiStibeeClient;
import com.investmetic.global.util.stibee.client.StibeeClient;
import com.investmetic.global.util.stibee.dto.EmailAndCode;
import com.investmetic.global.util.stibee.dto.request.EmailSubscribe;
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
    private int traderAddressBook;

    @Value("${stibee.email.address-book.investor}")
    private int investorAddressBook;

    /**
     * 주소록에 추가 - 회원 가입시, 탈퇴한 회원이 똑같은 이메일로 다시 회원가입 한 경우 이메일 발송 안함.
     */
    public Boolean addSubscriber(User user) {

        int listId = Role.isAdmin(user.getRole()) ? traderAddressBook : investorAddressBook;

        EmailSubscribe emailSubscribe = EmailSubscribe.fromUser(user, "SUBSCRIBER");

        // 실패 이유 SignupValue key로 되어있음.
        return stibeeClient.subscribe(listId, emailSubscribe).isOk();
    }

    /**
     * 이미등록된 회원의 정보를 변경.- 이름과 광고성 정보 수신이 변경되었을 때. 내용은 addSubScriber와 같음.
     */
    public Boolean updateSubscriber(User user, Role role) {

        int listId = Role.isAdmin(role) ? traderAddressBook : investorAddressBook;

        EmailSubscribe emailSubscribe = EmailSubscribe.fromUser(user, "MANUAL");

        return stibeeClient.subscribe(listId, emailSubscribe).isOk();
    }

    /**
     * 주소록에서 삭제 - 회원 탈퇴시
     * TODO : 수신거부와 차이점 확인하기
     */
    public Boolean deleteSubscriber(Role role, String email) {

        int listid = Role.isAdmin(role) ? traderAddressBook : investorAddressBook;
        List<String> list = new ArrayList<>(List.of(email));

        // restClient.delete()로는 body() 사용 못함. method(HttpMethod.DELETE)로 해야 가능함.

        return stibeeClient.deleteSubscriber(listid, list).isOk();
    }


    /**
     * 이메일 수신 거부 - 정보 수정시. 이메일 수신 거부이면 광고성 정보 수신 거부인지 확인하기.
     * TODO : 완전삭제와 차이점 확인하기
     */
    public Boolean unsubscribeEmail(Role role, String email) {

        int listid = Role.isAdmin(role) ? traderAddressBook : investorAddressBook;
        List<String> list = new ArrayList<>(List.of(email));

        return stibeeClient.unsubscribeEmail(listid, list).isOk();
    }


    /**
     * 인증코드 발송 - 비밀번호 재설정 및 이메일 인증시
     *
     * @param email - 회원의 Email
     * @param code  - 인증코드.
     */
    public void sendAuthenticationCode(String email, String code) {

        EmailAndCode emailAndCode = new EmailAndCode(email, code);

//         성공시 그냥 ok 만 옴
//         실패시 "구독자 상태를 파악할 수 없습니다.", HttpClientErrorException&BadRequest -> 스티비 안에 해당 이메일이 없음.
//         response.getBody() -> inputStream
        autoApiStibeeClient.sendAuthenticationCode(emailAndCode);
    }


}
