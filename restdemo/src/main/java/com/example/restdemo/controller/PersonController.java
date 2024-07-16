package com.example.restdemo.controller;

import com.example.restdemo.dto.Person;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class PersonController {
    private List<Person> persons = new ArrayList<>();

    @PostMapping("/person")
    public Person addPerson(@RequestBody Person person) {
        persons.add(person);
        return person;
    }

    @GetMapping("/person")
    public Iterable<Person> getPersons() {
        return persons;
    }

    @GetMapping("/person/{id}")
    public Optional<Person> getPerson(@PathVariable int id) {
        return persons.stream().filter(p -> p.getId() == id).findFirst();
    }

    @PutMapping("/person/{id}")
    public Person updatePerson(@PathVariable int id, @RequestBody Person person) {
        int index = -1;
        for (Person p : persons) {
            if(p.getId() == id) {
                index = persons.indexOf(p);
                persons.set(index, person);
            }
        }
        return index == -1 ? addPerson(person) : person;
    }

    @DeleteMapping("/person/{id}")
    public void deletePerson(@PathVariable int id) {
        persons.removeIf(p -> p.getId() == id);
    }
}
