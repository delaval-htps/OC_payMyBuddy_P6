package com.paymybuddy.service;

import com.paymybuddy.model.Role;
import com.paymybuddy.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * Class that contains services for roles.
 */
@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role findByName(String roleName) {
        return roleRepository.findByName(roleName);
    }
}
