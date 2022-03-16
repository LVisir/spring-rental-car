package it.si2001.rentalcar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.Booking;
import it.si2001.rentalcar.exception.CustomException;
import it.si2001.rentalcar.exception.ResourceAlreadyExistingException;
import it.si2001.rentalcar.exception.ResourceNotFoundException;
import it.si2001.rentalcar.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookings")
@Slf4j
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

                responseNode.put("error", "No Bookings found");

                return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

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
    public ResponseEntity<?> insertBooking(@Valid @RequestBody Booking b){

        try{

            logger.info("***** Try to insert Booking *****");

            bookingService.insertBooking(b);

            return new ResponseEntity<>(b, HttpStatus.CREATED);

        }
        catch (ResourceAlreadyExistingException | ResourceNotFoundException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

        }
        catch (CustomException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e){

            return bookingService.manageExceptions(e, logger, responseNode);

        }

    }





    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateBooking(@Valid @RequestBody Booking b, @PathVariable("id") Long id){

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





    @GetMapping(value = "/customers/{id}", produces = "application/json")
    public ResponseEntity<?> getBookingsOfUser(@PathVariable("id") Long id){

        try{

            logger.info("***** Try to fetch the Bookings of the User with id "+id);

            List<Booking> bookings = bookingService.getAllBookingsFromUser(id);

            if(bookings == null){

                responseNode.put("error", "No Bookings found");

                return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

            }

            return new ResponseEntity<>(bookings, HttpStatus.OK);

        }catch (ResourceNotFoundException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

        }
        catch (Exception e){

            return bookingService.manageExceptions(e, logger, responseNode);

        }

    }





    @GetMapping(value = "/search", produces = "application/json")
    public ResponseEntity<?> searchBy(@RequestParam("field") String field, @RequestParam("value") String value){

        try{

            log.info("***** Try to search by {} with value {}", field, value);

            List<Booking> bookings = bookingService.search(field, value);

            if(bookings.isEmpty()){

                log.error("No Booking/s found");

                responseNode.put("error", "No Booking/s found");

                return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

            }

            log.info("Booking/s found");

            return new ResponseEntity<>(bookings, HttpStatus.OK);

        } catch (ParseException e) {

            log.error("Error: {}", e.getMessage());

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.BAD_REQUEST);

        }catch (ResourceNotFoundException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

        }
        catch (Exception e){

            return bookingService.manageExceptions(e, logger, responseNode);

        }

    }





    @GetMapping(value = "/customers/{id}/search", produces = "application/json")
    public ResponseEntity<?> customerSearchBy(@RequestParam("field") String field, @RequestParam("value") String value, @PathVariable("id") Long id){

        try{

            log.info("***** Customer with id {} tried to search by {} with value {}",id, field, value);

            List<Booking> bookings = bookingService.search(field, value)
                    .stream()
                    .filter(x -> x.getUser().getIdUser().equals(id))
                    .collect(Collectors.toList());

            if(bookings.isEmpty()){

                log.error("No Booking/s found");

                responseNode.put("error", "No Booking/s found");

                return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

            }

            return new ResponseEntity<>(bookings, HttpStatus.OK);

        } catch (ParseException e) {

            log.error("Error: {}", e.getMessage());

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.BAD_REQUEST);

        }catch (ResourceNotFoundException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

        }
        catch (Exception e){

            return bookingService.manageExceptions(e, logger, responseNode);

        }

    }

}
