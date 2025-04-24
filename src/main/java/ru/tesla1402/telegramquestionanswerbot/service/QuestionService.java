package ru.tesla1402.telegramquestionanswerbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tesla1402.telegramquestionanswerbot.mapper.QuestionMapper;
import ru.tesla1402.telegramquestionanswerbot.model.Question;
import ru.tesla1402.telegramquestionanswerbot.repository.QuestionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

    @Transactional(readOnly = true)
    public Question getRequiredById(Long questionId) {
        return questionRepository.findById(questionId).orElseThrow(() -> new IllegalStateException("Question not found"));
    }

    @Transactional(readOnly = true)
    public List<Question> getAll() {
        return questionRepository.findAll();
    }

    @Transactional
    public void save(String text) {
        questionRepository.save(questionMapper.question(text));
    }

    public boolean existsById(Long questionId) {
        return questionRepository.existsById(questionId);
    }
}
