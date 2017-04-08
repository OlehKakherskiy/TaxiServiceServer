package ua.kpi.mobiledev.web.converter;

import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.MobileNumber;
import ua.kpi.mobiledev.domain.dto.MobileNumberDto;

@Component("mobileNumberConverter")
public class MobileNumberConverter implements CustomConverter<MobileNumber, MobileNumberDto> {

    @Override
    public void convert(MobileNumber source, MobileNumberDto target) {

    }

    @Override
    public void reverseConvert(MobileNumberDto source, MobileNumber target) {
        target.setMobileNumber(source.getMobileNumber());
    }
}
