package it.si2001.rentalcar.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import it.si2001.rentalcar.entity.User;
import it.si2001.rentalcar.exception.ResourceAlreadyExistingException;
import it.si2001.rentalcar.exception.ResourceNotFoundException;
import it.si2001.rentalcar.repository.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

        // will contains a list of objects of the form (asc|desc)
        List<Sort.Direction> directions = new ArrayList<>();

        for(String s : order){
            if(s.equals("asc")){
                directions.add(Sort.Direction.ASC);
            }
            else if(s.equals("desc")){
                directions.add(Sort.Direction.DESC);
            }
        }

        // will contains a list og objects of {(asc|desc), field1}, {(asc|desc), field2}, {(asc|desc), field3}, ...
        List<Sort.Order> sortOrders = new ArrayList<>();

        IntStream.range(0, order.size())
                        .forEach(index -> sortOrders.add(new Sort.Order(directions.get(index), fields.get(index))));



        return userRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(sortOrders)));
    }

}
