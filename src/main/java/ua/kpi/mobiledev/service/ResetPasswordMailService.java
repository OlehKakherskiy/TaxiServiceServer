package ua.kpi.mobiledev.service;

import ua.kpi.mobiledev.domain.User;

public interface ResetPasswordMailService {
    void sendResetPasswordEmail(User sendTo, String privateCode);
}
