package ua.kpi.mobiledev.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import ua.kpi.mobiledev.domain.User;

import java.util.Objects;

@Repository("notificationTokenRepository")
public class NotificationTokenRepositoryImpl implements NotificationTokenRepository {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void saveNotificationToken(User user, String notificationToken) {
        if (!StringUtils.isEmpty(notificationToken)) {
            redisTemplate.opsForValue().set(user.getEmail(), notificationToken);
        }
    }

    @Override
    public String getNotificationToken(User user) {
        return Objects.isNull(user) ? null : redisTemplate.opsForValue().get(user.getEmail());
    }
}
