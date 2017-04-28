package ua.kpi.mobiledev.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ua.kpi.mobiledev.domain.User;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:appContext.xml", "classpath:repositoryMockContext.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class ResetPasswordMailServiceImplTest {


    @Resource(name = "resetPasswordMailService")
    private ResetPasswordMailServiceImpl resetPasswordMailService;

    @Test
    @Ignore
    public void testSendEmail() {
        User user = new User();
        user.setEmail("olehkakherskiy@gmail.com");
        user.setName("Oleh");

        resetPasswordMailService.sendResetPasswordEmail(user,"private_code");
    }
}