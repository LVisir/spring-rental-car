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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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





    @GetMapping(value = "/lastBooking/{id}", produces = "application/json")
    public ResponseEntity<?> getLastBookingDateOfVehicle(@PathVariable("id") Long id){

        try{

            logger.info("***** Try to get the last Booking Date of the Vehicle with id "+ id+ " *****");

            Date lastDate = vehicleService.getFirstAvailableBookingDay(id);

            Map<String, String> dates = new HashMap<>();

            //adding a day to the last date returned
            Date nextDay = new Date(lastDate.getTime() + (1000 * 60 * 60 * 24));

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            dates.put("startDate", df.format(nextDay));

            //adding a day to the next date calculated
            Date nextNextDay = new Date(nextDay.getTime() + (1000 * 60 * 60 * 24));

            dates.put("endDate", df.format(nextNextDay));

            return new ResponseEntity<>(dates, HttpStatus.OK);

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





    @GetMapping(value = "/search", produces = "application/json")
    public ResponseEntity<?> searchBy(@RequestParam("field") String field, @RequestParam("value") String value){

        try{

            logger.info("***** Try to search Vehicle/s by "+field+" with value "+value);

            List<Vehicle> result = vehicleService.search(field, value);

            if(result.isEmpty()){

                logger.error("No Vehicle/s found");

                responseNode.put("error", "No Vehicle/s found");

                return new ResponseEntity<>(responseNode, HttpStatus.NO_CONTENT);

            }

            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (ParseException e) {

            logger.error("Error: "+e.getMessage());

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.BAD_REQUEST);

        }catch (ResourceNotFoundException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

        }
        catch (Exception e) {

            return vehicleService.manageExceptions(e, logger, responseNode);

        }

    }

}
