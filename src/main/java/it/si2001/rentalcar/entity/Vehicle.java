package it.si2001.rentalcar.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "vehicles")
@JsonIgnoreProperties({"vehicleBookings"})
public class Vehicle implements Serializable {

    @Serial
    private static final long serialVersionUID = 1719267525239310690L;

    public enum Typology { SUV, MINIVAN, COMPACT }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVehicle;

    @Column(name = "license_plate", unique = true)
    @NotEmpty
    @Size(min = 7, max = 7, message = "The licence plate must have exactly seven characters")
    private String licensePlate;

    @Column(name = "manufacturer")
    @NotEmpty
    @Size(min = 5, max = 50, message = "The manufacturer must have at least five characters and at most 50")
    private String manufacturer;

    @Column(name = "model")
    @NotEmpty
    @Size(min = 5, max = 50, message = "The model must have at least five characters and at most 50")
    private String model;

    @Column(name = "registr_year")
    @Temporal(TemporalType.DATE)
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Madrid")
    private Date registrYear;

    @Column(name = "typology")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Typology typology;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "vehicle")
    @JsonManagedReference(value = "vehicle_bookings")
    @JsonProperty("vehicleBookings")
    @ToString.Exclude
    private List<Booking> bookings = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Vehicle vehicles = (Vehicle) o;
        return idVehicle != null && Objects.equals(idVehicle, vehicles.idVehicle);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
