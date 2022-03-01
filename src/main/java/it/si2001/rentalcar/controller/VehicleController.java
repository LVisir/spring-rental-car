package it.si2001.rentalcar.controller;

import it.si2001.rentalcar.entity.Vehicle;
import it.si2001.rentalcar.service.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/vehicles")
public class VehicleController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);

    @Autowired
    private VehicleService vehicleService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getAllVehicles(){

        try{

            logger.info("***** Fetch all vehicles *****");

            List<Vehicle> vehicles = vehicleService.fetchVehicles();

            if(vehicles == null){

                logger.error("***** No vehicles found *****");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No vehicles found");

            }

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(vehicles);

        }
        catch (Exception e) {

            logger.error("Users: Exception thrown: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);

        }

    }





    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> getVehicle(@PathVariable("id") Long id){

        try{

            logger.info("***** Get Vehicle with id "+id+" *****");

            Vehicle v = vehicleService.fetchVehicle(id);

            if(v == null){

                logger.error("***** No Vehicle found with id "+id+" *****");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Vehicle found with id "+id);

            }

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(v);

        }
        catch (Exception e) {

            logger.error("Users: Exception thrown: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);

        }

    }





    @PutMapping(value = "/update/{id}", produces = "application/json")
    public ResponseEntity<?> updateVehicle(@RequestBody Vehicle v, @PathVariable("id") Long id){

        try{

            logger.info("***** Update vehicle with id "+id+" *****");

            Vehicle vehicleUpdated = vehicleService.updateVehicle(v, id);

            if(vehicleUpdated == null){

                logger.error("***** The Vehicle with id "+id+" that you are trying to update doesn't exists");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The Vehicle with id "+id+" that you are trying to update doesn't exists");

            }

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(vehicleUpdated);

        }
        catch (Exception e) {

            logger.error("Users: Exception thrown: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);

        }

    }





    @DeleteMapping(value = "/delete/{id}", produces = "application/json")
    public ResponseEntity<?> deleteVehicle(@PathVariable("id") Long id){

        try{

            logger.info("***** Delete Vehicle with id "+id+" *****");

            if(vehicleService.deleteVehicle(id)){

                return new ResponseEntity<>(HttpStatus.OK);

            }

            logger.error("***** Vehicle with id "+id+" doesn't exists *****");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vehicle with id "+id+" doesn't exists");

        }
        catch (Exception e) {

            logger.error("Users: Exception thrown: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);

        }

    }





    @PostMapping(value = "/add", produces = "application/json")
    public ResponseEntity<?> addVehicle(@RequestBody Vehicle v){

        try{

            logger.info("***** Insert Vehicle *****");

            Vehicle vehicle = vehicleService.insertVehicle(v);

            if(vehicle == null){

                logger.error("***** Vehicle with license plate "+v.getLicensePlate()+" already existing");

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vehicle with license plate "+v.getLicensePlate()+" already existing");

            }

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(vehicle);

        }
        catch (Exception e) {

            logger.error("Users: Exception thrown: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);

        }

    }

}
