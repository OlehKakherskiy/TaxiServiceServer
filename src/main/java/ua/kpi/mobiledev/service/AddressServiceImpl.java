package ua.kpi.mobiledev.service;

import lombok.Setter;
import org.springframework.stereotype.Service;
import ua.kpi.mobiledev.domain.Address;
import ua.kpi.mobiledev.domain.Street;
import ua.kpi.mobiledev.repository.AddressRepository;

import javax.annotation.Resource;

@Service("addressService")
@Setter
public class AddressServiceImpl implements AddressService {

    @Resource(name = "addressRepository")
    private AddressRepository<Address, Long> addressRepository;

    @Override
    public Address getAddress(String streetName, String houseNumber) {
        return addressRepository.customGet(streetName, houseNumber);
    }

    @Override
    public Address addAddress(Street street, String houseNumber) {
        Address address = new Address(null, houseNumber, street);
        return addressRepository.save(address);
    }

}
