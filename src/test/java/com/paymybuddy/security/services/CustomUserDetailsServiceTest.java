package com.paymybuddy.security.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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

        when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

        UserDetails loadUserByUsername = cut.loadUserByUsername(existedUser.getEmail());

        assertThat(loadUserByUsername).isNotNull();

    }

}
