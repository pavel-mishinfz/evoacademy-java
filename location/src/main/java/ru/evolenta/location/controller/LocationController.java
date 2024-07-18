package ru.evolenta.location.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.evolenta.location.model.Location;
import ru.evolenta.location.repository.LocationRepository;

import java.util.Optional;

@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private LocationRepository repository;

    @PostMapping
    public ResponseEntity<Location> save(@RequestBody Location location) {
        Optional<Location> existingLocation = repository.findById(location.getId());
        if (existingLocation.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(existingLocation.get());
        }
        existingLocation = repository.findLocationByNameLikeIgnoreCase(location.getName());
        return existingLocation.map(value -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(value))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CREATED).body(repository.save(location)));
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
