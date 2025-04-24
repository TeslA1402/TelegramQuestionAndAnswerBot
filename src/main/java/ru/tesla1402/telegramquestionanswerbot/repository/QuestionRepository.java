package ru.tesla1402.telegramquestionanswerbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tesla1402.telegramquestionanswerbot.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
