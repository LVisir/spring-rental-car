package it.si2001.rentalcar.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.User;
import it.si2001.rentalcar.exception.ResourceAlreadyExistingException;
import it.si2001.rentalcar.exception.ResourceNotFoundException;
import it.si2001.rentalcar.repository.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }





    @Override
    public User getUserById(Long id) {

        User u = userRepository.findByIdUser(id);

        if(u == null){
            throw new ResourceNotFoundException("User","id",id);
        }

        return u;
    }






    @Override
    public void insertUser(User u) {

        Optional<User> user = userRepository.findAll()
                .stream()
                .filter(x -> x.getEmail().equals(u.getEmail()) || x.getCf().equals(u.getCf()))
                .findFirst();

        if(user.isPresent()){

            throw new ResourceAlreadyExistingException("User", "email", u.getEmail());

        }

        userRepository.saveAndFlush(u);
    }






    @Override
    public void deleteUser(User u) {

        User user = userRepository.findByIdUser(u.getIdUser());

        if(user == null){

            throw new ResourceNotFoundException("User", "id", u.getIdUser());

        }

        userRepository.delete(u);
    }






    @Override
    public void updateUser(User u, Long id) {

        User existingUser = userRepository.findByIdUser(id);

        if(existingUser == null){
            throw new ResourceNotFoundException("User", "id", id);
        }

        existingUser.setIdUser(u.getIdUser());
        existingUser.setName(u.getName());
        existingUser.setSurname(u.getSurname());
        existingUser.setBirthDate(u.getBirthDate());
        existingUser.setEmail(u.getEmail());
        existingUser.setPassword(u.getPassword());
        existingUser.setRole(u.getRole());
        existingUser.setCf(u.getCf());

        userRepository.saveAndFlush(existingUser);

    }






    @Override
    public ResponseEntity<ObjectNode> manageExceptions(Exception e, Logger logger, ObjectNode responseNode, HttpHeaders headers) {

        if(e.getCause().getCause() instanceof SQLException){

            logger.info("***** "+e.getCause().getCause()+" *****");

            responseNode.put("code", "error");

            responseNode.put("message", e.getCause().getCause().getMessage());

            return new ResponseEntity<>(responseNode, headers, HttpStatus.BAD_REQUEST);
        }

        logger.info("***** "+e.getCause().getMessage()+" *****");

        responseNode.put("code", "error");

        responseNode.put("message", e.getCause().getMessage());

        return new ResponseEntity<>(responseNode, headers, HttpStatus.BAD_REQUEST);

    }






    @Override
    public List<User> getUsersSortedBy(String field) {

        userRepository.findAll(Sort.by(new ArrayList<>(
                Arrays.asList(new Sort.Order(Sort.Direction.ASC, field), new Sort.Order(Sort.Direction.ASC, field))
        )));

        return null;
    }


    /**
     * By taking a list of fields to order and the corresponding list of order type (asc|desc)
     * it will sort by the above settings
     * @param offset
     * @param pageSize
     * @param order
     * @param fields
     * @return
     */
    @Override
    public Page<User> getPagingUsersMultipleSortOrder(int offset, int pageSize, List<String> order, List<String> fields) {

        // will contain a list og objects of {(asc|desc), field1}, {(asc|desc), field2}, {(asc|desc), field3}, ...
        List<Sort.Order> sortOrders = getListSortOrders(order, fields);

        return userRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(sortOrders)));
    }





    @Override
    public List<User> findAllCustomer() {

        List<User> customers = userRepository.findByRole(User.Role.CUSTOMER);

        return customers;
    }





    @Override
    public List<User> findAllCustomersWithPaging(int offset, int pageSize) {

        List<User> customers = userRepository.findAll(PageRequest.of(offset, pageSize))
                .stream()
                .filter(x -> x.getRole().equals(User.Role.CUSTOMER))
                .collect(Collectors.toList());

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

        List<User> customersByDate = userRepository.findByBirthDate(birthDate)
                .stream()
                .filter(x -> x.getRole().equals(User.Role.CUSTOMER))
                .collect(Collectors.toList());

        return customersByDate;
    }






    @Override
    public User findCustomerByEmail(String email) {

        User customerByEmail = userRepository.findByEmail(email);

        if(customerByEmail.getRole().equals(User.Role.CUSTOMER)){
            return customerByEmail;
        }

        return null;
    }





    @Override
    public User findCustomerByCf(String cf) {

        User customerByEmail = userRepository.findByCf(cf);

        if(customerByEmail.getRole().equals(User.Role.CUSTOMER)){
            return customerByEmail;
        }

        return null;

    }





    /**
     * Search a field from a certain value and in a certain page
     * @param field
     * @param value
     * @param offset
     * @param pageSize
     * @return
     */
    @Override
    public List<User> searchInCustomers(String field, String value, int offset, int pageSize) throws ParseException {

        List<User> results = new ArrayList<>();

        User u;

        switch(field){

            case "id":

                u = userRepository.findByIdUser(Long.parseLong(value));

                if(u == null){
                    throw new ResourceNotFoundException("User", "id", value);
                }

                else if(!u.getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("User", "id", value);
                }

                results.add(u);

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

                throw  new ResourceNotFoundException("User", "name", value);

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

                throw  new ResourceNotFoundException("User", "surname", value);

            case "cf":

                u = userRepository.findByCf(value);

                if(u == null){
                    throw new ResourceNotFoundException("User", "cf", value);
                }

                else if(!u.getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("User", "id", value);
                }

                results.add(u);

                return results;

            case "email":

                u = userRepository.findByEmail(value);

                if(u == null){
                    throw new ResourceNotFoundException("User", "email", value);
                }

                else if(!u.getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("User", "id", value);
                }

                results.add(u);

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

                throw  new ResourceNotFoundException("User", "birthDate", value);

            default:

                return results;

        }

    }

    @Override
    public List<User> searchInCustomersBySort(String field, String value, int offset, int pageSize, List<String> order, List<String> fields) throws ParseException {

        List<User> results = new ArrayList<>();

        List<Sort.Order> sortOrders = getListSortOrders(order, fields);

        User u;

        switch(field){

            case "id":

                u = userRepository.findByIdUser(Long.parseLong(value));

                if(u == null){
                    throw new ResourceNotFoundException("User", "id", value);
                }

                else if(!u.getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("User", "id", value);
                }

                results.add(u);

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

                throw  new ResourceNotFoundException("User", "name", value);

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

                throw  new ResourceNotFoundException("User", "surname", value);

            case "cf":

                u = userRepository.findByCf(value);

                if(u == null){
                    throw new ResourceNotFoundException("User", "cf", value);
                }

                else if(!u.getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("User", "id", value);
                }

                results.add(u);

                return results;

            case "email":

                u = userRepository.findByEmail(value);

                if(u == null){
                    throw new ResourceNotFoundException("User", "email", value);
                }

                else if(!u.getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("User", "id", value);
                }

                results.add(u);

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

                throw  new ResourceNotFoundException("User", "birthDate", value);

            default:

                return results;

        }

    }





    @Override
    public User getUserByEmail(String email) {

        User u = userRepository.findByEmail(email);

        if(u == null){

            throw new ResourceNotFoundException("User", "email", email);

        }

        return u;

    }


    /**
     * From URL ?_sort=field1, field2, ..., fieldN&_order=asc,desc,...,asc
     * To ---> List<String> , List<Sort.Direction>
     * @param order
     * @param fields
     * @return
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


}
