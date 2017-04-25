package ua.kpi.mobiledev.service;

import org.springframework.stereotype.Service;
import ua.kpi.mobiledev.domain.Country;
import ua.kpi.mobiledev.repository.CountryRepository;

import javax.annotation.Resource;
import java.util.Optional;

@Service("countryService")
public class CountryServiceImpl implements CountryService {

    @Resource(name = "countryRepository")
    private CountryRepository countryRepository;

    @Override
    public Optional<Country> getCountry(String countryName) {
        return Optional.ofNullable(countryRepository.findByName(countryName));
    }
}
