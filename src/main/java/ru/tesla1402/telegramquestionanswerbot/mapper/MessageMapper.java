package ru.tesla1402.telegramquestionanswerbot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.tesla1402.telegramquestionanswerbot.model.Client;
import ru.tesla1402.telegramquestionanswerbot.model.Message;
import ru.tesla1402.telegramquestionanswerbot.model.MessageType;
import ru.tesla1402.telegramquestionanswerbot.model.Question;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MessageMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    Message message(Client client, Question question, Integer messageId, MessageType type);
}
