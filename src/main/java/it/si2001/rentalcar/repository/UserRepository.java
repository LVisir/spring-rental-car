package it.si2001.rentalcar.repository;

import it.si2001.rentalcar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByIdUser(Long id); // the method must be called as 'findBy ...()'

    User findByEmail(String email);

    User findByEmailAndCf(String email, String cf);

}
