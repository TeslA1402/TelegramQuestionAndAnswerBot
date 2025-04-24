package ru.tesla1402.telegramquestionanswerbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tesla1402.telegramquestionanswerbot.model.Client;
import ru.tesla1402.telegramquestionanswerbot.repository.ClientRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    @Transactional
    public Client save(Client client) {
        return clientRepository.findByChatId(client.getChatId()).orElseGet(() -> clientRepository.save(client));
    }

    @Transactional(readOnly = true)
    public Client getRequiredById(Long clientId) {
        return clientRepository.findById(clientId).orElseThrow(() -> new IllegalStateException("Client not found"));
    }

    public List<Client> getAll() {
        return clientRepository.findAll();
    }

    public boolean existsById(Long clientId) {
        return clientRepository.existsById(clientId);
    }

    public Optional<Client> findById(Long clientId) {
        return clientRepository.findById(clientId);
    }
}
