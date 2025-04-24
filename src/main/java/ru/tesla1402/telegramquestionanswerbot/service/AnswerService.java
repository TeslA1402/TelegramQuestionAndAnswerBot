package ru.tesla1402.telegramquestionanswerbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tesla1402.telegramquestionanswerbot.mapper.AnswerMapper;
import ru.tesla1402.telegramquestionanswerbot.model.Answer;
import ru.tesla1402.telegramquestionanswerbot.model.Client;
import ru.tesla1402.telegramquestionanswerbot.model.Question;
import ru.tesla1402.telegramquestionanswerbot.repository.AnswerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final AnswerMapper answerMapper;
    private final ClientService clientService;
    private final QuestionService questionService;

    @Transactional
    public void saveAnswer(Long clientId, Long questionId, String text) {
        Client client = clientService.getRequiredById(clientId);
        Question question = questionService.getRequiredById(questionId);
        answerRepository.save(answerMapper.answer(client, question, text));
    }

    public List<Answer> getAllByClientId(Long clientId) {
        return answerRepository.findByClientIdOrderByCreateDateDesc(clientId);
    }
}
