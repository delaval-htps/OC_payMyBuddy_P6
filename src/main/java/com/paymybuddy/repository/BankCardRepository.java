package com.paymybuddy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.paymybuddy.model.BankCard;
import com.paymybuddy.model.User;

@Repository
public interface BankCardRepository extends CrudRepository<BankCard, Long> {
    @Query(value = "select bc from BankCard bc  join fetch bc.user u where u=?1")
    public Optional<BankCard> findByUser(User user);
}
