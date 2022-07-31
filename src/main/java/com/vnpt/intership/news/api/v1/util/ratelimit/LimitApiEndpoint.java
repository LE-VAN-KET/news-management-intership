package com.vnpt.intership.news.api.v1.util.ratelimit;

import com.vnpt.intership.news.api.v1.exception.TooManyRequestException;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Builder
public class LimitApiEndpoint implements SessionCallback<List<Object>> {
    private String key;
    private int timeWindowInMilliSeconds;
    private int bucketCapacity;

    @Override
    public <K, V> List<Object> execute(@NotNull RedisOperations<K, V> operations) throws DataAccessException {
        RedisTemplate<String, Object> redisOperations = (RedisTemplate<String, Object> ) operations;
        ZSetOperations<String, Object> zSetOperations = redisOperations.opsForZSet();

        // Get already sent count
        Long amount = zSetOperations.zCard(this.key);

        try {
            long currentTime = System.currentTimeMillis();

            operations.multi();
            if (Objects.isNull(amount) || amount.equals(0)) {
                zSetOperations.addIfAbsent(this.key, currentTime, currentTime);
            } else if (amount < this.bucketCapacity) {
                long clearBefore = currentTime - this.timeWindowInMilliSeconds;
                // Remove keys older than now - window
                zSetOperations.removeRangeByScore(this.key, 0, clearBefore);

                // if allowed, then add to sorted set
                zSetOperations.add(this.key, currentTime, currentTime);
            } else {
                throw new TooManyRequestException("Too many request");
            }

            // expire the whole set after the window size
            redisOperations.expire(this.key, this.timeWindowInMilliSeconds, TimeUnit.MILLISECONDS);
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
