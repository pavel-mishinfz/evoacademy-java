package ru.evolenta.task.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Task {
    @Id @GeneratedValue
    private int id;
    @NonNull
    private String title;
    @NonNull
    private String description;
    @NonNull
    private LocalDateTime createDate;
    @NonNull
    private LocalDateTime completionDate;
    @ManyToOne
    @NonNull
    private Status status;

    public Task(@NonNull String title, @NonNull String description, @NonNull LocalDateTime createDate, @NonNull LocalDateTime completionDate, @NonNull Status status) {
        this.title = title;
        this.description = description;
        this.createDate = createDate;
        this.completionDate = completionDate;
        this.status = status;
    }
}
