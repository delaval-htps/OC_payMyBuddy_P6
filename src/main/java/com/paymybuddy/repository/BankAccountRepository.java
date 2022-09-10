package com.paymybuddy.repository;

import java.util.Optional;
import com.paymybuddy.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
/**
 * Repository extends {@link JpaRepository} to acces to {@link BankAccount}
 */
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    @Query(value = "select ba from BankAccount ba where ba.iban=?1")
    Optional<BankAccount> findByIban(String iban);

    @Query(value= "select ba from BankAccount as ba left join fetch ba.users as u where u.email=?1")
    Optional<BankAccount> findByEmail(String email);
}
