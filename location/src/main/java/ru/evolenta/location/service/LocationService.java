package ru.evolenta.location.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.evolenta.location.model.Location;
import ru.evolenta.location.model.Weather;
import ru.evolenta.location.repository.LocationRepository;

import java.util.Optional;

@Service
public class LocationService {

    @Autowired
    private LocationRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<Location> save(Location location) {
        Optional<Location> existingLocation = repository.findById(location.getId());
        if (existingLocation.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(existingLocation.get());
        }
        existingLocation = repository.findByName(location.getName());
        return existingLocation.map(value -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(value))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CREATED).body(repository.save(location)));
    }

    public ResponseEntity<Location> updateByName(String name, Location location) {
        Optional<Location> locationOptional = repository.findByName(name);
        if (locationOptional.isPresent()) {
            Location updateLocation = locationOptional.get();
            updateLocation.setName(location.getName());
            updateLocation.setLongitude(location.getLongitude());
            updateLocation.setLatitude(location.getLatitude());
            return ResponseEntity.ok().body(repository.save(updateLocation));
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<Weather> redirectRequestWeather(String name) {
        Optional<Location> locationOptional = repository.findByName(name);
        if (locationOptional.isPresent()) {
            Location location = locationOptional.get();
            String url = String.format("http://localhost:8082/weather?lat=%s&lon=%s", location.getLatitude(), location.getLongitude());
            return ResponseEntity.ok().body(restTemplate.getForObject(url, Weather.class));
        }
        return ResponseEntity.notFound().build();
    }
}
