package com.example.restdemo.service;

import com.example.restdemo.dto.Message;
import com.example.restdemo.dto.Person;
import com.example.restdemo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PersonService {
    @Autowired
    PersonRepository repository;

    public ResponseEntity<Person> addMessageToPerson(int personId, Message message) {
        Optional<Person> personOptional = repository.findById(personId);
        if (personOptional.isPresent()) {
            Person person = personOptional.get();
            message.setPerson(person);
            message.setTime(LocalDateTime.now());
            person.addMessage(message);
            return new ResponseEntity<>(repository.save(person), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Iterable<Message>> getMessagesOfPerson(int personId) {
        Optional<Person> personOptional = repository.findById(personId);
        if(personOptional.isPresent()) {
            Person person = personOptional.get();
            return new ResponseEntity<>(person.getMessages(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Optional<Message>> findMessageOfPersonById(int personId, int messageId) {
        Optional<Person> personOptional = repository.findById(personId);
        if (personOptional.isPresent()) {
            Person person = personOptional.get();
            Optional<Message> messageOptional = person.getMessages().stream().filter(msg -> msg.getId() == messageId).findFirst();
            return new ResponseEntity<>(messageOptional, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public void deleteMessageOfPerson(int personId, int messageId) {
        Optional<Person> personOptional = repository.findById(personId);
        if(personOptional.isPresent()) {
            Person person = personOptional.get();
            person.getMessages().removeIf(msg -> msg.getId() == messageId);
            repository.save(person);
        }
    }
}
