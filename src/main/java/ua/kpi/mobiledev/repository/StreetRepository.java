package ua.kpi.mobiledev.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ua.kpi.mobiledev.domain.Street;

@org.springframework.stereotype.Repository
public interface StreetRepository extends Repository<Street, Long> {

    @Query("Select street From Street street where street.streetName = :streetName AND street.district.city.name =:cityName")
    Street customGet(@Param("streetName") String streetName, @Param("cityName") String cityName);
}
