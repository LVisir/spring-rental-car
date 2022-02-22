package it.si2001.rentalcar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.User;
import it.si2001.rentalcar.exception.ResourceAlreadyExistingException;
import it.si2001.rentalcar.exception.ResourceNotFoundException;
import it.si2001.rentalcar.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    private final ObjectMapper mapper = new ObjectMapper();

    private final ObjectNode responseNode = mapper.createObjectNode();





    @GetMapping(produces = "application/json")
    public ResponseEntity<List<User>> listAllUsers(){
        logger.info("***** Fetch users *****");

        List<User> users = userService.getAllUsers();

        if(users.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);   // null users
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }





    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> getUser(@PathVariable("id") Long id) {

        User u;

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON); // converts body into JSON type

        try{

            logger.info("***** Fetch the user with id "+id+" *****");

            u = userService.getUserById(id);

        }catch (ResourceNotFoundException e){

            logger.info("***** Fetch the user with id "+id+" not found *****");

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }catch (Exception e){

            return userService.manageExceptions(e, logger, responseNode, headers);

        }

        return new ResponseEntity<>(u, HttpStatus.OK);

    }





    @PostMapping(value = "/addUser")
    public ResponseEntity<ObjectNode> insertUser(@RequestBody User u){    // @RequestBody it takes a JSON format

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON); // converts body into JSON type

        try{

            logger.info("***** Insert user *****");

            userService.insertUser(u);

        }catch (ResourceAlreadyExistingException e){

            logger.info("***** "+e.getMessage()+" *****");

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }catch (Exception e){

            return userService.manageExceptions(e, logger, responseNode, headers);

        }

        return new ResponseEntity<>(HttpStatus.CREATED);

    }





    @DeleteMapping(value = "/deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id){

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON); // converts into JSON type

        User u;

        try{

            logger.info("***** Delete User *****");

            u = userService.getUserById(id);

        }catch (ResourceNotFoundException e){

            logger.warn("***** "+e.getMessage()+" *****");

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }catch (Exception e){

            return userService.manageExceptions(e, logger, responseNode, headers);

        }

        userService.deleteUser(u);

        return new ResponseEntity<>(HttpStatus.OK);
    }





    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User u, @PathVariable("id") Long id){

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON); // converts into JSON type

        try{

            logger.info("***** Modify user *****");

            userService.updateUser(u, id);

        }catch (ResourceNotFoundException e){

            logger.info("***** "+e.getMessage()+" *****");

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }catch (Exception e){

            return userService.manageExceptions(e, logger, responseNode, headers);

        }

        return new ResponseEntity<>(u, HttpStatus.OK);

    }

}
