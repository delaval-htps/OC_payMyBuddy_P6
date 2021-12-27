package com.paymybuddy.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.paymybuddy.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  // need to check with lastName and FirstName in any order because of different
  // process of clientRegistrationId ("dorian delaval or delaval dorian")

  @Query(value = "select u from User as u where u.email=?3 and (( u.firstName =?1 and u.lastName=?2 ) or (u.firstName=?2 and u.lastName=?1 ))")
  Optional<User> findbyOauth2Information(String firstName, String lastName, String email);

  User findByEmail(String username);
}
