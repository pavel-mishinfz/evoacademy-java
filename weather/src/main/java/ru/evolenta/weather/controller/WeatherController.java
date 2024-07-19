package ru.evolenta.weather.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.evolenta.weather.model.Main;
import ru.evolenta.weather.model.Root;

import java.util.Objects;

@RestController
public class WeatherController {

    @Autowired
    RestTemplate restTemplate;
    @Value("${appid}")
    String appId;
    @Value("${url.weather}")
    private String urlWeather;

    @GetMapping("/weather")
    @Cacheable("weather")
    public Main getWeather(@RequestParam String lat, @RequestParam String lon) {
        String request = String.format("%s?lat=%s&lon=%s&units=metric&appid=%s", urlWeather, lat, lon, appId);
        return Objects.requireNonNull(restTemplate.getForObject(request, Root.class)).getMain();
    }
}
