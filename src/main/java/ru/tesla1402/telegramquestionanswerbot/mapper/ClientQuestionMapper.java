package ru.tesla1402.telegramquestionanswerbot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.tesla1402.telegramquestionanswerbot.model.Client;
import ru.tesla1402.telegramquestionanswerbot.model.ClientQuestion;
import ru.tesla1402.telegramquestionanswerbot.model.Question;

import java.time.OffsetDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientQuestionMapper {
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    ClientQuestion clientQuestion(Client client, Question question, Integer periodHours, OffsetDateTime nextSendDate);
}
