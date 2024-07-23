package ru.evolenta.location.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Weather {
    private int id;
    private Double latitude;
    private Double longitude;
    private String conditions;
    private Double temperature;
}