package com.paymybuddy.repository;

import java.util.List;
import java.util.Optional;

import com.paymybuddy.model.ApplicationTransaction;
import com.paymybuddy.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
/**
 * Repository extends {@link JpaRepository} to acces to {@link ApplicationTransaction}
 */
@Repository
public interface ApplicationTransactionRepository extends JpaRepository<ApplicationTransaction, Long> {

    @Query(value = "select at from ApplicationTransaction at where at.sender= ?1 or at.receiver=?1")
    List<ApplicationTransaction> findByUser(User sender);

    @Query(value = "select at from ApplicationTransaction at where at.sender= ?1")
    List<ApplicationTransaction> findBySender(User sender);

    @Query(value = "select at from ApplicationTransaction at where at.receiver=?1")
    List<ApplicationTransaction> findByReceiver(User reciever);
    
    @Query(value = "select at from ApplicationTransaction at where at.sender= ?1")
    Optional<Page<ApplicationTransaction>> findAllBySender(User user, Pageable pageable);
}
