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
import it.si2001.rentalcar.service.PrettyLogger;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
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

        return bookings;

    }





    @Override
    public Booking getBooking(Long id) {

        Optional<Booking> booking = bookingRepository.findById(id);

        if(booking.isPresent()){

            return booking.get();

        }
        else{

            throw new ResourceNotFoundException("Booking", "id", id);

        }

    }





    @Override
    public void checkInsertUpdateConstraint(Booking b) {

        Optional<Vehicle> vehicle = vehicleRepository.findByIdVehicle(b.getVehicle().getIdVehicle());

        Optional<User> user = userRepository.findByIdUser(b.getUser().getIdUser());

        if(vehicle.isPresent()){
            if(user.isPresent()){

                if(vehicle.get().getRegistrYear().compareTo(b.getStart()) >= 0){

                    throw new CustomException("The registration date of the vehicle is higher or equal to the start date of the booking");

                }
                else if(b.getStart().compareTo(b.getEnd()) >= 0){

                    throw new CustomException("The start date of the booking is higher or equal to the end date");

                }

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

            Optional<Booking> booking = bookingRepository.findById(b.getIdBooking());

            if(booking.isPresent()){

                throw new ResourceAlreadyExistingException("Booking", "id", b.getIdBooking());

            }

        }

        checkInsertUpdateConstraint(b);

        bookingRepository.saveAndFlush(b);

        return b;
    }





    @Override
    public void deleteBooking(Long id) {

        Optional<Booking> booking = bookingRepository.findById(id);

        if(booking.isPresent()){

            bookingRepository.delete(booking.get());

        }
        else throw new ResourceNotFoundException("Booking", "id", id);

    }





    @Override
    public Booking updateBooking(Booking b, Long id) {

        Optional<Booking> booking = bookingRepository.findById(id);

        if(booking.isPresent()){

            if(!b.getIdBooking().equals(id)){

                throw new CustomException("The id is not valid");

            }

            checkInsertUpdateConstraint(b);

            booking.get().setIdBooking(b.getIdBooking());
            booking.get().setApproval(b.isApproval());
            booking.get().setStart(b.getStart());
            booking.get().setEnd(b.getEnd());

            bookingRepository.saveAndFlush(booking.get());

            return booking.get();

        }

        throw new ResourceNotFoundException("Booking", "id", id);
    }





    @Override
    public ResponseEntity<ObjectNode> manageExceptions(Exception e, Logger logger, ObjectNode responseNode) {

        return prettyLogger.prettyException(e, logger, responseNode);

    }


}
