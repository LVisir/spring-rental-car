package it.si2001.rentalcar.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
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
@Table(name = "users")
@JsonIgnoreProperties({"userBookings"})
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 7351511336161777099L;

    public enum Role { CUSTOMER, SUPERUSER }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "cf", unique = true)
    private String cf;

    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    // FetchType.LAZY works only if you use @JsonIgnoreProperties({"userBookings"}) because by ignoring the JSON properties Spring will listen the FetchType
    // If FetchType.EAGER is set, if @JsonIgnoreProperties({"userBookings"}) is set too, the JSON will not return the child entity but the select will be fetched EAGERLY
    // if u will not ignore the JSON properties, doesn't matter the FetchType, the select will follow the JSON properties
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    @ToString.Exclude
    @JsonManagedReference
    @JsonProperty("userBookings")
    private List<Booking> bookings = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User users = (User) o;
        return idUser != null && Objects.equals(idUser, users.idUser);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
