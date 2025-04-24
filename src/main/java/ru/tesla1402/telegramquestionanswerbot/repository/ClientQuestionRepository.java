package ru.tesla1402.telegramquestionanswerbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tesla1402.telegramquestionanswerbot.model.ClientQuestion;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public interface ClientQuestionRepository extends JpaRepository<ClientQuestion, Long> {
    Set<ClientQuestion> findAllByNextSendDateBefore(OffsetDateTime nextSendDateBefore);

    List<ClientQuestion> findAllByClientId(Long clientId);

    boolean existsByClientIdAndQuestionId(Long clientId, Long questionId);

    void deleteByClientIdAndQuestionId(Long clientId, Long questionId);
}
