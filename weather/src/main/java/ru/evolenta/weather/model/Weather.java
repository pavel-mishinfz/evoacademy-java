package ru.evolenta.weather.model;

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
public class Weather {
    @Id @GeneratedValue
    private int id;

    @NonNull private Double latitude;
    @NonNull private Double longitude;
    @NonNull private String conditions;
    @NonNull private Double temperature;

    public Weather(@NonNull Double latitude, @NonNull Double longitude, @NonNull String conditions, @NonNull Double temperature) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.conditions = conditions;
        this.temperature = temperature;
    }
}
