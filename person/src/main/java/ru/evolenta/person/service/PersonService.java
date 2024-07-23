package ru.evolenta.person.service;

import org.springframework.beans.factory.annotation.Autowired;
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

    public ResponseEntity<Person> save(Person person) {
        return repository.findById(person.getId()).isPresent()
                ? new ResponseEntity<>(repository.findById(person.getId()).get(), HttpStatus.BAD_REQUEST)
                : new ResponseEntity<>(repository.save(person), HttpStatus.CREATED);
    }

    public ResponseEntity<Person> update(int id, Person person) {
        Optional<Person> personOptional = repository.findById(id);
        if (personOptional.isPresent()) {
            Person updatedPerson = personOptional.get();
            updatedPerson.setName(person.getName());
            updatedPerson.setLocationName(person.getLocationName());
            return ResponseEntity.ok(repository.save(updatedPerson));
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<Weather> getWeather(int id) {
        Optional<Person> person = repository.findById(id);
        if (person.isPresent()) {
            String location = person.get().getLocationName();
            Weather weather = restTemplate.getForObject("http://localhost:8083/location/weather?name=" + location, Weather.class);
            return ResponseEntity.ok().body(weather);
        }
        return ResponseEntity.notFound().build();
    }
}
