package it.si2001.rentalcar;

import it.si2001.rentalcar.encoder.PlainPasswordEncoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
public class RentalCarApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentalCarApplication.class, args);
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();

    }

    @Bean
    PlainPasswordEncoder plainPasswordEncoder() {

        return new PlainPasswordEncoder();

    }

}
