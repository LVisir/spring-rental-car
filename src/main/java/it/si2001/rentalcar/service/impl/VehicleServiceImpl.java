package it.si2001.rentalcar.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.Vehicle;
import it.si2001.rentalcar.exception.ResourceAlreadyExistingException;
import it.si2001.rentalcar.exception.ResourceNotFoundException;
import it.si2001.rentalcar.repository.VehicleRepository;
import it.si2001.rentalcar.service.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    PrettyLogger prettyLogger;





    @Override
    public Vehicle insertVehicle(Vehicle v) {

        if(v.getIdVehicle() != null){

            log.info("Check if the Vehicle exists");

            Optional<Vehicle> existingVehicle = vehicleRepository.findByIdVehicle(v.getIdVehicle());

            if(existingVehicle.isPresent()){

                throw new ResourceAlreadyExistingException("Vehicle", "id", v.getIdVehicle());

            }

        }

        log.info("Check if the exists a Vehicle with same license plate");

        Optional<Vehicle> vechileFromLicensePlate = vehicleRepository.findByLicensePlate(v.getLicensePlate());

        if(vechileFromLicensePlate.isPresent()){

            throw new ResourceAlreadyExistingException("Vehicle", "license plate", v.getLicensePlate());

        }
        else{

            log.info("Save the Vehicle");

            vehicleRepository.saveAndFlush(v);

            return v;

        }

    }





    @Override
    public void deleteVehicle(Long id) {

        log.info("Check if the Vehicle exists");

        Optional<Vehicle> vehicle = vehicleRepository.findByIdVehicle(id);

        if(vehicle.isPresent()){

            log.info("Delete the Vehicle");

            vehicleRepository.delete(vehicle.get());

        }
        else{

            throw new ResourceNotFoundException("Vehicle", "id", id);

        }

    }





    @Override
    public Vehicle updateVehicle(Vehicle v, Long id) {

        log.info("Check if the Vehicle exists");

        Optional<Vehicle> vehicleToUpdate = vehicleRepository.findByIdVehicle(id);

        if(vehicleToUpdate.isPresent()){

            if(!vehicleToUpdate.get().getLicensePlate().equals(v.getLicensePlate())){

                log.info("Check if the exists a Vehicle with same license plate");

                Optional<Vehicle> vehicleFromLicense = vehicleRepository.findByLicensePlate(v.getLicensePlate());

                if(vehicleFromLicense.isPresent()){

                    throw new ResourceAlreadyExistingException("Vehicle", "license plate", v.getLicensePlate());

                }

            }

            vehicleToUpdate.get().setIdVehicle(v.getIdVehicle());
            vehicleToUpdate.get().setLicensePlate(v.getLicensePlate());
            vehicleToUpdate.get().setManufacturer(v.getManufacturer());
            vehicleToUpdate.get().setModel(v.getModel());
            vehicleToUpdate.get().setRegistrYear(v.getRegistrYear());
            vehicleToUpdate.get().setTypology(v.getTypology());

            log.info("Update Vehicle");

            vehicleRepository.saveAndFlush(vehicleToUpdate.get());

            return vehicleToUpdate.get();

        }

        throw new ResourceNotFoundException("Vehicle", "id", id);

    }





    @Override
    public Vehicle fetchVehicle(Long id) {

        log.info("Check if the Vehicle exists");

        Optional<Vehicle> vehicle = vehicleRepository.findByIdVehicle(id);

        if(vehicle.isPresent()){

            log.info("Return Vehicle");

            return vehicle.get();

        }

        throw new ResourceNotFoundException("Vehicle", "id", id);

    }





    @Override
    public List<Vehicle> fetchVehicles() {

        List<Vehicle> vehicles = vehicleRepository.findAll();

        if(vehicles.isEmpty()){

            return null;

        }

        log.info("Return Vehicles");

        return vehicles;

    }





    @Override
    public ResponseEntity<ObjectNode> manageExceptions(Exception e, Logger logger, ObjectNode responseNode) {

        return prettyLogger.prettyException(e, logger, responseNode);

    }

}
