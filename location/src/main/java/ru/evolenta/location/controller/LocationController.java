package ru.evolenta.location.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.evolenta.location.model.Location;
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

    @PostMapping
    public ResponseEntity<Location> save(@RequestBody Location location) {
        return service.save(location);
    }

    @GetMapping("/all")
    public Iterable<Location> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Location> findById(@PathVariable int id) {
        return repository.findById(id);
    }

    @GetMapping
    public Optional<Location> findByName(@RequestParam("name") String  name) {
        return repository.findLocationByNameLikeIgnoreCase(name);
    }

}
