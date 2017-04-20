package ua.kpi.mobiledev.repository.inmem;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.repository.UserRepository;

import javax.annotation.Resource;

import static java.util.Objects.isNull;

@Repository("userRepository")
public class InMemUserRepository implements UserRepository {

    @Resource
    private DBMock dbMock;

    @Override
    public User findByEmail(@Param("email") String email) {
        return dbMock.getUsers().stream().filter(user -> user.getEmail().equals(email)).findAny().get();
    }

    @Override
    public <S extends User> S save(S entity) {
        if (isNull(entity.getId())) {
            dbMock.addUser(entity);
        } else {
            dbMock.replace(entity);
        }
        return entity;
    }

    @Override
    public <S extends User> Iterable<S> save(Iterable<S> entities) {
        return null;
    }

    @Override
    public User findOne(Integer integer) {
        return dbMock.getUser(integer);
    }

    @Override
    public boolean exists(Integer integer) {
        return false;
    }

    @Override
    public Iterable<User> findAll() {
        return null;
    }

    @Override
    public Iterable<User> findAll(Iterable<Integer> integers) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(Integer integer) {

    }

    @Override
    public void delete(User entity) {

    }

    @Override
    public void delete(Iterable<? extends User> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
