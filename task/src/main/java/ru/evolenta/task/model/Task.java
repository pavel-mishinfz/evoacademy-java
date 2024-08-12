package ru.evolenta.task.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Task {
    @Id @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private LocalDateTime completionDate;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Status status;

    public Task(String title, String description, LocalDateTime createDate, LocalDateTime completionDate, Long userId, Status status) {
        this.title = title;
        this.description = description;
        this.createDate = createDate;
        this.completionDate = completionDate;
        this.userId = userId;
        this.status = status;
    }
}
