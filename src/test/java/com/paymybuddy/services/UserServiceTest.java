package com.paymybuddy.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.UserService;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
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
public class UserServiceTest {


    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService classUnderTest;

    @Test
    @Order(1)
    void findUserById_whenNotExistedUser_thenReturnUser() {

        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Optional<User> optUser = classUnderTest.findById(1L);

        assertTrue(optUser.isEmpty());
    }

    @Test
    @Order(2)
    void findUserById_whenExistedUser_thenReturnUser() {

        User user = new User();
        user.setId(1L);
        user.setLastName("mockUser");
        user.setFirstName("mocker");
        user.setEmail("mock@gmail.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> optUser = classUnderTest.findById(1L);

        assertThat(optUser).contains(user);
    }

    @Test
    @Order(3)
    void findUserByEmail_whenExistedUser_thenReturnUser() {

        User user = new User();
        user.setId(1L);
        user.setLastName("mockUser");
        user.setFirstName("mocker");
        user.setEmail("mock@gmail.com");
        when(userRepository.findByEmail("mock@gmail.com")).thenReturn(Optional.of(user));

        Optional<User> optUser = classUnderTest.findByEmail("mock@gmail.com");

        assertThat(optUser).contains(user);
    }

    @Test
    @Order(4)
    void findUserByEmail_whenNotExistedUser_thenReturnEmptyOptional() {

        when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        Optional<User> optUser = classUnderTest.findByEmail("mock@gmail.com");

        assertTrue(optUser.isEmpty());
    }

    @Test
    @Order(5)
    void findConnectedUserByEmail_whenExistedUser_thenReturnListOfUsers() {

        List<User> connectedUsers = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setLastName("mockUser");
        user1.setFirstName("mocker");
        user1.setEmail("mock@gmail.com");
        User user2 = new User();
        user2.setId(1L);
        user2.setLastName("mockUser2");
        user2.setFirstName("mocker2");
        user2.setEmail("mock2@gmail.com");

        connectedUsers.add(user1);
        connectedUsers.add(user2);

        when(userRepository.findConnectedUserByEmail("mock@gmail.com")).thenReturn(connectedUsers);

        List<User> users = classUnderTest.findConnectedUserByEmail("mock@gmail.com");

        assertThat(users).containsExactlyInAnyOrder(user1, user2);
    }

    @Test
    @Order(6)
    void findConnectedUserByEmail_whenNotExistedUser_thenReturnEmptyList() {

        when(userRepository.findConnectedUserByEmail(Mockito.anyString())).thenReturn(new ArrayList<>());

        List<User> users = classUnderTest.findConnectedUserByEmail("mock@gmail.com");

        assertTrue(users.isEmpty());
    }


    @Test
    @Order(7)
    void saveUser_whenExistedUser_thenReturnUser() {

        User user1 = new User();
        user1.setId(1L);
        user1.setLastName("mockUser");
        user1.setFirstName("mocker");
        user1.setEmail("mock@gmail.com");


        when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);

        User savedUser = classUnderTest.save(user1);

        assertThat(savedUser).isEqualTo(user1);
    }

    @Test
    @Order(7)
    void saveUser_whenUserNull_thenReturnNull() {

        User user = null;

        when(userRepository.save(null)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> {
            classUnderTest.save(user);
        });

    }
}
