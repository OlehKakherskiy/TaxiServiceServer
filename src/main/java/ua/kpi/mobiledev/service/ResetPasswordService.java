package ua.kpi.mobiledev.service;

import ua.kpi.mobiledev.web.dto.ResetPasswordDto;

public interface ResetPasswordService {
    String resetPassword(String email);

    void resetPassword(ResetPasswordDto resetPasswordDto, String key);
}
