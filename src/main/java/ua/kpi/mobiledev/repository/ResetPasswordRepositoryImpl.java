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
    private RedisTemplate<UUID, ResetPasswordData> redisTemplate;

    @Value("${security.resetPasswordCodeAlive}")
    private Integer expireDurationInMs;

    @Override
    public void save(UUID id, ResetPasswordData value) {
        redisTemplate.opsForValue().set(id, value);
        redisTemplate.expire(id, ofNullable(expireDurationInMs).orElse(DEFAULT_EXPIRE_DURATION), TimeUnit.MILLISECONDS);
    }

    @Override
    public void remove(UUID id) {
        redisTemplate.delete(id);
    }

    @Override
    public ResetPasswordData get(UUID resetPasswordDataId) {
        return redisTemplate.opsForValue().get(resetPasswordDataId);
    }
}
