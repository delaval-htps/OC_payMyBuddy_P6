package com.paymybuddy.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.model.BankCard;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankCardRepository;

@Service
public class BankCardService {

    @Autowired
    private BankCardRepository bankCardRepository;

    public Optional<BankCard> findByUser(User user) {
        return bankCardRepository.findByUser(user);
    }

    public Optional<BankCard> findById(Long id) {
        return bankCardRepository.findById(id);
    }

    public BankCard save(BankCard card) {
        return bankCardRepository.save(card);
    }

    public void delete(BankCard card) {
        if (card != null) {
            bankCardRepository.delete(card);
        }
    }
}
