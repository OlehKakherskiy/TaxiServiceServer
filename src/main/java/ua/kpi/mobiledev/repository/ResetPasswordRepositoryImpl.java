package ua.kpi.mobiledev.repository;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ua.kpi.mobiledev.web.security.model.ResetPasswordData;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.util.Optional.ofNullable;

@Repository("resetPasswordRepository")
@Setter
public class ResetPasswordRepositoryImpl implements ResetPasswordRepository {

    private static final int DEFAULT_EXPIRE_DURATION = 3600;

    @Autowired
    private RedisTemplate<String, ResetPasswordData> redisTemplate;

    @Value("${security.resetPasswordCodeAlive}")
    private Integer expireDurationInMs;

    @Override
    public void save(UUID id, ResetPasswordData value) {
        String key = constructKey(id);
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, ofNullable(expireDurationInMs).orElse(DEFAULT_EXPIRE_DURATION), TimeUnit.SECONDS);
    }

    @Override
    public void remove(UUID id) {
        redisTemplate.delete(constructKey(id));
    }

    @Override
    public ResetPasswordData get(UUID resetPasswordDataId) {
        return redisTemplate.opsForValue().get(constructKey(resetPasswordDataId));
    }

    private String constructKey(UUID id) {
        return "resetPasswordData:" + id.toString();
    }
}
