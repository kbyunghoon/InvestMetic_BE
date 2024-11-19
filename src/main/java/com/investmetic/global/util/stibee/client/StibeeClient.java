package com.investmetic.global.util.stibee.client;


import com.investmetic.global.util.stibee.dto.object.DeleteValue;
import com.investmetic.global.util.stibee.dto.object.SignUpValue;
import com.investmetic.global.util.stibee.dto.request.EmailSubscribe;
import com.investmetic.global.util.stibee.dto.response.StibeeSubscribeResponse;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@Component
@HttpExchange
public interface StibeeClient {
    /**
     * 주소록에 회원 추가.
     * */
    @PostExchange("/{listid}/subscribers")
    StibeeSubscribeResponse<SignUpValue> subscribe(@PathVariable("listid") int listId,
                                                   @RequestBody EmailSubscribe emailSubscribe);

    /**
     * 주소록에서 회원 삭제.
     * */
    @DeleteExchange("/{listid}/subscribers")
    StibeeSubscribeResponse<DeleteValue> deleteSubscriber(@PathVariable("listid") int listId,
                                                          @RequestBody List<String> list);

}
