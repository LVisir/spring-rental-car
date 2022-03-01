package it.si2001.rentalcar.service;

import it.si2001.rentalcar.entity.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleService {

    Vehicle insertVehicle(Vehicle v);

    boolean deleteVehicle(Long id);

    Vehicle updateVehicle(Vehicle v, Long id);

    Vehicle fetchVehicle(Long id);

    List<Vehicle> fetchVehicles();

}
