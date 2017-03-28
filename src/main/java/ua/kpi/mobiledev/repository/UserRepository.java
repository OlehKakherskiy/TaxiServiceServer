package ua.kpi.mobiledev.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository<User, T extends Integer> extends CrudRepository<User, Integer> {

    @Query("Select u from User u where u.email=:email")
    User findByEmail(@Param("email") String email);
}
