package ru.evolenta.person.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Person {
    @Id @GeneratedValue
    private int id;

    @NonNull private String name;
    @NonNull private String locationName;

    public Person(@NonNull String name, @NonNull String locationName) {
        this.name = name;
        this.locationName = locationName;
    }
}
