package ru.tesla1402.telegramquestionanswerbot.bot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@Validated
@ConfigurationProperties("app.telegram")
public record TelegramConfigurationProperties(@NotBlank String token, @NotEmpty Set<Long> admins, @NotNull Text text) {
    @Validated
    public record Text(@NotNull Command command) {
        @Validated
        public record Command(@NotBlank String start) {
        }
    }
}
