package com.paymybuddy.repository;

import java.util.Optional;
import com.paymybuddy.model.AuthProvider;
import com.paymybuddy.model.OAuth2Provider;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuth2ProviderRepository extends JpaRepository<OAuth2Provider, Long> {

    @Query("select op from OAuth2Provider as op "
            + "join op.user as u "
            + "where u.email =?1 and op.registrationClient=?2")
    Optional<OAuth2Provider> findByEmail(String email,AuthProvider provider);

}
