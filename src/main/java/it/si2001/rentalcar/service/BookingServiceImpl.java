package it.si2001.rentalcar.service;

import it.si2001.rentalcar.entity.Booking;
import it.si2001.rentalcar.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class BookingServiceImpl implements BookingService{

    @Autowired
    BookingRepository bookingRepository;


    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
