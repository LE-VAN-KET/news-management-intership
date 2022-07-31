package com.vnpt.intership.news.api.v1.util.ratelimit;

import com.vnpt.intership.news.api.v1.exception.TooManyRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor(staticName = "of")
public class LimitVerifyPerOtp implements SessionCallback<List<Object>> {
    private String key;
    private int MAX_REQUEST_VALIDATE_PER_OTP;
    private String prefixOtpCountHit;

    @Override
    public <K, V> List<Object> execute(@NotNull RedisOperations<K, V> operations) throws DataAccessException {
        RedisTemplate<String, Object> redisOperations = (RedisTemplate<String, Object>) operations;
        Integer countRequestVerifyOtp = (Integer) redisOperations.opsForValue().get(prefixOtpCountHit + this.key);

        try {
            operations.multi();
            if (Objects.isNull(countRequestVerifyOtp)) {
                redisOperations.opsForValue().setIfAbsent(prefixOtpCountHit + key, 1, 3, TimeUnit.MINUTES);
            } else if (countRequestVerifyOtp < this.MAX_REQUEST_VALIDATE_PER_OTP) {
                redisOperations.opsForValue().increment(prefixOtpCountHit + key);
            } else {
                throw new TooManyRequestException("Maximum 5 request verify per OTP! Please resend renew OTP against.");
            }
            return operations.exec();
        } catch (TooManyRequestException e) {
            operations.discard();
            throw e;
        } catch (Exception e) {
            operations.discard();
        }
        return Collections.emptyList();
    }
}
