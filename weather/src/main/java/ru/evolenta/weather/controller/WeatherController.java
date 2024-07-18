package ru.evolenta.weather.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.evolenta.weather.model.Weather;
import ru.evolenta.weather.repository.WeatherRepository;

import java.util.Optional;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherRepository repository;

    @PostMapping
    public ResponseEntity<Weather> save(@RequestBody Weather weather) {
        repository.save(weather);
        return ResponseEntity.ok(weather);
    }

    @GetMapping
    public Optional<Weather> findByLatAndLon(@RequestParam("lat") double lat, @RequestParam("lon") double lon) {
        return repository.findWeatherByLatitudeAndLongitude(lat, lon);
    }
}
