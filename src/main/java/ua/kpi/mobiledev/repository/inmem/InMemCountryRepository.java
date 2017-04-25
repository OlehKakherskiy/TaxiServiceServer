package ua.kpi.mobiledev.repository.inmem;

import org.springframework.stereotype.Repository;
import ua.kpi.mobiledev.domain.Country;
import ua.kpi.mobiledev.repository.CountryRepository;

@Repository("countryRepository")
public class InMemCountryRepository implements CountryRepository{
    @Override
    public Country findByName(String name) {
        return null;
    }
}
