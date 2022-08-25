package com.paymybuddy.security.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.paymybuddy.model.Role;
import com.paymybuddy.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService cut;

    @Test
    void loadUserByUsername_whenUserNotFound_thenThrowException() {
        String usernameNotFound = "usernameNotFound";

        when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            cut.loadUserByUsername(usernameNotFound);
        });
    }

    @Test
    void loadUserByUsername_whenUserIsFound_thenReturnUserDetails() {

        com.paymybuddy.model.User existedUser = new com.paymybuddy.model.User();
        existedUser.setEmail("test@gmail.com");
        existedUser.setLastName("test_lastname");
        existedUser.setFirstName("testFirstname");
        existedUser.setPassword("test");
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setName("USER_ROLE");
        role.getUsers().add(existedUser);
        roles.add(role);
        existedUser.setRoles(roles);

        when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

        UserDetails loadUserByUsername = cut.loadUserByUsername(existedUser.getEmail());

        assertThat(loadUserByUsername).isNotNull();

    }

}
