package org.example.noti.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.example.noti.Entities.Reminder;
import org.example.noti.Entities.User;
import org.example.noti.Repositories.ReminderRepository;
import org.example.noti.Repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Controller
public class MainController {
    private final UserRepository userRepository;
    private final ReminderRepository reminderRepository;
    private final String[] DAY_OF_WEEK = new String[]{"ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС"};

    public MainController(UserRepository userRepository, ReminderRepository reminderRepository) {
        this.userRepository = userRepository;
        this.reminderRepository = reminderRepository;
    }

    @GetMapping("/")
    private String home(Model model, HttpServletRequest request) {
        String userId = request.getParameter("userId");
        System.out.println("User ID: " + userId);
        List<Reminder> reminders = reminderRepository.findAll();
        for (Reminder reminder : reminders) {
            reminder.setUser(null);
        }
        Reminder reminder = new Reminder();
        model.addAttribute("reminder", reminder);
        List<Reminder> foundedReminder = findReminderByDate(request);
        if (foundedReminder != null) {
            for (Reminder reminder2 : foundedReminder) {
                reminder2.setUser(null);
            }
        }
        model.addAttribute("reminders", Objects.requireNonNullElse(foundedReminder, reminders));
        model.addAttribute("updateReminder", new Reminder());
        return "main";
    }

    private List<Reminder> findReminderByDate(HttpServletRequest request) {
        String year = request.getParameter("year");
        String month = request.getParameter("month");
        String day = request.getParameter("day");
        if ((year == null || month == null || day == null) || day.isEmpty()) return null;
        return reminderRepository.findByYearAndMonthAndDay(
                Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)
        );
    }

    @PostMapping("/addReminder")
    public String addReminder(@ModelAttribute("reminder") Reminder reminder) {
        reminder.setUser(userRepository.findAll().get(0));
        reminder.setDayOfWeek(getDayOfWeek(reminder.getYear(), reminder.getMonth(), reminder.getDay()));
        reminderRepository.save(reminder);
        return "redirect:/";
    }

    @PostMapping("/delReminder")
    public String delReminder(HttpServletRequest request) {
        int reminderId = Integer.parseInt(request.getParameter("deleteReminderId"));
        reminderRepository.deleteById(reminderId);
        return "redirect:/";
    }
    @PostMapping("/editReminder")
    public String editReminder(@ModelAttribute("reminder") Reminder updateReminder,HttpServletRequest request) {
        int reminderId = Integer.parseInt(request.getParameter("editReminderId"));
        updateReminder.setId(reminderId);
        User user = reminderRepository.findById(reminderId).getUser();
        updateReminder.setUser(user);
        updateReminder.setDayOfWeek(getDayOfWeek(updateReminder.getYear(), updateReminder.getMonth(), updateReminder.getDay()));
        reminderRepository.save(updateReminder);
        return "redirect:/";
    }

    public String getDayOfWeek(int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        int dayNum = date.getDayOfWeek().getValue();
        return DAY_OF_WEEK[dayNum - 1];
    }

}
