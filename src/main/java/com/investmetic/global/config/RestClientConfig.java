package com.investmetic.global.config;

import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.stibee.client.AutoApiStibeeClient;
import com.investmetic.global.util.stibee.client.StibeeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    @Value("${stibee.email.key}")
    private String stibeeEmailKey;


    @Bean
    public StibeeClient stibeeClient() {

        RestClient restClient = RestClient.builder().baseUrl("https://api.stibee.com/v1/lists")
                .defaultHeader("AccessToken", stibeeEmailKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();

        return proxyFactory.createClient(StibeeClient.class);
    }


    @Bean
    public AutoApiStibeeClient autoApiStibeeClient() {
        RestClient restClient = RestClient.builder().baseUrl("https://stibee.com/api/v1.0/auto")
                .defaultHeader("AccessToken", stibeeEmailKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {throw new BusinessException(
                ErrorCode.USERS_NOT_FOUND);}).build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();

        return proxyFactory.createClient(AutoApiStibeeClient.class);
    }


}
