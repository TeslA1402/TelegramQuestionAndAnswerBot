package ru.tesla1402.telegramquestionanswerbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tesla1402.telegramquestionanswerbot.model.Message;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findByClientIdAndMessageId(Long clientId, Integer messageId);

    Optional<Message> findFirstByClientIdOrderByCreateDateDesc(Long id);
}
