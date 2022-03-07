package it.si2001.rentalcar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.Booking;
import it.si2001.rentalcar.exception.CustomException;
import it.si2001.rentalcar.exception.ResourceAlreadyExistingException;
import it.si2001.rentalcar.exception.ResourceNotFoundException;
import it.si2001.rentalcar.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final ObjectMapper mapper = new ObjectMapper();

    // maps to JSON Object structures in JSON content
    private final ObjectNode responseNode = mapper.createObjectNode();





    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getBookings(){

        try{

            logger.info("***** Try to fetch all bookings *****");

            List<Booking> bookings = bookingService.getAllBookings();

            if (bookings == null) {

                logger.error("***** No Bookings found *****");

                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            }

            return new ResponseEntity<>(bookings, HttpStatus.OK);
        }
        catch (Exception e){

            return bookingService.manageExceptions(e, logger, responseNode);

        }

    }





    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> getBooking(@PathVariable("id") Long id){

        try{

            logger.info("***** Try fetch booking with id "+id+" *****");

            Booking booking = bookingService.getBooking(id);

            return new ResponseEntity<>(booking, HttpStatus.OK);

        }
        catch (ResourceNotFoundException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

        }
        catch (Exception e){

            return bookingService.manageExceptions(e, logger, responseNode);

        }

    }





    @PostMapping(value = "/add")
    public ResponseEntity<?> insertBooking(@RequestBody Booking b){

        try{

            logger.info("***** Try to insert Booking *****");

            bookingService.insertBooking(b);

            return new ResponseEntity<>(b, HttpStatus.CREATED);

        }
        catch (ResourceAlreadyExistingException e){

            logger.error("***** "+e.getMessage()+" *****");

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }
        catch (CustomException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.INTERNAL_SERVER_ERROR);

        }
        catch (ResourceNotFoundException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

        }
        catch (Exception e){

            return bookingService.manageExceptions(e, logger, responseNode);

        }

    }





    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable("id") Long id, @RequestBody Booking b){

        try{

            logger.info("***** Try to update Booking *****");

            Booking bookingUpdated = bookingService.updateBooking(b, id);

            return new ResponseEntity<>(bookingUpdated, HttpStatus.OK);

        }
        catch (ResourceNotFoundException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

        }
        catch (CustomException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.INTERNAL_SERVER_ERROR);

        }
        catch (Exception e){

            return bookingService.manageExceptions(e, logger, responseNode);

        }

    }





    @DeleteMapping(value = "/delete/{id}", produces = "application/json")
    public ResponseEntity<?> deleteBooking(@PathVariable("id") Long id){

        try{

            logger.info("***** Try to delete booking *****");

            bookingService.deleteBooking(id);

            return new ResponseEntity<>(HttpStatus.OK);

        }
        catch (ResourceNotFoundException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

        }
        catch (Exception e){

            return bookingService.manageExceptions(e, logger, responseNode);

        }

    }

}
