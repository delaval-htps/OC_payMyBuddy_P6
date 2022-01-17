package com.paymybuddy.repository;

import java.util.Optional;

import com.paymybuddy.model.OAuth2Provider;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OAuth2ProviderRepository extends JpaRepository<OAuth2Provider, Long> {

    @Query("select oap from OAuth2Provider as oap "
            + "join   oap.user as u "
            + "join  u.roles as r "
            + "where u.email =?1")
    Optional<OAuth2Provider> findByEmail(String email);

}
