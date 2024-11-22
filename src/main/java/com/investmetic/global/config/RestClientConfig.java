package com.investmetic.global.config;

import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.stibee.client.AutoApiStibeeClient;
import com.investmetic.global.util.stibee.client.StibeeClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * <a href="https://docs.spring.io/spring-boot/reference/io/rest-client.html#io.rest-client.restclient">...</a>
 */
@Configuration
public class RestClientConfig {

    //ClientHttpRequestFactorySettings - timeout설정.

    @Value("${stibee.email.key}")
    private String stibeeEmailKey;


    @Bean
    public StibeeClient stibeeClient() {

        // 기본 설정
        RestClient restClient = RestClient.builder().baseUrl("https://api.stibee.com/v1/lists")
                .defaultHeader("AccessToken", stibeeEmailKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();

        return proxyFactory.createClient(StibeeClient.class);
    }


    @Bean
    public AutoApiStibeeClient autoApiStibeeClient() {

        // 기본 설정.
        RestClient restClient = RestClient.builder().baseUrl("https://stibee.com/api/v1.0/auto")
                .defaultHeader("AccessToken", stibeeEmailKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new BusinessException(ErrorCode.USERS_NOT_FOUND);
                }).build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();

        return proxyFactory.createClient(AutoApiStibeeClient.class);
    }


    /**
     * RestClient - 동기식 Http Client, 체이닝 방식, restTemplate와 다를건 별로 없음.
     */
    @Bean
    public RestClient restClient() {
        return RestClient.builder().requestFactory(customRequestFactory()).build();
    }

    /**
     * HttpComponentsClientHttpRequestFactory
     */
    public ClientHttpRequestFactory customRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS.withConnectTimeout(
                        Duration.ofSeconds(5))  // 연결 타임아웃을 5초로 설정
                .withReadTimeout(Duration.ofSeconds(5)); // 읽기 타임아웃을 5초로 설정
        return ClientHttpRequestFactories.get(settings);
    }


}
