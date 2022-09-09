package com.paymybuddy.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.model.BankCard;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankCardRepository;

/**
 * Class of Service for {@link BankCard}
 */
@Service
public class BankCardService {

    @Autowired
    private BankCardRepository bankCardRepository;

    /**
     * retrieve a bankCard of user.
     * 
     * @param user the user that own the bank card
     * @return Optional bankcard of the user if exist or a optional empty.
     */
    public Optional<BankCard> findByUser(User user) {
        return bankCardRepository.findByUser(user);
    }

    /**
     * retrieve a bank card with its given id.
     * 
     * @param id the id of the reserch bank card
     * @return Optional bankcard if exist or a optional empty.
     */
    public Optional<BankCard> findById(Long id) {
        return bankCardRepository.findById(id);
    }

    /**
     * save a bank card if it's not null.
     * 
     * @param card the bank card to save
     * @return the bankCard saved
     * @throws IllegalArgumentException if bankCard is null.
     */
    public BankCard save(BankCard card) {
        return bankCardRepository.save(card);
    }

}
