package ua.kpi.mobiledev.repository;

import ua.kpi.mobiledev.domain.User;

public interface NotificationTokenRepository {
    void saveNotificationToken(User user, String notificationToken);
    String getNotificationToken(User user);
    void removeNotificationToken(User user);
}
