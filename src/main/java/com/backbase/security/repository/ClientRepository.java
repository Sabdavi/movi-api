package com.backbase.security.repository;

import com.backbase.security.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByClientId(String clientId);

    @Query("SELECT c.clientSecret FROM Client c WHERE c.clientId = :clientId")
    String findClientSecretByClientId(@Param("clientId") String clientId);
}
