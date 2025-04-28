package ru.tesla1402.telegramquestionanswerbot.bot;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.tesla1402.telegramquestionanswerbot.mapper.ClientMapper;
import ru.tesla1402.telegramquestionanswerbot.model.Answer;
import ru.tesla1402.telegramquestionanswerbot.model.Client;
import ru.tesla1402.telegramquestionanswerbot.model.ClientQuestion;
import ru.tesla1402.telegramquestionanswerbot.model.Question;
import ru.tesla1402.telegramquestionanswerbot.service.AnswerService;
import ru.tesla1402.telegramquestionanswerbot.service.ClientQuestionService;
import ru.tesla1402.telegramquestionanswerbot.service.ClientService;
import ru.tesla1402.telegramquestionanswerbot.service.MessageService;
import ru.tesla1402.telegramquestionanswerbot.service.QuestionService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramReminderBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private static final String ADMIN_MESSAGE = """
            🤖 *Панель администратора*
            
            /addQuestion <вопрос> — добавить вопрос
            /questions — список вопросов
            /clients — список клиентов
            /addQuestionToClient <ИД клиента> <ИД вопроса> <Периодичность в часах> — добавить вопрос пользователю, отправляемый с периодичностью
            /removeQuestionFromClient <ИД клиента> <ИД вопроса> — удалить вопрос пользователю
            /clientQuestions <ИД клиента> — получить вопросы назначенные на клиента
            /responses <ИД клиента> — ответы пользователя
            """;
    private static final Pattern ADD_QUESTION_PATTERN = Pattern.compile("^/addQuestion (.*)$");
    private static final Pattern ADD_QUESTION_TO_CLIENT_PATTERN = Pattern.compile("^/addQuestionToClient (\\d+) (\\d+) (\\d+)$");
    private static final Pattern CLIENT_QUESTIONS_PATTERN = Pattern.compile("^/clientQuestions (\\d+)$");
    private static final Pattern REMOVE_QUESTION_FROM_CLIENT_PATTERN = Pattern.compile("^/removeQuestionFromClient (\\d+) (\\d+)$");
    private static final Pattern RESPONSES_PATTERN = Pattern.compile("^/responses (\\d+)$");

    private static final String ADD_QUESTION_MESSAGE = "Вопрос добавлен";
    private static final String EXCEPTION_MESSAGE = "Ошибка обработки команды";
    private static final String CLIENT_NOT_FOUND_MESSAGE = "Клиент не найден";
    private static final String QUESTION_NOT_FOUND_MESSAGE = "Вопрос не найден";
    private static final String CLIENT_QUESTION_NOT_FOUND_MESSAGE = "Вопрос клиенту не найден";
    private static final String ADD_CLIENT_QUESTION_MESSAGE = "Вопрос добавлен пользователю";
    private static final String INCORRECT_PERIOD_HOURS_MESSAGE = "Некорректный период в часах";
    private static final String CLIENT_QUESTION_DELETED_MESSAGE = "Вопрос клиенту удалён";
    private final TelegramConfigurationProperties telegramConfigurationProperties;
    private final TelegramClient telegramClient;
    private final ClientService clientService;
    private final ClientMapper clientMapper;
    private final ClientQuestionService clientQuestionService;
    private final MessageService messageService;
    private final AnswerService answerService;
    private final QuestionService questionService;

    @Override
    public String getBotToken() {
        return telegramConfigurationProperties.token();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (!update.hasMessage()) {
            return;
        }
        Message message = update.getMessage();
        Client client = clientService.save(clientMapper.client(message.getChat()));
        if (message.isCommand()) {
            processCommand(message);
        } else {
            processAnswer(client, message);
        }
    }

    private void processAnswer(Client client, Message message) {
        if (message.hasText()) {
            Optional<ru.tesla1402.telegramquestionanswerbot.model.Message> messageOptional = Optional.empty();
            if (message.isReply() && message.getReplyToMessage().hasText()) {
                messageOptional = messageService.findByClientIdAndMessageId(client.getId(),
                        message.getReplyToMessage().getMessageId());
            }
            if (messageOptional.isEmpty()) {
                messageOptional = messageService.findFirstByClientIdOrderByCreateDateDesc(client.getId());
            }
            messageOptional.ifPresent(m -> answerService.saveAnswer(client.getId(), m.getQuestion().getId(),
                    message.getText()));
        }
    }

    private void processCommand(Message message) {
        String text = message.getText();
        Long chatId = message.getChatId();
        if (text.equals("/start")) {
            sendMessage(chatId, telegramConfigurationProperties.text().command().start());
        } else if (telegramConfigurationProperties.admins().contains(chatId)) {
            if (text.equals("/admin")) {
                sendMessage(chatId, ADMIN_MESSAGE);
            } else if (text.startsWith("/questions")) {
                questions(chatId);
            } else if (text.startsWith("/clients")) {
                clients(chatId);
            } else if (text.matches(ADD_QUESTION_PATTERN.pattern())) {
                addQuestion(text, chatId);
            } else if (text.matches(ADD_QUESTION_TO_CLIENT_PATTERN.pattern())) {
                addQuestionToClient(text, chatId);
            } else if (text.matches(CLIENT_QUESTIONS_PATTERN.pattern())) {
                clientQuestions(text, chatId);
            } else if (text.matches(REMOVE_QUESTION_FROM_CLIENT_PATTERN.pattern())) {
                removeQuestionFromClient(text, chatId);
            } else if (text.matches(RESPONSES_PATTERN.pattern())) {
                responses(text, chatId);
            } else {
                sendMessage(chatId, EXCEPTION_MESSAGE);
            }
        }
    }

    private void responses(String text, Long chatId) {
        Matcher matcher = RESPONSES_PATTERN.matcher(text);
        if (matcher.find()) {
            long clientId = Long.parseLong(matcher.group(1));
            if (!clientService.existsById(clientId)) {
                sendMessage(chatId, CLIENT_NOT_FOUND_MESSAGE);
            } else {
                List<Answer> answers = answerService.getAllByClientId(clientId);
                StringBuilder sb = new StringBuilder();
                sb.append("Список вопросов и ответов пользователя, с сортировкой от новых к старым:\n\n");
                answers.forEach(a -> sb.append(a.getQuestion().getId()).append(". ")
                        .append(a.getQuestion().getText()).append(" | ").append(a.getText()).append("\n"));
                sendMessage(chatId, sb.toString());
            }
        }
    }

    private void removeQuestionFromClient(String text, Long chatId) {
        Matcher matcher = REMOVE_QUESTION_FROM_CLIENT_PATTERN.matcher(text);
        if (matcher.find()) {
            long clientId = Long.parseLong(matcher.group(1));
            long questionId = Long.parseLong(matcher.group(2));
            if (!clientQuestionService.existsByClientIdAndQuestionId(clientId, questionId)) {
                sendMessage(chatId, CLIENT_QUESTION_NOT_FOUND_MESSAGE);
            } else {
                clientQuestionService.delete(clientId, questionId);
                sendMessage(chatId, CLIENT_QUESTION_DELETED_MESSAGE);
            }
        }
    }

    private void clientQuestions(String text, Long chatId) {
        Matcher matcher = CLIENT_QUESTIONS_PATTERN.matcher(text);
        if (matcher.find()) {
            long clientId = Long.parseLong(matcher.group(1));
            Optional<Client> clientOptional = clientService.findById(clientId);
            clientOptional.ifPresentOrElse(client -> {
                List<ClientQuestion> clientQuestions = clientQuestionService.getAllByClientId(clientId);
                StringBuilder sb = new StringBuilder();
                sb.append("Список вопросов для клиента ").append(getClientInfo(client)).append(" с периодом в часах:\n\n");
                clientQuestions.forEach(c -> sb.append(c.getQuestion().getId()).append(". ")
                        .append(c.getQuestion().getText()).append(" | ").append(c.getPeriodHours()).append("\n"));
                sendMessage(chatId, sb.toString());
            }, () -> sendMessage(chatId, CLIENT_NOT_FOUND_MESSAGE));
        }
    }

    private void addQuestionToClient(String text, Long chatId) {
        Matcher matcher = ADD_QUESTION_TO_CLIENT_PATTERN.matcher(text);
        if (matcher.find()) {
            long clientId = Long.parseLong(matcher.group(1));
            long questionId = Long.parseLong(matcher.group(2));
            int periodHours = Integer.parseInt(matcher.group(3));
            if (!clientService.existsById(clientId)) {
                sendMessage(chatId, CLIENT_NOT_FOUND_MESSAGE);
            } else if (!questionService.existsById(questionId)) {
                sendMessage(chatId, QUESTION_NOT_FOUND_MESSAGE);
            } else if (periodHours < 1) {
                sendMessage(chatId, INCORRECT_PERIOD_HOURS_MESSAGE);
            } else {
                clientQuestionService.save(clientId, questionId, periodHours);
                sendMessage(chatId, ADD_CLIENT_QUESTION_MESSAGE);
            }
        }
    }

    private void addQuestion(String text, Long chatId) {
        Matcher matcher = ADD_QUESTION_PATTERN.matcher(text);
        if (matcher.find()) {
            questionService.save(matcher.group(1).trim());
            sendMessage(chatId, ADD_QUESTION_MESSAGE);
        }
    }

    private void clients(Long chatId) {
        List<Client> clients = clientService.getAll();
        StringBuilder sb = new StringBuilder();
        sb.append("Список клиентов:\n\n");
        clients.forEach(c -> sb.append(c.getId()).append(". ").append(getClientInfo(c)).append("\n"));
        sendMessage(chatId, sb.toString());
    }

    private String getClientInfo(Client client) {
        return "@%s (%s %s)".formatted(client.getUserName(), client.getFirstName(), client.getLastName());
    }

    private void questions(Long chatId) {
        List<Question> questions = questionService.getAll();
        StringBuilder sb = new StringBuilder();
        sb.append("Список вопросов:\n\n");
        questions.forEach(q -> sb.append(q.getId()).append(". ").append(q.getText()).append("\n"));
        sendMessage(chatId, sb.toString());
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void sendQuestions() {
        log.info("Sending questions");
        Set<ClientQuestion> clientQuestions = clientQuestionService.getQuestionsToSend();
        log.info("Found {} questions to send", clientQuestions.size());
        clientQuestions.forEach(clientQuestion -> {
            Client client = clientQuestion.getClient();
            Question question = clientQuestion.getQuestion();
            Message message = sendMessage(client.getChatId(), question.getText());
            messageService.saveQuestion(client.getId(), question.getId(), message.getMessageId());
            clientQuestionService.setNextSendDate(clientQuestion.getId());
        });
    }

    @SneakyThrows
    private Message sendMessage(Long chatId, String text) {
        SendMessage.SendMessageBuilder<?, ?> builder = SendMessage.builder()
                .chatId(chatId)
                .text(text);
        return telegramClient.execute(builder.build());
    }
}