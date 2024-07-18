package ru.evolenta.person.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.evolenta.person.model.Person;
import ru.evolenta.person.repository.PersonRepository;

@Service
public class PersonService {

    @Autowired
    private PersonRepository repository;

    public ResponseEntity<Person> save(Person person) {
        return repository.findById(person.getId()).isPresent()
                ? new ResponseEntity<>(repository.findById(person.getId()).get(), HttpStatus.BAD_REQUEST)
                : new ResponseEntity<>(repository.save(person), HttpStatus.CREATED);
    }
}
