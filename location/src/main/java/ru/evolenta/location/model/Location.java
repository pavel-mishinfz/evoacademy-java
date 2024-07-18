package ru.evolenta.location.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Location {
    @Id @GeneratedValue
    private int id;

    @Column(unique = true)
    @NonNull
    private String name;
    @NonNull private Double latitude;
    @NonNull private Double longitude;

    public Location(@NonNull String name, @NonNull Double latitude, @NonNull Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
