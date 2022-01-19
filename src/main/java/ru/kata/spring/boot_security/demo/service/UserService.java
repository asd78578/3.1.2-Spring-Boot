package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;

public interface UserService extends UserDetailsService {

    List<User> getUsersList();
    User getUser(Integer id);
    void addUser(User user);
    void deleteUser(Integer id);
    void editUser(User user);
    User findByUsername(String username);

}
