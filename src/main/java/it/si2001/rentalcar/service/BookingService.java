package it.si2001.rentalcar.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.Booking;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BookingService {

    List<Booking> getAllBookings();

    Booking getBooking(Long id);

    void checkInsertUpdateConstraint(Booking b);

    Booking insertBooking(Booking b);

    void deleteBooking(Long id);

    Booking updateBooking(Booking b, Long id);

    ResponseEntity<ObjectNode> manageExceptions(Exception e, Logger logger, ObjectNode responseNode);
}
