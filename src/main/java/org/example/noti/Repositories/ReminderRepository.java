package org.example.noti.Repositories;

import org.example.noti.Entities.Reminder;
import org.example.noti.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Integer> {
    List<Reminder> findByYearAndMonthAndDayAndUser(int year, int month, int day,User user);
    Reminder findById(int id);
    List<Reminder> findByUser(User user);
    @Query("SELECT MIN(e.nextCall) FROM Reminder e")
    LocalDateTime getNextCall();
    List<Reminder> findByNextCall(LocalDateTime nextCall);
    @Query("SELECT COUNT(r) FROM Reminder r WHERE r.nextCall IS NOT NULL")
    int getRemindersCount();
}
