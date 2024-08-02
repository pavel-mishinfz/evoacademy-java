package ru.evolenta.task.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Status {
    @Id @GeneratedValue
    private int id;
    @Column(unique = true, nullable = false)
    private String name;
    @OneToMany
    @JsonIgnore
    private List<Task> tasks;

    public Status(String name) {
        this.name = name;
    }
}
