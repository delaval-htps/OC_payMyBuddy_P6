package com.paymybuddy.service;

import java.util.Optional;

import com.paymybuddy.model.OAuth2Provider;
import com.paymybuddy.repository.OAuth2ProviderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OAuth2ProviderService {

    @Autowired
    private OAuth2ProviderRepository oAuth2ProviderRepository;

    public Optional<OAuth2Provider> getOAuht2ProviderByEmail(String email) {
        return oAuth2ProviderRepository.findByEmail(email);
    }

}
