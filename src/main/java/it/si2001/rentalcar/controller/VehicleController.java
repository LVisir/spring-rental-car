package it.si2001.rentalcar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.Vehicle;
import it.si2001.rentalcar.exception.ResourceAlreadyExistingException;
import it.si2001.rentalcar.exception.ResourceNotFoundException;
import it.si2001.rentalcar.service.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
//@CrossOrigin
@RequestMapping("/vehicles")
public class VehicleController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);

    @Autowired
    private VehicleService vehicleService;

    private final ObjectMapper mapper = new ObjectMapper();

    // maps to JSON Object structures in JSON content
    private final ObjectNode responseNode = mapper.createObjectNode();





    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getAllVehicles(){

        try{

            logger.info("***** Try to fetch all vehicles *****");

            List<Vehicle> vehicles = vehicleService.fetchVehicles();

            if(vehicles == null){

                logger.error("***** No vehicles found *****");

                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            }

            return new ResponseEntity<>(vehicles, HttpStatus.OK);

        }
        catch (Exception e) {

            return vehicleService.manageExceptions(e, logger, responseNode);

        }

    }





    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> getVehicle(@PathVariable("id") Long id){

        try{

            logger.info("***** Try to get Vehicle with id "+id+" *****");

            Vehicle v = vehicleService.fetchVehicle(id);

            return ResponseEntity.ok().body(v);

        }
        catch (ResourceNotFoundException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

        }
        catch (Exception e) {

            return vehicleService.manageExceptions(e, logger, responseNode);

        }

    }





    @PutMapping(value = "/update/{id}", produces = "application/json")
    public ResponseEntity<?> updateVehicle(@Valid @RequestBody Vehicle v, @PathVariable("id") Long id){

        try{

            logger.info("***** Try to update Vehicle with id "+id+" *****");

            Vehicle vehicleUpdated = vehicleService.updateVehicle(v, id);

            return ResponseEntity.ok().body(vehicleUpdated);

        }
        catch (ResourceAlreadyExistingException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NO_CONTENT);

        }
        catch (ResourceNotFoundException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

        }
        catch (Exception e) {

            return vehicleService.manageExceptions(e, logger, responseNode);

        }

    }





    @DeleteMapping(value = "/delete/{id}", produces = "application/json")
    public ResponseEntity<?> deleteVehicle(@PathVariable("id") Long id){

        try{

            logger.info("***** Try to delete Vehicle with id "+id+" *****");

            vehicleService.deleteVehicle(id);

            return new ResponseEntity<>(HttpStatus.OK);

        }
        catch (ResourceNotFoundException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

        }
        catch (Exception e) {

            return vehicleService.manageExceptions(e, logger, responseNode);

        }

    }





    @PostMapping(value = "/add", produces = "application/json")
    public ResponseEntity<?> addVehicle(@Valid @RequestBody Vehicle v){

        try{

            logger.info("***** Try to insert Vehicle *****");

            Vehicle vehicle = vehicleService.insertVehicle(v);

            return ResponseEntity.ok().body(vehicle);

        }
        catch (ResourceAlreadyExistingException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NO_CONTENT);

        }
        catch (Exception e) {

            return vehicleService.manageExceptions(e, logger, responseNode);

        }

    }

}
