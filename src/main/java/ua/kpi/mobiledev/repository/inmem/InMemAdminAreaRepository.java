package ua.kpi.mobiledev.repository.inmem;

import org.springframework.stereotype.Repository;
import ua.kpi.mobiledev.domain.AdministrationArea;
import ua.kpi.mobiledev.repository.AdminAreaRepository;

@Repository("adminAreaRepository")
public class InMemAdminAreaRepository implements AdminAreaRepository {
    @Override
    public AdministrationArea getByName(String areaName) {
        return null;
    }
}
