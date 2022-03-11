package it.si2001.rentalcar.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.Vehicle;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

public interface VehicleService {

    Vehicle insertVehicle(Vehicle v);

    void deleteVehicle(Long id);

    Vehicle updateVehicle(Vehicle v, Long id);

    Vehicle fetchVehicle(Long id);

    List<Vehicle> fetchVehicles();

    ResponseEntity<ObjectNode> manageExceptions(Exception e, Logger logger, ObjectNode responseNode);

    Date getFirstAvailableBookingDay(Long id);

}
