package it.si2001.rentalcar.service;

import it.si2001.rentalcar.entity.User;
import it.si2001.rentalcar.exception.ResourceAlreadyExistingException;
import it.si2001.rentalcar.exception.ResourceNotFoundException;
import it.si2001.rentalcar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

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
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User getUserByEmailAndCf(String email, String cf) {
        return userRepository.findByEmailAndCf(email, cf);
    }

}
