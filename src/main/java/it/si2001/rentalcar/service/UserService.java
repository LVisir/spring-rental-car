package it.si2001.rentalcar.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.User;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Long id);

    User insertUser(User u);

    boolean deleteUser(Long id);

    User updateUser(User u, Long id);

    User insertCustomer(User u);

    User updateCustomer(User u, Long id);

    boolean deleteCustomer(Long id);

    ResponseEntity<ObjectNode> manageExceptions(Exception e, Logger logger, ObjectNode responseNode, HttpHeaders headers);

    List<User> getUsersSortedBy(String field);

    Page<User> getPagingUsersMultipleSortOrder(int offset, int pageSize, List<String> order, List<String> fields);

    List<User> findAllCustomer();

    List<User> findAllCustomersWithPaging(int offset, int pageSize);

    List<User> findAllCustomersByName(String name);

    List<User> findAllCustomersBySurname(String surname);

    List<User> findAllCustomersByBirthDate(Date birthDate);

    User findCustomerByEmail(String email);

    User findCustomerByCf(String cf);

    List<User> searchInCustomers(String field, String value, int offset, int pageSize) throws ParseException;

    List<User> searchInCustomersBySort(String field, String value, int offset, int pageSize, List<String> order, List<String> fields) throws ParseException;

    User getUserByEmail(String email);

}
