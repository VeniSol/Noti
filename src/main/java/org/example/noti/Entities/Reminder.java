package org.example.noti.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reminders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;
    private int year;
    private int month;
    private int day;
    private LocalTime time;
    private String repeat;
    private String dayOfWeek;
    private LocalDateTime nextCall;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
