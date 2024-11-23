package com.investmetic.global.util.stibee;


import com.investmetic.domain.user.dto.request.UserModifyDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.util.stibee.dto.DeleteRequest;
import com.investmetic.global.util.stibee.dto.EmailAndCode;
import com.investmetic.global.util.stibee.dto.InfoUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailTestController {

    private final StibeeEmailService stibeeEmailService;

    //확인
    @PostMapping("/mail/create")
    public ResponseEntity<BaseResponse<Boolean>> signup(
            @RequestBody User user) {

        return BaseResponse.success(stibeeEmailService.addSubscriber(user));
    }
    //확인
    @PostMapping("/mail/delete")
    public ResponseEntity<BaseResponse<Boolean>> delete(
            @RequestBody DeleteRequest request) {

        return BaseResponse.success(stibeeEmailService.deleteSubscriber(request.getEmail()));
    }

    //회원 정보 업데이트 확인
    @PostMapping("/mail/info/update")
    public ResponseEntity<BaseResponse<Boolean>> infoUpdate(
            @RequestBody InfoUpdateRequest infoUpdateRequest) {

        UserModifyDto userModifyDto = UserModifyDto.builder()
                .email(infoUpdateRequest.getEmail())
                .infoAgreement(infoUpdateRequest.getInfoAgreement())
                .build();

        Role role = infoUpdateRequest.getRole();

        return BaseResponse.success(stibeeEmailService.updateSubscriberInfo(userModifyDto, role));
    }

    // 약관 날짜 업데이트 확인
    @GetMapping("/mail/term/update")
    public ResponseEntity<BaseResponse<Boolean>>  updateSubscriberTermDate(
            @RequestParam String email
    ){
        return BaseResponse.success(stibeeEmailService.updateSubscriberTermDate(email));
    }


    //그룹 할당 확인
    @GetMapping("/mail/group/assign")
    public ResponseEntity<BaseResponse<Boolean>> assign(
            @RequestParam String email
    ){
        return BaseResponse.success(stibeeEmailService.assignGroup(email));
    }

    //그룹 할당 취소 확인
    @GetMapping("/mail/group/release")
    public ResponseEntity<BaseResponse<Boolean>> release(
            @RequestParam String email
    ){
        return BaseResponse.success(stibeeEmailService.releaseGroup(email));
    }

    // 인증코드 확인
    @PostMapping("/mail/code")
    public ResponseEntity<BaseResponse<Void>> codeAuth(@RequestBody EmailAndCode emailAndCode) {

        stibeeEmailService.sendAuthenticationCode(emailAndCode.getSubscriber(), emailAndCode.getCode());

        return BaseResponse.success();
    }


    // 수신 거부 확인
    @GetMapping("/mail/unsubscribe")
    public ResponseEntity<BaseResponse<Boolean>> unsubscribe(
            @RequestParam String email
    ){
        return BaseResponse.success(stibeeEmailService.unsubscribeEmail(email));
    }

}
