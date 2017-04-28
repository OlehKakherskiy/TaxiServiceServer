package ua.kpi.mobiledev.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import ua.kpi.mobiledev.domain.User;

import javax.annotation.Resource;
import java.util.Locale;

@Service("resetPasswordMailService")
public class ResetPasswordMailServiceImpl implements ResetPasswordMailService {

    private static final String DEFAULT_SUBJECT = "RESET PASSWORD FOR TAXI SERVICE";
    private static final int SECONDS = 3600;

    @Resource(name = "mailSender")
    private MailSender mailSender;

    @Resource(name = "localizedEmailMessageResource")
    private ReloadableResourceBundleMessageSource localizedEmailMessageResource;

    @Value("${security.resetPasswordCodeAlive}")
    private Integer resetCodeExpirationTime;

    @Value("${email.sendFrom}")
    private String sendFrom;

    @Override
    public void sendResetPasswordEmail(User sendTo, String privateCode) {
        Locale locale = LocaleContextHolder.getLocale();
        Double expirationTime = resetCodeExpirationTime / (double) SECONDS;
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(sendTo.getEmail());
        simpleMailMessage.setFrom(sendFrom);
        simpleMailMessage.setSubject(getSubject(locale));
        simpleMailMessage.setText(getBody(sendTo, privateCode, expirationTime, locale));
        mailSender.send(simpleMailMessage);
    }

    private String getSubject(Locale locale) {
        return localizedEmailMessageResource.getMessage("email.subject", new Object[0], DEFAULT_SUBJECT, locale);
    }

    private String getBody(User sendTo, String privateCode, Double expirationTime, Locale locale) {
        return localizedEmailMessageResource.getMessage("email.body", new Object[]{sendTo.getName(), privateCode, expirationTime},
                "YOUR CODE: " + privateCode, locale);
    }
}
