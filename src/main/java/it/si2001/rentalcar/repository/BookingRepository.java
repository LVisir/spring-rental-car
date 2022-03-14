package it.si2001.rentalcar.repository;

import it.si2001.rentalcar.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByEnd(Date end);

    List<Booking> findByStart(Date start);

}
