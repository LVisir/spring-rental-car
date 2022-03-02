package it.si2001.rentalcar.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.Vehicle;
import it.si2001.rentalcar.exception.ResourceAlreadyExistingException;
import it.si2001.rentalcar.exception.ResourceNotFoundException;
import it.si2001.rentalcar.repository.VehicleRepository;
import it.si2001.rentalcar.service.VehicleService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    VehicleRepository vehicleRepository;





    @Override
    public Vehicle insertVehicle(Vehicle v) {

        if(v.getIdVehicle() != null){

            Optional<Vehicle> existingVehicle = vehicleRepository.findByIdVehicle(v.getIdVehicle());

            if(existingVehicle.isPresent()){

                throw new ResourceAlreadyExistingException("Vehicle", "id", v.getIdVehicle());

            }

        }

        Optional<Vehicle> vechileFromLicensePlate = vehicleRepository.findByLicensePlate(v.getLicensePlate());

        if(vechileFromLicensePlate.isPresent()){

            throw new ResourceAlreadyExistingException("Vehicle", "license plate", v.getLicensePlate());

        }
        else{

            vehicleRepository.saveAndFlush(v);

            return v;

        }

    }





    @Override
    public void deleteVehicle(Long id) {

        Optional<Vehicle> vehicle = vehicleRepository.findByIdVehicle(id);

        if(vehicle.isPresent()){

            vehicleRepository.delete(vehicle.get());

        }
        else{

            throw new ResourceNotFoundException("Vehicle", "id", id);

        }

    }





    @Override
    public Vehicle updateVehicle(Vehicle v, Long id) {

        Optional<Vehicle> vehicleToUpdate = vehicleRepository.findByIdVehicle(id);

        if(vehicleToUpdate.isPresent()){

            if(!vehicleToUpdate.get().getLicensePlate().equals(v.getLicensePlate())){

                Optional<Vehicle> vehicleFromLicense = vehicleRepository.findByLicensePlate(v.getLicensePlate());

                if(vehicleFromLicense.isPresent()){

                    throw new ResourceAlreadyExistingException("Vehicle", "licese plate", v.getLicensePlate());

                }

            }

            vehicleToUpdate.get().setIdVehicle(v.getIdVehicle());
            vehicleToUpdate.get().setLicensePlate(v.getLicensePlate());
            vehicleToUpdate.get().setManufacturer(v.getManufacturer());
            vehicleToUpdate.get().setModel(v.getModel());
            vehicleToUpdate.get().setRegistrYear(v.getRegistrYear());
            vehicleToUpdate.get().setTypology(v.getTypology());

            vehicleRepository.saveAndFlush(vehicleToUpdate.get());

            return vehicleToUpdate.get();

        }

        throw new ResourceNotFoundException("Vehicle", "id", id);

    }





    @Override
    public Vehicle fetchVehicle(Long id) {

        Optional<Vehicle> vehicle = vehicleRepository.findByIdVehicle(id);

        if(vehicle.isPresent()){

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

        return vehicles;

    }





    @Override
    public ResponseEntity<ObjectNode> manageExceptions(Exception e, Logger logger, ObjectNode responseNode) {

        if(e.getCause().getCause() instanceof SQLException){

            logger.error("***** "+e.getCause().getCause()+" *****");

            responseNode.put("error", "Server error");

            return new ResponseEntity<>(responseNode, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.error("***** "+e.getCause().getMessage()+" *****");

        responseNode.put("error", "Bad request");

        return new ResponseEntity<>(responseNode, HttpStatus.BAD_REQUEST);

    }

}
