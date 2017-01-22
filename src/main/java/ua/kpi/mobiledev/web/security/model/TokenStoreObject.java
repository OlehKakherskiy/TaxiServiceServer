package ua.kpi.mobiledev.web.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.security.Key;
import java.util.Date;

@Data
@AllArgsConstructor
public class TokenStoreObject implements Serializable {

    private boolean isValid;

    private Date expiredIn;

    private Key tokenDigestKey;
}
