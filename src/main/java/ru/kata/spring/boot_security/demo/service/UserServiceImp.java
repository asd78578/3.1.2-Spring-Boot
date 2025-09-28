package ru.kata.spring.boot_security.demo.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class UserServiceImp implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImp(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username); // Используем репозиторий, а не этот же метод
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersList() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional
    public void addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void editUser(User user) {
        // Получаем текущего пользователя из БД
        User existingUser = getUser(user.getId());

        // Сохраняем старый зашифрованный пароль
        String currentEncryptedPassword = existingUser.getPassword();

        // Обновляем основные поля
        existingUser.setName(user.getName());
        existingUser.setSurname(user.getSurname());
        existingUser.setAge(user.getAge());
        existingUser.setUsername(user.getUsername());
        existingUser.setRoles(user.getRoles());

        // Умная обработка пароля
        String newPassword = user.getPassword();
        if (shouldUpdatePassword(newPassword, currentEncryptedPassword)) {
            // Пароль изменился - шифруем новый
            existingUser.setPassword(passwordEncoder.encode(newPassword));
            System.out.println("🔐 Пароль обновлен для пользователя: " + user.getUsername());
        } else {
            // Пароль не менялся - оставляем старый
            existingUser.setPassword(currentEncryptedPassword);
            System.out.println("⚡ Пароль не изменен для пользователя: " + user.getUsername());
        }

        userRepository.save(existingUser);
    }

    // Проверяем, нужно ли обновлять пароль

    private boolean shouldUpdatePassword(String newPassword, String currentEncryptedPassword) {
        // 1. Если пароль null или пустой - не обновляем
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }

        // 2. Если пароль уже зашифрован (скорее всего, это старый пароль из формы) - не обновляем
        if (isPasswordEncrypted(newPassword)) {
            return false;
        }

        // 3. Если новый пароль совпадает с текущим (после проверки) - не обновляем
        if (passwordEncoder.matches(newPassword, currentEncryptedPassword)) {
            return false;
        }

        // 4. Во всех остальных случаях - пароль изменился, нужно обновить
        return true;
    }

    // Проверяем, является ли пароль уже зашифрованным

    private boolean isPasswordEncrypted(String password) {
        return password.startsWith("$2a$") || password.startsWith("$2b$");
    }
}

