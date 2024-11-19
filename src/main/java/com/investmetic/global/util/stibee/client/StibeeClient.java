package com.investmetic.global.util.stibee.client;


import com.investmetic.global.util.stibee.dto.EmailSubscribeDto;
import com.investmetic.global.util.stibee.dto.object.DeleteValue;
import com.investmetic.global.util.stibee.dto.object.SignUpValue;
import com.investmetic.global.util.stibee.dto.response.StibeeSubscribeResponse;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;

@Component
@HttpExchange
public interface StibeeClient {

    @PostMapping("/{listId}/subscribers")
    StibeeSubscribeResponse<SignUpValue> subscribe(@PathVariable("listId") int listId,
                                                   @RequestBody EmailSubscribeDto emailSubscribeDto);


    @DeleteExchange("/{listId}/subscribers")
    StibeeSubscribeResponse<DeleteValue> deleteSubscriber(@PathVariable("listId") int listId,
                                                          @RequestBody List<String> list);


}
