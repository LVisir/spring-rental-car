package it.si2001.rentalcar.service;

import it.si2001.rentalcar.entity.User;

import java.util.List;

public interface UserService {

    public List<User> getAllUsers();

    public User getUserById(Long id);

    public void insertUser(User u);

    public void deleteUser(User u);

    public void updateUser(User u, Long id);

    public User getUserByEmail(String email);

    public User getUserByEmailAndCf(String email, String cf);

}
