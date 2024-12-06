package com.investmetic.global.security.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import com.investmetic.global.util.RedisUtil;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;

@Component
@Primary
@RequiredArgsConstructor
public class RedisRememberMeTokenRepository implements PersistentTokenRepository { // Remember-me를 redis에 저장하기 위한

    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;
    private static final String KEY_PREFIX = "REMEMBER_ME_TOKEN:"; // Redis 키 접두사
    private static final long TOKEN_VALIDITY_SECONDS = 604800L; // 7일

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        try {
            String key = KEY_PREFIX + token.getSeries();
            String tokenJson = objectMapper.writeValueAsString(token);
            redisUtil.setDataExpire(key, tokenJson, TOKEN_VALIDITY_SECONDS);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.TOKEN_NOT_FOUND);
        }
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        try {
            String key = KEY_PREFIX + series;
            String tokenJson = redisUtil.getData(key)
                    .orElseThrow(() -> new RuntimeException("Token not found for series: " + series));
            PersistentRememberMeToken currentToken = objectMapper.readValue(tokenJson, PersistentRememberMeToken.class);
            PersistentRememberMeToken updatedToken = new PersistentRememberMeToken(
                    currentToken.getUsername(), series, tokenValue, lastUsed
            );
            redisUtil.setDataExpire(key, objectMapper.writeValueAsString(updatedToken), TOKEN_VALIDITY_SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update remember-me token in Redis", e);
        }
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        try {
            String key = KEY_PREFIX + seriesId;
            String tokenJson = redisUtil.getData(key).orElse(null);
            if (tokenJson != null) {
                return objectMapper.readValue(tokenJson, PersistentRememberMeToken.class);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve remember-me token from Redis", e);
        }
    }

    @Override
    public void removeUserTokens(String username) {
        try {
            // Redis에 저장된 모든 Remember-Me 키를 탐색
            redisUtil.getAllKeys(KEY_PREFIX + "*").forEach(key -> {
                String tokenJson = redisUtil.getData(key).orElse(null);
                if (tokenJson != null) {
                    try {
                        PersistentRememberMeToken token = objectMapper.readValue(tokenJson,
                                PersistentRememberMeToken.class);
                        if (token.getUsername().equals(username)) {
                            redisUtil.deleteData(key);
                        }
                    } catch (Exception ignored) {
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove remember-me tokens from Redis", e);
        }
    }
}
