package ua.kpi.mobiledev.web.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ua.kpi.mobiledev.web.security.model.TokenStoreObject;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class RedisTokenStoreService implements RedisStoreService<String, TokenStoreObject> {

    @Autowired
    private RedisTemplate<String, TokenStoreObject> redisTemplate;

    @Override
    public void save(String key, TokenStoreObject value) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, getExistedTime(value.getExpiredIn()), TimeUnit.MILLISECONDS);
    }

    private long getExistedTime(Date expiredTime) {
        return expiredTime.getTime() - new Date().getTime();
    }

    @Override
    public TokenStoreObject get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
