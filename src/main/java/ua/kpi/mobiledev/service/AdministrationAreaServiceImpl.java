package ua.kpi.mobiledev.service;

import org.springframework.stereotype.Service;
import ua.kpi.mobiledev.domain.AdministrationArea;
import ua.kpi.mobiledev.repository.AdminAreaRepository;

import javax.annotation.Resource;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service("adminAreaService")
public class AdministrationAreaServiceImpl implements AdministrationAreaService {

    @Resource(name = "adminAreaRepository")
    private AdminAreaRepository adminAreaRepository;

    @Override
    public Optional<AdministrationArea> getAdministrationArea(String areaName) {
        return ofNullable(adminAreaRepository.getByName(areaName));
    }
}
