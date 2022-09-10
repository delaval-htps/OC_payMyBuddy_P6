package com.paymybuddy.repository;

import com.paymybuddy.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
/**
 * Repository extends {@link JpaRepository} to acces to {@link User}
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String username);

  @Query(
    value = "select cu from User u join u.connectionUsers cu where u.email=?1"
  )
  List<User> findConnectedUserByEmail(String email);
}
