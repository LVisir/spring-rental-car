package it.si2001.rentalcar.controller;

import it.si2001.rentalcar.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthcheck")
public class StatusController {

    private static final Logger logger = LoggerFactory.getLogger(StatusController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<String> status() {
        try {
            userService.testConnection();
        }catch (Exception e) {
            logger.error("*** Error during /healthcheck: {} ***", e.getMessage());
            return new ResponseEntity<>("KO", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

}
