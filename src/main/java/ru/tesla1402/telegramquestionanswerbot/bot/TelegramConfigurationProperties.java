package ru.tesla1402.telegramquestionanswerbot.bot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@Validated
@ConfigurationProperties("app.telegram")
public record TelegramConfigurationProperties(@NotBlank String token, @NotEmpty Set<Long> admins) {
}
