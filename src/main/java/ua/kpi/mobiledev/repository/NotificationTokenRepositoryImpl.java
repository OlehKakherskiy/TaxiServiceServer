package ua.kpi.mobiledev.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import ua.kpi.mobiledev.domain.NotificationToken;
import ua.kpi.mobiledev.domain.User;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Repository("notificationTokenRepository")
public class NotificationTokenRepositoryImpl implements NotificationTokenRepository {

    private static final String DRIVER_TOKEN_LIST = "driverTokenList:notificationToken:";
    private static final int REMOVE_FIRST = 1;

    @Value("${system.notificationTokenAliveDay}")
    private Integer expirationTimeDays;

    private RedisTemplate<String, NotificationToken> redisTemplate;

    @Autowired
    public NotificationTokenRepositoryImpl(RedisTemplate<String, NotificationToken> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveNotificationToken(User user, String token) {
        if (!StringUtils.isEmpty(token)) {
            NotificationToken notificationToken = getNotificationToken(user);
            if (isNull(notificationToken)) {
                persistToken(user, token, true);
            } else if (!notificationToken.getToken().equals(token)) {
                replaceOldToken(notificationToken, user, token);
            }
            //token is the same as saved one
        }
    }

    private void persistToken(User user, String newToken, boolean switchOnNotification) {
        NotificationToken newNotificationToken = setAndExpireToken(user, newToken, switchOnNotification);
        if (isTaxiDriver(user)) {
            addToBroadcastNotification(newNotificationToken);
        }
    }

    private NotificationToken setAndExpireToken(User user, String newToken, boolean switchOnNotification) {
        NotificationToken newNotificationToken = new NotificationToken(newToken, switchOnNotification);
        String key = constructNotificationTokenKey(user);
        redisTemplate.opsForValue().set(key, newNotificationToken);
        redisTemplate.expireAt(key, processExpireDate());
        return newNotificationToken;
    }

    private Date processExpireDate() {
        Date d = Date.from(LocalDate.now().atStartOfDay().plusDays(expirationTimeDays).toInstant(ZoneOffset.UTC));
        d.setHours(0);
        return d;
    }

    private void replaceOldToken(NotificationToken oldToken, User user, String newToken) {
        long expireAt = redisTemplate.getExpire(constructNotificationTokenKey(user), TimeUnit.DAYS);
        persistToken(user, newToken, oldToken.isSwitchOnNotification());
        if (isTaxiDriver(user)) {
            removeFromBroadcastNotification(oldToken, expireAt);
        }
    }

    private void addToBroadcastNotification(NotificationToken notificationToken) {
        String key = DRIVER_TOKEN_LIST + LocalDate.now();
        if (redisTemplate.opsForSet().size(key) == 0) {
            redisTemplate.expireAt(key, processExpireDate());
        }
        redisTemplate.opsForSet().add(key, notificationToken);
    }

    private void removeFromBroadcastNotification(NotificationToken notificationToken, long leftDays) {
        String key = DRIVER_TOKEN_LIST + LocalDate.now().minusDays(expirationTimeDays - (leftDays + 1)); //driverTokenList:notificationToken:<add_date>
        redisTemplate.opsForSet().remove(key, REMOVE_FIRST, notificationToken);
    }

    @Override
    public NotificationToken getNotificationToken(User user) {
        return isNull(user) ? null : redisTemplate.opsForValue().get(constructNotificationTokenKey(user));
    }

    @Override
    public void toggleNotificationToken(User user, boolean switchOn) {
        NotificationToken notificationToken = getNotificationToken(user);
        if (nonNull(notificationToken) && notificationToken.isSwitchOnNotification() != switchOn) {
            update(user, notificationToken, switchOn);
        }
    }

    private void update(User user, NotificationToken notificationToken, boolean switchOnUpdate) {
        long expireAt = redisTemplate.getExpire(constructNotificationTokenKey(user), TimeUnit.DAYS);

        NotificationToken updatedToken = setAndExpireToken(user, notificationToken.getToken(), switchOnUpdate);

        if (isTaxiDriver(user)) {
            if (updatedToken.isSwitchOnNotification()) {
                addToBroadcastNotification(updatedToken);
            }
            removeFromBroadcastNotification(notificationToken, expireAt);
        }
    }

    @Override
    public List<NotificationToken> getDriverTokens() {
        List<NotificationToken> notificationTokens = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (long epochDay = now.toEpochDay(); epochDay > now.minusDays(expirationTimeDays).toEpochDay(); epochDay--) {
            String key = DRIVER_TOKEN_LIST + LocalDate.ofEpochDay(epochDay).toString();
            if (redisTemplate.hasKey(key)) {
                notificationTokens.addAll(redisTemplate.opsForSet().members(key));
            } else {
                return notificationTokens;
            }
        }
        return notificationTokens;
    }

    private boolean isTaxiDriver(User user) {
        return user.getUserType().equals(User.UserType.TAXI_DRIVER);
    }

    private String constructNotificationTokenKey(User user) {
        return "notificationToken:" + user.getEmail();
    }
}
