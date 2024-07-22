package ru.evolenta.person.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.evolenta.person.model.Person;
import ru.evolenta.person.model.Weather;
import ru.evolenta.person.repository.PersonRepository;

import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${location.url}")
    private String locationUrl;

    public ResponseEntity<Person> save(Person person) {
        Optional<Person> personOptional = repository.findById(person.getId());
        return personOptional.map(value -> ResponseEntity.badRequest().body(value))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CREATED).body(repository.save(person)));
    }

    public ResponseEntity<Person> update(int id, Person person) {
        Optional<Person> personOptional = repository.findById(id);
        if (personOptional.isPresent()) {
            person.setId(personOptional.get().getId());
            return ResponseEntity.ok(repository.save(person));
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<Weather> getWeather(int id) {
        Optional<Person> person = repository.findById(id);
        if (person.isPresent()) {
            String location = person.get().getLocationName();
            Weather weather = restTemplate.getForObject(locationUrl + location, Weather.class);
            return ResponseEntity.ok().body(weather);
        }
        return ResponseEntity.notFound().build();
    }
}
