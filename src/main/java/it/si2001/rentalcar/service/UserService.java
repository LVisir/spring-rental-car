package it.si2001.rentalcar.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.User;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {

    public List<User> getAllUsers();

    public User getUserById(Long id);

    public void insertUser(User u);

    public void deleteUser(User u);

    public void updateUser(User u, Long id);

    public ResponseEntity<ObjectNode> manageExceptions(Exception e, Logger logger, ObjectNode responseNode, HttpHeaders headers);

}
