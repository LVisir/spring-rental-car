package it.si2001.rentalcar.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.Booking;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface BookingService {

    List<Booking> getAllBookings();

    List<Booking> getAllBookingsFromUser(Long id);

    List<Booking> getAllBookingsByEmail(String email);

    Booking getBooking(Long id);

    void checkInsertUpdateConstraint(Booking b);

    Booking insertBooking(Booking b);

    void deleteBooking(Long id);

    Booking updateBooking(Booking b, Long id);

    ResponseEntity<ObjectNode> manageExceptions(Exception e, Logger logger, ObjectNode responseNode);

    List<Booking> search(String field, String value) throws ParseException;

    Date getLastBooking(Long id);

}
