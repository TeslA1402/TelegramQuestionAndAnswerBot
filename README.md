# Telegram Question Answer Bot

## Описание проекта

Telegram Question Answer Bot - это приложение для автоматизации процесса опроса пользователей через Telegram.
Бот отправляет вопросы пользователям с заданной периодичностью, собирает и сохраняет их ответы для последующего анализа
администраторами.

## Технологии

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Liquibase (для миграций базы данных)
- Telegram Bot API
- Lombok
- Gradle

## Функциональность

### Для пользователей

- Получение вопросов с заданной периодичностью
- Ответ на вопросы через функцию ответа в Telegram
- Автоматическое сохранение ответов

### Для администраторов

- Управление вопросами (добавление, просмотр)
- Управление пользователями (просмотр списка)
- Назначение вопросов пользователям с указанием периодичности
- Удаление вопросов у пользователей
- Просмотр ответов пользователей

## Настройка и запуск

### Предварительные требования

- JDK 17 или выше
- PostgreSQL
- Telegram Bot Token (получается через @BotFather в Telegram)

### Настройка базы данных

1. Настройки подключения к базе данных указываются в файле `application.yml`

### Настройка бота

1. Создайте бота в Telegram через @BotFather и получите токен
2. Укажите токен и ID администраторов в файле `application.yml` или через переменные окружения:
    - `APP_TELEGRAM_TOKEN` - токен бота
    - `APP_TELEGRAM_ADMINS` - список ID администраторов (через запятую)
3. При необходимости настройте текст приветственного сообщения для команды `/start` в файле `application.yml` в разделе
   `app.telegram.text.command.start` или переменную окружения `APP_TELEGRAM_TEXT_COMMAND_START`

#### Пример конфигурации в application.yml

```yaml
app:
  telegram:
    token: your_bot_token_here
    admins: 123456789,987654321
    text:
      command:
        start: Привет! 👋 Это ваш помощник. Я буду напоминать о наших задачах и присылать вопросы – отвечать можно в любое время. Ваши ответы сохраняются и будут видны только вашему куратору, чтобы вы могли работать ещё эффективнее. Готовы начать? 😊
```

### Запуск приложения

```bash
./gradlew bootRun
```

Или с указанием переменных окружения:

```bash
APP_TELEGRAM_TOKEN=your_token APP_TELEGRAM_ADMINS=123456789,987654321 ./gradlew bootRun
```

## Использование

### Команды для пользователей

- `/start` - начать взаимодействие с ботом

### Команды для администраторов

- `/admin` - показать панель администратора
- `/addQuestion <вопрос>` - добавить новый вопрос
- `/questions` - показать список всех вопросов
- `/clients` - показать список всех клиентов
- `/addQuestionToClient <ИД клиента> <ИД вопроса> <Периодичность в часах>` - добавить вопрос пользователю с указанной
  периодичностью
- `/removeQuestionFromClient <ИД клиента> <ИД вопроса>` - удалить вопрос у пользователя
- `/clientQuestions <ИД клиента>` - показать вопросы, назначенные пользователю
- `/responses <ИД клиента>` - показать ответы пользователя

## Структура проекта

- `model` - сущности базы данных (Client, Question, Answer, ClientQuestion, Message)
- `repository` - репозитории для работы с базой данных
- `service` - сервисы бизнес-логики
- `mapper` - маппинг между DTO и сущностями
- `bot` - реализация Telegram бота

## Периодическая отправка вопросов

Бот автоматически проверяет каждый час, есть ли вопросы, которые нужно отправить пользователям, основываясь на
настроенной периодичности и текущем времени.
