package it.si2001.rentalcar.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.User;
import it.si2001.rentalcar.exception.ResourceAlreadyExistingException;
import it.si2001.rentalcar.exception.ResourceNotFoundException;
import it.si2001.rentalcar.repository.UserRepository;
import it.si2001.rentalcar.service.UserService;
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
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {

        try{

            return userRepository.findAll();

        }catch (Exception e){

            e.printStackTrace();

            return null;

        }

    }





    @Override
    public User getUserById(Long id) {

        try{

            Optional<User> u = userRepository.findByIdUser(id);

            if (u.isEmpty()) {

                return null;

            }

            return u.get();

        }catch (Error e){

            e.printStackTrace();

            return null;

        }

    }






    @Override
    public User insertUser(User u) {

        try{

            Optional<User> user = userRepository.findAll()
                    .stream()
                    .filter(x -> x.getEmail().equals(u.getEmail()) || x.getCf().equals(u.getCf()))
                    .findFirst();

            if (user.isPresent()) {

                return null;

            }

            userRepository.saveAndFlush(u);

            return u;

        }catch (Exception e){

            e.printStackTrace();

            return null;

        }

    }






    @Override
    public boolean deleteUser(Long id) {

        try{

            Optional<User> user = userRepository.findByIdUser(id);

            if (user.isEmpty()) {

                return false;

            }

            userRepository.delete(user.get());

            return true;

        }catch (Exception e){

            e.printStackTrace();

            return false;

        }

    }






    @Override
    public User updateUser(User u, Long id) {

        try{

            Optional<User> existingUser = userRepository.findByIdUser(id);

            if (existingUser.isEmpty()) {

                return null;

            }

            existingUser.get().setName(u.getName());
            existingUser.get().setSurname(u.getSurname());
            existingUser.get().setBirthDate(u.getBirthDate());
            existingUser.get().setEmail(u.getEmail());
            existingUser.get().setPassword(u.getPassword());
            existingUser.get().setRole(u.getRole());
            existingUser.get().setCf(u.getCf());

            userRepository.saveAndFlush(existingUser.get());

            return existingUser.get();

        }catch (Exception e){

            e.printStackTrace();

            return null;

        }

    }





    @Override
    public User insertCustomer(User u) {

        try{

            Optional<User> user = userRepository.findAll()
                    .stream()
                    .filter(x -> x.getEmail().equals(u.getEmail()) || x.getCf().equals(u.getCf()))
                    .findFirst();

            if (user.isPresent()) {

                return null;

            }

            u.setRole(User.Role.CUSTOMER);

            userRepository.saveAndFlush(u);

            return u;

        }catch (Exception e){

            e.printStackTrace();

        }

        return null;

    }





    @Override
    public User updateCustomer(User u, Long id) {

        try{
            Optional<User> existingUser = userRepository.findByIdUser(id);

            if (existingUser.isEmpty()) {
                return null;
            } else if (existingUser.get().getRole().equals(User.Role.SUPERUSER)) {
                return null;
            }

            existingUser.get().setName(u.getName());
            existingUser.get().setSurname(u.getSurname());
            existingUser.get().setBirthDate(u.getBirthDate());
            existingUser.get().setEmail(u.getEmail());
            existingUser.get().setPassword(u.getPassword());
            existingUser.get().setRole(User.Role.CUSTOMER);
            existingUser.get().setCf(u.getCf());

            userRepository.saveAndFlush(existingUser.get());

            return existingUser.get();

        }catch (Exception e){

            e.printStackTrace();

            return null;

        }

    }





    @Override
    public boolean deleteCustomer(Long id) {

        try{

            Optional<User> user = userRepository.findByIdUser(id);

            if (user.isEmpty()) {

                return false;

            } else if (user.get().getRole().equals(User.Role.SUPERUSER)) {

                return false;

            }

            userRepository.delete(user.get());

            return true;

        }catch (Exception e){

            e.printStackTrace();

            return false;

        }

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

        List<User> results = userRepository.findAll(Sort.by(new ArrayList<>(
                Arrays.asList(new Sort.Order(Sort.Direction.ASC, field), new Sort.Order(Sort.Direction.ASC, field))
        )));

        return results;
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

        try{

            List<User> customers = userRepository.findByRole(User.Role.CUSTOMER);

            if(customers.isEmpty()){

                return null;

            }

            return customers;

        }
        catch (Exception e){

            e.printStackTrace();

            return null;

        }
    }





    @Override
    public List<User> findAllCustomersWithPaging(int offset, int pageSize) {

        List<User> customers = userRepository.findAll()
                .stream()
                .filter(x -> x.getRole().equals(User.Role.CUSTOMER))
                .skip((long) offset * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        return customers;
    }






    @Override
    public List<User> findAllCustomersByName(String name) {

        try{
            List<User> customersByName = userRepository.findByName(name)
                    .stream()
                    .filter(x -> x.getRole().equals(User.Role.CUSTOMER))
                    .collect(Collectors.toList());

            return customersByName;

        }catch (Exception e){

            e.printStackTrace();

        }

        return null;

    }






    @Override
    public List<User> findAllCustomersBySurname(String surname) {

        try{
            List<User> customersBySurname = userRepository.findBySurname(surname)
                    .stream()
                    .filter(x -> x.getRole().equals(User.Role.CUSTOMER))
                    .collect(Collectors.toList());

            return customersBySurname;

        }catch (Exception e){

            e.printStackTrace();

        }

        return null;

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

        try{
            Optional<User> customerByEmail = userRepository.findByEmail(email);

            if (customerByEmail.isPresent()) {

                if (customerByEmail.get().getRole().equals(User.Role.CUSTOMER)) {

                    return customerByEmail.get();

                }

            }

            return null;


        }catch (Exception e){

            e.printStackTrace();

            return null;

        }

    }





    @Override
    public User findCustomerByCf(String cf) {

        Optional<User> customerByCf = userRepository.findByCf(cf);

        if(customerByCf.isPresent()){

            if(customerByCf.get().getRole().equals(User.Role.CUSTOMER)){

                return customerByCf.get();

            }

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

                if(u.isEmpty()){
                    throw new ResourceNotFoundException("User", "cf", value);
                }

                else if(!u.get().getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("User", "id", value);
                }

                results.add(u.get());

                return results;

            case "email":

                u = userRepository.findByEmail(value);

                if(u.isEmpty()){
                    throw new ResourceNotFoundException("User", "email", value);
                }

                else if(!u.get().getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("User", "id", value);
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

                throw  new ResourceNotFoundException("User", "birthDate", value);

            default:

                return results;

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
                    throw new ResourceNotFoundException("User", "id", value);
                }

                else if(!u.get().getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("User", "id", value);
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

                if(u.isEmpty()){
                    throw new ResourceNotFoundException("User", "cf", value);
                }

                else if(!u.get().getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("User", "id", value);
                }

                results.add(u.get());

                return results;

            case "email":

                u = userRepository.findByEmail(value);

                if(u.isEmpty()){
                    throw new ResourceNotFoundException("User", "email", value);
                }

                else if(!u.get().getRole().equals(User.Role.CUSTOMER)){
                    throw new ResourceNotFoundException("User", "id", value);
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

                throw  new ResourceNotFoundException("User", "birthDate", value);

            default:

                return results;

        }

    }





    @Override
    public User getUserByEmail(String email) {

        try{

            Optional<User> u = userRepository.findByEmail(email);

            if (u.isEmpty()) {

                return null;

            }

            return u.get();

        }catch (Exception e){

            e.printStackTrace();

            return null;

        }

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
