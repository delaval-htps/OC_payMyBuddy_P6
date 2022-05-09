package com.paymybuddy.repository;

import java.util.List;
import com.paymybuddy.model.ApplicationTransaction;
import com.paymybuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationTransactionRepository extends JpaRepository<ApplicationTransaction, Long> {

    @Query(value = "select at from ApplicationTransaction at where at.sender= ?1 or at.receiver=?1")
    List<ApplicationTransaction> findByUser(User sender);

    List<ApplicationTransaction> findBySender(User sender);

    List<ApplicationTransaction> findByReceiver(User reciever);

}
