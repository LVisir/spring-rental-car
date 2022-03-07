package it.si2001.rentalcar.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.Hibernate;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "bookings")
public class Booking implements Serializable {

    @Serial
    private static final long serialVersionUID = 3123560406315339165L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBooking;

    @Column(name = "start")
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date start;

    @Column(name = "end")
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date end;

    @Column(name = "approval")
    @NotNull
    private boolean approval;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id")
    @JsonBackReference(value = "vehicle_bookings")
    @ToString.Exclude
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ToString.Exclude
    @JsonBackReference(value = "user_bookings")
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Booking bookings = (Booking) o;
        return idBooking != null && Objects.equals(idBooking, bookings.idBooking);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
