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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@RestController
@CrossOrigin
@RequestMapping("/users")
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





    @GetMapping(value = "/customers/id/{id}", produces = "application/json")
    public ResponseEntity<?> getCustomer(@PathVariable("id") Long id){

        User u = userService.getUserById(id);

        if(u != null){
            if(u.getRole().equals(User.Role.CUSTOMER)){
                return new ResponseEntity<>(u, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }





    @GetMapping(value = "/customers/email/{email}", produces = "application/json")
    public ResponseEntity<?> getCustomerByEmail(@PathVariable("email") String email){

        logger.info("***** Fetch User with email "+email+" *****");

        User u = userService.findCustomerByEmail(email);

        if(u != null){

            return new ResponseEntity<>(u, HttpStatus.OK);

        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping(value = "/email/{email}", produces = "application/json")
    public ResponseEntity<?> getUserByEmail(@PathVariable("email") String email){

        User u = null;

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON); // converts into JSON type

        try{

            logger.info("***** Fetch User with email "+email+" *****");

            u = userService.getUserByEmail(email);

        }catch (ResourceNotFoundException e){

            logger.info("***** "+e.getMessage()+" *****");

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }catch (Exception e){

            userService.manageExceptions(e, logger, responseNode, headers);

        }

        return new ResponseEntity<>(u, HttpStatus.OK);

    }




    @GetMapping(value = "/customers/paging", produces = "application/json")
    public ResponseEntity<?> getCustomersAtPageX(@RequestParam("_page") int page, @RequestParam("_limit") int pageSize){

        logger.info("***** Fetch "+pageSize+" Customers from page "+page+" *****");

        // the page get from input minus 1 because it is the offset so the page start from 0 and not from 1 differently from the front-end when you are requesting the exact page
        return new ResponseEntity<>(userService.findAllCustomersWithPaging(--page, pageSize), HttpStatus.OK);

    }





    @GetMapping(value = "/customers/search", produces = "application/json")
    public ResponseEntity<?> searchWithPaging(@RequestParam("field") String field, @RequestParam("value") String value, @RequestParam("_page") int page, @RequestParam("_limit") int pageSize){

        List<User> results = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON); // converts into JSON type

        try{

            logger.info("***** Fetch Customers that have "+field+" = "+value+" *****");

            results = userService.searchInCustomers(field, value, --page, pageSize);

        }catch (ParseException e){

            logger.info("***** Date format not valid *****");

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }catch (ResourceNotFoundException e){

            logger.info("***** error : "+e.getMessage()+" *****");

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }
        catch (Exception e){

            userService.manageExceptions(e, logger, responseNode, headers);

        }

        return new ResponseEntity<>(results, HttpStatus.OK);

    }




    @GetMapping(value = "/customers/search/sort", produces = "application/json")
    public ResponseEntity<?> searchWithPagingAndSorting(@RequestParam("field") String field, @RequestParam("value") String value,
                                                        @RequestParam("_page") int page, @RequestParam("_limit") int pageSize,
                                                        @RequestParam("_sort") List<String> fields, @RequestParam("_order") List<String> order){

        List<User> results = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON); // converts into JSON type

        try{

            StringBuilder fieldsForLogger = new StringBuilder();
            StringBuilder orderForLogger = new StringBuilder();

            for(String x : fields) fieldsForLogger.append(" ").append(x);
            for(String x : order) orderForLogger.append(" ").append(x);

            logger.info("***** Fetch Customers that have "+field+" = "+value+" at page "+page+" with order fields+"+fieldsForLogger+" and order type"+orderForLogger+" *****");

            results = userService.searchInCustomersBySort(field, value, --page, pageSize, order, fields);

        }catch (ParseException e){

            logger.info("***** Date format not valid *****");

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }catch (ResourceNotFoundException e){

            logger.info("***** error : "+e.getMessage()+" *****");

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }
        catch (Exception e){

            userService.manageExceptions(e, logger, responseNode, headers);

        }

        return new ResponseEntity<>(results, HttpStatus.OK);

    }

}
