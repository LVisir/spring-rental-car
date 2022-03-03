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

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    private final ObjectMapper mapper = new ObjectMapper();

    // maps to JSON Object structures in JSON content
    private final ObjectNode responseNode = mapper.createObjectNode();





    @GetMapping(produces = "application/json")
    public ResponseEntity<?> listAllUsers(){

        try{

            logger.info("***** Fetch users *****");

            List<User> users = userService.getAllUsers();

            if(users == null){

                logger.error("***** No Users found *****");

                return new ResponseEntity<>(HttpStatus.NOT_FOUND);

            }

            return ResponseEntity.ok().body(users);

        }
        catch (Exception e) {

            return userService.manageExceptions(e, logger, responseNode);

        }

    }





    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> getUser(@PathVariable("id") Long id) {

        try{

            logger.info("***** Fetch the user with id "+id+" *****");

            User u = userService.getUserById(id);

            return ResponseEntity.ok().body(u);

        }
        catch (ResourceNotFoundException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

        }
        catch (Exception e) {

            return userService.manageExceptions(e, logger, responseNode);

        }

    }





    @PostMapping(value = "/addUser")
        public ResponseEntity<?> insertUser(@RequestBody User u){    // @RequestBody it takes a JSON format

        try{

            logger.info("***** Insert user *****");

            logger.info(u.getName()+" "+u.getSurname()+" "+u.getCf()+" "+u.getRole()+" "+u.getPassword()+" "+u.getEmail()+" "+u.getBirthDate());

            User userInserted = userService.insertUser(u);

            return new ResponseEntity<>(userInserted, HttpStatus.CREATED);

        }
        catch (ResourceAlreadyExistingException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NO_CONTENT);

        }
        catch (Exception e) {

            return userService.manageExceptions(e, logger, responseNode);

        }

    }




    @PostMapping(value = "/customers/add")
    public ResponseEntity<?> insertCustomer(@RequestBody User u){    // @RequestBody it takes a JSON format

        try{

            logger.info("***** Insert customer *****");

            logger.info(u.getName()+" "+u.getSurname()+" "+u.getCf()+" "+u.getRole()+" "+u.getPassword()+" "+u.getEmail()+" "+u.getBirthDate());

            User customer = userService.insertCustomer(u);

            return new ResponseEntity<>(customer, HttpStatus.CREATED);

        }
        catch (ResourceAlreadyExistingException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NO_CONTENT);

        }
        catch (Exception e) {

            return userService.manageExceptions(e, logger, responseNode);

        }

    }





    @DeleteMapping(value = "/deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id){

        try{

            logger.info("***** Delete User *****");

            userService.deleteUser(id);

            return new ResponseEntity<>(HttpStatus.OK);

        }
        catch (ResourceNotFoundException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

        }
        catch (Exception e) {

            return userService.manageExceptions(e, logger, responseNode);

        }

    }




    @DeleteMapping(value = "/customers/delete/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable("id") Long id){

        try{

            logger.info("***** Delete Customer *****");

            userService.deleteCustomer(id);

            return new ResponseEntity<>(HttpStatus.OK);

        }
        catch (ResourceNotFoundException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

        }
        catch (Exception e) {

            return userService.manageExceptions(e, logger, responseNode);

        }

    }





    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User u, @PathVariable("id") Long id){

        try{

            logger.info("***** Update User with id "+id+" *****");

            User userUpdated = userService.updateUser(u, id);

            return new ResponseEntity<>(userUpdated, HttpStatus.OK);

        }
        catch (ResourceNotFoundException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

        }
        catch (ResourceAlreadyExistingException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NO_CONTENT);

        }
        catch (Exception e) {

            return userService.manageExceptions(e, logger, responseNode);

        }

    }




    @PutMapping(value = "/customers/update/{id}")
    public ResponseEntity<?> updateCustomer(@RequestBody User u, @PathVariable("id") Long id){

        try{

            logger.info("***** Modify customer *****");

            User customerUpdated = userService.updateCustomer(u, id);

            return new ResponseEntity<>(customerUpdated, HttpStatus.OK);

        }
        catch (ResourceNotFoundException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

        }
        catch (ResourceAlreadyExistingException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NO_CONTENT);

        }
        catch (Exception e) {

            return userService.manageExceptions(e, logger, responseNode);

        }

    }





    @GetMapping(value = "/customers/paging/sortBy", produces = "application/json")
    public ResponseEntity<?> getSortedListByOrderTypesAtSomePage(@RequestParam("_page") int page,
                                                                 @RequestParam("_limit") int pageSize,
                                                                 @RequestParam("_sort") List<String> fields,
                                                                 @RequestParam("_order") List<String> order){


        StringBuilder fieldsForLogger = new StringBuilder();
        StringBuilder orderForLogger = new StringBuilder();

        for(String x : fields) fieldsForLogger.append(" ").append(x);
        for(String x : order) orderForLogger.append(" ").append(x);

        logger.info("***** Fetch Customers at page "+page+" sort by"+fieldsForLogger+" with order type"+orderForLogger+" *****");

        // the page get from input minus 1 because it is the offset so the page start from 0 and not from 1 differently from the front-end when you are requesting the exact page
        Page<User> usersSorted = userService.getPagingUsersMultipleSortOrder(--page, pageSize, order, fields);

        return new ResponseEntity<>(usersSorted.getContent(), HttpStatus.OK);

    }





    @GetMapping(value = "/customers", produces = "application/json")
    public ResponseEntity<?> getCustomers(){

        try{

            logger.info("***** Fetch all the Customers *****");

            List<User> customers = userService.findAllCustomer();

            if(customers == null){

                logger.error("***** No Customers found *****");

                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            }

            return new ResponseEntity<>(customers, HttpStatus.OK);

        }
        catch (Exception e) {

            return userService.manageExceptions(e, logger, responseNode);

        }
    }





    @GetMapping(value = "/customers/id/{id}", produces = "application/json")
    public ResponseEntity<?> getCustomer(@PathVariable("id") Long id){

        try{

            logger.info("***** Get customer with id " + id + " *****");

            User u = userService.getCustomer(id);

            return new ResponseEntity<>(u, HttpStatus.OK);

        }
        catch (ResourceNotFoundException e){

            logger.error("***** "+e.getMessage()+" *****");

            responseNode.put("error", e.getMessage());

            return new ResponseEntity<>(responseNode, HttpStatus.NOT_FOUND);

        }
        catch (Exception e) {

            return userService.manageExceptions(e, logger, responseNode);

        }

    }





    @GetMapping(value = "/customers/email/{email}", produces = "application/json")
    public ResponseEntity<?> getCustomerByEmail(@PathVariable("email") String email){

        try{

            logger.info("***** Fetch User with email " + email + " *****");

            User u = userService.findCustomerByEmail(email);

            if (u != null) {

                return new ResponseEntity<>(u, HttpStatus.OK);

            }

            logger.error("***** No Customer found with email "+email+" *****");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Customer found with email "+email);

        }
        catch (Exception e){

            logger.error("Users: Exception thrown: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);

        }

    }

    @GetMapping(value = "/email/{email}", produces = "application/json")
    public ResponseEntity<?> getUserByEmail(@PathVariable("email") String email){

        try{

            logger.info("***** Fetch User with email "+email+" *****");

            User u = userService.getUserByEmail(email);

            if(u != null){

                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(u);

            }

            logger.error("***** User with email "+email+" not found *****");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with email "+email+" not found");

        }
        catch (Exception e){

            logger.error("Users: Exception thrown: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);

        }

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

            userService.manageExceptions(e, logger, responseNode);

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

            logger.info("***** Fetch Customers that have "+field+" = "+value+" at page "+page+" sort by"+fieldsForLogger+" with order type"+orderForLogger+" *****");

            results = userService.searchInCustomersBySort(field, value, --page, pageSize, order, fields);

        }catch (ParseException e){

            logger.info("***** Date format not valid *****");

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }catch (ResourceNotFoundException e){

            logger.info("***** error : "+e.getMessage()+" *****");

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }
        catch (Exception e){

            userService.manageExceptions(e, logger, responseNode);

        }

        return new ResponseEntity<>(results, HttpStatus.OK);

    }

}
