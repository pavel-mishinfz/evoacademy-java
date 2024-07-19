package ru.evolenta.location.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.evolenta.location.model.Geodata;
import ru.evolenta.location.model.Weather;
import ru.evolenta.location.repository.GeodataRepository;

import java.util.Optional;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private GeodataRepository repository;
    private RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/weather")
    public Weather redirectRequestWeather(@RequestParam String name) {
        Geodata geodata = repository.findByName(name).get();
        String url = String.format("http://localhost:8082/weather?lat=%s&lon=%s", geodata.getLat(), geodata.getLon());
        return restTemplate.getForObject(url, Weather.class);
    }

    @GetMapping
    public Optional<Geodata> getLocation(@RequestParam String name) {
        return repository.findByName(name);
    }

    @PostMapping
    public Geodata save(@RequestBody Geodata geodata) {
        return repository.save(geodata);
    }

}
