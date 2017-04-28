package ua.kpi.mobiledev.service;

import lombok.Setter;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.exception.RequestException;
import ua.kpi.mobiledev.repository.ResetPasswordRepository;
import ua.kpi.mobiledev.web.dto.ResetPasswordDto;
import ua.kpi.mobiledev.web.security.model.ResetPasswordData;
import ua.kpi.mobiledev.web.security.service.CustomerUserDetailsCrudService;

import javax.annotation.Resource;
import java.util.Base64;
import java.util.UUID;

import static java.util.Optional.ofNullable;
import static ua.kpi.mobiledev.exception.ErrorCode.SECURE_CODE_IS_WRONG;
import static ua.kpi.mobiledev.exception.ErrorCode.WRONG_RESET_PASSWORD_REQUEST_ID;

@Component("resetPasswordService")
@Setter
public class ResetPasswordServiceImpl implements ResetPasswordService {

    private static final Integer DEFAULT_CODE_DIGIT_COUNT = 10;

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "resetPasswordMailService")
    private ResetPasswordMailService resetPasswordMailService;

    @Resource(name = "resetPasswordRepository")
    private ResetPasswordRepository resetPasswordRepository;

    @Resource(name = "securityDetailsRepository")
    private CustomerUserDetailsCrudService securityDetailsRepository;

    @Value("${security.resetCodeDigitCount}")
    private Integer codeDigitCount;

    @Override
    public String resetPassword(String email) {
        User user = userService.getByUsername(email);
        String code = generateCode();
        resetPasswordMailService.sendResetPasswordEmail(user, code);
        UUID key = UUID.randomUUID();
        resetPasswordRepository.save(key, new ResetPasswordData(email, code));
        return encodeBase64(key);
    }

    protected String generateCode() {
        return RandomStringUtils.randomAlphanumeric(ofNullable(codeDigitCount).orElse(DEFAULT_CODE_DIGIT_COUNT));
    }

    protected String encodeBase64(UUID key) {
        return Base64.getUrlEncoder().encodeToString(key.toString().getBytes());
    }

    protected UUID decodeBase64(String encodedUuid) {
        return UUID.fromString(new String(Base64.getUrlDecoder().decode(encodedUuid)));
    }

    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto, String key) {
        UUID id = decodeBase64(key);
        ResetPasswordData passwordData = ofNullable(resetPasswordRepository.get(id))
                .orElseThrow(() -> new RequestException(WRONG_RESET_PASSWORD_REQUEST_ID));
        if (passwordData.getCode().equals(resetPasswordDto.getCode().trim())) {
            securityDetailsRepository.updatePassword(passwordData.getEmail(), resetPasswordDto.getPassword());
        } else {
            throw new RequestException(SECURE_CODE_IS_WRONG);
        }
        resetPasswordRepository.remove(id);
    }
}
