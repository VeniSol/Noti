package org.example.noti.Repositories;

import org.example.noti.Entities.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Integer> {
    List<Reminder> findByYearAndMonthAndDay(int year, int month, int day);
    Reminder findById(int id);
}
