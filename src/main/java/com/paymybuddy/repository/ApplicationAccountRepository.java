package com.paymybuddy.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.paymybuddy.model.ApplicationAccount;

/**
 * Repository extends {@link JpaRepository} to acces to {@link ApplicationAccount}
 */
@Repository
public interface ApplicationAccountRepository extends JpaRepository<ApplicationAccount,Long>{

    public Optional<ApplicationAccount> findById(Long id);

    @Query(value = "select a from ApplicationAccount as a join a.user as u where u.email=?1")
    public Optional<ApplicationAccount> findByEmail(String email);
}
