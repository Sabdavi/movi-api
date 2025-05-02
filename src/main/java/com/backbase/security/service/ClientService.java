package com.backbase.security.service;

import com.backbase.security.entity.Client;
import com.backbase.security.repository.ClientRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ClientService {

    private final ClientRepository clientRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client registerClient() {
        String clientId = UUID.randomUUID().toString();
        String clientSecret = UUID.randomUUID().toString();
        String hashed = passwordEncoder.encode(clientSecret);
        Client client = new Client(clientId, hashed);
        clientRepository.save(client);
        return new Client(clientId, clientSecret);
    }

    public boolean isValid(String clientId, String rawSecret) {
        return clientRepository.findByClientId(clientId)
                .map(client -> passwordEncoder.matches(rawSecret, client.getHashedSecret()))
                .orElse(false);
    }
}
