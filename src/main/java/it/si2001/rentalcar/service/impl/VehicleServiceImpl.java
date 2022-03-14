package it.si2001.rentalcar.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.Booking;
import it.si2001.rentalcar.entity.Vehicle;
import it.si2001.rentalcar.exception.ResourceAlreadyExistingException;
import it.si2001.rentalcar.exception.ResourceNotFoundException;
import it.si2001.rentalcar.repository.BookingRepository;
import it.si2001.rentalcar.repository.VehicleRepository;
import it.si2001.rentalcar.service.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    BookingRepository bookingRepository;

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





    /**
     *
     * @param id: id of a Vehicle
     * @return the last Booking date of a Vehicle given in input
     */
    @Override
    public Date getFirstAvailableBookingDay(Long id) {

        log.info("Check if the Vehicle with id {} exista", id);

        Optional<Vehicle> vehicle = vehicleRepository.findByIdVehicle(id);

        if(vehicle.isPresent()){

            log.info("Getting the last Booking for the Vehicle with id {}", id);

            Optional<Booking> lastDateBookings = bookingRepository.findAll()
                    .stream()
                    .filter(x -> x.getVehicle().getIdVehicle().equals(id))
                    .max(Comparator.comparing(Booking::getEnd));

            if(lastDateBookings.isPresent()){

                // checking if the date of the booking is lower than today
                if(lastDateBookings.get().getEnd().compareTo(new Date()) < 0){
                    return new Date();
                }
                else{
                    return lastDateBookings.get().getEnd();
                }

            }

            else{
                return new Date();
            }

        }

        throw new ResourceNotFoundException("Vehicle", "id", id);
    }





    @Override
    public List<Vehicle> search(String field, String value) throws ParseException {

        List<Vehicle> results = new ArrayList<>();

        Optional<Vehicle> v;

        switch(field){

            case "idVehicle":

                log.info("Try to search by id");

                v = vehicleRepository.findByIdVehicle(Long.parseLong(value));

                if(v.isEmpty()){
                    throw new ResourceNotFoundException("Vehicle", "id", value);
                }

                results.add(v.get());

                return results;

            case "licensePlate":

                log.info("Try to search by license plate");

                v = vehicleRepository.findByLicensePlate(value);

                if(v.isPresent()){
                    results.add(v.get());
                    return results;
                }

                throw  new ResourceNotFoundException("Vehicle", "license plate", value);

            case "manufacturer":

                log.info("Try to search by manufacturer");

                results = vehicleRepository.findByManufacturer(value);

                if(results.size() != 0){
                    return results;
                }

                throw  new ResourceNotFoundException("Vehicles", "manufacturer", value);

            case "model":

                log.info("Try to search by model");

                results = vehicleRepository.findAll()
                        .stream()
                        .filter(x -> x.getModel().equals(value))
                        .collect(Collectors.toList());

                if(results.size() != 0){
                    return results;
                }

                throw  new ResourceNotFoundException("Vehicles", "model", value);

            case "registrYear":

                log.info("Try to search by registration year");

                Date d = new SimpleDateFormat("yyyy-MM-dd").parse(value);

                results = vehicleRepository.findAll()
                        .stream()
                        .filter(x -> x.getRegistrYear().equals(d))
                        .collect(Collectors.toList());

                if(results.size() != 0){
                    return results;
                }

                throw  new ResourceNotFoundException("Vehicles", "date", value);

            case "typology":

                log.info("Try to search by typology");

                results = vehicleRepository.findAll()
                        .stream()
                        .filter(x -> x.getTypology().equals(Vehicle.Typology.valueOf(value)))
                        .collect(Collectors.toList());

                if(results.size() != 0){
                    return results;
                }

                throw  new ResourceNotFoundException("Vehicles", "typology", value);

            default:

                return results;

        }

    }

}
