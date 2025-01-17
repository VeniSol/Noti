package org.example.noti.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.noti.Entities.Reminder;
import org.example.noti.Entities.User;
import org.example.noti.Repositories.ReminderRepository;
import org.example.noti.Repositories.UserRepository;
import org.example.noti.Service.Sleeper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Controller
public class MainController {
    private final UserRepository userRepository;
    private final ReminderRepository reminderRepository;
    private final String[] DAY_OF_WEEK = new String[]{"ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС"};
    private final Sleeper sleeper;

    public MainController(UserRepository userRepository, ReminderRepository reminderRepository, Sleeper sleeper) {
        this.userRepository = userRepository;
        this.reminderRepository = reminderRepository;
        this.sleeper = sleeper;
    }

    @GetMapping("/")
    private String getUser() {
        return "authorization";
    }

    @GetMapping("/{id}")
    private String home(@PathVariable int id, Model model, HttpServletRequest request) {
        if (!userRepository.existsByTelegram(id)) {
            User newUser = new User();
            newUser.setTelegram(id);
            userRepository.save(newUser);
        }
        User user = userRepository.findByTelegram(id);
        user.setReminders(null);
        List<Reminder> reminders = reminderRepository.findByUser(user);
        for (Reminder reminder : reminders) {
            reminder.setUser(null);
        }
        Reminder reminder = new Reminder();
        model.addAttribute("reminder", reminder);
        List<Reminder> foundedReminder = findReminderByDateAndUser(request, user);
        if (foundedReminder != null) {
            for (Reminder reminder2 : foundedReminder) {
                reminder2.setUser(null);
            }
        }
        model.addAttribute("reminders", Objects.requireNonNullElse(foundedReminder, reminders));
        model.addAttribute("updateReminder", new Reminder());
        return "main";
    }

    private List<Reminder> findReminderByDateAndUser(HttpServletRequest request, User user) {
        String year = request.getParameter("year");
        String month = request.getParameter("month");
        String day = request.getParameter("day");
        if ((year == null || month == null || day == null) || day.isEmpty()) return null;
        return reminderRepository.findByYearAndMonthAndDayAndUser(
                Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), user);
    }

    @PostMapping("/addReminder/{id}")
    public String addReminder(@PathVariable int id,@ModelAttribute("reminder") Reminder reminder) {
        User user = userRepository.findByTelegram(id);
        reminder.setUser(user);
        reminder.setDayOfWeek(getDayOfWeek(reminder.getYear(), reminder.getMonth(), reminder.getDay()));
        reminder.setId(0);
        reminder.setNextCall(LocalDateTime.of(reminder.getYear(),reminder.getMonth(),reminder.getDay(),reminder.getTime().getHour(),reminder.getTime().getMinute()));
        reminderRepository.save(reminder);
        sleeper.startThread();
        return "redirect:/";
    }

    @PostMapping("/delReminder")
    public String delReminder(HttpServletRequest request) {
        int reminderId = Integer.parseInt(request.getParameter("deleteReminderId"));
        reminderRepository.deleteById(reminderId);
        return "redirect:/";
    }

    @PostMapping("/editReminder")
    public String editReminder(@ModelAttribute("reminder") Reminder updateReminder, HttpServletRequest request) {
        int reminderId = Integer.parseInt(request.getParameter("editReminderId"));
        updateReminder.setId(reminderId);
        User user = reminderRepository.findById(reminderId).getUser();
        updateReminder.setUser(user);
        updateReminder.setDayOfWeek(getDayOfWeek(updateReminder.getYear(), updateReminder.getMonth(), updateReminder.getDay()));
        updateReminder.setNextCall(LocalDateTime.of(updateReminder.getYear(),updateReminder.getMonth(),updateReminder.getDay(),updateReminder.getTime().getHour(),updateReminder.getTime().getMinute()));
        reminderRepository.save(updateReminder);
        sleeper.startThread();
        return "redirect:/";
    }

    public String getDayOfWeek(int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        int dayNum = date.getDayOfWeek().getValue();
        return DAY_OF_WEEK[dayNum - 1];
    }

}
