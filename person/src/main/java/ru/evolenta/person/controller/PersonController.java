package ru.evolenta.person.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.evolenta.person.model.Person;
import ru.evolenta.person.repository.PersonRepository;
import ru.evolenta.person.service.PersonService;

import java.util.Optional;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonRepository repository;

    @Autowired
    private PersonService service;

    @GetMapping
    public Iterable<Person> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Person> findById(@PathVariable int id) {
        return repository.findById(id);
    }

    @PostMapping
    public ResponseEntity<Person> save(@RequestBody Person person) {
        return service.save(person);
    }
}
