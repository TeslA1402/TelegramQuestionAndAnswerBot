package ru.tesla1402.telegramquestionanswerbot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import ru.tesla1402.telegramquestionanswerbot.model.Client;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientMapper {
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chatId", source = "chat.id")
    @Mapping(target = "userName", source = "chat.userName")
    @Mapping(target = "firstName", source = "chat.firstName")
    @Mapping(target = "lastName", source = "chat.lastName")
    Client client(Chat chat);
}
