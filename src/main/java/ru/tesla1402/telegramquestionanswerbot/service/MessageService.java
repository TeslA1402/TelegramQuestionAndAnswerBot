package ru.tesla1402.telegramquestionanswerbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tesla1402.telegramquestionanswerbot.mapper.MessageMapper;
import ru.tesla1402.telegramquestionanswerbot.model.Client;
import ru.tesla1402.telegramquestionanswerbot.model.Message;
import ru.tesla1402.telegramquestionanswerbot.model.MessageType;
import ru.tesla1402.telegramquestionanswerbot.model.Question;
import ru.tesla1402.telegramquestionanswerbot.repository.MessageRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final ClientService clientService;
    private final QuestionService questionService;

    @Transactional
    public void saveQuestion(Long clientId, Long questionId, Integer messageId) {
        Client client = clientService.getRequiredById(clientId);
        Question question = questionService.getRequiredById(questionId);
        messageRepository.save(messageMapper.message(client, question, messageId, MessageType.QUESTION));
    }

    @Transactional(readOnly = true)
    public Optional<Message> findByClientIdAndMessageId(Long clientId, Integer messageId) {
        return messageRepository.findByClientIdAndMessageId(clientId, messageId);
    }

    public Optional<Message> findFirstByClientIdOrderByCreateDateDesc(Long clientId) {
        return messageRepository.findFirstByClientIdOrderByCreateDateDesc(clientId);
    }
}
