package ru.evolenta.person.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Person {
    @Id @GeneratedValue
    private int id;

    @NonNull
    private String firstname;
    @NonNull
    private String surname;
    private String lastname;
    @NonNull
    private LocalDate birthday;
    @NonNull
    private String locationName;

    public Person(@NonNull String firstname, @NonNull String surname, String lastname, @NonNull LocalDate birthday, @NonNull String locationName) {
        this.firstname = firstname;
        this.surname = surname;
        this.lastname = lastname;
        this.birthday = birthday;
        this.locationName = locationName;
    }
}
