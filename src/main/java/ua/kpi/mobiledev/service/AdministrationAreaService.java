package ua.kpi.mobiledev.service;

import ua.kpi.mobiledev.domain.AdministrationArea;

import java.util.Optional;

public interface AdministrationAreaService {
    Optional<AdministrationArea> getAdministrationArea(String areaName);
}
