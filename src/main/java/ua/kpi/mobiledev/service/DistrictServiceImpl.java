package ua.kpi.mobiledev.service;

import org.springframework.stereotype.Service;
import ua.kpi.mobiledev.domain.District;
import ua.kpi.mobiledev.repository.DistrictRepository;

import javax.annotation.Resource;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service("districtService")
public class DistrictServiceImpl implements DistrictService {

    @Resource(name = "districtRepository")
    private DistrictRepository districtRepository;

    @Override
    public Optional<District> get(String districtName, String cityName) {
        return ofNullable(districtRepository.getByNameAndCityName(districtName, cityName));
    }
}
