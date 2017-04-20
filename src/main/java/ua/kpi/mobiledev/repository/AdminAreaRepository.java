package ua.kpi.mobiledev.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ua.kpi.mobiledev.domain.AdministrationArea;

public interface AdminAreaRepository extends Repository<AdministrationArea, Integer> {

    @Query("Select area From AdministrationArea area where area.name = :areaName")
    AdministrationArea getByName(@Param("areaName") String areaName);
}
