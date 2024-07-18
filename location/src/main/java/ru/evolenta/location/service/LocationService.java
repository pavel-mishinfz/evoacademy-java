package ru.evolenta.location.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.evolenta.location.model.Location;
import ru.evolenta.location.repository.LocationRepository;

import java.util.Optional;

@Service
public class LocationService {

    @Autowired
    private LocationRepository repository;

    public ResponseEntity<Location> save(Location location) {
        Optional<Location> existingLocation = repository.findById(location.getId());
        if (existingLocation.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(existingLocation.get());
        }
        existingLocation = repository.findLocationByNameLikeIgnoreCase(location.getName());
        return existingLocation.map(value -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(value))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CREATED).body(repository.save(location)));
    }
}
