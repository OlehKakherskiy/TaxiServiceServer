package ua.kpi.mobiledev.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.kpi.mobiledev.domain.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    @Query("Select u from User u where u.email=:email")
    User findByEmail(@Param("email") String email);
}
