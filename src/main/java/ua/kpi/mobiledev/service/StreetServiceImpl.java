package ua.kpi.mobiledev.service;

import lombok.Setter;
import org.springframework.stereotype.Service;
import ua.kpi.mobiledev.domain.Street;
import ua.kpi.mobiledev.repository.StreetRepository;

import javax.annotation.Resource;

@Service("streetService")
@Setter
public class StreetServiceImpl implements StreetService {

    @Resource(name = "streetRepository")
    private StreetRepository streetRepository;

    @Override
    public Street getStreet(String streetName, String cityName) {
        return streetRepository.customGet(streetName, cityName);
    }
}
