package com.investmetic.global.util.stibee;


import com.investmetic.domain.user.dto.request.UserSignUpDto;
import com.investmetic.global.exception.BaseResponse;
import com.investmetic.global.util.stibee.dto.DeleteRequest;
import com.investmetic.global.util.stibee.dto.EmailAndCode;
import com.investmetic.global.util.stibee.dto.object.DeleteValue;
import com.investmetic.global.util.stibee.dto.object.SignUpValue;
import com.investmetic.global.util.stibee.dto.response.StibeeSubscribeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailTestController {

    private final StibeeEmailService stibeeEmailService;


    @PostMapping("/mail/create")
    public ResponseEntity<BaseResponse<StibeeSubscribeResponse<SignUpValue>>> signup(
            @RequestBody UserSignUpDto userSignUpDto) {

        return BaseResponse.success(stibeeEmailService.addSubscriber(userSignUpDto));
    }

    @PostMapping("/mail/delete")
    public ResponseEntity<BaseResponse<StibeeSubscribeResponse<DeleteValue>>> delete(
            @RequestBody DeleteRequest request) {

        return BaseResponse.success(stibeeEmailService.deleteSubscriber(request.getRole(), request.getEmail()));
    }

    @PostMapping("/mail/code")
    public ResponseEntity<BaseResponse<Void>> codeAuth(@RequestBody EmailAndCode emailAndCode) {

        stibeeEmailService.sendAuthenticationCode(emailAndCode.getSubscriber(), emailAndCode.getCode());

        return BaseResponse.success();
    }


}
