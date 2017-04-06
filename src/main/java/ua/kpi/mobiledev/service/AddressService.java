package ua.kpi.mobiledev.service;

import ua.kpi.mobiledev.domain.Address;
import ua.kpi.mobiledev.domain.Street;

public interface AddressService {
    Address getAddress(String streetName, String houseNumber);
    Address addAddress(Street street, String houseNumber);
}
