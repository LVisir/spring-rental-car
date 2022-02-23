package it.si2001.rentalcar.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.User;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

public interface UserService {

    public List<User> getAllUsers();

    public User getUserById(Long id);

    public void insertUser(User u);

    public void deleteUser(User u);

    public void updateUser(User u, Long id);

    public ResponseEntity<ObjectNode> manageExceptions(Exception e, Logger logger, ObjectNode responseNode, HttpHeaders headers);

    public List<User> getUsersSortedBy(String field);

    public Page<User> getPagingUsersMultipleSortOrder(int offset, int pageSize, List<String> order, List<String> fields);

    public List<User> findAllCustomer();

    public List<User> findAllCustomersWithPaging(int offset, int pageSize);

    public List<User> findAllCustomersByName(String name);

    public List<User> findAllCustomersBySurname(String surname);

    public List<User> findAllCustomersByBirthDate(Date birthDate);

    public User findCustomerByEmail(String email);

    public User findCustomerByCf(String cf);

}
