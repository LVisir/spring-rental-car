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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

            logger.info(u.getIdUser()+" "+u.getName()+" "+u.getSurname()+" "+u.getCf()+" "+u.getRole()+" "+u.getPassword()+" "+u.getEmail()+" "+u.getBirthDate());

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





    @GetMapping(value = "/customers/paging/sortBy", produces = "application/json")
    public ResponseEntity<?> getSortedListByOrderTypesAtSomePage(@RequestParam("_page") int page,
                                                                 @RequestParam("_limit") int pageSize,
                                                                 @RequestParam("_sort") List<String> fields,
                                                                 @RequestParam("_order") List<String> order){

        // the page get from input minus 1 because it is the offset so the page start from 0 and not from 1 differently from the front-end when you are requesting the exact page
        Page<User> usersSorted = userService.getPagingUsersMultipleSortOrder(--page, pageSize, order, fields);

        return new ResponseEntity<>(usersSorted.getContent(), HttpStatus.OK);

    }



    @GetMapping(value = "/customers", produces = "application/json")
    public ResponseEntity<?> getCustomers(){

        logger.info("***** Fetch all the Customers *****");

        return new ResponseEntity<>(userService.findAllCustomer(), HttpStatus.OK);

    }




    @GetMapping(value = "/customers/paging", produces = "application/json")
    public ResponseEntity<?> getCustomersAtPageX(@RequestParam("_page") int page, @RequestParam("_limit") int pageSize){

        logger.info("***** Fetch "+pageSize+" Customers from page "+page+" *****");

        // the page get from input minus 1 because it is the offset so the page start from 0 and not from 1 differently from the front-end when you are requesting the exact page
        return new ResponseEntity<>(userService.findAllCustomersWithPaging(--page, pageSize), HttpStatus.OK);

    }




    @GetMapping(value = "/customers/search", produces = "application/json")
    public ResponseEntity<?> getCustomerSearch(@RequestParam("field") String field, @RequestParam("value") String value){

        logger.info("***** Fetch Customers that have "+field+" = "+value+" *****");

        switch (field){
            case "id":

                User user;

                try{

                    logger.info("***** Fetch the user with id "+value+" *****");

                    user = userService.getUserById(Long.parseLong(value));

                }catch (ResourceNotFoundException e){

                    logger.info("***** Fetch the user with id "+value+" not found *****");

                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);

                }

                if(user == null){

                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);

                }

                return new ResponseEntity<>(user, HttpStatus.OK);

            case "name":

                return new ResponseEntity<>(userService.findAllCustomersByName(value), HttpStatus.OK);

            case "surname":

                return new ResponseEntity<>(userService.findAllCustomersBySurname(value), HttpStatus.OK);

            case "cf":

                return new ResponseEntity<>(userService.findCustomerByCf(value), HttpStatus.OK);

            case "email":

                return new ResponseEntity<>(userService.findCustomerByEmail(value), HttpStatus.OK);

            case "birthDate":

                List<User> customersByBirthDate;

                try {

                    customersByBirthDate = userService.findAllCustomersByBirthDate(new SimpleDateFormat("yyyy-MM-dd").parse(value));

                }catch (ParseException e){

                    logger.info("***** Date format not valid *****");

                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);

                }

                return new ResponseEntity<>(customersByBirthDate, HttpStatus.OK);

            default:

                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }

    }

}
