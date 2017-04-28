package ua.kpi.mobiledev.repository;

import ua.kpi.mobiledev.web.security.model.ResetPasswordData;

import java.util.UUID;

public interface ResetPasswordRepository {

    void save(UUID resetPasswordDataId, ResetPasswordData resetPasswordData);

    void remove(UUID resetPasswordDataId);

    ResetPasswordData get(UUID resetPasswordDataId);
}
