package ua.kpi.mobiledev.repository.inmem;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ua.kpi.mobiledev.domain.Street;
import ua.kpi.mobiledev.repository.StreetRepository;

@Repository("streetRepository")
public class InMemStreetRepository implements StreetRepository {

    @Query("Select street From Street street where street.streetName = :streetName AND street.district.city.name =:cityName")
    @Override
    public Street customGet(String streetName, String cityName) {
        return null;
    }
}
