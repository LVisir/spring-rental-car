package it.si2001.rentalcar.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.User;
import it.si2001.rentalcar.exception.ResourceAlreadyExistingException;
import it.si2001.rentalcar.exception.ResourceNotFoundException;
import it.si2001.rentalcar.repository.UserRepository;
import it.si2001.rentalcar.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PrettyLogger prettyLogger;





    @Override
    public List<User> getAllUsers() {

        List<User> users = userRepository.findAll();

        if(users.isEmpty()){

            return null;

        }

        return users;

    }





    @Override
    public User getUserById(Long id) {

        log.info(" Check if the User exists ");

        Optional<User> user = userRepository.findByIdUser(id);

        if(user.isPresent()){

            log.info("Return User");

            return user.get();

        }

        else {

            throw new ResourceNotFoundException("User", "id", id);

        }

    }






    @Override
    public User insertUser(User u) {

        if(u.getIdUser() != null){

            log.info(" Check if the User exists ");

            Optional<User> userFromId = userRepository.findByIdUser(u.getIdUser());

            if(userFromId.isPresent()){

                throw new ResourceAlreadyExistingException("User", "id", u.getIdUser());

            }

        }

        log.info(" Check if the email of the User exists ");

        Optional<User> userFromEmail = userRepository.findByEmail(u.getEmail());

        if(userFromEmail.isPresent()){

            throw new ResourceAlreadyExistingException("User", "email", u.getEmail());

        }

        log.info(" Check if the fiscal code of the User exists ");

        Optional<User> userFromCf = userRepository.findByCf(u.getCf());

        if(userFromCf.isPresent()){

            throw new ResourceAlreadyExistingException("User", "cf", u.getCf());

        }

        log.info(" Save the User ");

        userRepository.saveAndFlush(u);

        return u;


    }






    @Override
    public void deleteUser(Long id) {

        log.info(" Check if the User exists ");

        Optional<User> user = userRepository.findByIdUser(id);

        if(user.isPresent()){

            log.info("Delete User");

            userRepository.delete(user.get());

        }
        else {

            throw new ResourceNotFoundException("User", "id", id);

        }

    }






    @Override
    public User updateUser(User u, Long id) {

        log.info(" Check if the User exists ");

        Optional<User> existingUser = userRepository.findByIdUser(id);

        if (existingUser.isPresent()) {

            if(!u.getEmail().equals(existingUser.get().getEmail())){

                log.info(" Check if the email of the User exists ");

                Optional<User> existingUserEmail = userRepository.findByEmail(u.getEmail());

                if(existingUserEmail.isPresent()){

                    throw new ResourceAlreadyExistingException("User", "email", u.getEmail());

                }

            }

            if(!u.getCf().equals(existingUser.get().getCf())){

                log.info(" Check if the fiscal code of the User exists ");

                Optional<User> existingUserCf = userRepository.findByCf(u.getCf());

                if(existingUserCf.isPresent()){

                    throw new ResourceAlreadyExistingException("User", "cf", u.getCf());

                }

            }

            existingUser.get().setIdUser(u.getIdUser());
            existingUser.get().setName(u.getName());
            existingUser.get().setSurname(u.getSurname());
            existingUser.get().setBirthDate(u.getBirthDate());
            existingUser.get().setEmail(u.getEmail());
            existingUser.get().setPassword(u.getPassword());
            existingUser.get().setRole(u.getRole());
            existingUser.get().setCf(u.getCf());

            log.info(" Update User ");

            userRepository.saveAndFlush(existingUser.get());

            return existingUser.get();

        }

        throw new ResourceNotFoundException("User", "id", id);

    }





    @Override
    public User insertCustomer(User u) {

        if(u.getIdUser() != null){

            log.info(" Check if the Customer exists ");

            Optional<User> userFromId = userRepository.findByIdUser(u.getIdUser());

            if(userFromId.isPresent()){

                throw new ResourceAlreadyExistingException("User", "id", u.getIdUser());

            }

        }

        log.info(" Check if the email of the Customer exists ");

        Optional<User> userFromEmail = userRepository.findByEmail(u.getEmail());

        if(userFromEmail.isPresent()){

            throw new ResourceAlreadyExistingException("Customer", "email", u.getEmail());

        }

        log.info(" Check if the fiscal code of the Customer exists ");

        Optional<User> userFromCf = userRepository.findByCf(u.getCf());

        if(userFromCf.isPresent()){

            throw new ResourceAlreadyExistingException("Customer", "cf", u.getCf());

        }

        u.setRole(User.Role.CUSTOMER);

        log.info(" Save the customer ");

        userRepository.saveAndFlush(u);

        return u;

    }





    @Override
    public User updateCustomer(User u, Long id) {

        log.info(" Check if the Customer exists ");

        Optional<User> existingUser = userRepository.findByIdUser(id);

        if(existingUser.isPresent()){

            if(!existingUser.get().getEmail().equals(u.getEmail())){

                log.info(" Check if the email of the Customer exists ");

                Optional<User> userFromEmail = userRepository.findByEmail(u.getEmail());

                if(userFromEmail.isPresent()){

                    throw new ResourceAlreadyExistingException("Customer", "email", u.getEmail());

                }

            }

            else if(!existingUser.get().getCf().equals(u.getCf())){

                log.info(" Check if the fiscal code of the Customer exists ");

                Optional<User> userFromCf = userRepository.findByCf(u.getCf());

                if(userFromCf.isPresent()){

                    throw new ResourceAlreadyExistingException("Customer", "cf", u.getCf());

                }

            }

            existingUser.get().setIdUser(u.getIdUser());
            existingUser.get().setName(u.getName());
            existingUser.get().setSurname(u.getSurname());
            existingUser.get().setBirthDate(u.getBirthDate());
            existingUser.get().setEmail(u.getEmail());
            existingUser.get().setPassword(u.getPassword());
            existingUser.get().setRole(User.Role.CUSTOMER);
            existingUser.get().setCf(u.getCf());

            log.info(" Update Customer ");

            userRepository.saveAndFlush(existingUser.get());

            return existingUser.get();

        }

        throw new ResourceNotFoundException("User", "id", id);

    }





    @Override
    public void deleteCustomer(Long id) {

        log.info(" Check if the Customer exists ");

        Optional<User> user = userRepository.findByIdUser(id);

        if(user.isPresent()){

            if(user.get().getRole().equals(User.Role.CUSTOMER)){

                log.info(" Delete Customer ");

                userRepository.delete(user.get());

            }

            else throw new ResourceNotFoundException("Customer", "id", id);

        }

        else throw new ResourceNotFoundException("User", "id", id);

    }





    @Override
    public ResponseEntity<ObjectNode> manageExceptions(Exception e, Logger logger, ObjectNode responseNode) {

        return prettyLogger.prettyException(e, logger, responseNode);

    }






    @Override
    public List<User> getUsersSortedBy(String field) {

        List<User> results = userRepository.findAll(
                Sort.by(new ArrayList<>(Arrays.asList(new Sort.Order(Sort.Direction.ASC, field), new Sort.Order(Sort.Direction.ASC, field)))))
                .stream()
                .filter(x -> x.getRole().equals(User.Role.CUSTOMER))
                .collect(Collectors.toList());

        if(results.isEmpty()){

           return null;

        }

        return results;

    }





    /**
     * By taking a list of fields to order and the corresponding list of order type (asc|desc)
     * it will sort by the above settings
     * @param offset : from which page start (0 is the first)
     * @param pageSize : the size of the page
     * @param order : asc|desc
     * @param fields : by which fields do the order
     * @return : a list of User from page offset, pageSize element, with order by order and by fields
     */
    @Override
    public Page<User> getPagingUsersMultipleSortOrder(int offset, int pageSize, List<String> order, List<String> fields) {

        // will contain a list og objects of {(asc|desc), field1}, {(asc|desc), field2}, {(asc|desc), field3}, ...
        List<Sort.Order> sortOrders = getListSortOrders(order, fields);

        List<User> sortedPagingUsers = userRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(sortOrders)))
                .stream()
                .filter(x -> x.getRole().equals(User.Role.CUSTOMER))
                .collect(Collectors.toList());

        if(sortedPagingUsers.isEmpty()){

            return null;

        }

        return new PageImpl<>(sortedPagingUsers);

    }





    @Override
    public List<User> findAllCustomer() {

        List<User> customers = userRepository.findByRole(User.Role.CUSTOMER);

        if(customers.isEmpty()){

            return null;

        }

        log.info("Return Customers");

        return customers;

    }





    @Override
    public List<User> findAllCustomersWithPaging(int offset, int pageSize) {

        List<User> customers = userRepository.findAll()
                .stream()
                .filter(x -> x.getRole().equals(User.Role.CUSTOMER))
                .skip((long) offset * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        if(customers.isEmpty()){

            return null;

        }

        return customers;
    }






    @Override
    public List<User> findAllCustomersByName(String name) {

        List<User> customersByName = userRepository.findByName(name)
                .stream()
                .filter(x -> x.getRole().equals(User.Role.CUSTOMER))
                .collect(Collectors.toList());

        return customersByName;

    }






    @Override
    public List<User> findAllCustomersBySurname(String surname) {


        List<User> customersBySurname = userRepository.findBySurname(surname)
                .stream()
                .filter(x -> x.getRole().equals(User.Role.CUSTOMER))
                .collect(Collectors.toList());

        return customersBySurname;



    }






    @Override
    public List<User> findAllCustomersByBirthDate(Date birthDate) {

        try{
            List<User> customersByDate = userRepository.findByBirthDate(birthDate)
                    .stream()
                    .filter(x -> x.getRole().equals(User.Role.CUSTOMER))
                    .collect(Collectors.toList());

            return customersByDate;

        }catch (Exception e){

            e.printStackTrace();

        }

        return null;

    }






    @Override
    public User findCustomerByEmail(String email) {


        Optional<User> customerByEmail = userRepository.findByEmail(email);

        if (customerByEmail.isPresent()) {

            if (customerByEmail.get().getRole().equals(User.Role.CUSTOMER)) {

                return customerByEmail.get();

            }

        }

        throw new ResourceNotFoundException("Customer", "email", email);


    }





    @Override
    public User findCustomerByCf(String cf) {

        Optional<User> customerByCf = userRepository.findByCf(cf);

        if(customerByCf.isPresent()){

            if(customerByCf.get().getRole().equals(User.Role.CUSTOMER)){

                return customerByCf.get();

            }

        }

        throw new ResourceNotFoundException("Customer", "cf", cf);

    }





    @Override
    public List<User> search(String field, String value) throws ParseException {

        List<User> results = new ArrayList<>();

        Optional<User> u;

        switch(field){

            case "idUser":

                log.info("Try to search by id");

                u = userRepository.findByIdUser(Long.parseLong(value));

                if(u.isEmpty()){
                    throw new ResourceNotFoundException("User", "id", value);
                }

                else if(!u.get().getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("User", "id", value);
                }

                results.add(u.get());

                return results;

            case "name":

                log.info("Try to search by name");

                results = userRepository.findByRole(User.Role.CUSTOMER)
                        .stream()
                        .filter(x -> x.getName().equals(value))
                        .collect(Collectors.toList());

                if(results.size() != 0){
                    return results;
                }

                throw  new ResourceNotFoundException("Customers", "name", value);

            case "surname":

                log.info("Try to search by surname");

                results = userRepository.findByRole(User.Role.CUSTOMER)
                        .stream()
                        .filter(x -> x.getSurname().equals(value))
                        .collect(Collectors.toList());

                if(results.size() != 0){
                    return results;
                }

                throw  new ResourceNotFoundException("Customers", "surname", value);

            case "cf":

                log.info("Try to search by cf");

                u = userRepository.findByCf(value);

                if(u.isEmpty()){
                    throw new ResourceNotFoundException("Customer", "cf", value);
                }

                else if(!u.get().getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("Customer", "id", value);
                }

                results.add(u.get());

                return results;

            case "email":

                log.info("Try to search by email");

                u = userRepository.findByEmail(value);

                if(u.isEmpty()){
                    throw new ResourceNotFoundException("Customer", "email", value);
                }

                else if(!u.get().getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("Customer", "id", value);
                }

                results.add(u.get());

                return results;

            case "birthDate":

                log.info("Try to search by birth date");

                Date d = new SimpleDateFormat("yyyy-MM-dd").parse(value);

                results = userRepository.findByRole(User.Role.CUSTOMER)
                        .stream()
                        .filter(x -> x.getBirthDate().equals(d))
                        .collect(Collectors.toList());

                if(results.size() != 0){
                    return results;
                }

                throw  new ResourceNotFoundException("Customer", "birthDate", value);

            default:

                return results;

        }

    }


    /**
     * Search a field from a certain value and in a certain page
     * @param field : search by this field
     * @param value : search by this value
     * @param offset : starts from page offset (0 is the first page)
     * @param pageSize : number of element per page
     * @return : list of User search by a certain field with a certain value in a certain page of a certain number of elements
     */
    @Override
    public List<User> searchInCustomers(String field, String value, int offset, int pageSize) throws ParseException {

        List<User> results = new ArrayList<>();

        Optional<User> u;

        switch(field){

            case "idUser":

                u = userRepository.findByIdUser(Long.parseLong(value));

                if(u.isEmpty()){
                    throw new ResourceNotFoundException("User", "id", value);
                }

                else if(!u.get().getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("User", "id", value);
                }

                results.add(u.get());

                return results;

            case "name":

                results = userRepository.findByRole(User.Role.CUSTOMER)
                        .stream()
                        .filter(x -> x.getName().equals(value))
                        .skip((long) offset * pageSize)
                        .limit(pageSize)
                        .collect(Collectors.toList());

                if(results.size() != 0){
                    return results;
                }

                throw  new ResourceNotFoundException("Customers", "name", value);

            case "surname":

                results = userRepository.findByRole(User.Role.CUSTOMER)
                        .stream()
                        .filter(x -> x.getSurname().equals(value))
                        .skip((long) offset * pageSize)
                        .limit(pageSize)
                        .collect(Collectors.toList());

                if(results.size() != 0){
                    return results;
                }

                throw  new ResourceNotFoundException("Customers", "surname", value);

            case "cf":

                u = userRepository.findByCf(value);

                if(u.isEmpty()){
                    throw new ResourceNotFoundException("Customer", "cf", value);
                }

                else if(!u.get().getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("Customer", "id", value);
                }

                results.add(u.get());

                return results;

            case "email":

                u = userRepository.findByEmail(value);

                if(u.isEmpty()){
                    throw new ResourceNotFoundException("Customer", "email", value);
                }

                else if(!u.get().getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("Customer", "id", value);
                }

                results.add(u.get());

                return results;

            case "birthDate":

                Date d = new SimpleDateFormat("yyyy-MM-dd").parse(value);

                results = userRepository.findByRole(User.Role.CUSTOMER)
                        .stream()
                        .filter(x -> x.getBirthDate().equals(d))
                        .skip((long) offset * pageSize)
                        .limit(pageSize)
                        .collect(Collectors.toList());

                if(results.size() != 0){
                    return results;
                }

                throw  new ResourceNotFoundException("Customer", "birthDate", value);

            default:

                return null;

        }

    }

    @Override
    public List<User> searchInCustomersBySort(String field, String value, int offset, int pageSize, List<String> order, List<String> fields) throws ParseException {

        List<User> results = new ArrayList<>();

        List<Sort.Order> sortOrders = getListSortOrders(order, fields);

        Optional<User> u;

        switch(field){

            case "idUser":

                u = userRepository.findByIdUser(Long.parseLong(value));

                if(u.isEmpty()){
                    throw new ResourceNotFoundException("Customer", "id", value);
                }

                else if(!u.get().getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("Customer", "id", value);
                }

                results.add(u.get());

                return results;

            case "name":

                results = userRepository.findAll(Sort.by(sortOrders))
                        .stream()
                        .filter(x -> x.getName().equals(value) && x.getRole().equals(User.Role.CUSTOMER))
                        .skip((long) offset * pageSize)
                        .limit(pageSize)
                        .collect(Collectors.toList());

                if(results.size() != 0){
                    return results;
                }

                throw  new ResourceNotFoundException("Customers", "name", value);

            case "surname":

                results = userRepository.findAll(Sort.by(sortOrders))
                        .stream()
                        .filter(x -> x.getSurname().equals(value) && x.getRole().equals(User.Role.CUSTOMER))
                        .skip((long) offset * pageSize)
                        .limit(pageSize)
                        .collect(Collectors.toList());

                if(results.size() != 0){
                    return results;
                }

                throw  new ResourceNotFoundException("Customers", "surname", value);

            case "cf":

                u = userRepository.findByCf(value);

                if(u.isEmpty()){
                    throw new ResourceNotFoundException("Customer", "cf", value);
                }

                else if(!u.get().getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("Customer", "id", value);
                }

                results.add(u.get());

                return results;

            case "email":

                u = userRepository.findByEmail(value);

                if(u.isEmpty()){
                    throw new ResourceNotFoundException("Customer", "email", value);
                }

                else if(!u.get().getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("Customer", "id", value);
                }

                results.add(u.get());

                return results;

            case "birthDate":

                Date d = new SimpleDateFormat("yyyy-MM-dd").parse(value);

                results = userRepository.findAll(Sort.by(sortOrders))
                        .stream()
                        .filter(x -> x.getBirthDate().equals(d) && x.getRole().equals(User.Role.CUSTOMER))
                        .skip((long) offset * pageSize)
                        .limit(pageSize)
                        .collect(Collectors.toList());

                if(results.size() != 0){
                    return results;
                }

                throw  new ResourceNotFoundException("Customers", "birthDate", value);

            default:

                return null;

        }

    }





    @Override
    public User getUserByEmail(String email) {

        log.info(" Check if the email of the User exists ");

        Optional<User> u = userRepository.findByEmail(email);

        if (u.isEmpty()) {

            return null;

        }

        log.info("Return User");

        return u.get();

    }





    @Override
    public User getCustomer(Long id) {

        log.info(" Check if the Customer exists ");

        Optional<User> customer = userRepository.findByIdUser(id);

        if(customer.isPresent()){

            if(customer.get().getRole().equals(User.Role.CUSTOMER)){

                log.info("Return Customer");

                return customer.get();

            }

        }

        throw new ResourceNotFoundException("Customer", "id", id);

    }


    /**
     * From URL ?_sort=field1, field2, ..., fieldN&_order=asc,desc,...,asc
     * To ---> List<String> , List<Sort.Direction>
     * @param order : asc/desc
     * @param fields : sort by this fields
     * @return : a List containing elements where each element are represented as: [order, field]
     */
    public List<Sort.Order> getListSortOrders(List<String> order, List<String> fields) {

        // will contain a list of objects of the form (asc|desc)
        List<Sort.Direction> directions = new ArrayList<>();

        for(String s : order){
            if(s.equals("asc")){
                directions.add(Sort.Direction.ASC);
            }
            else if(s.equals("desc")){
                directions.add(Sort.Direction.DESC);
            }
        }

        // will contain a list og objects of {(asc|desc), field1}, {(asc|desc), field2}, {(asc|desc), field3}, ...
        List<Sort.Order> sortOrders = new ArrayList<>();

        IntStream.range(0, order.size())
                .forEach(index -> sortOrders.add(new Sort.Order(directions.get(index), fields.get(index))));

        return sortOrders;

    }





    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> u = userRepository.findByEmail(email);

        if(u.isEmpty()){

            log.error("User with email {} not found", email);

            throw new UsernameNotFoundException("User not found in the database");

        }
        else{

            log.info("User with email {} found", email);

        }

        // Collection with Roles inside
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // adding the Role of this User
        authorities.add(new SimpleGrantedAuthority(u.get().getRole().toString()));

        // Object User from the Spring Framework and not from this application
        return new org.springframework.security.core.userdetails.User(u.get().getEmail(), u.get().getPassword(), authorities);
    }
}
