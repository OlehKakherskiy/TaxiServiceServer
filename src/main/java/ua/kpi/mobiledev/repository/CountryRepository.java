package ua.kpi.mobiledev.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ua.kpi.mobiledev.domain.Country;

@org.springframework.stereotype.Repository
public interface CountryRepository extends Repository<Country, Integer> {

    @Query("Select c from Country c where c.countryName=:countryName")
    Country findByName(@Param("countryName") String name);
}
