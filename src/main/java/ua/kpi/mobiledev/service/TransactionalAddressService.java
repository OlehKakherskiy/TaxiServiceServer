package ua.kpi.mobiledev.service;

import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.mobiledev.domain.Address;
import ua.kpi.mobiledev.repository.AddressRepository;

import javax.annotation.Resource;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service("addressService")
@Setter
public class TransactionalAddressService implements AddressService {

    @Resource(name = "addressRepository")
    private AddressRepository addressRepository;

    @Override
    public Optional<Address> getAddress(String streetName, String houseNumber) {
        return ofNullable(addressRepository.customGet(streetName, houseNumber));
    }

    @Override
    public Optional<Address> getAddress(Double latitude, Double longtitude) {
        return ofNullable(addressRepository.getByCoordinates(latitude, longtitude));
    }

    @Transactional
    @Override
    public Address addAddress(Address address) {
        return addressRepository.save(address);
    }

}
