package ua.kpi.mobiledev.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ua.kpi.mobiledev.domain.Address;
import ua.kpi.mobiledev.facade.AddressFacade;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:appContext.xml", "classpath:repoContext.xml"})
@DirtiesContext
@ActiveProfiles("local")
public class AddressServiceIntegrationTest {

    @Resource
    private AddressFacade addressFacade;

    @Test
    @Ignore
    public void addAddress() {
        Address address = addressFacade.createAndGet(50.4475113,30.4527215);
        System.out.println(address);
    }
}