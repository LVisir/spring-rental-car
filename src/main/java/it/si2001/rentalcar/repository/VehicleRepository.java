package it.si2001.rentalcar.repository;

import it.si2001.rentalcar.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByIdVehicle(Long id);

    Optional<Vehicle> findByLicensePlate(String lp);

}
