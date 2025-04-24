package ru.tesla1402.telegramquestionanswerbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tesla1402.telegramquestionanswerbot.model.Client;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByChatId(Long chatId);
}