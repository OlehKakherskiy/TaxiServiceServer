package ua.kpi.mobiledev.repository.inmem;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.Address;
import ua.kpi.mobiledev.repository.AddressRepository;

import javax.annotation.Resource;

@Component("addressRepository")
public class InMemAddressRepository implements AddressRepository {

    @Resource(name = "dbMock")
    private DBMock dbMock;

    @Override
    public Address customGet(String streetName, String houseNumber) {
        return null;
    }

    @Override
    public Address getByCoordinates(@Param("latitude") double latitude, @Param("longtitude") double longtitude) {
        return null;
    }

    @Override
    public <S extends Address> S save(S entity) {
        return entity;
    }

    @Override
    public <S extends Address> Iterable<S> save(Iterable<S> entities) {
        return null;
    }

    @Override
    public Address findOne(Long aLong) {
        return null;
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    public Iterable<Address> findAll() {
        return null;
    }

    @Override
    public Iterable<Address> findAll(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public void delete(Address entity) {

    }

    @Override
    public void delete(Iterable<? extends Address> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
