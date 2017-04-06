package ua.kpi.mobiledev.repository;

import org.springframework.data.repository.CrudRepository;

public interface AddressRepository<Address, T extends Long> extends CrudRepository<Address, Long> {
    Address customGet(String streetName, String houseNumber);
}
