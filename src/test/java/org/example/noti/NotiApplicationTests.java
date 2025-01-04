package org.example.noti;

import org.example.noti.Service.MessageSender;
import org.example.noti.Controllers.MainController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@DataJpaTest
class NotiApplicationTests {
    MainController mainController = new MainController(null,null);
    MessageSender messageSender = new MessageSender("Token");
    @Test
    void addReminder() {
        Assertions.assertEquals("ПН",mainController.getDayOfWeek(2024,12,30));
        Assertions.assertEquals("ВТ",mainController.getDayOfWeek(2024,12,31));
        Assertions.assertEquals("СР",mainController.getDayOfWeek(2025,1,1));
        Assertions.assertEquals("ЧТ",mainController.getDayOfWeek(2025,1,2));
        Assertions.assertEquals("ПТ",mainController.getDayOfWeek(2025,1,3));
        Assertions.assertEquals("СБ",mainController.getDayOfWeek(2025,1,4));
        Assertions.assertEquals("ВС",mainController.getDayOfWeek(2025,1,5));
    }
    @Test
    void messageSender() throws TelegramApiException {
        messageSender.sendMessage("userId","text");
    }
}
