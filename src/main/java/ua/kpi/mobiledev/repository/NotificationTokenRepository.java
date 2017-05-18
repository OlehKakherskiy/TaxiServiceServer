package ua.kpi.mobiledev.repository;

import ua.kpi.mobiledev.domain.NotificationToken;
import ua.kpi.mobiledev.domain.User;

public interface NotificationTokenRepository {
    void saveNotificationToken(User user, String notificationToken);

    NotificationToken getNotificationToken(User user);

    void removeNotificationToken(User user);

    void toggleNotificationToken(User user, boolean switchOn);
}
