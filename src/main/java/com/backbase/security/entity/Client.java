package com.backbase.security.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "api_clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String clientId;

    @Column(nullable = false)
    private String hashedSecret;

    public Client() {
    }

    public Client(String clientId, String hashedSecret) {
        this.clientId = clientId;
        this.hashedSecret = hashedSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public String getHashedSecret() {
        return hashedSecret;
    }
}
