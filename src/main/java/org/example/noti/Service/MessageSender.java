package org.example.noti.Service;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class MessageSender {
    private final TelegramClient telegramClient;

    public MessageSender(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
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
