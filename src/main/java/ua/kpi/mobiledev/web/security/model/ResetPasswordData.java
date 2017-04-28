package ua.kpi.mobiledev.web.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ResetPasswordData implements Serializable {
    private String email;
    private String code;
}
