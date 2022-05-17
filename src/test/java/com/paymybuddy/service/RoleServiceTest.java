package com.paymybuddy.service;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import com.paymybuddy.model.Role;
import com.paymybuddy.repository.RoleRepository;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks

    private RoleService cut;

    @Test
    @Order(1)
    void findByName_whenNameIsRegistred_thenReturnRoleAssigned() {

        Role mockRole = new Role();
        mockRole.setName("TEST");
        when(roleRepository.findByName(Mockito.anyString())).thenReturn(mockRole);

        Role existedRole = cut.findByName("TEST");

        assertThat(existedRole).isEqualTo(mockRole);
    }

    @Test
    @Order(2)
    void findByName_whenNameNotRegistred_thenReturnNull() {

        when(roleRepository.findByName(Mockito.anyString())).thenReturn(null);

        Role existedRole = cut.findByName("TEST");

        assertThat(existedRole).isNull();
    }
}
