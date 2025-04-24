package ru.tesla1402.telegramquestionanswerbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tesla1402.telegramquestionanswerbot.mapper.ClientQuestionMapper;
import ru.tesla1402.telegramquestionanswerbot.model.Client;
import ru.tesla1402.telegramquestionanswerbot.model.ClientQuestion;
import ru.tesla1402.telegramquestionanswerbot.model.Question;
import ru.tesla1402.telegramquestionanswerbot.repository.ClientQuestionRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientQuestionService {
    private final ClientQuestionRepository clientQuestionRepository;
    private final ClientQuestionMapper clientQuestionMapper;
    private final ClientService clientService;
    private final QuestionService questionService;

    @Transactional(readOnly = true)
    public Set<ClientQuestion> getQuestionsToSend() {
        return clientQuestionRepository.findAllByNextSendDateBefore(OffsetDateTime.now());
    }

    @Transactional
    public void setNextSendDate(Long clientQuestionId) {
        ClientQuestion clientQuestion = clientQuestionRepository.findById(clientQuestionId)
                .orElseThrow(() -> new IllegalStateException("ClientQuestion not found"));
        clientQuestion.setNextSendDate(OffsetDateTime.now().plusHours(clientQuestion.getPeriodHours()));
    }

    @Transactional
    public void save(Long clientId, Long questionId, Integer periodHours) {
        Client client = clientService.getRequiredById(clientId);
        Question question = questionService.getRequiredById(questionId);
        clientQuestionRepository.save(clientQuestionMapper.clientQuestion(client, question, periodHours, OffsetDateTime.now()));
    }

    @Transactional(readOnly = true)
    public List<ClientQuestion> getAllByClientId(long clientId) {
        return clientQuestionRepository.findAllByClientId(clientId);
    }

    public boolean existsByClientIdAndQuestionId(Long clientId, Long questionId) {
        return clientQuestionRepository.existsByClientIdAndQuestionId(clientId, questionId);
    }

    @Transactional
    public void delete(Long clientId, Long questionId) {
        clientQuestionRepository.deleteByClientIdAndQuestionId(clientId, questionId);
    }
}
