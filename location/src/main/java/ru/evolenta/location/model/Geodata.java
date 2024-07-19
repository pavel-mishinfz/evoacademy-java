package ru.evolenta.location.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Geodata {
    @Id @GeneratedValue
    int id;
    @NonNull private Double lon;
    @NonNull private Double lat;
    @NonNull private String name;
}
