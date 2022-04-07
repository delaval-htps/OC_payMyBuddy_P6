package com.paymybuddy.repository;

import java.util.List;
import com.paymybuddy.model.ApplicationTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ApplicationTransactionRepository extends JpaRepository<ApplicationTransaction, Long> {

    @Query(value = "select at from ApplicationTransaction at where at.userId = ?1")
    List<ApplicationTransaction> findByUserId(Long id);

}
