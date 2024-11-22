package com.investmetic.global.util.stibee.client;


import com.investmetic.global.util.stibee.dto.object.DeleteValue;
import com.investmetic.global.util.stibee.dto.object.GroupErrorValue;
import com.investmetic.global.util.stibee.dto.object.SignUpValue;
import com.investmetic.global.util.stibee.dto.object.UnsubscribeValue;
import com.investmetic.global.util.stibee.dto.request.EmailSubscribe;
import com.investmetic.global.util.stibee.dto.response.StibeeSubscribeResponse;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

@Component
@HttpExchange
public interface StibeeClient {
    /**
     * 주소록에 회원 추가.
     */
    @PostExchange("/{listId}/subscribers")
    StibeeSubscribeResponse<SignUpValue> subscribe(@PathVariable("listId") int listId,
                                                   @RequestBody EmailSubscribe emailSubscribe);

    /**
     * 주소록에서 회원 삭제.
     */
    @DeleteExchange("/{listId}/subscribers")
    StibeeSubscribeResponse<DeleteValue> deleteSubscriber(@PathVariable("listId") int listId,
                                                          @RequestBody List<String> list);

    /**
     * 그룹 할당.
     * */
    @PostExchange("/{listId}/groups/{groupId}/subscribers/assign")
    StibeeSubscribeResponse<GroupErrorValue> assignGroup(@PathVariable("listId") int listId,
                                                     @PathVariable("groupId") int groupId,
                                                     @RequestBody List<String> list);


    /**
     * 그룹 해제.
     * */
    @PostExchange("/{listId}/groups/{groupId}/subscribers/release")
    StibeeSubscribeResponse<GroupErrorValue> releaseGroup(@PathVariable("listId") int listId,
                                                          @PathVariable("groupId") int groupId,
                                                          @RequestBody List<String> list);

    /**
     * 수신 거부.
     */
    @PutExchange("/{listId}/subscribers/unsubscribe")
    StibeeSubscribeResponse<UnsubscribeValue> unsubscribeEmail(@PathVariable("listId") int listId,
                                                               @RequestBody List<String> list);
}
