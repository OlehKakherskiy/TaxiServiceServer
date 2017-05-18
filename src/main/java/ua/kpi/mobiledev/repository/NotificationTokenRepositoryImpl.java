package ua.kpi.mobiledev.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import ua.kpi.mobiledev.domain.NotificationToken;
import ua.kpi.mobiledev.domain.User;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Repository("notificationTokenRepository")
public class NotificationTokenRepositoryImpl implements NotificationTokenRepository {

    @Autowired
    private RedisTemplate<String, NotificationToken> redisTemplate;

    @Override
    public void saveNotificationToken(User user, String token) {
        if (!StringUtils.isEmpty(token)) {
            save(user, new NotificationToken(token, true));
        }
    }

    @Override
    public NotificationToken getNotificationToken(User user) {
        return isNull(user) ? null : redisTemplate.opsForValue().get(user.getEmail());
    }

    @Override
    public void removeNotificationToken(User user) {
        redisTemplate.delete(user.getEmail());
    }

    @Override
    public void toggleNotificationToken(User user, boolean switchOn) {
        NotificationToken notificationToken = getNotificationToken(user);
        if (nonNull(notificationToken)) {
            notificationToken.setSwitchOnNotification(switchOn);
            save(user, notificationToken);
        }
    }

    private void save(User user, NotificationToken notificationToken) {
        redisTemplate.opsForValue().set(user.getEmail(), notificationToken);
    }
}
