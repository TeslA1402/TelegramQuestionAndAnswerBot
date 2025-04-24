package ru.tesla1402.telegramquestionanswerbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tesla1402.telegramquestionanswerbot.model.Answer;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByClientIdOrderByCreateDateDesc(Long id);
}
