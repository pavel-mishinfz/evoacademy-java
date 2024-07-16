package com.example.restdemo.controller;

import com.example.restdemo.dto.Message;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class MessageController {
    private List<Message> messages = new ArrayList<>();

    @PostMapping("/message")
    public Message addMessage(@RequestBody Message message) {
        messages.add(message);
        return message;
    }

    @GetMapping("/message")
    public Iterable<Message> getMessages() {
        return messages;
    }

    @GetMapping("/message/{id}")
    public Optional<Message> getMessage(@PathVariable int id) {
        return messages.stream().filter(msg -> msg.getId() == id).findFirst();
    }

    @PutMapping("/message/{id}")
    public Message updateMessage(@PathVariable int id, @RequestBody Message message) {
        int index = -1;
        for (Message msg : messages) {
            if(msg.getId() == id) {
                index = messages.indexOf(msg);
                messages.set(index, message);
            }
        }
        return index == -1 ? addMessage(message) : message;
    }

    @DeleteMapping("/message/{id}")
    public void deleteMessage(@PathVariable int id) {
        messages.removeIf(msg -> msg.getId() == id);
    }
}
