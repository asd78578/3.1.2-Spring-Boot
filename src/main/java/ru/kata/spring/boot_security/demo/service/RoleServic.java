package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;

public interface RoleServic {
    List<Role> getAllRoles();
    Role getRoleById(Integer id);
    Role getRoleByType(String roleType);
    void saveRole(Role role);
    void deleteRole(Integer id);
    List<Role> getRolesByTypes(List<String> roleTypes);
}
