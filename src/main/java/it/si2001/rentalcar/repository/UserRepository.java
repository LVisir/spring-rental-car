package it.si2001.rentalcar.repository;

import it.si2001.rentalcar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdUser(Long id); // the method must be called as 'findBy ...()'

    List<User> findByRole(User.Role role);

    List<User> findByName(String name);

    List<User> findBySurname(String surname);

    List<User> findByBirthDate(Date birthDate);

    Optional<User> findByEmail(String email);

    Optional<User> findByCf(String cf);

}
