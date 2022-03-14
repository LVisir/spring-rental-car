package it.si2001.rentalcar.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.Booking;
import it.si2001.rentalcar.entity.User;
import it.si2001.rentalcar.entity.Vehicle;
import it.si2001.rentalcar.exception.CustomException;
import it.si2001.rentalcar.exception.ResourceAlreadyExistingException;
import it.si2001.rentalcar.exception.ResourceNotFoundException;
import it.si2001.rentalcar.repository.BookingRepository;
import it.si2001.rentalcar.repository.UserRepository;
import it.si2001.rentalcar.repository.VehicleRepository;
import it.si2001.rentalcar.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class BookingServiceImpl implements BookingService {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PrettyLogger prettyLogger;





    @Override
    public List<Booking> getAllBookings() {

        List<Booking> bookings = bookingRepository.findAll();

        if(bookings.isEmpty()){

            return null;

        }

        log.info("Return Bookings");

        return bookings;

    }





    @Override
    public List<Booking> getAllBookingsFromUser(Long id) {

        log.info(" Getting the Bookings of the User of id {}", id);

        List<Booking> bookings = bookingRepository.findAll()
                .stream()
                .filter(x -> x.getUser().getIdUser().equals(id))
                .collect(Collectors.toList());

        if(bookings.isEmpty()){
            return null;
        }

        return bookings;

    }


    @Override
    public Booking getBooking(Long id) {

        log.info(" Check if the Booking exists ");

        Optional<Booking> booking = bookingRepository.findById(id);

        if(booking.isPresent()){

            log.info(" Return Booking ");

            return booking.get();

        }
        else{

            throw new ResourceNotFoundException("Booking", "id", id);

        }

    }





    @Override
    public void checkInsertUpdateConstraint(Booking b) {

        log.info(" Checking if the Booking of the Vehicle and the Booking for the Customer exists ");

        Optional<Vehicle> vehicle = vehicleRepository.findByIdVehicle(b.getVehicle().getIdVehicle());

        Optional<User> user = userRepository.findByIdUser(b.getUser().getIdUser());

        if(vehicle.isPresent()){
            if(user.isPresent()){

                log.info(" Checking date constraint ");

                if(vehicle.get().getRegistrYear().compareTo(b.getStart()) >= 0){

                    throw new CustomException("The registration date of the vehicle is higher or equal to the start date of the booking");

                }
                else if(b.getStart().compareTo(b.getEnd()) >= 0){

                    throw new CustomException("The start date of the booking is higher or equal to the end date");

                }

                log.info(" Fetch all the Bookings of the User to check if he has not Booking under approval or Booking of the same period ");

                List<Booking> bookingsOfUser = bookingRepository.findAll()
                        .stream()
                        .filter(x -> x.getUser().getIdUser().equals(b.getUser().getIdUser()) && !x.getIdBooking().equals(b.getIdBooking()))
                        .collect(Collectors.toList());

                if(!bookingsOfUser.isEmpty()){

                    if(bookingsOfUser.stream().anyMatch(x -> !x.isApproval())){

                        throw new CustomException("The User with id "+b.getUser().getIdUser()+" has already a booking under approval");

                    }
                    else if(bookingsOfUser.stream()
                            .anyMatch(x ->
                                    (x.getStart().compareTo(b.getStart()) < 0 && x.getEnd().compareTo(b.getStart()) > 0) ||
                                            (x.getStart().compareTo(b.getEnd()) < 0 && x.getEnd().compareTo(b.getEnd()) > 0)))
                    {

                        throw new CustomException("Already existing a booking between "+b.getStart().toString()+" to "+b.getEnd().toString()+" of the Customer with id "+b.getUser().getIdUser());

                    }

                }

                log.info(" Fetch all the Bookings of the Vehicle to check if it has not Booking of the same period ");

                List<Booking> bookingsOfVehicle = bookingRepository.findAll()
                        .stream()
                        .filter(x -> x.getVehicle().getIdVehicle().equals(b.getVehicle().getIdVehicle()) && !x.getIdBooking().equals(b.getIdBooking()))
                        .collect(Collectors.toList());

                if(!bookingsOfVehicle.isEmpty()){

                    if(bookingsOfVehicle.stream()
                            .anyMatch(x ->
                                    (x.getStart().compareTo(b.getStart()) < 0 && x.getEnd().compareTo(b.getStart()) > 0) ||
                                            (x.getStart().compareTo(b.getEnd()) < 0 && x.getEnd().compareTo(b.getEnd()) > 0)))
                    {

                        throw new CustomException("Already existing a booking between "+b.getStart().toString()+" to "+b.getEnd().toString()+" of the Vehicle of the id "+b.getVehicle().getIdVehicle());

                    }

                }

            }
            else throw new ResourceNotFoundException("User", "id", b.getUser().getIdUser());

        }
        else throw new ResourceNotFoundException("Vehicle", "id", b.getVehicle().getIdVehicle());

    }





    @Override
    public Booking insertBooking(Booking b) {

        if(b.getIdBooking() != null){

            log.info(" Check if this Booking doesn't exists ");

            Optional<Booking> booking = bookingRepository.findById(b.getIdBooking());

            if(booking.isPresent()){

                throw new ResourceAlreadyExistingException("Booking", "id", b.getIdBooking());

            }

        }

        checkInsertUpdateConstraint(b);

        log.info(" Save the Booking ");

        bookingRepository.saveAndFlush(b);

        return b;
    }





    @Override
    public void deleteBooking(Long id) {

        log.info(" Check if the booking exists ");

        Optional<Booking> booking = bookingRepository.findById(id);

        if(booking.isPresent()){

            log.info(" Delete Booking ");

            bookingRepository.delete(booking.get());

        }
        else throw new ResourceNotFoundException("Booking", "id", id);

    }





    @Override
    public Booking updateBooking(Booking b, Long id) {

        log.info(" Check if the Booking exists ");

        Optional<Booking> booking = bookingRepository.findById(id);

        if(booking.isPresent()){

            booking.get().setIdBooking(b.getIdBooking());

            checkInsertUpdateConstraint(b);

            booking.get().setApproval(b.isApproval());
            booking.get().setStart(b.getStart());
            booking.get().setEnd(b.getEnd());

            log.info(" Update the Booking ");

            bookingRepository.saveAndFlush(booking.get());

            return booking.get();

        }

        throw new ResourceNotFoundException("Booking", "id", id);
    }





    @Override
    public ResponseEntity<ObjectNode> manageExceptions(Exception e, Logger logger, ObjectNode responseNode) {

        return prettyLogger.prettyException(e, logger, responseNode);

    }





    @Override
    public List<Booking> search(String field, String value) throws ParseException {

        Optional<Booking> b;

        List<Booking> bookings = new ArrayList<>();

        Date d;

        switch (field){

            case "idBooking":

                log.info("Try to get the Booking with id {}", value);

                b = bookingRepository.findById(Long.valueOf(value));

                if(b.isPresent()){

                    bookings.add(b.get());

                    return bookings;

                }

                throw new ResourceNotFoundException("Booking", "id", value);

            case "start":

                log.info("Try to get the Bookings with start date {}", value);

                d = new SimpleDateFormat("yyyy-MM-dd").parse(value);

                bookings = bookingRepository.findByStart(d);

                if(bookings.isEmpty()){

                    throw new ResourceNotFoundException("Bookings", "start date", value);

                }

                return bookings;

            case "end":

                log.info("Try to get the Bookings with end date {}", value);

                d = new SimpleDateFormat("yyyy-MM-dd").parse(value);

                bookings = bookingRepository.findByEnd(d);

                if(bookings.isEmpty()){

                    throw new ResourceNotFoundException("Bookings", "end date", value);

                }

                return bookings;

            case "vehicle":

                log.info("Try to get the Bookings for the Vehicle with id {}", value);

                bookings = bookingRepository.findAll()
                        .stream()
                        .filter(x -> x.getVehicle().getIdVehicle().equals(Long.valueOf(value)))
                        .collect(Collectors.toList());

                if(bookings.isEmpty()){

                    throw new ResourceNotFoundException("Bookings", "Vehicle id", value);

                }

                return bookings;

            case "user":

                log.info("Try to get the Bookings for the User with id {}", value);

                bookings = bookingRepository.findAll()
                        .stream()
                        .filter(x -> x.getUser().getIdUser().equals(Long.valueOf(value)))
                        .collect(Collectors.toList());

                if(bookings.isEmpty()){

                    throw new ResourceNotFoundException("Bookings", "User id", value);

                }

                return bookings;

            default:

                return bookings;

        }

    }
}
