package com.investmetic.global.util;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisUtil {

    private final StringRedisTemplate template;

    public Optional<String> getData(String key) {
        ValueOperations<String, String> valueOperations = template.opsForValue();
        return Optional.ofNullable(valueOperations.get(key));
    }

    public Set<String> getAllKeys(String pattern) {
        return template.keys(pattern);
    }


    public boolean existData(String key) {
        return Boolean.TRUE.equals(template.hasKey(key));
    }

    public void setDataExpire(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = template.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    public void deleteData(String key) {
        template.delete(key);
    }

    public void saveRefreshToken(String username, String refreshToken, long durationInSeconds) {
        template.opsForValue().set("REFRESH_TOKEN:" + username, refreshToken, durationInSeconds, TimeUnit.SECONDS);
    }

    public String getRefreshToken(String username) {
        return template.opsForValue().get("REFRESH_TOKEN:" + username);
    }

    public void deleteRefreshToken(String username) {
        template.delete("REFRESH_TOKEN:" + username);
    }
}