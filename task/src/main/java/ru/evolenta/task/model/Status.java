package ru.evolenta.task.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Status {
    @Id @GeneratedValue
    private int id;
    @NonNull
    @Column(unique = true)
    private String name;
    @OneToMany
    @JsonIgnore
    private List<Task> tasks;

    public Status(@NonNull String name) {
        this.name = name;
    }
}
