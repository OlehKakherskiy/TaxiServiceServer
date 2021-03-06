package ua.kpi.mobiledev.service;

import ua.kpi.mobiledev.domain.Address;

import java.util.Optional;

public interface AddressService {
    Optional<Address> getAddress(String streetName, String houseNumber);
    Optional<Address> getAddress(Double latitude, Double longtitude);
    Address addAddress(Address address);
}
