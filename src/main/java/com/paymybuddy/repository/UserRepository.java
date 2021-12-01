package com.paymybuddy.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.paymybuddy.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @Query(value = "select u from User as u "
      + "left join u.applicationIdentifier as uai "
      + "where u.firstName =?1 and u.lastName=?2 and uai.email=?3")
  Optional<User> findbyOauth2Information(String firstName, String lastName, String email);


}
