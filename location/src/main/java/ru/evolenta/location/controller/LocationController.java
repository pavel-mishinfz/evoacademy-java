package ru.evolenta.location.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.evolenta.location.model.Location;
import ru.evolenta.location.model.Weather;
import ru.evolenta.location.repository.LocationRepository;
import ru.evolenta.location.service.LocationService;

import java.util.Optional;


@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private LocationRepository repository;

    @Autowired
    private LocationService service;

    @GetMapping
    public Iterable<Location> findAll() {
        return repository.findAll();
    }

    @GetMapping(params = "name")
    public Optional<Location> findByName(@RequestParam String name) {
        return repository.findByName(name);
    }

    @PostMapping
    public ResponseEntity<Location> save(@RequestBody Location location) {
        return service.save(location);
    }

    @PutMapping
    public ResponseEntity<Location> updateByName(@RequestParam String name, @RequestBody Location location) {
        return service.updateByName(name, location);
    }

    @DeleteMapping
    @Transactional
    public void deleteByName(@RequestParam String name) {
        repository.deleteByName(name);
    }

    @GetMapping("/weather")
    public ResponseEntity<Weather> redirectRequestWeather(@RequestParam String name) {
        return service.redirectRequestWeather(name);
    }

}
