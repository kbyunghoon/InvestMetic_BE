package com.investmetic.global.util.stibee;


import com.investmetic.domain.user.dto.request.UserModifyDto;
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

    //확인
    @PostMapping("/mail/info/update")
    public ResponseEntity<BaseResponse<Boolean>> infoUpdate(
            @RequestBody InfoUpdateRequest infoUpdateRequest){

        return BaseResponse.success(stibeeEmailService.updateSubscriberInfo(UserModifyDto.builder().email(
                        infoUpdateRequest.getEmail()).infoAgreement(infoUpdateRequest.getInfoAgreement()).build(),
                infoUpdateRequest.getRole()));

    }

    //확인
    @GetMapping("/mail/term/update")
    public ResponseEntity<BaseResponse<Boolean>>  updateSubscriberTermDate(
            @RequestParam String email
    ){
        return BaseResponse.success(stibeeEmailService.updateSubscriberTermDate(email));
    }


    //확인
    @GetMapping("/mail/group/assign")
    public ResponseEntity<BaseResponse<Boolean>> assign(
            @RequestParam String email
    ){
        return BaseResponse.success(stibeeEmailService.assignGroup(email));
    }

    //확인
    @GetMapping("/mail/group/release")
    public ResponseEntity<BaseResponse<Boolean>> release(
            @RequestParam String email
    ){
        return BaseResponse.success(stibeeEmailService.releaseGroup(email));
    }

    //확인
    @PostMapping("/mail/code")
    public ResponseEntity<BaseResponse<Void>> codeAuth(@RequestBody EmailAndCode emailAndCode) {

        stibeeEmailService.sendAuthenticationCode(emailAndCode.getSubscriber(), emailAndCode.getCode());

        return BaseResponse.success();
    }


    //확인
    @GetMapping("/mail/unsubscribe")
    public ResponseEntity<BaseResponse<Boolean>> unsubscribe(
            @RequestParam String email
    ){
        return BaseResponse.success(stibeeEmailService.unsubscribeEmail(email));
    }

}
