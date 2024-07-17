package com.example.restdemo.controller;

import com.example.restdemo.dto.Message;
import com.example.restdemo.dto.Person;
import com.example.restdemo.repository.PersonRepository;
import com.example.restdemo.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
public class PersonController {
    @Autowired
    private PersonRepository repository;

    @Autowired
    private PersonService service;

    @PostMapping("/person")
    public Person addPerson(@RequestBody Person person) {
        repository.save(person);
        return person;
    }

    @GetMapping("/person")
    public Iterable<Person> getPersons() {
        return repository.findAll();
    }

    @GetMapping("/person/{id}")
    public Optional<Person> findPersonById(@PathVariable int id) {
        return repository.findById(id);
    }

    @PutMapping("/person/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable int id, @RequestBody Person person) {
        HttpStatus status = repository.existsById(id) ? HttpStatus.OK : HttpStatus.CREATED;
        person.setId(id);
        return new ResponseEntity<>(repository.save(person), status);
    }

    @DeleteMapping("/person/{id}")
    public void deletePerson(@PathVariable int id) {
        repository.deleteById(id);
    }

    @PostMapping("/person/{p_id}/message")
    public ResponseEntity<Person> addMessage(@PathVariable int p_id, @RequestBody Message message) {
        return service.addMessageToPerson(p_id, message);
    }

    @GetMapping("/person/{p_id}/message")
    public ResponseEntity<Iterable<Message>> getMessages(@PathVariable int p_id) {
        return service.getMessagesOfPerson(p_id);
    }

    @GetMapping("/person/{p_id}/message/{m_id}")
    public ResponseEntity<Optional<Message>> findMessageById(@PathVariable int p_id, @PathVariable int m_id) {
        return service.findMessageOfPersonById(p_id, m_id);
    }

    @DeleteMapping("/person/{p_id}/message/{m_id}")
    public void deleteMessage(@PathVariable int p_id, @PathVariable int m_id) {
        service.deleteMessageOfPerson(p_id, m_id);
    }

}
