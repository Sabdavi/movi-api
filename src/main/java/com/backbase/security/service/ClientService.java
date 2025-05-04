package com.backbase.security.service;

import com.backbase.security.entity.Client;
import com.backbase.security.repository.ClientRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client registerClient() {
        String clientId = UUID.randomUUID().toString();
        String clientSecret = UUID.randomUUID().toString();
        Client client = Client.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
        clientRepository.save(client);
        return client;
    }

    public boolean isValid(String clientId, String clientSecret) {
        return clientRepository.findByClientId(clientId)
                .map(client -> client.getClientSecret().equals(clientSecret))
                .orElse(false);
    }

    public String findSecretByClientId(String clientId) {
        return clientRepository.findClientSecretByClientId(clientId);
    }
}
