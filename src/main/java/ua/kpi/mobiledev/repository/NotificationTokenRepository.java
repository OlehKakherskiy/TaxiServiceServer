package ua.kpi.mobiledev.repository;

import ua.kpi.mobiledev.domain.NotificationToken;
import ua.kpi.mobiledev.domain.User;

import java.util.List;

public interface NotificationTokenRepository {
    void saveNotificationToken(User user, String notificationToken);

    NotificationToken getNotificationToken(User user);

    List<NotificationToken> getDriverTokens();

    void toggleNotificationToken(User user, boolean switchOn);
}
