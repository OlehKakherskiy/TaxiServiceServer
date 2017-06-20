package ua.kpi.mobiledev.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ua.kpi.mobiledev.domain.User;

import javax.annotation.Resource;
import java.util.Locale;

@Service("resetPasswordMailService")
public class ResetPasswordMailServiceImpl implements ResetPasswordMailService {

    private static final Logger LOGGER = Logger.getLogger(ResetPasswordMailServiceImpl.class);

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

    @Async
    @Override
    public void sendResetPasswordEmail(User sendTo, String privateCode) {
        try {
            Locale locale = LocaleContextHolder.getLocale();
            Double expirationTime = resetCodeExpirationTime / (double) SECONDS;
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(sendTo.getEmail());
            simpleMailMessage.setFrom(sendFrom);
            simpleMailMessage.setSubject(getSubject(locale));
            simpleMailMessage.setText(getBody(sendTo, privateCode, expirationTime, locale));
            mailSender.send(simpleMailMessage);
        } catch (Exception e) {
            LOGGER.error("Error during sending reset password email occurred.", e);
        }
    }

    private String getSubject(Locale locale) {
        return localizedEmailMessageResource.getMessage("email.subject", new Object[0], DEFAULT_SUBJECT, locale);
    }

    private String getBody(User sendTo, String privateCode, Double expirationTime, Locale locale) {
        return localizedEmailMessageResource.getMessage("email.body", new Object[]{sendTo.getName(), privateCode, expirationTime},
                "YOUR CODE: " + privateCode, locale);
    }
}
