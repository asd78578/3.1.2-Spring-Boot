package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserServiceImp(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
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

//    @PostConstruct
//    @Transactional
//    public void addDefaultUsers() {
//        if (userRepository.count() > 0) return;
//
//        Role userRole = roleRepository.findById(1)
//                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
//        Role adminRole = roleRepository.findById(2)
//                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));
//
//        User user = new User();
//        user.setName("Ron");
//        user.setSurname("Uizli");
//        user.setAge((byte) 55);
//        user.setUsername("user@mail.com");
//        user.setPassword(passwordEncoder.encode("12345"));
//        user.setRoles(Set.of(userRole));
//
//        User admin = new User();
//        admin.setName("Garri");
//        admin.setSurname("Potter");
//        admin.setAge((byte) 56);
//        admin.setUsername("admin@mail.com");
//        admin.setPassword(passwordEncoder.encode("admin"));
//        admin.setRoles(Set.of(userRole, adminRole));
//
//        userRepository.save(user);
//        userRepository.save(admin);
//
//        System.out.println(" Default users created.");
//    }

    @PostConstruct
    public void testPasswords() {
        System.out.println(passwordEncoder.matches("12345", "$2a$10$E1pKkn13HCrZKZCCmKp3K.9pE0DtaVJpsKVD.KINOrZVgmzVqvvf2")); // должно быть true
        System.out.println(passwordEncoder.matches("admin", "$2a$10$vXQtSWeQSozIrCW1ysb3aeOtJdguakFxvzHjFZ1P1Tk0t3bYhvKq2")); // должно быть true
    }

    @PostConstruct
    public void generatePasswords() {
        System.out.println("user@mail.com -> " + passwordEncoder.encode("12345"));
        System.out.println("admin@mail.com -> " + passwordEncoder.encode("admin"));
    }


    // Метод для добавления дефолтных пользователей при старте
    @Override
    @Transactional
    public void addUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("User with username " + user.getUsername() + " already exists");
        }
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
        // Загружаем существующего пользователя
        User existingUser = getUser(user.getId());

        // Обновляем данные
        existingUser.setName(user.getName());
        existingUser.setSurname(user.getSurname());
        existingUser.setAge(user.getAge());
        existingUser.setUsername(user.getUsername());

        // Обновляем роли корректно: загружаем из базы по ID
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<Role> rolesFromDb = user.getRoles().stream()
                    .map(r -> roleRepository.findById(r.getId())
                            .orElseThrow(() -> new RuntimeException("Role not found: " + r.getId())))
                    .collect(Collectors.toSet());
            existingUser.setRoles(rolesFromDb);
        }

        // Обновляем пароль, если он изменился
        String newPassword = user.getPassword();
        if (shouldUpdatePassword(newPassword, existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(newPassword));
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

