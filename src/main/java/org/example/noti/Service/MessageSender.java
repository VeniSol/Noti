package org.example.noti.Service;

import org.example.noti.Entities.Reminder;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.format.DateTimeFormatter;

public class MessageSender {
    private final TelegramClient telegramClient;

    public MessageSender(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }


    public void sendMessage(Reminder reminder) throws TelegramApiException {
        String chatId = String.valueOf(reminder.getUser().getTelegram());
        String message = generateMessageFromReminder(reminder);
        sendMessage(chatId, message);
    }

    private String generateMessageFromReminder(Reminder reminder) {
        StringBuilder builder = new StringBuilder();
        builder.append(reminder.getTitle()).append("\n\n");

        if (!reminder.getDescription().isEmpty())
            builder.append("Описание: ").append(reminder.getDescription()).append("\n\n");

        // Информация о следующем повторе
        if (!reminder.getRepeat().equals("Один раз")) {
            builder.append("Следующее напоминание: ").append(reminder.getNextCall().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))).append("\n");
        }

        return builder.toString();
    }


    public void sendMessage(String chatId, String messageText) throws TelegramApiException {
        SendMessage message = SendMessage // Create a message object
                .builder()
                .chatId(chatId)
                .text(messageText)
                .build();
        telegramClient.execute(message);
    }
}
