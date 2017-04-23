package ua.kpi.mobiledev.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.kpi.mobiledev.domain.Address;

@Repository
public interface AddressRepository extends CrudRepository<Address, Long> {

    @Query("Select addr from Address addr where addr.houseNumber = :houseNumber and addr.street.streetName = :streetName")
    Address customGet(@Param("streetName") String streetName, @Param("houseNumber") String houseNumber);
}
