package it.si2001.rentalcar.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.User;
import it.si2001.rentalcar.exception.ResourceAlreadyExistingException;
import it.si2001.rentalcar.exception.ResourceNotFoundException;
import it.si2001.rentalcar.repository.UserRepository;
import it.si2001.rentalcar.service.PrettyLogger;
import it.si2001.rentalcar.service.UserService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

        Optional<User> user = userRepository.findByIdUser(id);

        if(user.isPresent()){

            return user.get();

        }

        else {

            throw new ResourceNotFoundException("User", "id", id);

        }

    }






    @Override
    public User insertUser(User u) {

        if(u.getIdUser() != null){

            Optional<User> userFromId = userRepository.findByIdUser(u.getIdUser());

            if(userFromId.isPresent()){

                throw new ResourceAlreadyExistingException("User", "id", u.getIdUser());

            }

        }

        Optional<User> userFromEmail = userRepository.findByEmail(u.getEmail());

        if(userFromEmail.isPresent()){

            throw new ResourceAlreadyExistingException("User", "email", u.getEmail());

        }

        Optional<User> userFromCf = userRepository.findByCf(u.getCf());

        if(userFromCf.isPresent()){

            throw new ResourceAlreadyExistingException("User", "cf", u.getCf());

        }

        userRepository.saveAndFlush(u);

        return u;


    }






    @Override
    public void deleteUser(Long id) {

        Optional<User> user = userRepository.findByIdUser(id);

        if(user.isPresent()){

            userRepository.delete(user.get());

        }
        else {

            throw new ResourceNotFoundException("User", "id", id);

        }

    }






    @Override
    public User updateUser(User u, Long id) {


        Optional<User> existingUser = userRepository.findByIdUser(id);

        if (existingUser.isPresent()) {

            if(!u.getEmail().equals(existingUser.get().getEmail())){

                Optional<User> existingUserEmail = userRepository.findByEmail(u.getEmail());

                if(existingUserEmail.isPresent()){

                    throw new ResourceAlreadyExistingException("User", "email", u.getEmail());

                }

            }

            if(!u.getCf().equals(existingUser.get().getCf())){

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

            userRepository.saveAndFlush(existingUser.get());

            return existingUser.get();

        }

        throw new ResourceNotFoundException("User", "id", id);

    }





    @Override
    public User insertCustomer(User u) {

        if(u.getIdUser() != null){

            Optional<User> userFromId = userRepository.findByIdUser(u.getIdUser());

            if(userFromId.isPresent()){

                throw new ResourceAlreadyExistingException("User", "id", u.getIdUser());

            }

        }

        Optional<User> userFromEmail = userRepository.findByEmail(u.getEmail());

        if(userFromEmail.isPresent()){

            throw new ResourceAlreadyExistingException("User", "email", u.getEmail());

        }

        Optional<User> userFromCf = userRepository.findByCf(u.getCf());

        if(userFromCf.isPresent()){

            throw new ResourceAlreadyExistingException("User", "cf", u.getCf());

        }

        u.setRole(User.Role.CUSTOMER);

        userRepository.saveAndFlush(u);

        return u;

    }





    @Override
    public User updateCustomer(User u, Long id) {

        Optional<User> existingUser = userRepository.findByIdUser(id);

        if(existingUser.isPresent()){

            if(!existingUser.get().getEmail().equals(u.getEmail())){

                Optional<User> userFromEmail = userRepository.findByEmail(u.getEmail());

                if(userFromEmail.isPresent()){

                    throw new ResourceAlreadyExistingException("User", "email", u.getEmail());

                }

            }

            else if(!existingUser.get().getCf().equals(u.getCf())){

                Optional<User> userFromCf = userRepository.findByCf(u.getCf());

                if(userFromCf.isPresent()){

                    throw new ResourceAlreadyExistingException("User", "cf", u.getCf());

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

            userRepository.saveAndFlush(existingUser.get());

            return existingUser.get();

        }

        throw new ResourceNotFoundException("User", "id", id);

    }





    @Override
    public void deleteCustomer(Long id) {

        Optional<User> user = userRepository.findByIdUser(id);

        if(user.isPresent()){

            if(user.get().getRole().equals(User.Role.CUSTOMER)){

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

        List<User> results = userRepository.findAll(Sort.by(new ArrayList<>(
                Arrays.asList(new Sort.Order(Sort.Direction.ASC, field), new Sort.Order(Sort.Direction.ASC, field))
        )));

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

        return userRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(sortOrders)));
    }





    @Override
    public List<User> findAllCustomer() {

        List<User> customers = userRepository.findByRole(User.Role.CUSTOMER);

        if(customers.isEmpty()){

            return null;

        }

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





    @Override
    public User getCustomer(Long id) {

        Optional<User> customer = userRepository.findByIdUser(id);

        if(customer.isPresent()){

            if(customer.get().getRole().equals(User.Role.CUSTOMER)){

                return customer.get();

            }

            else throw new ResourceNotFoundException("Customer", "id", id);

        }

        else throw new ResourceNotFoundException("Customer", "id", id);
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








}
