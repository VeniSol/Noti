package org.example.noti.Service;

import jakarta.annotation.PostConstruct;
import org.example.noti.Entities.Reminder;
import org.example.noti.Repositories.ReminderRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class Sleeper {
    private final String[] DAY_OF_WEEK = new String[]{"ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС"};
    private final ReminderRepository reminderRepository;
    private final MessageSender messageSender = new MessageSender("8082701889:AAFOIhpwSPbQhWFML0oMJwScUcc10prkDcI");
    Thread currentThread;
    public Sleeper(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    public void startThread(){
        stopSleeper();
    }

    @PostConstruct
    private void startSendingMessage() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    if (reminderRepository.getRemindersCount() == 0) {
                        break;
                    }
                    long timeBeforeNextCall = getTimeBeforeNextCall();
                    if (timeBeforeNextCall < 0) timeBeforeNextCall=0L;
                    Thread.sleep(timeBeforeNextCall);
                    List<Reminder> reminders = reminderRepository.findByNextCall(reminderRepository.getNextCall());
                    sendMessage(reminders);
                    editReminder(reminders);
                } catch (InterruptedException | TelegramApiException e){
                    e.printStackTrace();
                }
            }
        });
        currentThread = thread;
        thread.start();
    }

    private void sendMessage(List<Reminder> reminders) throws TelegramApiException {
        for (Reminder reminder : reminders) {
            messageSender.sendMessage(reminder);
        }
    }

    private void editReminder(List<Reminder> reminders) {
        for (Reminder reminder : reminders) {
            int durationBeforeRepeat = 0;
            int dayMinutes = 24 * 60;
            switch (reminder.getRepeat()) {
                case "Один раз" -> reminder.setNextCall(null);
                case "Ежедневно" -> durationBeforeRepeat = dayMinutes;
                case "Еженедельно" -> durationBeforeRepeat = 7 * dayMinutes;
                case "Ежемесячно" -> {
                    int month = reminder.getMonth();
                    int year = reminder.getYear();
                    YearMonth yearMonth = YearMonth.of(year, month);
                    int daysInMonth = yearMonth.lengthOfMonth();
                    durationBeforeRepeat = dayMinutes * daysInMonth;
                }
            }
            if (reminder.getNextCall()!=null) {
            reminder.setNextCall(reminder.getNextCall().plus(Duration.ofMinutes(durationBeforeRepeat)));
            LocalDateTime oldDate = LocalDateTime.of(reminder.getYear(), reminder.getMonth(),
                    reminder.getDay(), reminder.getTime().getHour(), reminder.getTime().getMinute());
            LocalDateTime newDate = oldDate.plus(Duration.ofMinutes(durationBeforeRepeat));
            reminder.setYear(newDate.getYear());
            reminder.setMonth(newDate.getMonthValue());
            reminder.setDay(newDate.getDayOfMonth());
            reminder.setDayOfWeek(getDayOfWeek(newDate.getYear(),newDate.getMonthValue(),newDate.getDayOfMonth()));
           }
            reminderRepository.save(reminder);
        }
    }

    private Long getTimeBeforeNextCall() {
        LocalDateTime timeBeforeCall = reminderRepository.getNextCall();
        Duration duration = Duration.between(LocalDateTime.now(),timeBeforeCall);
        return duration.toMillis();
    }

    private void stopSleeper(){
        try{
            currentThread.interrupt();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getDayOfWeek(int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        int dayNum = date.getDayOfWeek().getValue();
        return DAY_OF_WEEK[dayNum - 1];
    }
}
