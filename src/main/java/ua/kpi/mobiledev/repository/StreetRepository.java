package ua.kpi.mobiledev.repository;

import org.springframework.data.repository.Repository;
import ua.kpi.mobiledev.domain.Street;

public interface StreetRepository extends Repository<Street, Long> {
    Street customGet(String streetName, String cityName);
}
